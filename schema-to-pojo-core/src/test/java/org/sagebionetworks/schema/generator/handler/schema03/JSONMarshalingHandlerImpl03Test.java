package org.sagebionetworks.schema.generator.handler.schema03;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.generator.InstanceFactoryGenerator;
import org.sagebionetworks.schema.generator.RegisterGenerator;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDeclaration;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JGenerable;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class JSONMarshalingHandlerImpl03Test {

	JCodeModel codeModel;
	JPackage _package;
	JDefinedClass sampleClass;
	JDefinedClass sampleInterface;
	JType type;
	ObjectSchema schema;
	ObjectSchema schemaInterface;
	ObjectSchema schemaInterfaceImpl;


	@Before
	public void before() throws JClassAlreadyExistsException,
			ClassNotFoundException {
		codeModel = new JCodeModel();
		_package = codeModel._package("org.sample");
		sampleClass = codeModel._class("Sample");
		sampleInterface = _package._interface("SampleInterface");
		schema = new ObjectSchema();
		schema.setType(TYPE.OBJECT);
		
		// Create a schema interface
		schemaInterface = new ObjectSchema();
		schemaInterface.setType(TYPE.INTERFACE);
		schemaInterface.putProperty("fromInterface", new ObjectSchema(TYPE.BOOLEAN));
		schemaInterface.setId(sampleInterface.fullName());

		// impl
		schemaInterfaceImpl = new ObjectSchema();
		schemaInterfaceImpl.setType(TYPE.OBJECT);
		schemaInterfaceImpl.setId("org.sample.SampleImpl");
		schemaInterfaceImpl.setImplements(new ObjectSchema[]{schemaInterface});

	}

	@Test
	public void testCreateConstructor() {
		// Now handle the
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod initMethod = sampleClass.method(JMod.PUBLIC, Void.class, "initializeFromJSONObject");
		JMethod constructor = handler.createConstructor(schema, sampleClass, initMethod);
		assertNotNull(constructor);
		assertEquals(JMod.PUBLIC, constructor.mods().getValue());
		assertNotNull(constructor.params());
		assertEquals(1, constructor.params().size());
		JVar param = constructor.params().get(0);
		assertNotNull(param);
		assertEquals(codeModel.ref(JSONObjectAdapter.class),param.type());
		// Make sure there is a null check
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		constructor.declare(formatter);
		String constructorString = writer.toString();
//		System.out.println(constructorString);
		assertTrue(constructorString.indexOf("if (adapter == null)") > 0);
		assertTrue(constructorString.indexOf("throw new java.lang.IllegalArgumentException") > 0);
		assertTrue(constructorString.indexOf("@param adapter") > 0);
		assertTrue(constructorString.indexOf("throws org.sagebionetworks.schema.adapter.JSONObjectAdapterException") > 0);
		assertTrue(constructorString.indexOf("@throws org.sagebionetworks.schema.adapter.JSONObjectAdapterException") > 0);
		assertTrue(constructorString.indexOf("initializeFromJSONObject(adapter);") > 0);
//		printClassToConsole(sampleClass);
	}
	
	
	@Test
	public void testCreateConstructorSuperClass() throws JClassAlreadyExistsException {
		// For this case we want to use class that has the sample as a base class
		ObjectSchema childSchema = new ObjectSchema();
		childSchema.setExtends(schema);
		JDefinedClass childClasss = codeModel._class("ChildOfSample");
		childClasss._extends(sampleClass);
		// Now handle the
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod initMethod = sampleClass.method(JMod.PUBLIC, Void.class, "initializeFromJSONObject");
		JMethod constructor = handler.createConstructor(childSchema, childClasss, initMethod);
		assertNotNull(constructor);
		JBlock body = constructor.body();
		assertNotNull(body);
		// Now get the string and check it.
		String constructorString = declareToString(constructor);
//		System.out.println(constructorString);
		// Make sure there is a call to super.
		assertTrue(constructorString.indexOf("super(adapter);") > 0);
//		printClassToConsole(childClasss);
	}
	
	@Test
	public void testCreateGetJSONSchemaMethod(){
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		sampleClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, sampleClass.owner()._ref(String.class), JSONEntity.EFFECTIVE_SCHEMA);
		JMethod getMethod = handler.createGetJSONSchemaMethod(schema, sampleClass);
		assertNotNull(getMethod);
		assertEquals(JMod.PUBLIC, getMethod.mods().getValue());
		assertNotNull(getMethod.params());
		assertEquals(0, getMethod.params().size());
		String methodString = declareToString(getMethod);
//		System.out.println(methodString);
		assertTrue(methodString.indexOf("return EFFECTIVE_SCHEMA;") > 0);
	}
	
	@Test
	public void testCreateBaseMethod() throws JClassAlreadyExistsException {
		// For this case we want to use class that has the sample as a base class
		// Now handle the
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createBaseMethod(schema, sampleClass,"baseName");
		assertNotNull(method);
		JBlock body = method.body();
		assertNotNull(body);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(methodString);
		// Make sure there is a call to super.
		assertTrue(methodString.indexOf("@java.lang.Override") > 0);
		assertTrue(methodString.indexOf("if (adapter == null)") > 0);
		assertTrue(methodString.indexOf("throw new java.lang.IllegalArgumentException") > 0);
		assertTrue(methodString.indexOf("@param adapter") > 0);
		assertTrue(methodString.indexOf("throws org.sagebionetworks.schema.adapter.JSONObjectAdapterException") > 0);
		assertTrue(methodString.indexOf("@throws org.sagebionetworks.schema.adapter.JSONObjectAdapterException") > 0);
		assertTrue(methodString.indexOf("@see JSONEntity#initializeFromJSONObject(JSONObjectAdapter)") > 0);
		assertTrue(methodString.indexOf("@see JSONEntity#writeToJSONObject(JSONObjectAdapter)") > 0);
	}
	
	@Test
	public void testCreateBaseMethodSuper() throws JClassAlreadyExistsException {
		// For this case we want to use class that has the sample as a base class
		ObjectSchema childSchema = new ObjectSchema();
		childSchema.setExtends(schema);
		JDefinedClass childClasss = codeModel._class("ChildOfSample");
		childClasss._extends(sampleClass);
		// Now handle the
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createBaseMethod(childSchema, childClasss,"baseName");
		assertNotNull(method);
		JBlock body = method.body();
		assertNotNull(body);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(methodString);
		// Make sure there is a call to super.
		assertTrue(methodString.indexOf("super.baseName(adapter);") > 0);
//		printClassToConsole(childClasss);
	}
	
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectStringProperty() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		String propName = "stringName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(constructorString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"stringName\")) {") > 0);
		// It should directly set the value
		assertTrue(methodString.indexOf("stringName = adapter.getString(\"stringName\");") > 0);
		// It should also have an else that sets it to null
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("stringName = null;") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectStringURI() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.URI);
		String propName = "uriName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(methodString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"uriName\")) {") > 0);
		// It should directly set the value
		assertTrue(methodString.indexOf("uriName = adapter.getString(\"uriName\");") > 0);
		// It should also have an else that sets it to null
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("uriName = null;") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	@Test
	public void testAssignJSONStringToPropertyNullFormat(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		// The format is null as this is simply a string
		propertySchema.setFormat(null);
		String propName = "stringName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propName, propertySchema);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.getString(\"stringName\")", methodString);
	}
	
	@Test
	public void testAssignJSONStringToPropertyDateTime(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.DATE_TIME);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propName, propertySchema);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), adapter.getString(\"dateName\"))", methodString);
	}
	
	@Test
	public void testAssignJSONStringToPropertyDate(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.DATE);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propName, propertySchema);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE\"), adapter.getString(\"dateName\"))", methodString);
	}
	
	@Test
	public void testAssignJSONStringToURI(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.URI);
		String propName = "someURI";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propName, propertySchema);
		String methodString = generateToString(rhs);
		System.out.println(methodString);
		assertEquals("adapter.getString(\"someURI\")", methodString);
	}
	
	@Test
	public void testAssignJSONStringToPropertyTime(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.TIME);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propName, propertySchema);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"TIME\"), adapter.getString(\"dateName\"))", methodString);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectStringFormateDateTime() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.DATE_TIME);
		String propName = "dateName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Date.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(declareToString(sampleClass));
