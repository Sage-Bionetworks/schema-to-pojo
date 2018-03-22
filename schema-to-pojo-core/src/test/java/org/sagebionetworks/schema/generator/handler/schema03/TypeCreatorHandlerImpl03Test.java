package org.sagebionetworks.schema.generator.handler.schema03;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.EnumValue;
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
	JDefinedClass sampleClass;
	JType type;
	ObjectSchema schema;

	@Before
	public void before() throws JClassAlreadyExistsException,
			ClassNotFoundException {
		codeModel = new JCodeModel();
		_package = codeModel._package("org.sample");
		sampleClass = codeModel._class("Sample");
		schema = new ObjectSchema();
		schema.setType(TYPE.OBJECT);
		// give it a name
		schema.setName("Sample");
		schema.setId("org.sample"+schema.getName());
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
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
		assertNotNull(clazz);
		// Make sure we can call this twice with the same class
		JType second = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
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
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null,
				new JType[] { parentInterface });
		assertNotNull(clazz);
		// Make sure we can call this twice with the same class
		JType second = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null,
				new JType[] { parentInterface });
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
	}
	
	@Test
	public void testClassExtendsClass() throws ClassNotFoundException, JClassAlreadyExistsException {
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		JDefinedClass parentClass = _package._class("ParentClass");
		JType clazz = handler.handelCreateType(codeModel, schema, parentClass, null, null, null);
		assertNotNull(clazz);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		String classString = declareToString(sampleClass);
//		System.out.println(classString);
		assertTrue(classString.indexOf("extends org.sample.ParentClass") > 0);
	}
	
	@Test
	public void testClassImplementsInterfaces() throws ClassNotFoundException, JClassAlreadyExistsException{
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		JDefinedClass parentInternace = _package._interface("ParentInterface");
		JDefinedClass parentInternace2 = _package._interface("ParentInterface2");
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, new JType[] {
				parentInternace, parentInternace2 });
		assertNotNull(clazz);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		String classString = declareToString(sampleClass);
//		System.out.println(classString);
		assertTrue(classString.indexOf("implements java.io.Serializable, org.sagebionetworks.schema.adapter.JSONEntity, org.sample.ParentInterface, org.sample.ParentInterface2") > 0);
	}
	
	@Test
	public void testInterfanceExtendsNull()throws ClassNotFoundException, JClassAlreadyExistsException{
		schema.setType(TYPE.INTERFACE);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
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
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, new JType[] {
				parentInternace, parentInternace2 });
		assertNotNull(clazz);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		String classString = declareToString(sampleClass);
