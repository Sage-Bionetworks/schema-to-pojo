package org.sagebionetworks.schema.generator.handler.schema03;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;

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

public class JSONMarshalingHandlerImpl03Test {

	JCodeModel codeModel;
	JPackage _package;
	JDefinedClass sampleClass;
	JDefinedClass sampleInterfance;
	JType type;
	ObjectSchema schema;

	@Before
	public void before() throws JClassAlreadyExistsException,
			ClassNotFoundException {
		codeModel = new JCodeModel();
		_package = codeModel._package("org.sample");
		sampleClass = codeModel._class("Sample");
		sampleInterfance = _package._interface("SampleInterface");
		schema = new ObjectSchema();
		schema.setType(TYPE.OBJECT);
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
		assertTrue(methodString.indexOf("if (adapter.has(\"stringName\")) {") > 0);
		// It should directly set the value
		assertTrue(methodString.indexOf("stringName = adapter.getString(\"stringName\");") > 0);
		// It should also have an else that sets it to null
		assertTrue(methodString.indexOf("} else {") > 0);
		assertTrue(methodString.indexOf("stringName = null;") > 0);
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
	public void testAssignJSONStringToPropertyUTCMill(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.UTC_MILLISEC);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JExpression rhs = handler.assignJSONStringToProperty(codeModel, adapter, propName, propertySchema);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("new java.util.Date(java.lang.Long.parseLong(adapter.getString(\"dateName\")))", methodString);
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
		assertTrue(methodString.indexOf("if (adapter.has(\"dateName\")) {") > 0);
		// This should convert from a string to a date.
		assertTrue(methodString.indexOf("dateName = adapter.convertStringToDate(org.sagebionetworks.schema.FORMAT.valueOf(\"DATE_TIME\"), adapter.getString(\"dateName\"));") > 0);
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
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("long"), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(method);;
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("longName = adapter.getLong(\"longName\");") > 0);
		assertFalse(methodString.indexOf("longName = null;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectDoubleProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.NUMBER);
		String propName = "doubleName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("double"), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
//		System.out.println(methodString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("doubleName = adapter.getDouble(\"doubleName\");") > 0);
		assertFalse(methodString.indexOf("doubleName = null;") > 0);
	}
	
	@Test
	public void testCreateMethodInitializeFromJSONObjectBooleanProperty() throws JClassAlreadyExistsException, ClassNotFoundException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.BOOLEAN);
		String propName = "propName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel.parseType("boolean"), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod constructor = handler.createMethodInitializeFromJSONObject(schema, sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("propName = adapter.getBoolean(\"propName\");") > 0);
		assertFalse(methodString.indexOf("propName = null;") > 0);
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
//		System.out.println(constructorString);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("propName = new Sample(adapter.getJSONObject(\"propName\"));") > 0);
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
		assertTrue(methodString.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter jsonArray = adapter.getJSONArray(\"arrayName\");") > 0);
		assertTrue(methodString.indexOf("arrayName.add(jsonArray.getString(i));") > 0);
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
		assertTrue(methodString.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter jsonArray = adapter.getJSONArray(\"arrayName\");") > 0);
		assertTrue(methodString.indexOf("arrayName.add(new Sample(jsonArray.getJSONObject(i)))") > 0);
	}
	
	@Test
	public void testWriteToJSONObjectStringProperty() throws JClassAlreadyExistsException {
		// Add add a string property
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		String propName = "stringName";
		schema.putProperty(propName, propertySchema);
		// Make sure this field exits
		sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JMethod method = handler.createWriteToJSONObject(schema, sampleClass);
		assertNotNull(method);
		// Now get the string and check it.
		String methodString = declareToString(method);
		// It should check to see if the property exits in the adapter
		assertTrue(methodString.indexOf("if (stringName!= null) {") > 0);
		// It should directly set the value
		assertTrue(methodString.indexOf("adapter.put(\"stringName\", stringName);") > 0);
		assertTrue(methodString.indexOf("return adapter;") > 0);
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
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propName, propertySchema, field);
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
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propName, propertySchema, field);
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
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propName, propertySchema, field);
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
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propName, propertySchema, field);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("adapter.convertDateToString(org.sagebionetworks.schema.FORMAT.valueOf(\"TIME\"), dateName)", methodString);
	}
	
	@Test
	public void testAssignPropertyToJSONStringUTCMil(){
		ObjectSchema propertySchema = new ObjectSchema();
		propertySchema.setType(TYPE.STRING);
		propertySchema.setFormat(FORMAT.UTC_MILLISEC);
		String propName = "dateName";
		// Create an adapter
		JMethod method  = sampleClass.method(JMod.PUBLIC, JSONObjectAdapter.class, "initializeFromJSONObject");
		// add the parameter
		JVar adapter = method.param(codeModel._ref(JSONObjectAdapter.class), "adapter");
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		JFieldVar field = sampleClass.field(JMod.PRIVATE, codeModel._ref(String.class), propName);
		JExpression rhs = handler.assignPropertyToJSONString(codeModel, adapter, propName, propertySchema, field);
		String methodString = generateToString(rhs);
//		System.out.println(methodString);
		assertEquals("java.lang.Long.toString(dateName.getTime())", methodString);
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
		assertTrue(methodString.indexOf("org.sagebionetworks.schema.adapter.JSONArrayAdapter array = adapter.createNewArray();") > 0);
		assertTrue(methodString.indexOf("java.util.Iterator<java.lang.String> it = arrayName.iterator();") > 0);
		assertTrue(methodString.indexOf("int index = 0;") > 0);
		assertTrue(methodString.indexOf("while (it.hasNext()) {") > 0);
		assertTrue(methodString.indexOf("array.put(index, it.next());") > 0);
		assertTrue(methodString.indexOf("index++;") > 0);
		assertTrue(methodString.indexOf("adapter.put(\"arrayName\", array);") > 0);
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
		assertTrue(methodString.indexOf("while (it.hasNext()) {") > 0);
		assertTrue(methodString.indexOf("array.put(index, it.next());") > 0);
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
		
//		printClassToConsole(sampleClass);
		// Now get the string and check it.
		String methodString = declareToString(constructor);
		// Is the primitive assigned correctly?
		assertTrue(methodString.indexOf("array.put(index, it.next().writeToJSONObject(adapter.createNew()));") > 0);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testAddJSONMarshalingInterfance(){
		JSONMarshalingHandlerImpl03 handler = new JSONMarshalingHandlerImpl03();
		ObjectSchema interfaceSchema = new ObjectSchema();
		interfaceSchema.setType(TYPE.INTERFACE);
		interfaceSchema.setName("SampleInterface");
		handler.addJSONMarshaling(interfaceSchema, sampleInterfance);
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

}
