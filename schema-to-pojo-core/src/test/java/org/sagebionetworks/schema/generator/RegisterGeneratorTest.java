package org.sagebionetworks.schema.generator;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

/**
 * Test for the register generator.
 * 
 * @author John
 * 
 */
public class RegisterGeneratorTest {
	
	List<ObjectSchema> list;
	@Before
	public void before(){
		list = new ArrayList<ObjectSchema>();
		ObjectSchema schema = new ObjectSchema(TYPE.OBJECT);
		schema.setName("one");
		schema.setId("org.example.One");
		list.add(schema);
		
		schema = new ObjectSchema(TYPE.OBJECT);
		schema.setName("Two");
		schema.setId("org.example.Two");
		list.add(schema);
	}

	@Test
	public void testGetClassName() {
		String result = RegisterGenerator
				.getClassName("org.sagebionetworks.model.Test");
		assertEquals("Test", result);
	}

	@Test
	public void testGetPackage() {
		String result = RegisterGenerator
				.getPackageName("org.sagebionetworks.model.Test");
		assertEquals("org.sagebionetworks.model", result);
	}
	
	@Test
	public void testCreateMapRef() throws JClassAlreadyExistsException{
		JCodeModel codeModel = new JCodeModel();
		JPackage _package = codeModel._package("org.sample");
		// Create the enum
		JDefinedClass testClass = _package._class("Test");
		JFieldRef mapRef = RegisterGenerator.createMapFieldRef(codeModel, testClass);
		assertNotNull(mapRef);
	}
	
	/**
	 * Test
	 * 
	 * @throws JClassAlreadyExistsException
	 */
	@Test
	public void testCreateConstructor() throws JClassAlreadyExistsException {
		JCodeModel codeModel = new JCodeModel();
		JPackage _package = codeModel._package("org.sample");
		// Create the enum
		JDefinedClass testClass = _package._class("Test");
		JFieldRef mapRef = RegisterGenerator.createMapFieldRef(codeModel, testClass);
		JMethod method = RegisterGenerator.createConstructor(codeModel, list, testClass, mapRef);
		assertNotNull(method);
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		method.declare(formatter);
		String methodValue = writer.toString();
//		System.out.println(methodValue);
		assertTrue(methodValue.indexOf("public Test() {") >= 0);
		assertTrue(methodValue.indexOf("this.map = new java.util.HashMap<java.lang.String, java.lang.Class>();") > 0);
		assertTrue(methodValue.indexOf("this.map.put(org.example.One.class.getName(), org.example.One.class);") > 0);
		assertTrue(methodValue.indexOf("this.map.put(org.example.Two.class.getName(), org.example.Two.class);") > 0);
	}
	
	/**
	 * Test
	 * 
	 * @throws JClassAlreadyExistsException
	 */
	@Test
	public void testCreateClassForName() throws JClassAlreadyExistsException {
		JCodeModel codeModel = new JCodeModel();
		JPackage _package = codeModel._package("org.sample");
		// Create the enum
		JDefinedClass testClass = _package._class("Test");
		JFieldRef mapRef = RegisterGenerator.createMapFieldRef(codeModel, testClass);
		JMethod method = RegisterGenerator.createClassForName(codeModel, testClass, mapRef);
		assertNotNull(method);
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		method.declare(formatter);
		String methodValue = writer.toString();
//		System.out.println(methodValue);
		assertTrue(methodValue.indexOf("Lookup a class using its full package name.  This works like Class.forName(className), but is GWT compatible.") >= 0);
		assertTrue(methodValue.indexOf("public java.lang.Class forName(java.lang.String className) {") > 0);
		assertTrue(methodValue.indexOf("return this.map.get(className);") > 0);
	}
	
	/**
	 * Test
	 * 
	 * @throws JClassAlreadyExistsException
	 */
	@Test
	public void testGetKeySetIterator() throws JClassAlreadyExistsException {
		JCodeModel codeModel = new JCodeModel();
		JPackage _package = codeModel._package("org.sample");
		// Create the enum
		JDefinedClass testClass = _package._class("Test");
		JFieldRef mapRef = RegisterGenerator.createMapFieldRef(codeModel, testClass);
		JMethod method = RegisterGenerator.createKeySetIterator(codeModel, testClass, mapRef);
		assertNotNull(method);
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		method.declare(formatter);
		String methodValue = writer.toString();
		System.out.println(methodValue);
		assertTrue(methodValue.indexOf("Get the key set iterator.") >= 0);
		assertTrue(methodValue.indexOf("public java.util.Iterator<java.lang.String> getKeySetIterator() {") > 0);
		assertTrue(methodValue.indexOf("return this.map.keySet().iterator();") > 0);
	}

	@Test
	public void testCreateMap() {
		RegisterGenerator regGen = new RegisterGenerator();
		JCodeModel model = new JCodeModel();
		JDefinedClass def = regGen.createRegister(model, list,	"org.example.Register");
		assertNotNull(def);

		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		def.declare(formatter);
		String value = writer.toString();
		System.out.println(writer.toString());
		assertTrue(value.indexOf("Note: This class was auto-generated, and should not be directly modified") > 0);
		assertTrue(value.indexOf("private java.util.Map<java.lang.String, java.lang.Class> map;") > 0);
	}


}
