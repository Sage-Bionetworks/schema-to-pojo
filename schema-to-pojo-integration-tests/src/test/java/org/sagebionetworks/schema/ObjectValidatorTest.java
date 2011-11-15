package org.sagebionetworks.schema;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.AllTypes;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
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
		
		ObjectValidator.validateEntity(testSchema, adapter);
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
		
		ObjectValidator.validateEntity(testSchema, adapter);
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
		adapter.put("theSecondProperty", 7);
		adapter.put("theFirstProperty", 123.456);
		
		//now validate
		ObjectValidator.validateEntity(testSchema, adapter);
	}
	
	/**
	 * Tests that validator has no problems when the schema has no properties
	 */
	@Test
	public void testValidatePropertiesWhenSchemaHasNoProperties() throws Exception {
		//set up schema
		testSchema.setName("testSchemasName");
		testSchema.setDescription("this is description for test schema");
		
		//now validate
		ObjectValidator.validateEntity(testSchema, adapter);
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
		
		ObjectValidator.validateEntity(testSchema, adapter);
	}
	
	
	@Test (expected=JSONObjectAdapterException.class)
	public void testNonDeclaredValue() throws JSONObjectAdapterException{
		AllTypes at = new AllTypes();
		at.setBooleanProp(true);
		at.setLongProp(123L);
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		at.writeToJSONObject(adapter);
		// Now add an undeclared property to the object
		adapter.put("NOT DECLARED", "I should not be here");
		// This should fail validation
		ObjectValidator.validateEntity(at.getJSONSchema(), adapter, AllTypes.class);
	}
	
	@Test (expected=JSONObjectAdapterException.class)
	public void testNonDeclaredValueOnInit() throws JSONObjectAdapterException{
		AllTypes at = new AllTypes();
		at.setBooleanProp(true);
		at.setLongProp(123L);
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		at.writeToJSONObject(adapter);
		// Now add an undeclared property to the object
		adapter.put("NOT DECLARED", "I should not be here");
		// This should fail validation
		AllTypes clone = new AllTypes();
		// The init should fail.
		clone.initializeFromJSONObject(adapter);
	}
}
