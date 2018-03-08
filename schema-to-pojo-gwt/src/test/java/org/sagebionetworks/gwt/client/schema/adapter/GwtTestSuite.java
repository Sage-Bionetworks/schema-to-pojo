package org.sagebionetworks.gwt.client.schema.adapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sagebionetworks.schema.ExtraFields;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.adapter.AdapterCollectionUtils;
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
	
	JSONArrayAdapter adapterArray = null;
	JSONObjectAdapter adapterObject = null;
	int index = 0;
	String propertyKey; 
	
	@Override
	public void gwtSetUp() {
		// This is a test for the JSONObjectAdapterImpl
		adapterArray = new JSONArrayGwt();
		adapterObject = new JSONObjectGwt();
		index = 0;
		propertyKey = "propKey";
	}
	
	@Test
	public void testCreatePropertyCannotBeNullMessage() {
		String result = ObjectSchema.createPropertyCannotBeNullMessage("propertyName");
		assertEquals("Property: 'propertyName' is required and cannot be null", result);
	}
	
	@Test
	public void testLongRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		long value = 123;
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertEquals(value, adapterArray.getLong(index));
	}

	@Test
	public void testStringRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		String value = "I am a tea pot";
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertEquals(value, adapterArray.getString(index));
		// Make sure we can also get it as an object
		assertEquals(value, adapterArray.get(index));
	}
	
	@Test
	public void testNullRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapterArray.length());
		long value = 123;
		adapterArray.putNull(index);
		assertEquals(1, adapterArray.length());
		assertTrue(adapterArray.isNull(index));
		assertEquals(null, adapterArray.get(index));
	}

	@Test
	public void testDoubleRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		double value = 34.3;
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertTrue(doubleCompare(value, adapterArray.getDouble(index)));
		// Make sure we can also get it as an object
		assertEquals(value, adapterArray.get(index));
	}
	
	@Test
	public void testDoubleNaNRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		double value = Double.NaN;
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertEquals(value, adapterArray.getDouble(index));
		// Make sure we can also get it as an object
		assertEquals(value, Double.parseDouble((String)adapterArray.get(index)));
	}
	
	@Test
	public void testDoubleInfinityRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		double value = Double.POSITIVE_INFINITY;
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertEquals(value, adapterArray.getDouble(index));
		// Make sure we can also get it as an object
		assertEquals(value, Double.parseDouble((String)adapterArray.get(index)));
	}
	
	@Test
	public void testDoubleNegativeInfRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		double value = Double.NEGATIVE_INFINITY;
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertEquals(value, adapterArray.getDouble(index));
		// Make sure we can also get it as an object
		assertEquals(value, Double.parseDouble((String)adapterArray.get(index)));
	}
	
	private boolean doubleCompare(double a, double b){
		return Math.abs(a-b) < 0.00000000001d;
	}

	@Test
	public void testBooleanRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		boolean value = true;
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertEquals(value, adapterArray.getBoolean(index));
		// Make sure we can also get it as an object
		assertEquals(value, adapterArray.get(index));
	}

	@Test
	public void testIntRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		int value = 34;
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertEquals(value, adapterArray.getInt(index));
		// Make sure we can also get it as an object
		assertNotNull(adapterArray.get(index));
	}

	@Test
	public void testJSONObjectRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		JSONObjectAdapter value = adapterArray.createNew();
		value.put("keyone", 123);
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertNotNull(adapterArray.getJSONObject(index));
		System.out.print(adapterArray.toJSONString());
		assertEquals(value.toJSONString(), adapterArray.getJSONObject(index)
				.toJSONString());
	}

	@Test
	public void testJSONArrayRoundTrip() throws JSONObjectAdapterException {
		// Start off at zero
		assertEquals(0, adapterArray.length());
		JSONArrayAdapter value = adapterArray.createNewArray();
		value.put(0, 123);
		adapterArray.put(index, value);
		assertEquals(1, adapterArray.length());
		assertNotNull(adapterArray.getJSONArray(index));
		System.out.print(adapterArray.toJSONString());
		assertEquals(value.toJSONString(), adapterArray.getJSONArray(index)
				.toJSONString());
	}
	
	@Test
	public void testIsNullString() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapterArray.isNull(index));
		adapterArray.put(index, "someValue");
		assertFalse(adapterArray.isNull(index));
		adapterArray.put(index, (String)null);
		assertTrue(adapterArray.isNull(index));
	}
	
	@Test
	public void testIsNullObject() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapterArray.isNull(index));
		adapterArray.put(index, adapterArray.createNew());
		assertFalse(adapterArray.isNull(index));
		adapterArray.put(index, (JSONObjectAdapter)null);
		assertTrue(adapterArray.isNull(index));
	}
	
	@Test
	public void testIsNullArray() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapterArray.isNull(index));
		adapterArray.put(index, adapterArray.createNewArray());
		assertFalse(adapterArray.isNull(index));
		adapterArray.put(index, (JSONArrayAdapter)null);
		assertTrue(adapterArray.isNull(index));
	}

	@Test
	public void testToString() throws JSONObjectAdapterException {
		adapterArray.put(0, 123);
		adapterArray.put(1, 34.5);
		adapterArray.put(2, "I am a great string!");
		JSONObjectAdapter object = adapterArray.createNew();
		object.put("childKeyOne", true);
		adapterArray.put(3, object);
		System.out.println(adapterArray.toJSONString());
		assertEquals(adapterArray.toJSONString(), adapterArray.toString());
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
		// Make sure we can also get it as an object
		assertEquals(value, adapterObject.get(propertyKey));
	}
	
	@Test
	public void testObjectNullRoundTrip() throws JSONObjectAdapterException{
		adapterObject.putNull(propertyKey);
		assertTrue(adapterObject.has(propertyKey));
		assertTrue(adapterObject.isNull(propertyKey));
		assertEquals(null, adapterObject.get(propertyKey));
	}
	
	@Test
	public void testObjectDoubleRoundTrip() throws JSONObjectAdapterException{
		double value = 12.5565;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertTrue(CompareUtils.doubleEquals(value, adapterObject.getDouble(propertyKey)));
		// Make sure we can also get it as an object
		assertEquals(value, adapterObject.get(propertyKey));
	}
	
	@Test
	public void testObjectDoubleNaNRoundTrip() throws JSONObjectAdapterException{
		double value = Double.NaN;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(value, adapterObject.getDouble(propertyKey));
		// Make sure we can also get it as an object
		assertEquals(value, adapterObject.get(propertyKey));
	}
	
	@Test
	public void testObjectDoubleInfinityRoundTrip() throws JSONObjectAdapterException{
		double value = Double.POSITIVE_INFINITY;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(value, adapterObject.getDouble(propertyKey));
		// Make sure we can also get it as an object
		assertEquals(value, adapterObject.get(propertyKey));
	}
	
	@Test
	public void testObjectDoubleNegativeInfRoundTrip() throws JSONObjectAdapterException{
		double value = Double.NEGATIVE_INFINITY;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(value, adapterObject.getDouble(propertyKey));
		// Make sure we can also get it as an object
		assertEquals(value, adapterObject.get(propertyKey));
	}
	
	@Test
	public void testObjectBooleanRoundTrip() throws JSONObjectAdapterException{
		boolean value = true;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(value, adapterObject.getBoolean(propertyKey));
		// Make sure we can also get it as an object
		assertEquals(value, adapterObject.get(propertyKey));
	}
	
	@Test
	public void testObjectIntRoundTrip() throws JSONObjectAdapterException{
		int value = 1232334;
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(value, adapterObject.getInt(propertyKey));
		// Make sure we can also get it as an object
		assertNotNull(adapterObject.get(propertyKey));
	}
	
	@Test
	public void testObjectArrayRoundTrip() throws JSONObjectAdapterException{
		JSONArrayAdapter array = adapterObject.createNewArray();
		array.put(0, Boolean.TRUE);
		adapterObject.put(propertyKey, array);
		assertTrue(adapterObject.has(propertyKey));
		assertEquals(array.toJSONString(), adapterObject.getJSONArray(propertyKey).toJSONString());
		// Make sure we can also get it as an object
		assertEquals(array.toJSONString(), adapterObject.get(propertyKey).toString());
	}
	
	@Test
	public void testObjectJSONObjectAdapterRoundTrip() throws JSONObjectAdapterException{
		JSONObjectAdapter value = adapterObject.createNew();
		assertNotNull(value);
		value.put("someValue", 123);
		assertNotNull(value);
		adapterObject.put(propertyKey, value);
		assertTrue(adapterObject.has(propertyKey));
		assertNotNull(adapterObject.getJSONObject(propertyKey));
		assertEquals(value.toJSONString(), adapterObject.getJSONObject(propertyKey).toJSONString());
		// Make sure we can also get it as an object
		assertEquals(value.toJSONString(), adapterObject.get(propertyKey).toString());
	}
	
	@Test
	public void testObjectJSONArrayAdapterRoundTrip() throws JSONObjectAdapterException{
		JSONArrayAdapter array = adapterArray.createNewArray();
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
		
		adapterObject.put(propertyKey, adapterArray.createNew());
		assertFalse(adapterObject.isNull(propertyKey));
		adapterObject.put(propertyKey, (JSONObjectAdapter)null);
		assertTrue(adapterObject.isNull(propertyKey));
	}
	
	@Test
	public void testObjectIsNullArray() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapterObject.isNull(propertyKey));
		adapterObject.put(propertyKey, adapterArray.createNewArray());
		assertFalse(adapterObject.isNull(propertyKey));
		adapterObject.put(propertyKey, (JSONArrayAdapter)null);
		assertTrue(adapterObject.isNull(propertyKey));
	}
	
	@Test
	public void testObjectToString() throws JSONObjectAdapterException{
		adapterObject.put("longKey", 123);
		adapterObject.put("doubleKey", 34.5);
		adapterObject.put("stringKey", "I am a great string!");
		JSONObjectAdapter object = adapterArray.createNew();
		object.put("childKeyOne", true);
		adapterObject.put("objecKey", object);
		System.out.println(adapterArray.toJSONString());
		assertEquals(adapterArray.toJSONString(), adapterArray.toString());
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
	
	@Test
	public void testExtraFieldsForGWT() throws JSONObjectAdapterException {
		
		adapterObject.put("keyOne", "valueOne");
		adapterObject.put("keyTwo", "valueTwo");
		adapterObject.put("extraKey", "extraValue");
	
		Map<String,Object> extraFields = ExtraFields.createExtraFieldsMap(adapterObject, "keyOne", "keyTwo");
		assertNotNull(extraFields);
		assertEquals(1, extraFields.size());
		assertEquals("extraValue", extraFields.get("extraKey"));
	}
	
	@Test
	public void testExtraFieldsForGWTArray() throws JSONObjectAdapterException {
		adapterObject.put("keyOne", "valueOne");
		adapterObject.put("keyTwo", "valueTwo");
		adapterArray.put(1, "extraValue");
		adapterObject.put("extraKey", adapterArray);
	
		Map<String,Object> extraFields = ExtraFields.createExtraFieldsMap(adapterObject, "keyOne", "keyTwo");
		assertNotNull(extraFields);
		assertEquals(1, extraFields.size());
		assertEquals(adapterArray, extraFields.get("extraKey"));
	}
	
	@Test
	public void testAdapterCollectionUtilsMapOfStringCollectionRoundTrip() throws JSONObjectAdapterException{
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> one = new ArrayList<String>();
		one.add("a");
		one.add("b");
		map.put("one", one);
		map.put("two", null);
		// Writ this to an adapter
		adapterObject = new JSONObjectGwt();
		AdapterCollectionUtils.writeToAdapter(adapterObject, map, String.class);
		System.out.println(adapterObject.toJSONString());
		// Now make sure we can come back
		Map<String, List<String>> clone = AdapterCollectionUtils.createMapOfCollection(adapterObject, String.class);
		assertEquals(map, clone);
	}
	
	@Test
	public void testAdapterCollectionUtilsMapOfDoubleCollectionRoundTrip() throws JSONObjectAdapterException{
		Map<String, List<Double>> map = new HashMap<String, List<Double>>();
		List<Double> one = new ArrayList<Double>();
		one.add(new Double(123));
		one.add(new Double(345.5));
		map.put("one", one);
		map.put("two", null);
		// Writ this to an adapter
		adapterObject = new JSONObjectGwt();
		AdapterCollectionUtils.writeToAdapter(adapterObject, map, Double.class);
		System.out.println(adapterObject.toJSONString());
		// Now make sure we can come back
		Map<String, List<Double>> clone = AdapterCollectionUtils.createMapOfCollection(adapterObject, Double.class);
		assertEquals(map, clone);
	}
	
	@Test
	public void testAdapterCollectionUtilsMapOfLongCollectionRoundTrip() throws JSONObjectAdapterException{
		Map<String, List<Long>> map = new HashMap<String, List<Long>>();
		List<Long> one = new ArrayList<Long>();
		one.add(new Long(123));
		one.add(new Long(345));
		map.put("one", one);
		map.put("two", null);
		// Writ this to an adapter
		adapterObject = new JSONObjectGwt();
		AdapterCollectionUtils.writeToAdapter(adapterObject, map, Long.class);
		System.out.println(adapterObject.toJSONString());
		// Now make sure we can come back
		Map<String, List<Long>> clone = AdapterCollectionUtils.createMapOfCollection(adapterObject, Long.class);
		assertEquals(map, clone);
	}
	
	@Test
	public void testAdapterCollectionUtilsMapOfDateCollectionRoundTrip() throws JSONObjectAdapterException{
		Map<String, List<Date>> map = new HashMap<String, List<Date>>();
		List<Date> one = new ArrayList<Date>();
		one.add(new Date(System.currentTimeMillis()));
		one.add(new Date(345*1000));
		map.put("one", one);
		map.put("two", null);
		// Writ this to an adapter
		adapterObject = new JSONObjectGwt();
		AdapterCollectionUtils.writeToAdapter(adapterObject, map, Date.class);
		System.out.println(adapterObject.toJSONString());
		// Now make sure we can come back
		Map<String, List<Date>> clone = AdapterCollectionUtils.createMapOfCollection(adapterObject, Date.class);
		assertEquals(map, clone);
	}
	
	@Test
	public void testAdapterCollectionUtilsMapOfBinaryCollectionRoundTrip() throws JSONObjectAdapterException, UnsupportedEncodingException{
		Map<String, List<byte[]>> map = new HashMap<String, List<byte[]>>();
		List<byte[]> one = new ArrayList<byte[]>();
		one.add("First blob".getBytes("UTF-8"));
		one.add("Second blob".getBytes("UTF-8"));
		map.put("one", one);
		map.put("two", null);
		// Writ this to an adapter
		adapterObject = new JSONObjectGwt();
		AdapterCollectionUtils.writeToAdapter(adapterObject, map, byte[].class);
		System.out.println(adapterObject.toJSONString());
		// Now make sure we can come back
		Map<String, List<byte[]>> clone = AdapterCollectionUtils.createMapOfCollection(adapterObject, byte[].class);
		assertEquals(2, clone.size());
		assertNull(clone.get("twp"));
		List<byte[]> list = clone.get("one");
		assertNotNull(list);
		assertEquals(2, list.size());
		assertEquals("First blob", new String(list.get(0), "UTF-8"));
		assertEquals("Second blob", new String(list.get(1), "UTF-8"));
	}
	
	@Test
	public void testDateRoundTrip() throws JSONObjectAdapterException{
		Date dateValue = new Date(System.currentTimeMillis());
		adapterObject.put("key", dateValue);
		System.out.println(adapterArray.toJSONString());
		Date clone = adapterObject.getDate("key");
		assertEquals(dateValue, clone);
	}
	
	@Test 
	public void testDateNull() throws JSONObjectAdapterException{
		try{
			Date dateValue = new Date(System.currentTimeMillis());
			adapterObject.put(null, dateValue);
			fail("Should have thrown an exception");
		}catch(IllegalArgumentException e){
			//expected
		}

	}
	
	@Test
	public void testDateNullValue() throws JSONObjectAdapterException{
		try{
			Date value = adapterObject.getDate("key");
			fail("Should have thrown an exception");
		}catch(JSONObjectAdapterException e){
			// expected
			
		}

	}
	
	@Test
	public void testDateRoundTripArray() throws JSONObjectAdapterException{
		Date dateValue = new Date(System.currentTimeMillis());
		adapterArray.put(0, dateValue);
		System.out.println(adapterArray.toJSONString());
		Date clone = adapterArray.getDate(0);
		assertEquals(dateValue, clone);
	}
	
	@Test
	public void testDateNullArray() throws JSONObjectAdapterException{
		try{
			adapterArray.put(0, (Date)null);
			fail("Should have thrown an exception");
		}catch(IllegalArgumentException e){
			// expected
		}

	}
	
	@Test
	public void testDateNullValueArray() throws JSONObjectAdapterException{
		try{
			Date value = adapterArray.getDate(0);
			fail("Should have thrown an exception");
		}catch(JSONObjectAdapterException e){
			// expected
		}
	}
	
	@Test
	public void binaryRoundTrip() throws JSONObjectAdapterException, UnsupportedEncodingException {
		// Make sure we can use base 64
		String startString = "This string will be encoded";
		byte[] value = startString.getBytes("UTF-8");
		adapterObject.put("binary", value);
		// Get the value out
		byte[] cloneArray = adapterObject.getBinary("binary");
		String clone = new String(cloneArray, "UTF-8");
		assertEquals(startString, clone);
	}
	
	@Test
	public void binaryRoundTripArray() throws JSONObjectAdapterException, UnsupportedEncodingException {
		// Make sure we can use base 64
		String startString = "This string will be encoded";
		byte[] value = startString.getBytes("UTF-8");
		adapterArray.put(0, value);
		// Get the value out
		byte[] cloneArray = adapterArray.getBinary(0);
		String clone = new String(cloneArray, "UTF-8");
		assertEquals(startString, clone);
	}
	
	@Test
	public void testDateUtilsDateTime(){
		// Make sure we can do a round trip for each date type
		Date now = new Date(System.currentTimeMillis());
		String dateString = DateUtils.convertDateToString(FORMAT.DATE_TIME, now);
		Date clone = DateUtils.convertStringToDate(FORMAT.DATE_TIME, dateString);
		assertEquals(now, clone);
	}
	
	@Test
	public void testDateUtilsUTC(){
		// Make sure we can do a round trip for each date type
		Date now = new Date(System.currentTimeMillis());
		String dateString = DateUtils.convertDateToString(FORMAT.UTC_MILLISEC, now);
		Date clone = DateUtils.convertStringToDate(FORMAT.UTC_MILLISEC, dateString);
		assertEquals(now, clone);
	}
}
