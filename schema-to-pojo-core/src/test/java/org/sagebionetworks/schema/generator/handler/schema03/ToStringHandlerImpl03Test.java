package org.sagebionetworks.schema.generator.handler.schema03;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDeclaration;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

public class ToStringHandlerImpl03Test {
	ObjectSchema schema;
	JCodeModel codeModel;
	JDefinedClass sampleClass;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		schema = new ObjectSchema();
		codeModel = new JCodeModel();
		sampleClass = codeModel._class("ImASampleClass");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Tests that toStringHandler creates method correctly
	 * when class has a super
	 */
	@Test
	public void testToStringSuper() throws Exception {
		// For this case we want to use class that has the sample as a base class
		ObjectSchema childSchema = new ObjectSchema();
		childSchema.setExtends(schema);
		JDefinedClass childClasss = codeModel._class("ChildOfSample");
		childClasss._extends(sampleClass);
		
		// Now handle
		ToStringHandlerImpl03 handler = new ToStringHandlerImpl03();
		JMethod method = handler.addToString(childSchema, childClasss);
		assertNotNull(method);
		JBlock body = method.body();
		assertNotNull(body);
		
		// Now get the string and check it.
		String methodString = declareToString(method);

		// Make sure there is a call to super.
		assertTrue(methodString.indexOf("result.append(super.toString());") > 0);
	}
	
	/**
	 * Tests that toString works for object property.
	 * @throws Exception
	 */
	@Test
	public void testToStringForObjectProperty() throws Exception {
		// Add a object property to the schema
		ObjectSchema imAObjectProp = new ObjectSchema();
		imAObjectProp.setType(TYPE.OBJECT);
		String objectPropKey = "objectPropKeyName";
		schema.putProperty(objectPropKey, imAObjectProp);
		
		//add field to sampleClass JDefinedClass
		sampleClass.field(JMod.PRIVATE, sampleClass, objectPropKey);
		
		//handle
		ToStringHandlerImpl03 handler = new ToStringHandlerImpl03();
		JMethod method = handler.addToString(schema, sampleClass);
		
		//let's see the results
		String methodString = declareToString(method);
		
		//verify everything was created correctly
		assertTrue(methodString.indexOf("result.append(\"objectPropKeyName=\");") > 0);
		assertTrue(methodString.indexOf("result.append(objectPropKeyName);") > 0);
	}

	/**
	 * Tests that toString works for array property.
	 * @throws Exception
	 */
	@Test
	public void testToStringForArrayProperty() throws Exception {
		// Add a array property to the schema
		ObjectSchema imAArrayProp = new ObjectSchema();
		imAArrayProp.setType(TYPE.ARRAY);
		String arrayPropKey = "arrayPropKeyName";
		schema.putProperty(arrayPropKey, imAArrayProp);
		
		//add field to sampleClass JDefinedClass
		sampleClass.field(JMod.PRIVATE, sampleClass, arrayPropKey);
		
		//handle
		ToStringHandlerImpl03 handler = new ToStringHandlerImpl03();
		JMethod method = handler.addToString(schema, sampleClass);
		
		//let's see the results
		String methodString = declareToString(method);
		
		//verify everything was created correctly
		assertTrue(methodString.indexOf("result.append(\"arrayPropKeyName=\");") > 0);
		assertTrue(methodString.indexOf("result.append(arrayPropKeyName);") > 0);
	}
	
	/**
	 * Tests that toString works for Double property.
	 * @throws Exception
	 */
	@Test
	public void testToStringForDoubleProperty() throws Exception {
		// Add a double property to the schema
		ObjectSchema imADoubleProp = new ObjectSchema();
		imADoubleProp.setType(TYPE.NUMBER);
		String doublePropKey = "doublePropKeyName";
		schema.putProperty(doublePropKey, imADoubleProp);
		
		//add field to sampleClass JDefinedClass
		sampleClass.field(JMod.PRIVATE, sampleClass, doublePropKey);
		
		//handle
		ToStringHandlerImpl03 handler = new ToStringHandlerImpl03();
		JMethod method = handler.addToString(schema, sampleClass);
		
		//let's see the results
		String methodString = declareToString(method);
		
		//verify everything was created correctly
		assertTrue(methodString.indexOf("result.append(\"doublePropKeyName=\");") > 0);
		assertTrue(methodString.indexOf("result.append(doublePropKeyName);") > 0);  
	}
	
	/**
	 * Tests that toString works for Long property.
	 * @throws Exception
	 */
	@Test
	public void testToStringForLongProperty() throws Exception {
		// Add a long property to the schema
		ObjectSchema imALongProp = new ObjectSchema();
		imALongProp.setType(TYPE.INTEGER);
		String longPropKey = "integerPropKeyName";
		schema.putProperty(longPropKey, imALongProp);
		
		//add field to sampleClass JDefinedClass
		sampleClass.field(JMod.PRIVATE, sampleClass, longPropKey);
		
		//handle
		ToStringHandlerImpl03 handler = new ToStringHandlerImpl03();
		JMethod method = handler.addToString(schema, sampleClass);
		
		//let's see the results
		String methodString = declareToString(method);
		
		//verify everything was created correctly
		assertTrue(methodString.indexOf("result.append(\"integerPropKeyName=\");") > 0);
		assertTrue(methodString.indexOf("result.append(integerPropKeyName);") > 0);  
	}
	
