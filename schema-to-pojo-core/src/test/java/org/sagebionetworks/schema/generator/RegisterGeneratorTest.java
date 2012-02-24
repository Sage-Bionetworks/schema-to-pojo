package org.sagebionetworks.schema.generator;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
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
	public void testCreateEnum() {
		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		
		ObjectSchema schema = new ObjectSchema(TYPE.OBJECT);
		schema.setName("one");
		schema.setId("org.example.One");
		list.add(schema);
		
		schema = new ObjectSchema(TYPE.OBJECT);
		schema.setName("Two");
		schema.setId("org.example.Two");
		list.add(schema);

		RegisterGenerator regGen = new RegisterGenerator();
		JCodeModel model = new JCodeModel();
		JDefinedClass def = regGen.createRegister(model, list,
				"org.example.Register");
		assertNotNull(def);

		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		def.declare(formatter);
		String value = writer.toString();
		System.out.println(writer.toString());
		assertTrue(value.indexOf("ORG_EXAMPLE_ONE(org.example.One.class),") > 0);
		assertTrue(value.indexOf("ORG_EXAMPLE_TWO(org.example.Two.class);") > 0);
		assertTrue(value.indexOf("private java.lang.Class<?> clazz;") > 0);
		assertTrue(value.indexOf("private Register(java.lang.Class<?> clazz) {") > 0);
		assertTrue(value.indexOf("public java.lang.Class<?> getRegisteredClass() {") > 0);
	}

	/**
	 * Test
	 * 
	 * @throws JClassAlreadyExistsException
	 */
	@Test
	public void testCreateStaticLookup() throws JClassAlreadyExistsException {
		JCodeModel codeModel = new JCodeModel();
		JPackage _package = codeModel._package("org.sample");
		// Create the enum
		JDefinedClass enumClass = _package._enum("Test");
		JMethod method = RegisterGenerator.createStaticLookup(codeModel, enumClass);
		assertNotNull(method);
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		method.declare(formatter);
		String methodValue = writer.toString();
		System.out.println(methodValue);
		assertTrue(methodValue.indexOf("public static org.sample.Test typeForName(java.lang.String fullClassName) {") > 0);
		assertTrue(methodValue.indexOf("for (org.sample.Test reg: org.sample.Test.values()) {") > 0);
		assertTrue(methodValue.indexOf("throw new java.lang.IllegalArgumentException((\"No class registered with the name: \"+ fullClassName));") > 0);
	}
}
