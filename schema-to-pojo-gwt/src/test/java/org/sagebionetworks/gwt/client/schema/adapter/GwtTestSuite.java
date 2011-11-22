package org.sagebionetworks.gwt.client.schema.adapter;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.Test;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.ObjectValidator;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.validation.ExpectedDateTime;
import org.sagebionetworks.schema.util.CompareUtils;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Since the GWT test are so slow to start and we could not get the GWTTestSuite to work,
 * we put all GWT tests in one class.
 * @author jmhill
 *
 */
public class GwtTestSuite extends GWTTestCase {

	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() { 
		return Constants.MODULE_NAME;
	}
	
	JSONArrayAdapter adapter = null;
	JSONObjectAdapter adapterObject = null;
	int index = 0;
	String propertyKey; 
	
	@Override
	public void gwtSetUp() {
		// This is a test for the JSONObjectAdapterImpl
		adapter = new JSONArrayGwt();
		adapterObject = JSONObjectGwt.createNewAdapter();
		index = 0;
		propertyKey = "propKey";
	}
	
	@Test
	public void testLongRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapter.length());
		long value = 123;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getLong(index));
	}

	@Test
	public void testStringRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapter.length());
		String value = "I am a tea pot";
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getString(index));
	}

	@Test
	public void testDoubleRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapter.length());
		double value = 34.3;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertTrue(doubleCompare(value, adapter.getDouble(index)));
	}
	
	private boolean doubleCompare(double a, double b){
		return Math.abs(a-b) < 0.00000000001d;
	}

	@Test
	public void testBooleanRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapter.length());
		boolean value = true;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getBoolean(index));
	}

	@Test
	public void testIntRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapter.length());
		int value = 34;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getInt(index));
	}

	@Test
	public void testJSONObjectRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapter.length());
		JSONObjectAdapter value = adapter.createNew();
		value.put("keyone", 123);
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertNotNull(adapter.getJSONObject(index));
		System.out.print(adapter.toJSONString());
		assertEquals(value.toJSONString(), adapter.getJSONObject(index)
				.toJSONString());
	}

	@Test
	public void testJSONArrayRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapter.length());
		JSONArrayAdapter value = adapter.createNewArray();
		value.put(0, 123);
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertNotNull(adapter.getJSONArray(index));
		System.out.print(adapter.toJSONString());
		assertEquals(value.toJSONString(), adapter.getJSONArray(index)
				.toJSONString());
	}
	
	@Test
	public void testIsNullString() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapter.isNull(index));
		adapter.put(index, "someValue");
		assertFalse(adapter.isNull(index));
		adapter.put(index, (String)null);
		assertTrue(adapter.isNull(index));
	}
	
	@Test
	public void testIsNullObject() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapter.isNull(index));
		adapter.put(index, adapter.createNew());
		assertFalse(adapter.isNull(index));
		adapter.put(index, (JSONObjectAdapter)null);
		assertTrue(adapter.isNull(index));
	}
	
	@Test
	public void testIsNullArray() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapter.isNull(index));
		adapter.put(index, adapter.createNewArray());
		assertFalse(adapter.isNull(index));
		adapter.put(index, (JSONArrayAdapter)null);
		assertTrue(adapter.isNull(index));
	}

	@Test
	public void testToString() throws JSONObjectAdapterException {
		adapter.put(0, 123);
		adapter.put(1, 34.5);
		adapter.put(2, "I am a great string!");
		JSONObjectAdapter object = adapter.createNew();
		object.put("childKeyOne", true);
		adapter.put(3, object);
		System.out.println(adapter.toJSONString());
		assertEquals(adapter.toJSONString(), adapter.toString());
	}
	
	// Make sure we can support all of the expected types
	@Test
	public void testAllDateFormatConversion(){
		// Test each format
		for(FORMAT format: FORMAT.values()){
			if(ExpectedDateTime.isSupportedFormat(format)){
				Iterator<String> it = ExpectedDateTime.getExpectedFormatedString(format);
				while(it.hasNext()){
					String formatedString = it.next();
					Date expectedDate = ExpectedDateTime.getExpectedDateForString(format, formatedString);
					// Make sure we get the same conversion.
					Date result = adapterObject.convertStringToDate(format, formatedString);
					assertNotNull("FORMAT: "+format.name()+" String: "+formatedString,result);
					assertEquals("FORMAT: "+format.name()+" String: "+formatedString,expectedDate.getTime(), result.getTime());
					// Now make sure we can get a string.
					String stringResult = adapterObject.convertDateToString(format, result);
					assertNotNull("FORMAT: "+format.name()+" String: "+formatedString, stringResult);
					// Since there are multiple ways write these strings it might not match the expected.
					// However, we should get the same date for this string.
					result = adapterObject.convertStringToDate(format, stringResult);
					assertNotNull("FORMAT: "+format.name()+" String: "+stringResult, result);
					assertEquals("FORMAT: "+format.name()+" String: "+stringResult, expectedDate.getTime(), result.getTime());
				}
			}
		}
	}
	
	@Test
	public void testConvertToDate(){
		// Convert this date to a string
		Date toConvert = new Date();
		String dateString = adapterObject.convertDateToString(FORMAT.DATE_TIME, toConvert);
		assertNotNull(dateString);
		System.out.println(toConvert.getTime());
		System.out.println(dateString);
		// Now convert it back to a date
		Date convertedDate = adapterObject.convertStringToDate(FORMAT.DATE_TIME, dateString);
		assertNotNull(convertedDate);
		assertEquals(toConvert.getTime(), convertedDate.getTime());
	}
	
	@Test
	public void testObjectLongRoundTrip() throws JSONObjectAdapterException{
		long value = Integer.MAX_VALUE -1;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(value, adapterObject.getLong(propertyKey));
	}

	@Test
	public void testObjectLongTooLarge() throws JSONObjectAdapterException{
		// This value will be too large since GWT will store it as a double.
		long value = Long.MAX_VALUE -1;
		try{
			adapterObject.put(propertyKey, value);
			fail();
		}catch(JSONObjectAdapterException e){
			// This is expected since the value is too large
		}
	}
	
	@Test
	public void testObjectStringRoundTrip() throws JSONObjectAdapterException{
		String value = "some string";
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(value, adapterObject.getString(propertyKey));
	}
	
	@Test
	public void testObjectDoubleRoundTrip() throws JSONObjectAdapterException{
		double value = 12.5565;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertTrue(CompareUtils.doubleEquals(value, adapterObject.getDouble(propertyKey)));
	}
	
	@Test
	public void testObjectBooleanRoundTrip() throws JSONObjectAdapterException{
		boolean value = true;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(value, adapterObject.getBoolean(propertyKey));
	}
	
	@Test
	public void testObjectIntRoundTrip() throws JSONObjectAdapterException{
		int value = 1232334;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(value, adapterObject.getInt(propertyKey));
	}
	
	@Test
	public void testObjectJSONObjectAdapterRoundTrip() throws JSONObjectAdapterException{
		JSONObjectAdapter value = adapter.createNew();
		assertNotNull(value);
		value.put("someValue", 123);
		assertNotNull(value);
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertNotNull(adapterObject.getJSONObject(propertyKey));
		assertEquals(value.toJSONString(), adapterObject.getJSONObject(propertyKey).toJSONString());
	}
	
	@Test
	public void testObjectJSONArrayAdapterRoundTrip() throws JSONObjectAdapterException{
		JSONArrayAdapter array = adapter.createNewArray();
		// Add one object to the array
		array.put(0, 123);
		array.put(1, 345);
		assertEquals(2, array.length());
		// add the array to the value
		adapterObject.put(propertyKey, array);
		assertTrue(adapterObject.has(propertyKey));
		assertNotNull(adapterObject.getJSONArray(propertyKey));
		System.out.println(adapterObject.getJSONArray(propertyKey).toJSONString());
		assertEquals(array.toJSONString(), adapterObject.getJSONArray(propertyKey).toJSONString());
	}
	
	@Test
	public void testIterator() throws JSONObjectAdapterException{
		// add the array to the value
		HashSet<String> expecteKeys = new HashSet<String>(Arrays.asList(new String[]{"longKey", "doubleKey", "stringKey"}));
		adapterObject.put("longKey", 123);
		adapterObject.put("doubleKey", 34.5);
		adapterObject.put("stringKey", "I am a great string!");
		Iterator<String> it = adapterObject.keys();
		assertNotNull(it);
		while(it.hasNext()){
			String key = it.next();
			assertTrue(expecteKeys.contains(key));
			expecteKeys.remove(key);
		}
		assertEquals("All keys were not matched and removed", 0, expecteKeys.size());
		
	}
	
	@Test
	public void testObjectIsNullString() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapterObject.isNull(propertyKey));
		adapterObject.put(propertyKey, "someValue");
		assertFalse(adapterObject.isNull(propertyKey));
		adapterObject.put(propertyKey, (String)null);
		assertTrue(adapterObject.isNull(propertyKey));
	}
	
	@Test
	public void testObjectIsNullObject() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapterObject.isNull(propertyKey));
		
		adapterObject.put(propertyKey, adapter.createNew());
		assertFalse(adapterObject.isNull(propertyKey));
		adapterObject.put(propertyKey, (JSONObjectAdapter)null);
		assertTrue(adapterObject.isNull(propertyKey));
	}
	
	@Test
	public void testObjectIsNullArray() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapterObject.isNull(propertyKey));
		adapterObject.put(propertyKey, adapter.createNewArray());
		assertFalse(adapterObject.isNull(propertyKey));
		adapterObject.put(propertyKey, (JSONArrayAdapter)null);
		assertTrue(adapterObject.isNull(propertyKey));
	}
	
	@Test
	public void testObjectToString() throws JSONObjectAdapterException{
		adapterObject.put("longKey", 123);
		adapterObject.put("doubleKey", 34.5);
		adapterObject.put("stringKey", "I am a great string!");
		JSONObjectAdapter object = adapter.createNew();
		object.put("childKeyOne", true);
		adapterObject.put("objecKey", object);
		System.out.println(adapter.toJSONString());
		assertEquals(adapter.toJSONString(), adapter.toString());
	}
	
	@Test
	public void testCreateNewJSON() throws JSONObjectAdapterException{
		// Create a new object using JSON
		adapterObject.put("longKey", 123);
		adapterObject.put("doubleKey", 34.5);
		adapterObject.put("stringKey", "I am a great string!");
		String json = adapterObject.toJSONString();
		System.out.println(json);
		// Create a new Adapter with the JSON string
		JSONObjectAdapter adapter = adapterObject.createNew(json);
		assertNotNull(adapter);
		String cloneJson = adapter.toJSONString();
		assertEquals(json, cloneJson);
		JSONObjectAdapter clone = adapterObject.createNew(json);
		assertNotNull(clone);
		assertEquals(123, clone.getLong("longKey"));
		assertEquals(34.5, clone.getDouble("doubleKey"));
		assertEquals("I am a great string!", clone.getString("stringKey"));
	}
	
	/**
	 * Tests that validatePatternProperty works for a valid pattern
	 * and valid string property.
	 * @throws Exception
	 */
	@Test
	public void testValidatePatternProperty() throws Exception {
		String pattern = "a*b";
		String property = "aaaab";
		assertTrue(adapterObject.validatePatternProperty(pattern, property));
	}
	
	/**
	 * Tests that validatePatternProperty correctly returns false when 
	 * an property has an invalid letter before the valid string.
	 */
	@Test
	public void testValidatePatternPropertyForInvalidFirstLetterProperty() throws Exception {
		String pattern = "a*b";
		String badProperty = "caaaaaaaaab";
		assertFalse(adapterObject.validatePatternProperty(pattern, badProperty));
	}
	
	/**
	 * Tests that validatePatternProperty correctly returns false when
	 * an property has invalid letters after the valid string.
	 */
	@Test
	public void testValidatePatternPropertyForInvalidLettersAfterProperty() throws Exception {
		String pattern = "a*b";
		String badProperty = "aabc";
		assertFalse(adapterObject.validatePatternProperty(pattern, badProperty));
	}
	
	/**
	 * Tests that validatePatternProperty correctly returns false when
	 * an property has no matches for the pattern.
	 */
	@Test
	public void testValidatePatternPropertyForTotallyInvalidProperty() throws Exception {
		String pattern = "a*b";
		String totallyBadProperty = "cccc";
		assertFalse(adapterObject.validatePatternProperty(pattern, totallyBadProperty));
	}
	
	/**
	 * Tests that ObjectValidator works for GWT.
	 */
	@Test
	public void testObjectValidatorForGWT() throws Exception {
		ObjectSchema testSchema = new ObjectSchema();
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
		adapterObject.put(propKeyName, instanceOfRegexPattern);
		
		//validate
		ObjectValidator.validatePatternProperties(testSchema, adapterObject);
	}
}
