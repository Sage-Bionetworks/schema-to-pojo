package org.sagebionetworks.schema.generator.handler.schema03;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDeclaration;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

public class TypeCreatorHandlerImpl03Test {
	
	JCodeModel codeModel;
	JPackage _package;
	JType type;
	ObjectSchema schema;

	@Before
	public void before() throws JClassAlreadyExistsException,
			ClassNotFoundException {
		codeModel = new JCodeModel();
		_package = codeModel._package("org.sample");
//		sampleClass = codeModel._class("Sample");
		schema = new ObjectSchema();
		schema.setType(TYPE.OBJECT);
		// give it a name
		schema.setName("Sample");
	}
	
	@Test
	public void testCreateNewClass() throws ClassNotFoundException{
		// Add a title and description
		String title = "This is the title";
		String description = "Add a description";
		schema.setTitle(title);
		schema.setDescription(description);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		// Make sure we can call this twice with the same class
		JType second = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertEquals(clazz, second);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		// Write to a string
		String classString = declareToString(sampleClass);
//		System.out.println(classString);
		assertTrue(classString.indexOf(title) > 0);
		assertTrue(classString.indexOf(description) > 0);
		assertTrue(classString.indexOf(TypeCreatorHandlerImpl03.AUTO_GENERATED_MESSAGE) > 0);
	}
	
	@Test
	public void testCreateNewInterface() throws ClassNotFoundException, JClassAlreadyExistsException{
		// Add a title and description
		String title = "This is the title";
		String description = "Add a description";
		schema.setTitle(title);
		schema.setDescription(description);
		schema.setType(TYPE.INTERFACE);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JDefinedClass parentInterface = _package._interface("ParentInterface");
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, new JType[]{parentInterface});
		assertNotNull(clazz);
		// Make sure we can call this twice with the same class
		JType second = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, new JType[]{parentInterface});
		assertEquals(clazz, second);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		assertTrue(sampleClass.isInterface());
		JClass parent = sampleClass.getBaseClass(parentInterface);
		assertNotNull(parent);
		assertTrue(parent.isInterface());
		// Write to a string
		String classString = declareToString(sampleClass);
//		System.out.println(classString);
		assertTrue(classString.indexOf(title) > 0);
		assertTrue(classString.indexOf(description) > 0);
		assertTrue(classString.indexOf(TypeCreatorHandlerImpl03.AUTO_GENERATED_MESSAGE) > 0);
		assertTrue(classString.indexOf(TypeCreatorHandlerImpl03.AUTO_GENERATED_MESSAGE) > 0);
	}
	
	@Test
	public void testClassExtendsClass() throws ClassNotFoundException, JClassAlreadyExistsException {
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		JDefinedClass parentClass = _package._class("ParentClass");
		JType clazz = handler.handelCreateType(_package, schema, parentClass, null, null);
		assertNotNull(clazz);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		String classString = declareToString(sampleClass);
//		System.out.println(classString);
		assertTrue(classString.indexOf("extends org.sample.ParentClass") > 0);
	}
	
	@Test
	public void testClassImplementsInterfances() throws ClassNotFoundException, JClassAlreadyExistsException{
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		JDefinedClass parentInternace = _package._interface("ParentInterface");
		JDefinedClass parentInternace2 = _package._interface("ParentInterface2");
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, new JType[]{parentInternace, parentInternace2});
		assertNotNull(clazz);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		String classString = declareToString(sampleClass);
//		System.out.println(classString);
		assertTrue(classString.indexOf("implements org.sagebionetworks.schema.adapter.JSONEntity, org.sample.ParentInterface, org.sample.ParentInterface2") > 0);
	}
	
	@Test
	public void testInterfanceExtendsNull()throws ClassNotFoundException, JClassAlreadyExistsException{
		schema.setType(TYPE.INTERFACE);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		String classString = declareToString(sampleClass);
//		System.out.println(classString);
		assertTrue(classString.indexOf("public interface Sample") > 0);
		assertTrue(classString.indexOf("extends org.sagebionetworks.schema.adapter.JSONEntity") > 0);
	}
	
	@Test
	public void testInterfanceExtendsInterfance() throws ClassNotFoundException, JClassAlreadyExistsException{
		schema.setType(TYPE.INTERFACE);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		JDefinedClass parentInternace = _package._interface("ParentInterface");
		JDefinedClass parentInternace2 = _package._interface("ParentInterface2");
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, new JType[]{parentInternace, parentInternace2});
		assertNotNull(clazz);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		String classString = declareToString(sampleClass);
		System.out.println(classString);
		assertTrue(classString.indexOf("public interface Sample") > 0);
		assertTrue(classString.indexOf("extends org.sagebionetworks.schema.adapter.JSONEntity, org.sample.ParentInterface, org.sample.ParentInterface2") > 0);
	}
	
	@Test
	public void testStringFormatedDateTime() throws ClassNotFoundException{
		// String formated as date-time
		schema.setType(TYPE.STRING);
		schema.setFormat(FORMAT.DATE_TIME);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	@Test
	public void testStringFormatedDate() throws ClassNotFoundException{
		// String formated as date
		schema.setType(TYPE.STRING);
		schema.setFormat(FORMAT.DATE);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	@Test
	public void testStringFormatedTime() throws ClassNotFoundException{
		// String formated as date
		schema.setType(TYPE.STRING);
		schema.setFormat(FORMAT.TIME);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	@Test
	public void testStringFormatedUTC_MILLISEC() throws ClassNotFoundException{
		// String formated as date
		schema.setType(TYPE.STRING);
		schema.setFormat(FORMAT.UTC_MILLISEC);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	
	@Test
	public void testIntegerFormatedDateTime() throws ClassNotFoundException{
		// String formated as date-time
		schema.setType(TYPE.INTEGER);
		schema.setFormat(FORMAT.DATE_TIME);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	@Test
	public void testItegerFormatedDate() throws ClassNotFoundException{
		// String formated as date
		schema.setType(TYPE.INTEGER);
		schema.setFormat(FORMAT.DATE);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	@Test
	public void testIntegerFormatedTime() throws ClassNotFoundException{
		// String formated as date
		schema.setType(TYPE.STRING);
		schema.setFormat(FORMAT.TIME);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	@Test
	public void testIntegerFormatedUTC_MILLISEC() throws ClassNotFoundException{
		// String formated as date
		schema.setType(TYPE.STRING);
		schema.setFormat(FORMAT.UTC_MILLISEC);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(_package, schema, codeModel._ref(Object.class), null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	
	/**
	 * Helper to declare a model object to string.
	 * @param toDeclare
	 * @return
	 */
	public String declareToString(JDeclaration toDeclare){
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		toDeclare.declare(formatter);
		return writer.toString();
	}

}