//		System.out.println(methodString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"dateName\")) {") > 0);
		// This should convert from a string to a date.
		assertTrue(methodString.indexOf("dateName = adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), adapter.getString(\"dateName\"));") > 0);
		// It should also have an else that sets it to null
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("dateName = null;") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectLongFormatedUtcMilisecs() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.INTEGER);
		propertySchema.setFormat(FORMAT.UTC_MILLISEC);
		String propName = "dateName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Date.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(declareToString(sampleClass));
		System.out.println(methodString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"dateName\")) {") > 0);
		// This should convert from a string to a date.
		assertTrue(methodString.indexOf("dateName = new java.util.Date(adapter.getLong(\"dateName\"));") > 0);
		// It should also have an else that sets it to null
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("dateName = null;") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectRequiredProperty() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		// Make this required
		propertySchema.setRequired(true);
		String propName = "stringName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);
		// There should be an else block
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("throw new java.lang.IllegalArgumentException(\"Property: 'stringName' is required and cannot be null\");") > 0);
		// This case should not have a set to null
		assertFalse(methodString.indexOf("stringName = null;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectLongProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.INTEGER);
		String propName = "longName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Long.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);;
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("longName = new java.lang.Long(adapter.getLong(\"longName\"));") > 0);
		assertTrue(methodString.indexOf("longName = null;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectDoubleProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.NUMBER);
		String propName = "doubleName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Double.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("oubleName = new java.lang.Double(adapter.getDouble(\"doubleName\"));") > 0);
		assertTrue(methodString.indexOf("doubleName = null;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectBooleanProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.BOOLEAN);
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Boolean.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("propName = new java.lang.Boolean(adapter.getBoolean(\"propName\"));") > 0);
		assertTrue(methodString.indexOf("propName = null;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectObjectProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = schema;
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("propName = new Sample(adapter.getJSONObject(\"propName\"));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectInterface() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = schema;
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, sampleInterface, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		// this time we provide a register.
		InstanceFactoryGenerator ifg = new InstanceFactoryGenerator(codeModel, Arrays.asList(schema, schemaInterface, schemaInterfaceImpl));
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass, ifg);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONObjectAdapter __localAdapter = adapter.getJSONObject(\"propName\");") > 0);
		assertTrue(methodString
				.indexOf("propName = ((org.sample.SampleInterface) org.sample.SampleInterfaceInstanceFactory.singleton().newInstance(__localAdapter.getString(\"concreteType\")));") > 0);
		assertTrue(methodString.indexOf("propName.initializeFromJSONObject(__localAdapter);") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectConcreteClass() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = schema;
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		// this time we provide a register.
		InstanceFactoryGenerator ifg = new InstanceFactoryGenerator(codeModel, Arrays.asList(schema));
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass, ifg);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("propName = new Sample(adapter.getJSONObject(\"propName\"));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectArrayWithReg() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = schema;
		arrayTypeSchema.setType(TYPE.INTERFACE);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(sampleInterface), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		// this time we provide a register.
		InstanceFactoryGenerator ifg = new InstanceFactoryGenerator(codeModel, Arrays.asList(schema, schemaInterface, schemaInterfaceImpl));
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass, ifg);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONObjectAdapter __indexAdapter = __jsonArray.getJSONObject(__i);") > 0);
		assertTrue(methodString
				.indexOf("org.sample.SampleInterface __indexObject = ((org.sample.SampleInterface) org.sample.SampleInterfaceInstanceFactory.singleton().newInstance(__indexAdapter.getString(\"concreteType\")));") > 0);
		assertTrue(methodString.indexOf("__indexObject.initializeFromJSONObject(__indexAdapter);") > 0);
		assertTrue(methodString.indexOf("arrayName.add(__indexObject);") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectValidate() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = schema;
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("org.sagebionetworks.schema.ObjectValidator.validateEntity(Sample.EFFECTIVE_SCHEMA, adapter, Sample.class);") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectArrayPrimitive() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = new ObjectSchema();
		arrayTypeSchema.setType(TYPE.STRING);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("arrayName = new java.util.ArrayList<java.lang.String>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(\"arrayName\");") > 0);
		assertTrue(methodString.indexOf("arrayName.add((__jsonArray.isNull(__i)?null:__jsonArray.getString(__i)));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectSetPrimitive() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = new ObjectSchema();
		arrayTypeSchema.setType(TYPE.STRING);
		propertySchema.setItems(arrayTypeSchema);
		propertySchema.setUniqueItems(true);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(declareToString(constructor));
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("arrayName = new java.util.HashSet<java.lang.String>()") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectArrayObjects() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = schema;
		arrayTypeSchema.setType(TYPE.OBJECT);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(sampleClass), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("arrayName = new java.util.ArrayList<Sample>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(\"arrayName\");") > 0);
		assertTrue(methodString.indexOf("arrayName.add((__jsonArray.isNull(__i)?null:new Sample(__jsonArray.getJSONObject(__i))));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectEnum() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setEnum(new String[]{"A","B",});
		propertySchema.setName("SomeEnum");
		String propName = "enumName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		JDefinedClass enumCalss = _package._enum("SomeEnum");
		enumCalss.enumConstant("A");
		enumCalss.enumConstant("B");
		sampleClass.field(JMod.PRIVATE, enumCalss, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("enumName = org.sample.SomeEnum.valueOf(adapter.getString(\"enumName\"));") > 0);
		assertTrue(methodString.indexOf("catch (java.lang.IllegalArgumentException _x)") > 0);
		assertTrue(methodString.indexOf("throw new java.lang.IllegalArgumentException(\"'enumName' must be one of the following: 'A', 'B'.\")") > 0);
	}
	
	@Test
	public void testAssignPropertyToJSONStringNullFormat(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		// The format is null as this is simply a string
		propertySchema.setFormat(null);
		String propName = "stringName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JFieldVar field = sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propertySchema, field);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("stringName", methodString);
	}
	
	@Test
	public void testAssignPropertyToJSONStringDateTime(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.DATE_TIME);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JFieldVar field = sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propertySchema, field);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertDateToString(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), dateName)", methodString);
	}
	
	@Test
	public void testAssignPropertyToJSONStringDate(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.DATE);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JFieldVar field = sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propertySchema, field);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertDateToString(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE\"), dateName)", methodString);
	}
	
	@Test
	public void testAssignPropertyToJSONStringTime(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.TIME);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JFieldVar field = sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propertySchema, field);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertDateToString(org.sagebionetworks.schema.FORMAT.valueOf(\"TIME\"), dateName)", methodString);
	}
	
	@Test
	public void testAssignPropertyToJSONStringURI(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.URI);
		String propName = "uriName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JFieldVar field = sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propertySchema, field);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("uriName", methodString);
	}
	
	@Test
	public void testWriteToJSONObjectDateProperty() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		// Dates are strings with a date-time format.
		propertySchema.setFormat(FORMAT.DATE_TIME);
		String propName = "dateName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Date.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(methodString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (dateName!= null) {") > 0);
		// It should directly set the value
		assertTrue(methodString.indexOf("adapter.put(\"dateName\", adapter.convertDateToString(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), dateName));") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectRequiredProperty() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		// Make this required
		propertySchema.setRequired(true);
		String propName = "stringName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(methodString);
		// There should be an else block
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("throw new java.lang.IllegalArgumentException(\"Property: 'stringName' is required and cannot be null\");") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectLongProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.INTEGER);
		String propName = "longName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("long"), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);;
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertFalse(methodString.indexOf("if (longName!= null) {") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"longName\", longName);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectLongFormatedUTC() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.INTEGER);
		propertySchema.setFormat(FORMAT.UTC_MILLISEC);
		String propName = "dateName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("long"), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);;
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertFalse(methodString.indexOf("if (longName!= null) {") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"dateName\", dateName.getTime());") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectDoubleProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.NUMBER);
		String propName = "doubleName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("double"), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertFalse(methodString.indexOf("if (doubleName!= null) {") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"doubleName\", doubleName);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectBooleanProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.BOOLEAN);
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("boolean"), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertFalse(methodString.indexOf("if (propName!= null) {") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"propName\", propName);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectObjectProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = schema;
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("adapter.put(\"propName\", propName.writeToJSONObject(adapter.createNew()));") > 0);
//		printClassToConsole(sampleClass);
	}
	
	@Test
	public void testWriteToJSONObjectArrayPrimitive() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = new ObjectSchema();
		arrayTypeSchema.setType(TYPE.STRING);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __array = adapter.createNewArray();") > 0);
		assertTrue(methodString.indexOf("java.util.Iterator<java.lang.String> __it = arrayName.iterator();") > 0);
		assertTrue(methodString.indexOf("int __index = 0;") > 0);
		assertTrue(methodString.indexOf("while (__it.hasNext()) {") > 0);
		assertTrue(methodString.indexOf("String __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("array.put(__index, ((__value == null)?null:__value));") > 0);
		assertTrue(methodString.indexOf("__index++;") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"arrayName\", __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectArrayDate() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayDates";
		ObjectSchema arrayTypeSchema = new ObjectSchema();
		arrayTypeSchema.setType(TYPE.STRING);
		arrayTypeSchema.setFormat(FORMAT.DATE_TIME);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Date.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("if (arrayDates!= null) {") > 0);
		assertTrue(methodString.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __array = adapter.createNewArray();") > 0);
		assertTrue(methodString.indexOf("java.util.Iterator<java.util.Date> __it = arrayDates.iterator();") > 0);
		assertTrue(methodString.indexOf("int __index = 0;") > 0);
		assertTrue(methodString.indexOf("while (__it.hasNext()) {") > 0);
		assertTrue(methodString.indexOf("java.util.Date __value = __it.next();") > 0);
		assertTrue(methodString
				.indexOf("__array.put(__index, ((__value == null)?null:adapter.convertDateToString(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), __value)));") > 0);
		assertTrue(methodString.indexOf("__index++;") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"arrayDates\", __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectArrayDateUTCMilisec() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayDates";
		ObjectSchema arrayTypeSchema = new ObjectSchema();
		arrayTypeSchema.setType(TYPE.INTEGER);
		arrayTypeSchema.setFormat(FORMAT.UTC_MILLISEC);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Date.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("if (arrayDates!= null) {") > 0);
		assertTrue(methodString.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __array = adapter.createNewArray();") > 0);
		assertTrue(methodString.indexOf("java.util.Iterator<java.util.Date> __it = arrayDates.iterator();") > 0);
		assertTrue(methodString.indexOf("int __index = 0;") > 0);
		assertTrue(methodString.indexOf("while (__it.hasNext()) {") > 0);
		assertTrue(methodString.indexOf("java.util.Date __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("array.put(__index, ((__value == null)?null:__value.getTime()));") > 0);
		assertTrue(methodString.indexOf("__index++;") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"arrayDates\", __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectArrayLong() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "longList";
		ObjectSchema arrayTypeSchema = new ObjectSchema();
		arrayTypeSchema.setType(TYPE.INTEGER);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Long.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("if (longList!= null) {") > 0);
		assertTrue(methodString.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __array = adapter.createNewArray();") > 0);
		assertTrue(methodString.indexOf("java.util.Iterator<java.lang.Long> __it = longList.iterator();") > 0);
		assertTrue(methodString.indexOf("int __index = 0;") > 0);
		assertTrue(methodString.indexOf("while (__it.hasNext()) {") > 0);
		assertTrue(methodString.indexOf("java.lang.Long __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("array.put(__index, ((__value == null)?null:__value));") > 0);
		assertTrue(methodString.indexOf("__index++;") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"longList\", __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectArrayDouble() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "doubleList";
		ObjectSchema arrayTypeSchema = new ObjectSchema();
		arrayTypeSchema.setType(TYPE.NUMBER);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Double.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("if (doubleList!= null) {") > 0);
		assertTrue(methodString.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __array = adapter.createNewArray();") > 0);
		assertTrue(methodString.indexOf("java.util.Iterator<java.lang.Double> __it = doubleList.iterator();") > 0);
		assertTrue(methodString.indexOf("int __index = 0;") > 0);
		assertTrue(methodString.indexOf("while (__it.hasNext()) {") > 0);
		assertTrue(methodString.indexOf("java.lang.Double __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("array.put(__index, ((__value == null)?null:__value));") > 0);
		assertTrue(methodString.indexOf("__index++;") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"doubleList\", __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectSetPrimitive() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = new ObjectSchema();
		arrayTypeSchema.setType(TYPE.STRING);
		propertySchema.setItems(arrayTypeSchema);
		propertySchema.setUniqueItems(true);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(HashSet.class).narrow(String.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(declareToString(constructor));
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("while (__it.hasNext()) {") > 0);
		assertTrue(methodString.indexOf("String __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("array.put(__index, ((__value == null)?null:__value));") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectArrayObjects() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = schema;
		arrayTypeSchema.setType(TYPE.OBJECT);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(sampleClass), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		
		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("Sample __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("array.put(__index, ((__value == null)?null:__value.writeToJSONObject(adapter.createNew())));") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectEnum() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setEnum(new String[]{"A","B",});
		propertySchema.setName("SomeEnum");
		String propName = "enumName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		JDefinedClass enumCalss = _package._enum("SomeEnum");
		enumCalss.enumConstant("A");
		enumCalss.enumConstant("B");
		sampleClass.field(JMod.PRIVATE, enumCalss, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("adapter.put(\"enumName\", enumName.name());") > 0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAddJSONMarshalingInterfance(){
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		ObjectSchema interfaceSchema = new ObjectSchema();
		interfaceSchema.setType(TYPE.INTERFACE);
		interfaceSchema.setName("SampleInterface");
		handler.addJSONMarshaling(interfaceSchema, sampleInterface, null);
	}
	
	@Test
	public void testInitializeFromJSONForImplements() throws JClassAlreadyExistsException {
		// For this case we want to use class that has the sample as a base class
		ObjectSchema childSchema = new ObjectSchema();
		childSchema.setImplements(new ObjectSchema[]{schemaInterface});
		JDefinedClass childClasss = codeModel._class("ImplementsInterface");
		childClasss._implements(sampleInterface);
		childClasss.field(JMod.PRIVATE, codeModel.ref(Boolean.class), "fromInterface");
		// Now handle the
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(childSchema, childClasss);
		assertNotNull(method);
		JBlock body = method.body();
		assertNotNull(body);
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		// Make sure there is a call to super.
		assertTrue(methodString.indexOf("fromInterface = new java.lang.Boolean(adapter.getBoolean(\"fromInterface\"));") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectForImplements() throws JClassAlreadyExistsException {
		// For this case we want to use class that has the sample as a base class
		ObjectSchema childSchema = new ObjectSchema();
		childSchema.setImplements(new ObjectSchema[]{schemaInterface});
		JDefinedClass childClasss = codeModel._class("ImplementsInterface");
		childClasss._implements(sampleInterface);
		childClasss.field(JMod.PRIVATE, codeModel.BOOLEAN, "fromInterface");
		// Now handle the
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(childSchema, childClasss);
		assertNotNull(method);
		JBlock body = method.body();
		assertNotNull(body);
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		// Make sure there is a call to super.
		assertTrue(methodString.indexOf("adapter.put(\"fromInterface\", fromInterface);") > 0);
	}
	

	public void printClassToConsole(JDefinedClass classToPrint) {
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		classToPrint.declare(formatter);
		System.out.println(writer.toString());
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
	
	/**
	 * Helper to declare a model object to string.
	 * @param toDeclare
	 * @return
	 */
	public String generateToString(JGenerable toDeclare){
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		toDeclare.generate(formatter);
		return writer.toString();
	}

	/**
	 * Tests that initializeFromJSONObject works for properties
	 *  that have a default string set.
	 */
	@Test
	public void testCreateMethodInitializeFromJSONWithDefaultStringProperty() throws Exception {
		//make a property that has default set with a string
		ObjectSchema propertySchema = new ObjectSchema();
		String defaultString = "defaultString";
		propertySchema.setDefault(defaultString);
		propertySchema.setType(TYPE.STRING);
		propertySchema.setRequired(true);
		String propName = "stringName";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		
		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"stringName\")) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("stringName = adapter.getString(\"stringName\");") > 0);
		//check that else statement was generated
		assertTrue(methodString.indexOf("stringName = \"defaultString\";") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONDateList() throws Exception {
		//make a property that has default set with a string
		ObjectSchema propertySchema = new ObjectSchema();
		ObjectSchema dateType = new ObjectSchema();
		dateType.setType(TYPE.STRING);
		dateType.setFormat(FORMAT.DATE_TIME);
		propertySchema.setType(TYPE.ARRAY);
		propertySchema.setItems(dateType);
		String propName = "dateList";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Date.class), propName);
		
		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(methodString);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"dateList\")) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("dateList = new java.util.ArrayList<java.util.Date>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(\"dateList\");") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString
				.indexOf("dateList.add((__jsonArray.isNull(__i)?null:adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), __jsonArray.getString(__i))));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromLongList() throws Exception {
		//make a property that has default set with a string
		ObjectSchema propertySchema = new ObjectSchema();
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.INTEGER);
		propertySchema.setType(TYPE.ARRAY);
		propertySchema.setItems(arrayType);
		String propName = "longList";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Long.class), propName);
		
		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"longList\")) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("longList = new java.util.ArrayList<java.lang.Long>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(\"longList\");") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString.indexOf("longList.add((__jsonArray.isNull(__i)?null:__jsonArray.getLong(__i)));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromDoubleList() throws Exception {
		//make a property that has default set with a string
		ObjectSchema propertySchema = new ObjectSchema();
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.NUMBER);
		propertySchema.setType(TYPE.ARRAY);
		propertySchema.setItems(arrayType);
		String propName = "doubleList";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Double.class), propName);
		
		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"doubleList\")) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("doubleList = new java.util.ArrayList<java.lang.Double>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(\"doubleList\");") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString.indexOf("doubleList.add((__jsonArray.isNull(__i)?null:__jsonArray.getDouble(__i)));") > 0);
	}
	@Test
	public void testCreateMethodInitializeFromJSONDateListUtcMilisec() throws Exception {
		//make a property that has default set with a string
		ObjectSchema propertySchema = new ObjectSchema();
		ObjectSchema dateType = new ObjectSchema();
		dateType.setType(TYPE.INTEGER);
		dateType.setFormat(FORMAT.UTC_MILLISEC);
		propertySchema.setType(TYPE.ARRAY);
		propertySchema.setItems(dateType);
		String propName = "dateList";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Date.class), propName);
		
		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"dateList\")) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("dateList = new java.util.ArrayList<java.util.Date>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(\"dateList\");") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString.indexOf("dateList.add((__jsonArray.isNull(__i)?null:new java.util.Date(__jsonArray.getLong(__i))));") > 0);
	}
	/**
	 * Tests that initializeFromJSONObject works for properties 
	 * that have a default Number/double set.
	 */
	@Test
	public void testCreateMethodInitializeFromJSONWithDefaultDoubleProperty() throws Exception {
		//make a property that has default set with a double
		ObjectSchema propertySchema = new ObjectSchema();
		double defaultDouble = 7.12;
		propertySchema.setDefault(defaultDouble);
		propertySchema.setType(TYPE.NUMBER);
		propertySchema.setRequired(true);
		String propName = "defaultDoubleName";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		
		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		
		//check that if statement was generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"defaultDoubleName\")) {") > 0);
		//check that body of if statement was generated
		assertTrue(methodString.indexOf("defaultDoubleName = " +
				"new java.lang.String(adapter.getDouble(\"defaultDoubleName\"));") > 0);
		//check that body of else statement was generated
		assertTrue(methodString.indexOf("defaultDoubleName = 7.12D;") > 0);
	}
	
	/**
	 * Tests that initializeFromJSONObject works for properties
	 * that have a default Integer/long set.
	 */
	@Test
	public void testCreateMethodInitializeFromJSONWithDefaultIntegerProperty() throws Exception {
		//make a property that has a default set with a integer
		ObjectSchema propertySchema = new ObjectSchema();
		long defaultLong = 77;
		propertySchema.setDefault(defaultLong);
		propertySchema.setType(TYPE.INTEGER);
		propertySchema.setRequired(true);
		String propName = "defaultIntegerName";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		
		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		//check that if statement was generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"defaultIntegerName\")) {") > 0);
		//check that body of if statement was generated
		assertTrue(methodString.indexOf("defaultIntegerName = " +
				"new java.lang.String(adapter.getLong(\"defaultIntegerName\"));") > 0);
		//check that body of else statment was generated
		assertTrue(methodString.indexOf("defaultIntegerName = 77L;") > 0);
	}
	
	/**
	 * Tests that initializeFromJSONObject works for properties
	 *that have a default boolean set.
	 */
	@Test
	public void testCreateMethodInitializeFromJSONWithDefaultBooleanProperty() throws Exception {
		//make a property that has a default set with a boolean
		ObjectSchema propertySchema = new ObjectSchema();
		boolean defaultBoolean = false;
		propertySchema.setDefault(defaultBoolean);
		propertySchema.setType(TYPE.BOOLEAN);
		propertySchema.setRequired(true);
		String propName = "defaultBooleanName";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		
		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		//check that if statement was generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(\"defaultBooleanName\")) {") > 0);
		//check that body of if statement was generated
		assertTrue(methodString.indexOf("defaultBooleanName = " +
				"new java.lang.String(adapter.getBoolean(\"defaultBooleanName\"));") > 0);
		//check that body of else statment was generated
		assertTrue(methodString.indexOf("defaultBooleanName = false;") > 0);
	}
	
	/**
	 * Tests that initializeFromJSONObject works for properties
	 * that have a default object set.
	 */
	@Ignore
	@Test
	public void testCreateMethodInitializeFromJSONWithDefaultObjectProperty() throws Exception {
		//make a property that has default sent with a JSONObject
		ObjectSchema propertySchema = new ObjectSchema();
		JSONObject defaultObject = new JSONObject();
		defaultObject.put("imABool", true);
		propertySchema.setDefault(defaultObject);
		propertySchema.setType(TYPE.OBJECT);
		propertySchema.setRequired(true);
		String propName = "defaultObjectName";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		
		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		//String methodString = declareToString(method);
	}
	
	/**
	 * Tests that an schema that has a property of type ARRAY, whose item is
	 * an enumeration is supported in initializeFromJSONObject methods created
	 * by handler.
	 * @throws Exception
	 */
	@Test
	public void testCreateMethodInitializeFromJSONObjectArrayWithEnumItem() throws Exception {
		//create a property that is an Array
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayWhoseItemIsAnEnum";
		
		//create an enum ObjectSchema and add it to propertySchema's items
		//it must have a name, id, and it's type must be STRING
		//it must have an enum defined
		ObjectSchema typesEnum = new ObjectSchema();
		typesEnum.setType(TYPE.STRING);
		typesEnum.setName("Animals");
		typesEnum.setId("Animals");
		String[] forTheEnum = {"puppy", "mouse", "elephant"};
		typesEnum.setEnum(forTheEnum);
		
		//add enum to property's items
		propertySchema.setItems(typesEnum);
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
	
		//add field to sampleClass
		codeModel._package("org.sample");
		JDefinedClass testClass = _package._enum("Animals");
		testClass.enumConstant("puppy");
		testClass.enumConstant("mouse");
		testClass.enumConstant("elephant");
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(testClass), propName);
		
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		
		//check that array of enumeration got created successfully, and 
		//assignments are correct
		assertTrue(methodString.indexOf("arrayWhoseItemIsAnEnum = new java.util.ArrayList<org.sample.Animals>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(\"arrayWhoseItemIsAnEnum\");") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString
				.indexOf("arrayWhoseItemIsAnEnum.add((__jsonArray.isNull(__i)?null:org.sample.Animals.valueOf(__jsonArray.getString(__i))));") > 0);
	}
	
	/**
	 * Tests that an schema that has a property of type ARRAY, whose item is
	 * an enumeration and a property of type ARRAY, whose item is not an
	 * enumeration to verify handler correctly handles both.
	 * @throws Exception
	 */
	@Test
	public void testCreateMethodInitializeFromJSONObjectArrayWithAndWithoutEnumItem() throws Exception {
		//create a property that is an Array with item that is an enum
		ObjectSchema propertySchemaOne = new ObjectSchema();
		propertySchemaOne.setType(TYPE.ARRAY);
		String propName = "arrayWhoseItemIsAnEnum";
		//create an enum ObjectSchema and add it to propertySchema's items
		//it must have a name, id, and it's type must be STRING
		//it must have an enum defined
		ObjectSchema typesEnum = new ObjectSchema();
		typesEnum.setType(TYPE.STRING);
		typesEnum.setName("Animals");
		typesEnum.setId("Animals");
		String[] forTheEnum = {"puppy", "mouse", "elephant"};
		typesEnum.setEnum(forTheEnum);
		
		//add enum to property's items
		propertySchemaOne.setItems(typesEnum);
		
		//add property to schema
		schema.putProperty(propName, propertySchemaOne);
	
		//add field to sampleClass
		codeModel._package("org.sample");
		JDefinedClass testClass = _package._enum("Animals");
		testClass.enumConstant("puppy");
		testClass.enumConstant("mouse");
		testClass.enumConstant("elephant");
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(testClass), propName);
		
		//now create a property that is an Array with item that is not an enum
		ObjectSchema propertySchemaTwo = new ObjectSchema();
		propertySchemaTwo.setType(TYPE.ARRAY);
		String propTwoName = "arrayWhoseItemIsNotEnum";
		ObjectSchema typesString = new ObjectSchema();
		typesString.setType(TYPE.STRING);
		propertySchemaTwo.setItems(typesString);
		schema.putProperty(propTwoName, propertySchemaTwo);
		
		//add field to sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propTwoName);
		
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		
		//check that everything was created correctly for the array with an enum
		assertTrue(methodString.indexOf("arrayWhoseItemIsAnEnum = new java.util.ArrayList<org.sample.Animals>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(\"arrayWhoseItemIsAnEnum\");") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString
				.indexOf("arrayWhoseItemIsAnEnum.add((__jsonArray.isNull(__i)?null:org.sample.Animals.valueOf(__jsonArray.getString(__i))));") > 0);
		
		//check that everything was created correctly for the array without an enum
		assertTrue(methodString.indexOf("arrayWhoseItemIsNotEnum = new java.util.ArrayList<java.lang.String>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(\"arrayWhoseItemIsNotEnum\");") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString.indexOf("arrayWhoseItemIsNotEnum.add((__jsonArray.isNull(__i)?null:__jsonArray.getString(__i)));") > 0);
	}
	
	/**
	 * Tests that writeToJSONObject works for Array type that has an
	 * Enum for it's item.
	 * @throws Exception
	 */
	@Test
	public void testWriteToJSONObjectArrayWEnum()throws Exception {
		//make an array property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayPropWithEnum";
		
		//make the ObjectSchema that represents the array's items that contains an enum
		ObjectSchema itemWithEnum = new ObjectSchema();
		itemWithEnum.setType(TYPE.STRING);
		itemWithEnum.setName("Animals");
		itemWithEnum.setId("Animals");
		String[] forTheEnum = {"puppy", "mouse", "elephant"};
		itemWithEnum.setEnum(forTheEnum);
		
		//add item to property
		propertySchema.setItems(itemWithEnum);
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		//add field to test JDefinedClass
		codeModel._package("org.sample");
		JDefinedClass testClass = _package._enum("Animals");
		testClass.enumConstant("puppy");
		testClass.enumConstant("mouse");
		testClass.enumConstant("elephant");
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(testClass), propName);
		
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		
		//make sure everything was created correctly
		assertTrue(methodString.indexOf("org.sample.Animals __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("__array.put(__index, ((__value == null)?null:__value.name()));") > 0);
	}
	
	/**
	 * Tests that writeToJSONObject works for schema that has both an Array
	 * prop that has an enum for it's item and an Array prop that doesn't 
	 * have an enum for it's item.
	 * @throws Exception
	 */
	@Test
	public void testWriteToJSONObjectArrayWEnumAndWithoutEnum()throws Exception {
		//create a property that is an Array with item that is an enum
		ObjectSchema propertySchemaOne = new ObjectSchema();
		propertySchemaOne.setType(TYPE.ARRAY);
		String propName = "arrayWhoseItemIsAnEnum";
		//create an enum ObjectSchema and add it to propertySchema's items
		ObjectSchema typesEnum = new ObjectSchema();
		typesEnum.setType(TYPE.STRING);
		typesEnum.setName("Animals");
		typesEnum.setId("Animals");
		String[] forTheEnum = {"puppy", "mouse", "elephant"};
		typesEnum.setEnum(forTheEnum);
		
		//add enum to property's items
		propertySchemaOne.setItems(typesEnum);
		
		//add property to schema
		schema.putProperty(propName, propertySchemaOne);
	
		//add field to sampleClass
		codeModel._package("org.sample");
		JDefinedClass testClass = _package._enum("Animals");
		testClass.enumConstant("puppy");
		testClass.enumConstant("mouse");
		testClass.enumConstant("elephant");
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(testClass), propName);
		
		//now create a property that is an Array with item that is not an enum
		ObjectSchema propertySchemaTwo = new ObjectSchema();
		propertySchemaTwo.setType(TYPE.ARRAY);
		String propTwoName = "arrayWhoseItemIsNotEnum";
		ObjectSchema typesString = new ObjectSchema();
		typesString.setType(TYPE.STRING);
		propertySchemaTwo.setItems(typesString);
		schema.putProperty(propTwoName, propertySchemaTwo);
		
		//add field to sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propTwoName);
		
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		
		//make sure everything is in order for the property that has an enum
		assertTrue(methodString.indexOf("java.util.Iterator<org.sample.Animals> __it = arrayWhoseItemIsAnEnum.iterator();") > 0);
		assertTrue(methodString.indexOf("org.sample.Animals __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("__array.put(__index, ((__value == null)?null:__value.name()));") > 0);
		
		//make sure everything is in order for the property that does not have an enum
		assertTrue(methodString.indexOf("java.util.Iterator<java.lang.String> __it = arrayWhoseItemIsNotEnum.iterator();") > 0);
		assertTrue(methodString.indexOf("java.lang.String __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("__array.put(__index, ((__value == null)?null:__value));") > 0);
	}

	/**
	 * Tests that an schema that has a property of type MAP, whose item is an enumeration is supported in
	 * initializeFromJSONObject methods created by handler.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateMethodInitializeFromJSONObjectMapWithKeyValueEnumItem() throws Exception {
		// create a property that is an Map
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.MAP);
		String propName = "mapWhoseItemIsAnEnum";

		// create an enum ObjectSchema and add it to propertySchema's items
		// it must have a name, id, and it's type must be STRING
		// it must have an enum defined
		ObjectSchema keyEnum = new ObjectSchema();
		keyEnum.setType(TYPE.STRING);
		keyEnum.setName("Animals");
		keyEnum.setId("Animals");
		keyEnum.setEnum(new String[] { "puppy", "mouse", "elephant" });

		ObjectSchema valueEnum = new ObjectSchema();
		valueEnum.setType(TYPE.STRING);
		valueEnum.setName("Pets");
		valueEnum.setId("pets");
		valueEnum.setEnum(new String[] { "dog", "cat" });

		// add enum to property's key and value
		propertySchema.setKey(keyEnum);
		propertySchema.setValue(valueEnum);

		// add property to schema
		schema.putProperty(propName, propertySchema);

		// add field to sampleClass
		codeModel._package("org.sample");
		JDefinedClass testKeyClass = _package._enum("Animals");
		testKeyClass.enumConstant("puppy");
		testKeyClass.enumConstant("mouse");
		testKeyClass.enumConstant("elephant");
		JDefinedClass testValueClass = _package._enum("Pets");
		testValueClass.enumConstant("dog");
		testValueClass.enumConstant("cat");
		sampleClass.field(JMod.PRIVATE, codeModel.ref(Map.class).narrow(testKeyClass, testValueClass), propName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);

		// Now get the string and check it.
		String methodString = declareToString(method);

		// check that map of enumeration got created successfully, and
		// assignments are correct
		assertTrue(methodString.indexOf("mapWhoseItemIsAnEnum = new java.util.HashMap<org.sample.Animals, org.sample.Pets>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONMapAdapter __jsonMap = adapter.getJSONMap(\"mapWhoseItemIsAnEnum\");") > 0);
		assertTrue(methodString.indexOf("org.sample.Pets __value = org.sample.Pets.valueOf(__jsonMap.getString(__keyObject));") > 0);
		assertTrue(methodString.indexOf("org.sample.Animals __key = org.sample.Animals.valueOf(((java.lang.String) __keyObject));") > 0);
		assertTrue(methodString.indexOf("mapWhoseItemIsAnEnum.put(__key, __value);") > 0);
	}

	/**
	 * Tests that writeToJSONObject works for Array type that has an Enum for it's key.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWriteToJSONObjectMapWKeyEnum() throws Exception {
		// make an array property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.MAP);
		String propName = "arrayPropWithEnum";

		// make the ObjectSchema that represents the array's items that contains an enum
		ObjectSchema keyEnum = new ObjectSchema();
		keyEnum.setType(TYPE.STRING);
		keyEnum.setName("Animals");
		keyEnum.setId("Animals");
		keyEnum.setEnum(new String[] { "puppy", "mouse", "elephant" });

		ObjectSchema valueEnum = new ObjectSchema();
		valueEnum.setType(TYPE.STRING);
		valueEnum.setName("Pets");
		valueEnum.setId("pets");
		valueEnum.setEnum(new String[] { "dog", "cat" });

		// add enum to property's key and value
		propertySchema.setKey(keyEnum);
		propertySchema.setValue(valueEnum);

		// add property to schema
		schema.putProperty(propName, propertySchema);

		// add field to test JDefinedClass
		codeModel._package("org.sample");
		JDefinedClass testKeyClass = _package._enum("Animals");
		testKeyClass.enumConstant("puppy");
		testKeyClass.enumConstant("mouse");
		testKeyClass.enumConstant("elephant");
		JDefinedClass testValueClass = _package._enum("Pets");
		testValueClass.enumConstant("dog");
		testValueClass.enumConstant("cat");
		sampleClass.field(JMod.PRIVATE, codeModel.ref(Map.class).narrow(testKeyClass, testValueClass), propName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);

		// Now get the string and check it.
		String methodString = declareToString(constructor);

		// make sure everything was created correctly
		assertTrue(methodString.indexOf("__map.put(__entry.getKey(), __entry.getValue().name())") > 0);
	}

}