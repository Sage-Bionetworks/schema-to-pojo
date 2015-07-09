package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.cglib.core.ReflectUtils;
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
		
		Map<String, Object> undefinedEntries = ObjectValidator.validateEntity(testSchema, adapter);
		assertNull(undefinedEntries);
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
		
		Map<String, Object> undefinedEntries = ObjectValidator.validateEntity(testSchema, adapter);
		assertEquals(0, undefinedEntries.size());
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
		Map<String, Object> undefinedEntries = ObjectValidator.validateEntity(testSchema, adapter);
		assertNull(undefinedEntries);
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
		Map<String, Object> undefinedEntries = ObjectValidator.validateEntity(testSchema, adapter);
		assertNull(undefinedEntries);
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
		
		Map<String, Object> undefinedEntries = ObjectValidator.validateEntity(testSchema, adapter);
		assertNull(undefinedEntries);
	}
	
	
	@Test
	public void testNonDeclaredValue() throws JSONObjectAdapterException{
		AllTypes at = new AllTypes();
		at.setBooleanProp(true);
		at.setLongProp(123L);
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		at.writeToJSONObject(adapter);
		// Now add an undeclared property to the object
		adapter.put("NOT DECLARED", "I should not be here");
		// This should return the undeclared fiels
		Map<String, Object> undefinedEntries = ObjectValidator.validateEntity(at.getJSONSchema(), adapter, AllTypes.class);
		assertEquals(Collections.singletonMap("NOT DECLARED", "I should not be here"), undefinedEntries);
	}
	
	@Test
	public void testNonDeclaredValueOnInit() throws Exception {
		AllTypes at = new AllTypes();
		at.setBooleanProp(true);
		at.setLongProp(123L);
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		at.writeToJSONObject(adapter);
		// Now add an undeclared property to the object
		adapter.put("NOT DECLARED", "I should not be here");
		// This should fail validation
		AllTypes clone = new AllTypes();
		// The init should succeed and the extra param should be kept in the extra field map
		clone.initializeFromJSONObject(adapter);
		Field field = clone.getClass().getDeclaredField(ObjectSchema.EXTRA_FIELDS);
		field.setAccessible(true);
		assertEquals(Collections.singletonMap("NOT DECLARED", "I should not be here"), field.get(clone));
	}
	
	/**
	 * Tests that validatePatternProperties correctly verifies a property 
	 * that has a pattern defined correctly converts that pattern to a 
	 * java.regex Pattern and verifies corresponding adapter property
	 * holds a string that is a valid instance of that pattern.  
	 */
	@Test
	public void testValidatePropertyPattern() throws Exception {
		//set up schema
		testSchema.setName("imATestSchemaName");
		testSchema.setDescription("I'm the description for the test schema");
		
		//make the key/name for the property
		String propKeyName = "imAKeyForAPropertyWithAPattern";
		
		//make the pattern and the matcher that will work for that pattern
		String regexPattern = "a*b";
		String instanceOfRegexPattern = "aaaaab";
		
		//make an objectSchema that is the property that contains the pattern
		ObjectSchema patternProperty = new ObjectSchema();
		patternProperty.setPattern(regexPattern);
		patternProperty.setDescription("this property is to test pattern functionality");
		patternProperty.setType(TYPE.STRING);
		testSchema.putProperty(propKeyName, patternProperty);
		
		//make an adapter that has a string that will work schema's pattern
		JSONObjectAdapter patternAdapter = new JSONObjectAdapterImpl();
		patternAdapter.put(propKeyName, instanceOfRegexPattern);
		
		//validate
		ObjectValidator.validatePatternProperties(testSchema, patternAdapter);
	}
	
	/**
	 * Tests that validatePatternProperties works correctly when
	 * adapter holds a string that is not an instance of the schema's 
	 * pattern.  
	 */
	@Test (expected = JSONObjectAdapterException.class)
	public void testValidatePropertyPatternForBadPattern() throws Exception {
		//set up schema
		testSchema.setName("imATestSchemaName");
		testSchema.setDescription("I'm the description for the test schema");
		
		//make the key/name for the property
		String propKeyName = "imAPattern";
		
		//make the pattern and the matcher that will not work for that pattern
		String regexPattern = "a*b";
		String adaptersBadPattern = "ccc";
		
		//make an objectSchema that is the property that contains the pattern
		ObjectSchema patternProperty = new ObjectSchema();
		patternProperty.setPattern(regexPattern);
		patternProperty.setDescription("this property is to test pattern functionality");
		patternProperty.setType(TYPE.STRING);
		testSchema.putProperty(propKeyName, patternProperty);
		
		//make an adapter that has a string that will work schema's pattern
		JSONObjectAdapter patternAdapter = new JSONObjectAdapterImpl();
		patternAdapter.put(propKeyName, adaptersBadPattern);
		
		//validate
		ObjectValidator.validatePatternProperties(testSchema, patternAdapter);
	}
	
	/**
	 * Tests that validatePatternProperties correctly throws exception when
	 * adapter doesn't hold a corresponding property for the schema's
	 * pattern.  
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testValidatePropertyPatternForAdapterWithNoPattern() throws Exception {
		//set up schema
		testSchema.setName("imATestSchemaName");
		testSchema.setDescription("I'm the description for the test schema");
		
		//make the key/name for the property
		String propKeyName = "imAPattern";
		
		//make the pattern and the matcher that will work for that pattern
		String regexPattern = "a*b";
		
		//make an objectSchema that is the property that contains the pattern
		ObjectSchema patternProperty = new ObjectSchema();
		patternProperty.setPattern(regexPattern);
		patternProperty.setDescription("this property is to test pattern functionality");
		patternProperty.setType(TYPE.STRING);
		testSchema.putProperty(propKeyName, patternProperty);
		
		//make an adapter that has a string that will work schema's pattern
		JSONObjectAdapter adapterWNoPattern = new JSONObjectAdapterImpl();
		adapterWNoPattern.put("boolean", true);
		
		//validate
		ObjectValidator.validatePatternProperties(testSchema, adapterWNoPattern);
	}
	
	/**
	 * Tests that validatePatternProperties correctly throws exception when
	 * schema holds a pattern that can't be converted into a java regex
	 * pattern.  
	 */
	@Test (expected=JSONObjectAdapterException.class)
	public void testValidatePropertyPatternForSchemaWithInvalidPattern() throws Exception {
		//set up schema
		testSchema.setName("imATestSchemaName");
		testSchema.setDescription("I'm the description for the test schema");
		
		//make the key/name for the property
		String propKeyName = "imAPattern";
		
		//make the pattern and the matcher that will work for that pattern
		String notRegexPattern = "'''''''''''''";
		String instanceOfRegexPattern = "aaaaab";
		
		//make an objectSchema that is the property that contains the pattern
		ObjectSchema patternProperty = new ObjectSchema();
		patternProperty.setPattern(notRegexPattern);
		patternProperty.setDescription("this property is to test pattern functionality");
		patternProperty.setType(TYPE.STRING);
		testSchema.putProperty(propKeyName, patternProperty);
		
		//make an adapter that has a string that will work schema's pattern
		JSONObjectAdapter patternAdapter = new JSONObjectAdapterImpl();
		patternAdapter.put(propKeyName, instanceOfRegexPattern);
		
		//validate
		ObjectValidator.validatePatternProperties(testSchema, patternAdapter);
	}
}
