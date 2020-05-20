package org.sagebionetworks.schema.generator.handler.schema03;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
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
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.schema.EnumValue;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.ObjectSchemaImpl;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.generator.InstanceFactoryGenerator;

public class JSONMarshalingHandlerImpl03Test {

	JCodeModel codeModel;
	JPackage _package;
	JDefinedClass sampleClass;
	JDefinedClass sampleInterface;
	JType type;
	ObjectSchema schema;
	ObjectSchema schemaInterface;
	ObjectSchema schemaInterfaceImpl;


	@BeforeEach
	public void before() throws JClassAlreadyExistsException,
			ClassNotFoundException {
		codeModel = new JCodeModel();
		_package = codeModel._package("org.sample");
		sampleClass = codeModel._class("Sample");
		sampleInterface = _package._interface("SampleInterface");
		schema = new ObjectSchemaImpl();
		schema.setType(TYPE.OBJECT);
		
		// Create a schema interface
		schemaInterface = new ObjectSchemaImpl();
		schemaInterface.setType(TYPE.INTERFACE);
		schemaInterface.putProperty("fromInterface", new ObjectSchemaImpl(TYPE.BOOLEAN));
		schemaInterface.setId(sampleInterface.fullName());

		// impl
		schemaInterfaceImpl = new ObjectSchemaImpl();
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
		ObjectSchema childSchema = new ObjectSchemaImpl();
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
		ObjectSchema childSchema = new ObjectSchemaImpl();
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		String propName = "stringName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_STRINGNAME)) {") > 0);
		// It should directly set the value
		assertTrue(methodString.indexOf("stringName = adapter.getString(_KEY_STRINGNAME);") > 0);
		// It should also have an else that sets it to null
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("stringName = null;") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectStringURI() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.URI);
		String propName = "uriName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_URINAME)) {") > 0);
		// It should directly set the value
		assertTrue(methodString.indexOf("uriName = adapter.getString(_KEY_URINAME);") > 0);
		// It should also have an else that sets it to null
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("uriName = null;") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	@Test
	public void testAssignJSONStringToPropertyNullFormat(){
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		// The format is null as this is simply a string
		propertySchema.setFormat(null);
		String propName = "stringName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		JVar[] propertyKeyConstants = addKeyConstant(sampleClass, propName);
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propertyKeyConstants[0], propertySchema);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.getString(_KEY_STRINGNAME)", methodString);
	}
	
	@Test
	public void testAssignJSONStringToPropertyDateTime(){
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.DATE_TIME);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		JVar[] propertyKeyConstants = addKeyConstant(sampleClass, propName);
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propertyKeyConstants[0], propertySchema);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), adapter.getString(_KEY_DATENAME))", methodString);
	}
	
	@Test
	public void testAssignJSONStringToPropertyDate(){
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.DATE);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		JVar[] propertyKeyConstants = addKeyConstant(sampleClass, propName);
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propertyKeyConstants[0], propertySchema);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE\"), adapter.getString(_KEY_DATENAME))", methodString);
	}
	
	@Test
	public void testAssignJSONStringToURI(){
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.URI);
		String propName = "someURI";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		JVar[] propertyKeyConstants = addKeyConstant(sampleClass, propName);
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propertyKeyConstants[0], propertySchema);
		String methodString = generateToString(rhs);
		System.out.println(methodString);
		assertEquals("adapter.getString(_KEY_SOMEURI)", methodString);
	}
	
	@Test
	public void testAssignJSONStringToPropertyTime(){
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.TIME);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		JVar[] propertyKeyConstants = addKeyConstant(sampleClass, propName);
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propertyKeyConstants[0], propertySchema);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"TIME\"), adapter.getString(_KEY_DATENAME))", methodString);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectStringFormateDateTime() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.DATE_TIME);
		String propName = "dateName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Date.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(declareToString(sampleClass));
		System.out.println(methodString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_DATENAME)) {") > 0);
		// This should convert from a string to a date.
		assertTrue(methodString.indexOf("dateName = adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), adapter.getString(_KEY_DATENAME));") > 0);
		// It should also have an else that sets it to null
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("dateName = null;") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectLongFormatedUtcMilisecs() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.INTEGER);
		propertySchema.setFormat(FORMAT.UTC_MILLISEC);
		String propName = "dateName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Date.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(declareToString(sampleClass));
		System.out.println(methodString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_DATENAME)) {") > 0);
		// This should convert from a string to a date.
		assertTrue(methodString.indexOf("dateName = new java.util.Date(adapter.getLong(_KEY_DATENAME));") > 0);
		// It should also have an else that sets it to null
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("dateName = null;") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectRequiredProperty() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		// Make this required
		propertySchema.setRequired(true);
		String propName = "stringName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		// There should be an else block
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("throw new java.lang.IllegalArgumentException(org.sagebionetworks.schema.ObjectSchemaImpl.createPropertyCannotBeNullMessage(_KEY_STRINGNAME));") > 0);
		// This case should not have a set to null
		assertFalse(methodString.indexOf("stringName = null;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectLongProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.INTEGER);
		String propName = "longName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Long.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);;
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("longName = new java.lang.Long(adapter.getLong(_KEY_LONGNAME));") > 0);
		assertTrue(methodString.indexOf("longName = null;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectDoubleProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.NUMBER);
		String propName = "doubleName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Double.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.contains("oubleName = new java.lang.Double(adapter.getDouble(_KEY_DOUBLENAME));"));
		assertTrue(methodString.contains("doubleName = null;"));
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectBooleanProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.BOOLEAN);
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Boolean.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("propName = new java.lang.Boolean(adapter.getBoolean(_KEY_PROPNAME));") > 0);
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
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("propName = new Sample(adapter.getJSONObject(_KEY_PROPNAME));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectInterface() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = schema;
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, sampleInterface, propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		// this time we provide a register.
		InstanceFactoryGenerator ifg = new InstanceFactoryGenerator(codeModel, Arrays.asList(schema, schemaInterface, schemaInterfaceImpl));
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass, ifg);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString
				.contains("org.sagebionetworks.schema.adapter.JSONObjectAdapter __localAdapter = adapter.getJSONObject(_KEY_PROPNAME);"));
		assertTrue(methodString
				.contains("propName = ((org.sample.SampleInterface) org.sample.SampleInterfaceInstanceFactory.singleton().newInstance(__localAdapter.getString(org.sagebionetworks.schema.ObjectSchema.CONCRETE_TYPE)));"));
		assertTrue(methodString.contains("propName.initializeFromJSONObject(__localAdapter);"));
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectConcreteClass() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = schema;
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, sampleClass, propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		// this time we provide a register.
		InstanceFactoryGenerator ifg = new InstanceFactoryGenerator(codeModel, Arrays.asList(schema));
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass, ifg);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("propName = new Sample(adapter.getJSONObject(_KEY_PROPNAME));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectArrayWithReg() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = schema;
		arrayTypeSchema.setType(TYPE.INTERFACE);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(sampleInterface), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		// this time we provide a register.
		InstanceFactoryGenerator ifg = new InstanceFactoryGenerator(codeModel, Arrays.asList(schema, schemaInterface, schemaInterfaceImpl));
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass, ifg);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString
				.contains("org.sagebionetworks.schema.adapter.JSONObjectAdapter __indexAdapter = __jsonArray.getJSONObject(__i);"));
		assertTrue(methodString
				.contains("org.sample.SampleInterface __indexObject = ((org.sample.SampleInterface) org.sample.SampleInterfaceInstanceFactory.singleton().newInstance(__indexAdapter.getString(org.sagebionetworks.schema.ObjectSchema.CONCRETE_TYPE)));"));
		assertTrue(methodString.contains("__indexObject.initializeFromJSONObject(__indexAdapter);"));
		assertTrue(methodString.contains("arrayName.add(__indexObject);"));
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectValidate() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = schema;
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, sampleClass, propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
	}
	
	/**
	 * Helper to add Constant key with the given name to the given class.
	 * @param toAddTo
	 * @param propertyName
	 * @return
	 */
	public static JFieldVar[] addKeyConstant(JDefinedClass toAddTo, String...propertyNames) {
		JFieldVar[] vars = null;
		if(propertyNames != null) {
			vars = new JFieldVar[propertyNames.length];
			int index = 0;
			for(String propertyName: propertyNames) {
				JFieldVar var = toAddTo.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, String[].class, ObjectSchemaImpl.getKeyConstantName(propertyName));
				vars[index] = var;
				index++;
			}
		}
		toAddTo.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, String[].class, ObjectSchema.ALL_KEYS_NAME);
		return vars;
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectArrayPrimitive() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = new ObjectSchemaImpl();
		arrayTypeSchema.setType(TYPE.STRING);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("arrayName = new java.util.ArrayList<java.lang.String>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_ARRAYNAME);") > 0);
		assertTrue(methodString.indexOf("arrayName.add((__jsonArray.isNull(__i)?null:__jsonArray.getString(__i)));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectSetPrimitive() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = new ObjectSchemaImpl();
		arrayTypeSchema.setType(TYPE.STRING);
		propertySchema.setItems(arrayTypeSchema);
		propertySchema.setUniqueItems(true);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(declareToString(constructor));
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("arrayName = new java.util.LinkedHashSet<java.lang.String>()") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectArrayObjects() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = schema;
		arrayTypeSchema.setType(TYPE.OBJECT);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(sampleClass), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.contains("arrayName = new java.util.ArrayList<Sample>();"));
		assertTrue(methodString
				.contains("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_ARRAYNAME);"));
		assertTrue(methodString.contains("arrayName.add((__jsonArray.isNull(__i)?null:new Sample(__jsonArray.getJSONObject(__i))));"));
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectEnum() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setEnum(new EnumValue[]{
				new EnumValue("A"),
				new EnumValue("B")
		});
		propertySchema.setName("SomeEnum");
		String propName = "enumName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		JDefinedClass enumCalss = _package._enum("SomeEnum");
		enumCalss.enumConstant("A");
		enumCalss.enumConstant("B");
		sampleClass.field(JMod.PRIVATE, enumCalss, propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.contains("enumName = org.sample.SomeEnum.valueOf(org.sagebionetworks.schema.JavaKeyword.determineJavaName(adapter.getString(_KEY_ENUMNAME)));"));
	}
	
	@Test
	public void testAssignPropertyToJSONStringNullFormat(){
		ObjectSchema propertySchema = new ObjectSchemaImpl();
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		// Dates are strings with a date-time format.
		propertySchema.setFormat(FORMAT.DATE_TIME);
		String propName = "dateName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(Date.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (dateName!= null) {") > 0);
		// It should directly set the value
		assertTrue(methodString.indexOf("adapter.put(_KEY_DATENAME, adapter.convertDateToString(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), dateName));") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectRequiredProperty() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		// Make this required
		propertySchema.setRequired(true);
		String propName = "stringName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		// There should be an else block
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("throw new java.lang.IllegalArgumentException(org.sagebionetworks.schema.ObjectSchemaImpl.createPropertyCannotBeNullMessage(_KEY_STRINGNAME));") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectLongProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.INTEGER);
		String propName = "longName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("long"), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);;
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertFalse(methodString.indexOf("if (longName!= null) {") > 0);
		assertTrue(methodString.indexOf("adapter.put(_KEY_LONGNAME, longName);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectLongFormatedUTC() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.INTEGER);
		propertySchema.setFormat(FORMAT.UTC_MILLISEC);
		String propName = "dateName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("long"), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);;
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertFalse(methodString.indexOf("if (longName!= null) {") > 0);
		assertTrue(methodString.indexOf("adapter.put(_KEY_DATENAME, dateName.getTime());") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectDoubleProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.NUMBER);
		String propName = "doubleName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("double"), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertFalse(methodString.indexOf("if (doubleName!= null) {") > 0);
		assertTrue(methodString.indexOf("adapter.put(_KEY_DOUBLENAME, doubleName);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectBooleanProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.BOOLEAN);
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("boolean"), propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertFalse(methodString.indexOf("if (propName!= null) {") > 0);
		assertTrue(methodString.indexOf("adapter.put(_KEY_PROPNAME, propName);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectObjectProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Set the property type to be the same as the object
		ObjectSchema propertySchema = schema;
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, sampleClass, propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("adapter.put(_KEY_PROPNAME, propName.writeToJSONObject(adapter.createNew()));") > 0);
//		printClassToConsole(sampleClass);
	}
	
	@Test
	public void testWriteToJSONObjectArrayPrimitive() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = new ObjectSchemaImpl();
		arrayTypeSchema.setType(TYPE.STRING);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propName);
		addKeyConstant(sampleClass, propName);
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
		assertTrue(methodString.indexOf("adapter.put(_KEY_ARRAYNAME, __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectArrayDate() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayDates";
		ObjectSchema arrayTypeSchema = new ObjectSchemaImpl();
		arrayTypeSchema.setType(TYPE.STRING);
		arrayTypeSchema.setFormat(FORMAT.DATE_TIME);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Date.class), propName);
		addKeyConstant(sampleClass, propName);
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
		assertTrue(methodString
				.indexOf("__array.put(__index, ((__value == null)?null:adapter.convertDateToString(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), __value)));") > 0);
		assertTrue(methodString.indexOf("__index++;") > 0);
		assertTrue(methodString.indexOf("adapter.put(_KEY_ARRAYDATES, __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectArrayDateUTCMilisec() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayDates";
		ObjectSchema arrayTypeSchema = new ObjectSchemaImpl();
		arrayTypeSchema.setType(TYPE.INTEGER);
		arrayTypeSchema.setFormat(FORMAT.UTC_MILLISEC);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Date.class), propName);
		addKeyConstant(sampleClass, propName);
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
		assertTrue(methodString.indexOf("adapter.put(_KEY_ARRAYDATES, __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectArrayLong() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "longList";
		ObjectSchema arrayTypeSchema = new ObjectSchemaImpl();
		arrayTypeSchema.setType(TYPE.INTEGER);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Long.class), propName);
		addKeyConstant(sampleClass, propName);
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
		assertTrue(methodString.indexOf("adapter.put(_KEY_LONGLIST, __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectArrayDouble() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "doubleList";
		ObjectSchema arrayTypeSchema = new ObjectSchemaImpl();
		arrayTypeSchema.setType(TYPE.NUMBER);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Double.class), propName);
		addKeyConstant(sampleClass, propName);
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
		assertTrue(methodString.indexOf("adapter.put(_KEY_DOUBLELIST, __array);") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectSetPrimitive() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = new ObjectSchemaImpl();
		arrayTypeSchema.setType(TYPE.STRING);
		propertySchema.setItems(arrayTypeSchema);
		propertySchema.setUniqueItems(true);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(HashSet.class).narrow(String.class), propName);
		addKeyConstant(sampleClass, propName);
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayName";
		ObjectSchema arrayTypeSchema = schema;
		arrayTypeSchema.setType(TYPE.OBJECT);
		propertySchema.setItems(arrayTypeSchema);
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(sampleClass), propName);
		addKeyConstant(sampleClass, propName);
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setEnum(new EnumValue[]{
				new EnumValue("A"),
				new EnumValue("B")
		});
		propertySchema.setName("SomeEnum");
		String propName = "enumName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		JDefinedClass enumCalss = _package._enum("SomeEnum");
		enumCalss.enumConstant("A");
		enumCalss.enumConstant("B");
		sampleClass.field(JMod.PRIVATE, enumCalss, propName);
		addKeyConstant(sampleClass, propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("adapter.put(_KEY_ENUMNAME, org.sagebionetworks.schema.JavaKeyword.determineJsonName(enumName.name()));") > 0);
	}
	
	@Test
	public void testAddJSONMarshalingInterfance(){
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		ObjectSchema interfaceSchema = new ObjectSchemaImpl();
		interfaceSchema.setType(TYPE.INTERFACE);
		interfaceSchema.setName("SampleInterface");
		assertThrows(IllegalArgumentException.class, () ->
			handler.addJSONMarshaling(interfaceSchema, sampleInterface, null)
		);
	}
	
	@Test
	public void testInitializeFromJSONForImplements() throws JClassAlreadyExistsException {
		// For this case we want to use class that has the sample as a base class
		ObjectSchema childSchema = new ObjectSchemaImpl();
		childSchema.setImplements(new ObjectSchema[]{schemaInterface});
		JDefinedClass childClasss = codeModel._class("ImplementsInterface");
		childClasss._implements(sampleInterface);
		String propName = "fromInterface";
		childClasss.field(JMod.PRIVATE, codeModel.ref(Boolean.class), propName);
		addKeyConstant(childClasss, propName);
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
		assertTrue(methodString.indexOf("fromInterface = new java.lang.Boolean(adapter.getBoolean(_KEY_FROMINTERFACE));") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectForImplements() throws JClassAlreadyExistsException {
		// For this case we want to use class that has the sample as a base class
		ObjectSchema childSchema = new ObjectSchemaImpl();
		childSchema.setImplements(new ObjectSchema[]{schemaInterface});
		JDefinedClass childClasss = codeModel._class("ImplementsInterface");
		childClasss._implements(sampleInterface);
		String propName = "fromInterface";
		childClasss.field(JMod.PRIVATE, codeModel.BOOLEAN, propName);
		addKeyConstant(childClasss, propName);
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
		assertTrue(methodString.indexOf("adapter.put(_KEY_FROMINTERFACE, fromInterface);") > 0);
	}
	

	public void printClassToConsole(JDefinedClass classToPrint) {
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		classToPrint.declare(formatter);
		System.out.println(writer.toString());
	}
	
	/**
	 * Helper to check for sequential fragments
	 * 
	 * @param toDeclare
	 * @return
	 */
	public void hasFragments(JDeclaration toDeclare, String... fragments) {
		String result = declareToString(toDeclare);
		int nextIndex = -1;
		for (String fragment : fragments) {
			int found = result.indexOf(fragment);
			if (found < 0) {
				fail("'" + fragment + "' not found in " + result);
			}
			if (found < nextIndex) {
				fail("'" + fragment + "' found out of order in " + result);
			}
			nextIndex = found;
		}
	}

	/**
	 * Helper to declare a model object to string.
	 * 
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		String defaultString = "defaultString";
		propertySchema.setDefault(defaultString);
		propertySchema.setType(TYPE.STRING);
		propertySchema.setRequired(true);
		String propName = "stringName";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		addKeyConstant(sampleClass, propName);

		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_STRINGNAME)) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("stringName = adapter.getString(_KEY_STRINGNAME);") > 0);
		//check that else statement was generated
		assertTrue(methodString.indexOf("stringName = \"defaultString\";") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONDateList() throws Exception {
		//make a property that has default set with a string
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		ObjectSchema dateType = new ObjectSchemaImpl();
		dateType.setType(TYPE.STRING);
		dateType.setFormat(FORMAT.DATE_TIME);
		propertySchema.setType(TYPE.ARRAY);
		propertySchema.setItems(dateType);
		String propName = "dateList";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Date.class), propName);
		addKeyConstant(sampleClass, propName);

		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_DATELIST)) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("dateList = new java.util.ArrayList<java.util.Date>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_DATELIST);") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString
				.indexOf("dateList.add((__jsonArray.isNull(__i)?null:adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), __jsonArray.getString(__i))));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromLongList() throws Exception {
		//make a property that has default set with a string
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		ObjectSchema arrayType = new ObjectSchemaImpl();
		arrayType.setType(TYPE.INTEGER);
		propertySchema.setType(TYPE.ARRAY);
		propertySchema.setItems(arrayType);
		String propName = "longList";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Long.class), propName);
		addKeyConstant(sampleClass, propName);

		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_LONGLIST)) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("longList = new java.util.ArrayList<java.lang.Long>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_LONGLIST);") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString.indexOf("longList.add((__jsonArray.isNull(__i)?null:__jsonArray.getLong(__i)));") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromDoubleList() throws Exception {
		//make a property that has default set with a string
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		ObjectSchema arrayType = new ObjectSchemaImpl();
		arrayType.setType(TYPE.NUMBER);
		propertySchema.setType(TYPE.ARRAY);
		propertySchema.setItems(arrayType);
		String propName = "doubleList";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Double.class), propName);
		addKeyConstant(sampleClass, propName);

		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_DOUBLELIST)) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("doubleList = new java.util.ArrayList<java.lang.Double>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_DOUBLELIST);") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString.indexOf("doubleList.add((__jsonArray.isNull(__i)?null:__jsonArray.getDouble(__i)));") > 0);
	}
	@Test
	public void testCreateMethodInitializeFromJSONDateListUtcMilisec() throws Exception {
		//make a property that has default set with a string
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		ObjectSchema dateType = new ObjectSchemaImpl();
		dateType.setType(TYPE.INTEGER);
		dateType.setFormat(FORMAT.UTC_MILLISEC);
		propertySchema.setType(TYPE.ARRAY);
		propertySchema.setItems(dateType);
		String propName = "dateList";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(Date.class), propName);
		addKeyConstant(sampleClass, propName);

		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		// It check to see if the "if statement got generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_DATELIST)) {") > 0);
		//check that assignment statement got generated
		assertTrue(methodString.indexOf("dateList = new java.util.ArrayList<java.util.Date>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_DATELIST);") > 0);
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		double defaultDouble = 7.12;
		propertySchema.setDefault(defaultDouble);
		propertySchema.setType(TYPE.NUMBER);
		propertySchema.setRequired(true);
		String propName = "defaultDoubleName";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		addKeyConstant(sampleClass, propName);

		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		//check that if statement was generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_DEFAULTDOUBLENAME)) {") > 0);
		//check that body of if statement was generated
		assertTrue(methodString.indexOf("defaultDoubleName = " +
				"new java.lang.String(adapter.getDouble(_KEY_DEFAULTDOUBLENAME));") > 0);
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		long defaultLong = 77;
		propertySchema.setDefault(defaultLong);
		propertySchema.setType(TYPE.INTEGER);
		propertySchema.setRequired(true);
		String propName = "defaultIntegerName";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		addKeyConstant(sampleClass, propName);

		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		//check that if statement was generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_DEFAULTINTEGERNAME)) {") > 0);
		//check that body of if statement was generated
		assertTrue(methodString.indexOf("defaultIntegerName = " +
				"new java.lang.String(adapter.getLong(_KEY_DEFAULTINTEGERNAME));") > 0);
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		boolean defaultBoolean = false;
		propertySchema.setDefault(defaultBoolean);
		propertySchema.setType(TYPE.BOOLEAN);
		propertySchema.setRequired(true);
		String propName = "defaultBooleanName";
		
		//add property to schema
		schema.putProperty(propName, propertySchema);
		
		// put field in sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		addKeyConstant(sampleClass, propName);

		//create the method
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		assertNotNull(method);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		//check that if statement was generated
		assertTrue(methodString.indexOf("if (!adapter.isNull(_KEY_DEFAULTBOOLEANNAME)) {") > 0);
		//check that body of if statement was generated
		assertTrue(methodString.indexOf("defaultBooleanName = " +
				"new java.lang.String(adapter.getBoolean(_KEY_DEFAULTBOOLEANNAME));") > 0);
		//check that body of else statment was generated
		assertTrue(methodString.indexOf("defaultBooleanName = false;") > 0);
	}
	
	/**
	 * Tests that initializeFromJSONObject works for properties
	 * that have a default object set.
	 */
	@Disabled
	@Test
	public void testCreateMethodInitializeFromJSONWithDefaultObjectProperty() throws Exception {
		//make a property that has default sent with a JSONObject
		ObjectSchema propertySchema = new ObjectSchemaImpl();
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
		addKeyConstant(sampleClass, propName);

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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayWhoseItemIsAnEnum";
		
		//create an enum ObjectSchema and add it to propertySchema's items
		//it must have a name, id, and it's type must be STRING
		//it must have an enum defined
		ObjectSchema typesEnum = new ObjectSchemaImpl();
		typesEnum.setType(TYPE.STRING);
		typesEnum.setName("Animals");
		typesEnum.setId("Animals");
		EnumValue[] forTheEnum = {
				new EnumValue("puppy"),
				new EnumValue("mouse"),
				new EnumValue("elephant"),
		};
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
		addKeyConstant(sampleClass, propName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);
		
		//check that array of enumeration got created successfully, and 
		//assignments are correct
		assertTrue(methodString.indexOf("arrayWhoseItemIsAnEnum = new java.util.ArrayList<org.sample.Animals>();") > 0);
		assertTrue(methodString
				.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_ARRAYWHOSEITEMISANENUM);") > 0);
		assertTrue(methodString.indexOf("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {") > 0);
		assertTrue(methodString
				.indexOf("arrayWhoseItemIsAnEnum.add((__jsonArray.isNull(__i)?null:org.sample.Animals.valueOf(org.sagebionetworks.schema.JavaKeyword.determineJavaName(__jsonArray.getString(__i)))));") > 0);
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
		ObjectSchema propertySchemaOne = new ObjectSchemaImpl();
		propertySchemaOne.setType(TYPE.ARRAY);
		String propName = "arrayWhoseItemIsAnEnum";
		//create an enum ObjectSchema and add it to propertySchema's items
		//it must have a name, id, and it's type must be STRING
		//it must have an enum defined
		ObjectSchema typesEnum = new ObjectSchemaImpl();
		typesEnum.setType(TYPE.STRING);
		typesEnum.setName("Animals");
		typesEnum.setId("Animals");
		EnumValue[] forTheEnum = {
				new EnumValue("puppy"),
				new EnumValue("mouse"),
				new EnumValue("elephant"),
		};
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
		ObjectSchema propertySchemaTwo = new ObjectSchemaImpl();
		propertySchemaTwo.setType(TYPE.ARRAY);
		String propTwoName = "arrayWhoseItemIsNotEnum";
		ObjectSchema typesString = new ObjectSchemaImpl();
		typesString.setType(TYPE.STRING);
		propertySchemaTwo.setItems(typesString);
		schema.putProperty(propTwoName, propertySchemaTwo);
		
		//add field to sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propTwoName);
		addKeyConstant(sampleClass, propName, propTwoName);
		
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
//		System.out.println(methodString);
		
		//check that everything was created correctly for the array with an enum
		assertTrue(methodString.contains("arrayWhoseItemIsAnEnum = new java.util.ArrayList<org.sample.Animals>();"));
		assertTrue(methodString
				.contains("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_ARRAYWHOSEITEMISANENUM);"));
		assertTrue(methodString.contains("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {"));
		assertTrue(methodString
				.contains("arrayWhoseItemIsAnEnum.add((__jsonArray.isNull(__i)?null:org.sample.Animals.valueOf(org.sagebionetworks.schema.JavaKeyword.determineJavaName(__jsonArray.getString(__i)))));"));
		
		//check that everything was created correctly for the array without an enum
		assertTrue(methodString.contains("arrayWhoseItemIsNotEnum = new java.util.ArrayList<java.lang.String>();"));
		assertTrue(methodString
				.contains("org.sagebionetworks.schema.adapter.JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_ARRAYWHOSEITEMISNOTENUM);"));
		assertTrue(methodString.contains("for (int __i = 0; (__i<__jsonArray.length()); __i ++) {"));
		assertTrue(methodString.contains("arrayWhoseItemIsNotEnum.add((__jsonArray.isNull(__i)?null:__jsonArray.getString(__i)));"));
	}
	
	/**
	 * Tests that writeToJSONObject works for Array type that has an
	 * Enum for it's item.
	 * @throws Exception
	 */
	@Test
	public void testWriteToJSONObjectArrayWEnum()throws Exception {
		//make an array property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.ARRAY);
		String propName = "arrayPropWithEnum";
		
		//make the ObjectSchema that represents the array's items that contains an enum
		ObjectSchema itemWithEnum = new ObjectSchemaImpl();
		itemWithEnum.setType(TYPE.STRING);
		itemWithEnum.setName("Animals");
		itemWithEnum.setId("Animals");
		EnumValue[] forTheEnum = {
				new EnumValue("puppy"),
				new EnumValue("mouse"),
				new EnumValue("elephant"),
		};
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
		addKeyConstant(sampleClass, propName);
		
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		
		//make sure everything was created correctly
		assertTrue(methodString.indexOf("org.sample.Animals __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("__array.put(__index, ((__value == null)?null:org.sagebionetworks.schema.JavaKeyword.determineJsonName(__value.name())));") > 0);
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
		ObjectSchema propertySchemaOne = new ObjectSchemaImpl();
		propertySchemaOne.setType(TYPE.ARRAY);
		String propName = "arrayWhoseItemIsAnEnum";
		//create an enum ObjectSchema and add it to propertySchema's items
		ObjectSchema typesEnum = new ObjectSchemaImpl();
		typesEnum.setType(TYPE.STRING);
		typesEnum.setName("Animals");
		typesEnum.setId("Animals");
		EnumValue[] forTheEnum = {
				new EnumValue("puppy"),
				new EnumValue("mouse"),
				new EnumValue("elephant"),
		};
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
		ObjectSchema propertySchemaTwo = new ObjectSchemaImpl();
		propertySchemaTwo.setType(TYPE.ARRAY);
		String propTwoName = "arrayWhoseItemIsNotEnum";
		ObjectSchema typesString = new ObjectSchemaImpl();
		typesString.setType(TYPE.STRING);
		propertySchemaTwo.setItems(typesString);
		schema.putProperty(propTwoName, propertySchemaTwo);
		
		//add field to sampleClass
		sampleClass.field(JMod.PRIVATE, codeModel.ref(List.class).narrow(String.class), propTwoName);
		addKeyConstant(sampleClass, propName,propTwoName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		
		// Now get the string and check it.
		String methodString = declareToString(method);
		
		//make sure everything is in order for the property that has an enum
		assertTrue(methodString.indexOf("java.util.Iterator<org.sample.Animals> __it = arrayWhoseItemIsAnEnum.iterator();") > 0);
		assertTrue(methodString.indexOf("org.sample.Animals __value = __it.next();") > 0);
		assertTrue(methodString.indexOf("__array.put(__index, ((__value == null)?null:org.sagebionetworks.schema.JavaKeyword.determineJsonName(__value.name())));") > 0);
		
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
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.TUPLE_ARRAY_MAP);
		String propName = "mapWhoseItemIsAnEnum";

		// create an enum ObjectSchema and add it to propertySchema's items
		// it must have a name, id, and it's type must be STRING
		// it must have an enum defined
		ObjectSchema keyEnum = new ObjectSchemaImpl();
		keyEnum.setType(TYPE.STRING);
		keyEnum.setName("Animals");
		keyEnum.setId("Animals");
		keyEnum.setEnum(new EnumValue[] { 
				new EnumValue("puppy"),
				new EnumValue("mouse"),
				new EnumValue("elephant")
		});

		ObjectSchema valueEnum = new ObjectSchemaImpl();
		valueEnum.setType(TYPE.STRING);
		valueEnum.setName("Pets");
		valueEnum.setId("pets");
		valueEnum.setEnum(new EnumValue[] {
				new EnumValue("dog"),
				new EnumValue("cat")
		});

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
		addKeyConstant(sampleClass, propName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);

		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);

		// check that map of enumeration got created successfully, and
		// assignments are correct
		assertTrue(methodString.contains("mapWhoseItemIsAnEnum = new java.util.LinkedHashMap<org.sample.Animals, org.sample.Pets>();"));
		assertTrue(methodString
				.contains("org.sagebionetworks.schema.adapter.JSONMapAdapter __jsonMap = adapter.getJSONMap(_KEY_MAPWHOSEITEMISANENUM);"));
		assertTrue(methodString.contains("org.sample.Pets __value;"));
		assertTrue(methodString.contains("if (__jsonMap.isNull(__keyObject)) {"));
		assertTrue(methodString.contains("__value = null;"));
		assertTrue(methodString.contains("} else {"));
		assertTrue(methodString.contains("__value = org.sample.Pets.valueOf(org.sagebionetworks.schema.JavaKeyword.determineJavaName(__jsonMap.getString(__keyObject)));"));
		assertTrue(methodString.contains("org.sample.Animals __key = org.sample.Animals.valueOf(org.sagebionetworks.schema.JavaKeyword.determineJavaName(((java.lang.String) __keyObject)));"));
		assertTrue(methodString.contains("mapWhoseItemIsAnEnum.put(__key, __value);"));
	}

	/**
	 * Tests that writeToJSONObject works for Array type that has an Enum for it's key.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWriteToJSONObjectMapWKeyEnum() throws Exception {
		// make an array property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.TUPLE_ARRAY_MAP);
		String propName = "arrayPropWithEnum";

		// make the ObjectSchema that represents the array's items that contains an enum
		ObjectSchema keyEnum = new ObjectSchemaImpl();
		keyEnum.setType(TYPE.STRING);
		keyEnum.setName("Animals");
		keyEnum.setId("Animals");
		keyEnum.setEnum(new EnumValue[] { 
				new EnumValue("puppy"),
				new EnumValue("mouse"),
				new EnumValue("elephant")
		});

		ObjectSchema valueEnum = new ObjectSchemaImpl();
		valueEnum.setType(TYPE.STRING);
		valueEnum.setName("Pets");
		valueEnum.setId("pets");
		valueEnum.setEnum(new EnumValue[] { 
				new EnumValue("dog"),
				new EnumValue("cat")
		});

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
		addKeyConstant(sampleClass, propName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);

		// Now get the string and check it.
		String methodString = declareToString(constructor);

		// make sure everything was created correctly
		assertTrue(methodString.contains("if (__entry.getValue() == null) {"));
		assertTrue(methodString.contains("__map.putNull(__entry.getKey());"));
		assertTrue(methodString.contains("} else {"));
		assertTrue(methodString.contains("__map.put(__entry.getKey(), org.sagebionetworks.schema.JavaKeyword.determineJsonName(__entry.getValue().name()))"));
	}



	/**
	 * Tests that an schema that has a property of type STR_KEY_MAP, whose values are in an enumeration is supported in
	 * initializeFromJSONObject methods created by handler.
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateMethodInitializeFromJSONObjectStringKeyMap_WithEnumValue() throws Exception {
		// create a property that is an Map
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.MAP);
		String propName = "mapWhoseItemIsAnEnum";

		ObjectSchema valueEnum = new ObjectSchemaImpl();
		valueEnum.setType(TYPE.STRING);
		valueEnum.setName("Pets");
		valueEnum.setId("pets");
		valueEnum.setEnum(new EnumValue[] {
				new EnumValue("dog"),
				new EnumValue("cat")
		});

		// add enum to property's value
		propertySchema.setValue(valueEnum);

		// add property to schema
		schema.putProperty(propName, propertySchema);

		// add field to sampleClass
		codeModel._package("org.sample");
		JClass testKeyClass = codeModel.ref(String.class);
		JDefinedClass testValueClass = _package._enum("Pets");
		testValueClass.enumConstant("dog");
		testValueClass.enumConstant("cat");
		sampleClass.field(JMod.PRIVATE, codeModel.ref(Map.class).narrow(testKeyClass, testValueClass), propName);
		addKeyConstant(sampleClass, propName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		//method under test
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);

		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);

		// check that map of enumeration got created successfully, and
		// assignments are correct
		assertTrue(methodString.contains("mapWhoseItemIsAnEnum = new java.util.LinkedHashMap<java.lang.String, org.sample.Pets>();"));
		assertTrue(methodString
				.contains("org.sagebionetworks.schema.adapter.JSONObjectAdapter __jsonStringMap = adapter.getJSONObject(_KEY_MAPWHOSEITEMISANENUM);"));
		assertTrue(methodString.contains("org.sample.Pets __value;"));
		assertTrue(methodString.contains("if (__jsonStringMap.isNull(__key)) {"));
		assertTrue(methodString.contains("__value = null;"));
		assertTrue(methodString.contains("} else {"));
		assertTrue(methodString.contains("__value = org.sample.Pets.valueOf(org.sagebionetworks.schema.JavaKeyword.determineJavaName(__jsonStringMap.getString(__key)));"));
		assertTrue(methodString.contains("mapWhoseItemIsAnEnum.put(__key, __value);"));
	}

	@Test
	public void testCreateMethodInitializeFromJSONObjectStringKeyMap_nullValue() throws Exception {
		// create a property that is an Map
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.MAP);
		String propName = "mapWhoseItemIsAnEnum";

		// add property to schema
		schema.putProperty(propName, propertySchema);

		// add field to sampleClass
		codeModel._package("org.sample");
		JClass testKeyClass = codeModel.ref(String.class);
		JDefinedClass testValueClass = _package._enum("Pets");
		testValueClass.enumConstant("dog");
		testValueClass.enumConstant("cat");
		sampleClass.field(JMod.PRIVATE, codeModel.ref(Map.class).narrow(testKeyClass, testValueClass), propName);
		addKeyConstant(sampleClass, propName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();

		// value type is explicitly set to null
		propertySchema.setValue(null);
		assertThrows(IllegalArgumentException.class, () ->
			//method under test
			handler.createMethodInitializeFromJSONObject(schema, sampleClass)
		);
	}

	@Test
	public void testCreateMethodInitializeFromJSONObjectStringKeyMap_WithInterfaceValue() throws Exception {
		// create a property that is an Map
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.MAP);
		String propName = "mapWhoseItemIsAnInterface";

		// add enum to property's value
		propertySchema.setValue(schemaInterface);

		// add property to schema
		schema.putProperty(propName, propertySchema);

		// add field to sampleClass
		JClass testKeyClass = codeModel.ref(String.class);
		sampleClass.field(JMod.PRIVATE, codeModel.ref(Map.class).narrow(testKeyClass, sampleInterface), propName);
		addKeyConstant(sampleClass, propName);

		InstanceFactoryGenerator ifg = new InstanceFactoryGenerator(codeModel, Arrays.asList(schema, schemaInterface, schemaInterfaceImpl));
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		//method under test
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass, ifg);

		// Now get the string and check it.
		String methodString = declareToString(method);
		System.out.println(methodString);

		// check that map of enumeration got created successfully, and
		// assignments are correct
		assertTrue(methodString.contains("mapWhoseItemIsAnInterface = new java.util.LinkedHashMap<java.lang.String, org.sample.SampleInterface>();"));
		assertTrue(methodString
				.contains("org.sagebionetworks.schema.adapter.JSONObjectAdapter __jsonStringMap = adapter.getJSONObject(_KEY_MAPWHOSEITEMISANINTERFACE);"));
		assertTrue(methodString.contains("org.sample.SampleInterface __value;"));
		assertTrue(methodString.contains("if (__jsonStringMap.isNull(__key)) {"));
		assertTrue(methodString.contains("__value = null;"));
		assertTrue(methodString.contains("} else {"));
		assertTrue(methodString.contains("__value = ((org.sample.SampleInterface) org.sample.SampleInterfaceInstanceFactory.singleton().newInstance(__valueAdapter.getString(org.sagebionetworks.schema.ObjectSchema.CONCRETE_TYPE)));"));
		assertTrue(methodString.contains("__value.initializeFromJSONObject(__valueAdapter);"));

		assertTrue(methodString.contains("mapWhoseItemIsAnInterface.put(__key, __value);"));
	}



	/**
	 * Tests that writeToJSONObject works for Array type that has an Enum for it's key.
	 *
	 * @throws Exception
	 */
	@Test
	public void testWriteToJSONObjectStringKeyMap_WithValue() throws Exception {
		// make an array property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.MAP);
		String propName = "stringKeyMapPropWithEnum";

		ObjectSchema valueEnum = new ObjectSchemaImpl();
		valueEnum.setType(TYPE.STRING);
		valueEnum.setName("Pets");
		valueEnum.setId("pets");
		valueEnum.setEnum(new EnumValue[] {
				new EnumValue("dog"),
				new EnumValue("cat")
		});

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
		addKeyConstant(sampleClass, propName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createWriteToJSONObject(schema, sampleClass);

		// Now get the string and check it.
		String methodString = declareToString(constructor);
		System.out.println(methodString);

		// make sure everything was created correctly
		assertTrue(methodString.contains("if (__entry.getValue() == null) {"));
		assertTrue(methodString.contains("__map.putNull(__entry.getKey());"));
		assertTrue(methodString.contains("} else {"));
		assertTrue(methodString.contains("__map.put(__entry.getKey(), org.sagebionetworks.schema.JavaKeyword.determineJsonName(__entry.getValue().name()))"));
	}


	/**
	 * Tests that writeToJSONObject works for Array type that has an Enum for it's key.
	 *
	 * @throws Exception
	 */
	@Test
	public void testWriteToJSONObjectStringKeyMap_nullValue() throws Exception {
		// make an array property
		ObjectSchema propertySchema = new ObjectSchemaImpl();
		propertySchema.setType(TYPE.MAP);
		String propName = "stringKeyMapPropWithEnum";


		propertySchema.setValue(null);

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
		addKeyConstant(sampleClass, propName);

		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		assertThrows(IllegalArgumentException.class, () ->
				handler.createWriteToJSONObject(schema, sampleClass)
		);
	}
}