//		System.out.println(classString);
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
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
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
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
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
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
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
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
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
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
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
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	@Test
	public void testIntegerFormatedUTC_MILLISEC() throws ClassNotFoundException{
		// String formated as date
		schema.setType(TYPE.INTEGER);
		schema.setFormat(FORMAT.UTC_MILLISEC);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
		assertNotNull(clazz);
		assertEquals(codeModel._ref(Date.class), clazz);
	}
	
	/**
	 * Enumerations must have a type of string.
	 * @throws ClassNotFoundException
	 */
	@Test (expected=IllegalArgumentException.class)
	public void testCreateEnumerationNotString() throws ClassNotFoundException{
		schema.setType(TYPE.BOOLEAN);
		schema.setEnum(new EnumValue[]{
				new EnumValue("one"),
				new EnumValue("two"),
				new EnumValue("three")
		});
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateEnumerationNullName() throws ClassNotFoundException{
		schema.setType(TYPE.STRING);
		schema.setEnum(new EnumValue[]{
				new EnumValue("one"),
				new EnumValue("two"),
				new EnumValue("three")
		});
		schema.setName(null);
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
	}
	
	@Test
	public void testCreateEnumeration() throws ClassNotFoundException{
		String title = "This is the title";
		String description = "Add a description";
		schema.setTitle(title);
		schema.setDescription(description);
		schema.setType(TYPE.STRING);
		schema.setEnum(new EnumValue[]{
				new EnumValue("one"),
				new EnumValue("two", "two's description"),
				new EnumValue("three")
		});
		schema.setName("SampleEnum");
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// Create the class
		JType clazz = handler.handelCreateType(codeModel, schema, codeModel._ref(Object.class), null, null, null);
		assertNotNull(clazz);
		assertTrue(clazz instanceof JDefinedClass);
		JDefinedClass sampleClass = (JDefinedClass)clazz;
		String classString = declareToString(sampleClass);
		System.out.println(classString);
		assertTrue(classString.indexOf("one,") > 0);
		assertTrue(classString.indexOf("two,") > 0);
		assertTrue(classString.indexOf("three;") > 0);
		assertTrue(classString.indexOf(title) > 0);
		assertTrue(classString.indexOf(description) > 0);
		assertTrue(classString.indexOf(TypeCreatorHandlerImpl03.AUTO_GENERATED_MESSAGE) > 0);
		assertTrue(classString.contains("two's description"));
	}
	
	@Test
	public void testAddKeyConstantsConcreteClass() throws JClassAlreadyExistsException {
		LinkedHashMap<String, ObjectSchema> props = new LinkedHashMap<String, ObjectSchema>();
		props.put("foo", new ObjectSchema(TYPE.STRING));
		props.put("bar", new ObjectSchema(TYPE.INTEGER));
		schema.setProperties(props);

		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// call under test
		handler.addKeyConstants(schema, sampleClass);
		String classString = declareToString(sampleClass);
		System.out.println(classString);
		assertTrue(classString.contains("String _KEY_FOO = \"foo\";"));
		assertTrue(classString.contains("String _KEY_BAR = \"bar\";"));
		assertTrue(classString.contains("String[] _ALL_KEYS = new java.lang.String[] {_KEY_FOO, _KEY_BAR };"));
	}
	
	@Test
	public void testAddKeyConstantsInterfaceImplementor() throws JClassAlreadyExistsException {
		LinkedHashMap<String, ObjectSchema> props = new LinkedHashMap<String, ObjectSchema>();
		props.put("foo", new ObjectSchema(TYPE.STRING));
		props.put("bar", new ObjectSchema(TYPE.INTEGER));
		props.put(ObjectSchema.CONCRETE_TYPE, new ObjectSchema(TYPE.STRING));
		schema.setProperties(props);
		schema.setImplements(new ObjectSchema[] {new ObjectSchema(TYPE.INTERFACE)});

		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// call under test
		handler.addKeyConstants(schema, sampleClass);
		String classString = declareToString(sampleClass);
		System.out.println(classString);
		assertTrue(classString.contains("String _KEY_FOO = \"foo\";"));
		assertTrue(classString.contains("String _KEY_BAR = \"bar\";"));
		assertTrue(classString.contains("_KEY_CONCRETETYPE = \"concreteType\";"));
		assertTrue(classString.contains("String[] _ALL_KEYS = new java.lang.String[] {_KEY_FOO, _KEY_BAR, _KEY_CONCRETETYPE };"));
	}
	
	
	@Test
	public void testAddKeyConstantsInterface() throws JClassAlreadyExistsException {
		LinkedHashMap<String, ObjectSchema> props = new LinkedHashMap<String, ObjectSchema>();
		props.put("foo", new ObjectSchema(TYPE.STRING));
		props.put("bar", new ObjectSchema(TYPE.INTEGER));
		schema.setProperties(props);
		
		JDefinedClass someInterface = _package._interface("SomeInterface");

		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		// call under test
		handler.addKeyConstants(schema, someInterface);
		String classString = declareToString(someInterface);
		System.out.println(classString);
		assertFalse(classString.contains(ObjectSchema.getKeyConstantName("foo")));
		assertFalse(classString.contains(ObjectSchema.getKeyConstantName("bar")));
		assertFalse(classString.contains(ObjectSchema.ALL_KEYS_NAME));
	}
	
	@Test
	public void testAddKeyConstantsEnum() throws JClassAlreadyExistsException {
		schema.setEnum(new EnumValue[] {new EnumValue("foo")});
		TypeCreatorHandlerImpl03 handler = new TypeCreatorHandlerImpl03();
		
		JDefinedClass someEnum = _package._enum("SomeEnum");
		// call under test
		handler.addKeyConstants(schema, someEnum);
		String classString = declareToString(someEnum);
		System.out.println(classString);
		assertFalse(classString.contains(ObjectSchema.getKeyConstantName("foo")));
		assertFalse(classString.contains(ObjectSchema.ALL_KEYS_NAME));
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
