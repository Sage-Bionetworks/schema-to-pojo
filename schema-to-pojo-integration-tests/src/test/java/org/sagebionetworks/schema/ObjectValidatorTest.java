package org.sagebionetworks.schema;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Map;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONArrayAdapterImpl;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class ObjectValidatorTest {
	ObjectSchema testSchema;
	JSONObjectAdapter adapter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		testSchema = new ObjectSchema();
		adapter = new JSONObjectAdapterImpl();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Tests that validateObject correctly checks that adapter has
	 * all properties marked as "required" in schema.  Test should
	 * pass as adapter does have all required properties.
	 * @throws Exception
	 */
	@Test
	public void testAdapterHasAllRequiredProperties() throws Exception {
		//set up schema
		testSchema.setName("testSchemasName");
		testSchema.setDescription("this is description for test schema");
		
		ObjectSchema nextProp = new ObjectSchema();
		//make first required  property and add
		nextProp.setType(TYPE.NUMBER);
		nextProp.setName("firstProperty");
		nextProp.setRequired(true);
		testSchema.putProperty("theFirstProperty", nextProp);
		
		//make second unrequired property and add
		nextProp = new ObjectSchema();
		nextProp.setName("secondProperty");		
		nextProp.setType(TYPE.INTEGER);
		nextProp.setRequired(false);		
		testSchema.putProperty("theSecondProperty", nextProp);
		
		//make a third required property
		nextProp = new ObjectSchema();
		nextProp.setName("thirdProperty");
		nextProp.setType(TYPE.STRING);
		nextProp.setRequired(true);
		testSchema.putProperty("theThirdProperty", nextProp);
		
		//set up adapter to have all valid properties
		adapter.put("theFirstProperty", 123.456);
		adapter.put("theSecondProperty", 7);
		adapter.put("theThirdProperty", "hello");
		
		ObjectValidator.validateObject(adapter, testSchema);
	}
	
	/**
	 * Verifies validateObject correctly throws exception when adapter
	 * is missing a property the schema has marked as required.
	 * @throws Exception
	 */
	@Test (expected = JSONObjectAdapterException.class)
	public void testAdapterIsMissingARequiredProperty() throws Exception {
		//set up schema
		testSchema.setName("testSchemasName");
		testSchema.setDescription("this is description for test schema");
		
		//add first required property
		ObjectSchema nextProp = new ObjectSchema();
		nextProp.setType(TYPE.NUMBER);
		nextProp.setName("firstProperty");
		nextProp.setRequired(true);
		testSchema.putProperty("theFirstProperty", nextProp);
		
		//add second required property
		nextProp = new ObjectSchema();
		nextProp.setType(TYPE.INTEGER);
		nextProp.setRequired(true);
		testSchema.putProperty("theSecondProperty", nextProp);
		
		//now add a third required property to schema that adapter will not have
		nextProp = new ObjectSchema();
		nextProp.setType(TYPE.STRING);
		nextProp.setRequired(true);
		testSchema.putProperty("theThirdProperty", nextProp);
		
		//now set up adapter to match testSchema
		adapter.put("theFristProperty", 123.456);
		adapter.put("theSecondProperty", 7);		
		
		ObjectValidator.validateObject(adapter, testSchema);
	}
	
	/**
	 * Validates that the order of the adapter's properties does not have to 
	 * be the same as the order of properties in schema.  As long as all
	 * required properties are present, validator will pass.
	 */
	@Test
	public void testAdapterHasDifferentlyOrderedPropsThanSchema() throws Exception {
		//set up schema
		testSchema.setName("testSchemasName");
		testSchema.setDescription("this is description for test schema");
		
		//add first required property
		ObjectSchema nextProp = new ObjectSchema();
		nextProp.setType(TYPE.NUMBER);
		nextProp.setName("firstProperty");
		nextProp.setRequired(true);
		testSchema.putProperty("theFirstProperty", nextProp);
		
		//add second required property
		nextProp = new ObjectSchema();
		nextProp.setType(TYPE.INTEGER);
		nextProp.setRequired(true);
		testSchema.putProperty("theSecondProperty", nextProp);
		
		//set up adapter
		adapter.put("name", "testSchemaName");
		adapter.put("description", "this is description for test schema");
		adapter.put("theSecondProperty", 7);
		adapter.put("aRandomProperty", 45);
		adapter.put("theFirstProperty", 123.456);
		
		//now validate
		ObjectValidator.validateObject(adapter, testSchema);
	}
	
	/**
	 * Tests that validator has no problems when the schema has no properties
	 */
	@Test
	public void testValidatePropertiesWhenSchemaHasNoProperties() throws Exception {
		//set up schema
		testSchema.setName("testSchemasName");
		testSchema.setDescription("this is description for test schema");
		
		//set up adapter
		adapter.put("name", "testSchemaName");
		adapter.put("description", "this is description for test schema");
		
		//now validate
		ObjectValidator.validateObject(adapter, testSchema);
	}
	
	/**
	 * Tests that validateProperties works correctly when a schema has not set a 
	 * required to either true or false (it is null)
	 * @throws Exception
	 */
	@Test
	public void testValidPropertiesWhenSchemaHasPropertiesWithNoRequiredSet() throws Exception {
		//set up schema
		testSchema.setName("testSchemasName");
		testSchema.setDescription("this is description for test schema");
		
		//make first required  property and add
		ObjectSchema nextProp = new ObjectSchema();
		nextProp.setType(TYPE.NUMBER);
		nextProp.setName("firstProperty");
		nextProp.setRequired(true);
		testSchema.putProperty("theFirstProperty", nextProp);
		
		//make second property with no required set and add
		nextProp = new ObjectSchema();
		nextProp.setType(TYPE.INTEGER);		
		testSchema.putProperty("theSecondProperty", nextProp);
		
		//set up adapter to have all valid properties
		adapter.put("theFirstProperty", 123.456);
		adapter.put("theSecondProperty", 7);
		
		ObjectValidator.validateObject(adapter, testSchema);
	}
	
	/**
	 * Verifies that the validatePropetyType method works for valid strings.
	 * @throws Exception
	 */
	@Test
	public void testValidatePropertyTypeForString() throws Exception {
		//make a name for that property
		String propertyName = "stringProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.STRING);
		property.setRequired(true);
		
		//make an corresponding adapter
		adapter.put("stringProperty", "I am a string");
		
		//validate
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
	
	/**
	 * Verifies validatePropertyType correctly throws exception when
	 * adapter holds a null string.
	 * @throws Exception
	 */
	@Test(expected = JSONObjectAdapterException.class)
	public void testValidatePropertyTypeForNullString() throws Exception {
		//make a name for that property
		String propertyName = "stringProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.STRING);
		property.setRequired(true);
		
		//make an corresponding adapter
		String badString = null;
		adapter.put("stringProperty", badString);
		
		//validate
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
	
	/**
	 * Verifies validatePropertyType works for valid doubles/numbers.
	 * @throws Exception
	 */
	@Test
	public void testValidatePropertyTypeForNumber() throws Exception {
		//make a name for that property
		String propertyName = "numberProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.NUMBER);
		property.setRequired(true);
		
		//make an corresponding adapter
		adapter.put("numberProperty", 123.456);
		
		//validate
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
	
	/**
	 * Verifies validatePropertyType correctly throws an exception when an
	 * adapter's property is not of the type specified by schema.  In this
	 * case schema indicates a Number, but adapter has a string.
	 * @throws Exception
	 */
	@Test(expected = JSONObjectAdapterException.class)
	public void testValidatePropertyTypeForInvalidNumber() throws Exception {
		//make a name for that property
		String propertyName = "numberProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.NUMBER);
		property.setRequired(true);
		
		//make an corresponding adapter
		adapter.put("numberProperty", "I'm not a number");
		
		//validate
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
	
	/**
	 * Verifies that validatePropertyType works for integers.
	 * @throws Exception
	 */
	@Test
	public void testValidatePropertyTypeForInteger() throws Exception {
		//make a name for that property
		String propertyName = "integerProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.INTEGER);
		property.setRequired(true);
		
		//make an corresponding adapter
		adapter.put("integerProperty", 7);
		
		//validate
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
	
	/**
	 * Verifies validatePropertyType correctly throws an exception when an
	 * adapter's property is not of the type specified by schema.  In this
	 * case schema indicates a integer, but adapter has a string.
	 * @throws Exception
	 */
	@Test(expected = JSONObjectAdapterException.class)
	public void testValidatePropertyTypeForInvalidInteger() throws Exception {
		//make a name for that property
		String propertyName = "integerProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.INTEGER);
		property.setRequired(true);
		
		//make an corresponding adapter
		adapter.put("integerProperty", "I'm not a integer");
		
		//validate
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
	
	/**
	 * Verifies that validatePropertyType works for booleans.
	 * @throws Exception
	 */
	@Test
	public void testValidatePropertyTypeForBool() throws Exception {
		//make a name for that property
		String propertyName = "boolProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.BOOLEAN);
		property.setRequired(true);
		
		//make an corresponding adapter
		adapter.put("boolProperty", false);
		
		//validate
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
	
	/**
	 * Verifies validatePropertyType correctly throws an exception when an
	 * adapter's property is not of the type specified by schema.  In this
	 * case schema indicates a boolean, but adapter has a string.
	 * @throws Exception
	 */
	@Test(expected = JSONObjectAdapterException.class)
	public void testValidatePropertyTypeForInvalidBool() throws Exception {
		//make a name for that property
		String propertyName = "boolProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.BOOLEAN);
		property.setRequired(true);
		
		//make an corresponding adapter
		adapter.put("boolProperty", "I'm not a boolean");
		
		//validate
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
	
	/**
	 * Verifies that validatePropertyType works for JSONObjects.
	 * @throws Exception
	 */
	@Test
	public void testValidatePropertyTypeForJSONObjectAdapter() throws Exception {
		//make a name for that property
		String propertyName = "adapterProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.OBJECT);
		property.setRequired(true);
		
		//make an corresponding adapter
		JSONObjectAdapter objectProp = new JSONObjectAdapterImpl();
		objectProp.put("name", "property");
		adapter.put("adapterProperty", objectProp);
		//validate
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
	
	/**
	 * Verifies validatePropertyType correctly throws an exception when an
	 * adapter's property is not of the type specified by schema.  In this
	 * case schema indicates a object, but adapter has a string.
	 * @throws Exception
	 */
	@Test(expected = JSONObjectAdapterException.class)
	public void testValidatePropertyTypeForInvalidJSONobjectAdapter() throws Exception {
		//make a name for that property
		String propertyName = "adapterProperty";
		
		//make a name for that property
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.OBJECT);
		property.setRequired(true);
		
		//make an corresponding adapter that will not contain the correct type
		adapter.put("adapterProperty", 5);
		ObjectValidator.validatePropertyType(adapter, property, propertyName);
	}
}
