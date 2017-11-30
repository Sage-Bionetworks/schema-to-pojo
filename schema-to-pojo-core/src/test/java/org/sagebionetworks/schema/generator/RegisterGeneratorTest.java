package org.sagebionetworks.schema.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.EnumValue;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONEntity;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
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
	public void testIsConcreteClass(){
		RegisterGenerator regGen = new RegisterGenerator();
		// Enums are not concrete classes
		ObjectSchema enumClass = new ObjectSchema(TYPE.OBJECT);
		enumClass.setEnum(new EnumValue[] {
				new EnumValue("one")
		});
		assertFalse(regGen.isConcreteClass(enumClass));
		// Interface is not a concrete class.
		ObjectSchema interfaceClass = new ObjectSchema(TYPE.INTERFACE);
		assertFalse(regGen.isConcreteClass(interfaceClass));
		// This is a concrete class
		ObjectSchema concreteClass = new ObjectSchema(TYPE.OBJECT);
		// Still needs an id
		assertFalse(regGen.isConcreteClass(concreteClass));
		concreteClass.setId("org.sample.Test");
		assertTrue(regGen.isConcreteClass(concreteClass));
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
		assertTrue(methodValue.indexOf("this.map = new java.util.HashMap<java.lang.String, java.lang.Integer>();") > 0);
		assertTrue(methodValue.indexOf("this.map.put(org.example.One.class.getName(), 0);") > 0);
		assertTrue(methodValue.indexOf("this.map.put(org.example.Two.class.getName(), 1);") > 0);
	}
	
	
	/**
	 * Test
	 * 
	 * @throws JClassAlreadyExistsException
	 */
	@Test
	public void testCreateNewInstanceMethod() throws JClassAlreadyExistsException {
		JCodeModel codeModel = new JCodeModel();
		JPackage _package = codeModel._package("org.sample");
		// Create the enum
		JDefinedClass testClass = _package._class("Test");
		JFieldRef mapRef = RegisterGenerator.createMapFieldRef(codeModel, testClass);
		JMethod method = RegisterGenerator.createNewInstanceMethod(codeModel, list, testClass, mapRef, JSONEntity.class.getName());
		assertNotNull(method);
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		method.declare(formatter);
		String methodValue = writer.toString();
		System.out.println(methodValue);
		assertTrue(methodValue.indexOf("public org.sagebionetworks.schema.adapter.JSONEntity newInstance(java.lang.String className) {") >= 0);
		assertTrue(methodValue.indexOf("java.lang.Integer intObject = this.map.get(className);") > 0);
		assertTrue(methodValue.indexOf("if (intObject == null) {") > 0);
		assertTrue(methodValue.indexOf("throw new java.lang.IllegalArgumentException((\"Cannot create new instance. Unknown class: \"+ className));") > 0);
		assertTrue(methodValue.indexOf("int index = intObject.intValue();") > 0);
		assertTrue(methodValue.indexOf("switch (index) {") > 0);
		assertTrue(methodValue.indexOf("case  0 :") > 0);
		assertTrue(methodValue.indexOf("return new org.example.One();") > 0);
		assertTrue(methodValue.indexOf("default:") > 0);
		assertTrue(methodValue.indexOf("throw new java.lang.IllegalStateException((((\"No match found for index: \"+ index)+\" for class name: \")+ className));") > 0);
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
		JCodeModel model = new JCodeModel();
		JDefinedClass def = RegisterGenerator.createRegister(model, list, RegisterGenerator.createClassFromFullName(model,"org.example.Register"), JSONEntity.class.getName());
		assertNotNull(def);

		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		def.declare(formatter);
		String value = writer.toString();
//		System.out.println(writer.toString());
		assertTrue(value.indexOf("Note: This class was auto-generated, and should not be directly modified") > 0);
		assertTrue(value.indexOf("private java.util.Map<java.lang.String, java.lang.Integer> map;") > 0);
	}

	@Test
	public void testSingleton() {
		JCodeModel model = new JCodeModel();
		JDefinedClass def = RegisterGenerator.createRegister(model, list,	RegisterGenerator.createClassFromFullName(model,"org.example.Register"), JSONEntity.class.getName());
		assertNotNull(def);

		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		def.declare(formatter);
		String value = writer.toString();
		System.out.println(writer.toString());
		assertTrue(value.indexOf("private final static org.example.Register SINGLETON = new org.example.Register();") > 0);
		assertTrue(value.indexOf("public static org.example.Register singleton() {") > 0);
		assertTrue(value.indexOf("return SINGLETON;") > 0);
	}


}