	/**
	 * Tests that toString works for Boolean property.
	 * @throws Exception
	 */
	@Test
	public void testToStringForBooleanProperty() throws Exception {
		// Add a boolean property to the schema
		ObjectSchema imABoolProp = new ObjectSchema();
		imABoolProp.setType(TYPE.BOOLEAN);
		String boolPropKey = "booleanPropKeyName";
		schema.putProperty(boolPropKey, imABoolProp);
		
		//add field to sampleClass JDefinedClass
		sampleClass.field(JMod.PRIVATE, sampleClass, boolPropKey);
		
		//handle
		ToStringHandlerImpl03 handler = new ToStringHandlerImpl03();
		JMethod method = handler.addToString(schema, sampleClass);
		
		//let's see the results
		String methodString = declareToString(method);
		
		//verify everything was created correctly
		assertTrue(methodString.indexOf("result.append(\"booleanPropKeyName=\");") > 0);
		assertTrue(methodString.indexOf("result.append(booleanPropKeyName);") > 0);   
	}
	
	/**
	 * Tests that toString works for String property.
	 * @throws Exception
	 */
	@Test
	public void testToStringForStringProperty() throws Exception {
		// Add a string property to the schema
		ObjectSchema imAStringProp = new ObjectSchema();
		imAStringProp.setType(TYPE.STRING);
		String stringPropKey = "stringPropKeyName";
		schema.putProperty(stringPropKey, imAStringProp);
		
		//add field to sampleClass JDefinedClass
		sampleClass.field(JMod.PRIVATE, sampleClass, stringPropKey);
		
		//handle
		ToStringHandlerImpl03 handler = new ToStringHandlerImpl03();
		JMethod method = handler.addToString(schema, sampleClass);
		
		//let's see the results
		String methodString = declareToString(method);
		
		//verify everything was created correctly
		assertTrue(methodString.indexOf("result.append(\"stringPropKeyName=\");") > 0);
		assertTrue(methodString.indexOf("result.append(stringPropKeyName);") > 0);  
	}

	/**
	 * Tests that toString works for a schema that has several properties.
	 * @throws Exception
	 */
	@Test
	public void testToStringForSeveralProperties() throws Exception {
		
		//a code  model is used to get a reference to a JDefinedClass
		//the JDefinedClass holds the pieces of the soon to be generated classes
		
		//add two properties to the sample class
		//add a string and a boolean
		sampleClass.field(JMod.PRIVATE, codeModel.ref(String.class), "imAStringMemberVariable");
		sampleClass.field(JMod.PRIVATE, codeModel.ref(Boolean.class), "imABooleanMemberVariable");
		
		//set up a corresponding schema with the two properties
		schema.setType(TYPE.OBJECT);
		
		ObjectSchema nextProperty =  new ObjectSchema();
		nextProperty.setType(TYPE.STRING);
		schema.putProperty("imAStringMemberVariable", nextProperty);
		
		nextProperty = new ObjectSchema();
		nextProperty.setType(TYPE.BOOLEAN);
		schema.putProperty("imABooleanMemberVariable", nextProperty);
		
		//create the handler
		ToStringHandlerImpl03 handler = new ToStringHandlerImpl03();
		
		//make the toString method
		JMethod method = handler.addToString(schema, sampleClass);
		String methodString = declareToString(method);
		
		//verify method was created correctly
		assertTrue(methodString.indexOf("java.lang.StringBuilder result;") > 0);
		assertTrue(methodString.indexOf("result = new java.lang.StringBuilder();") > 0);
		assertTrue(methodString.indexOf("result.append(\"\");") > 0);
		assertTrue(methodString.indexOf("result.append(\"ImASampleClass\");") > 0);
		assertTrue(methodString.indexOf("result.append(\" [\");") > 0);
		assertTrue(methodString.indexOf("result.append(\"imABooleanMemberVariable=\");") > 0);
		assertTrue(methodString.indexOf("result.append(imABooleanMemberVariable);") > 0);
		assertTrue(methodString.indexOf("result.append(\"imAStringMemberVariable=\");") > 0);
		assertTrue(methodString.indexOf("result.append(imAStringMemberVariable);") > 0);
		assertTrue(methodString.indexOf("result.append(\"]\");") > 0);
		assertTrue(methodString.indexOf("return result.toString();") > 0);    
	}

	/**
	 * Tests that toString works for Boolean property.
	 * @throws Exception
	 */
	@Test
	public void testToStringForStringKeyMapProperty() throws Exception {
		// Add a boolean property to the schema
		ObjectSchema strKeyMapProp = new ObjectSchema();
		strKeyMapProp.setType(TYPE.STR_KEY_MAP);
		String mapPropertyName = "mapPropName";
		schema.putProperty(mapPropertyName, strKeyMapProp);

		//add field to sampleClass JDefinedClass
		sampleClass.field(JMod.PRIVATE, sampleClass, mapPropertyName);

		//handle
		ToStringHandlerImpl03 handler = new ToStringHandlerImpl03();
		JMethod method = handler.addToString(schema, sampleClass);

		//let's see the results
		String methodString = declareToString(method);

		//verify everything was created correctly
		assertTrue(methodString.indexOf("result.append(\"mapPropName=\");") > 0);
		assertTrue(methodString.indexOf("result.append(mapPropName);") > 0);
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
