package org.sagebionetworks.schema.adapter.org.json;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.validation.ExpectedDateTime;

public class JSONObjectAdapterImplTest {
	
	JSONObjectAdapter adapter = null;
	String propertyKey = null;
	
	@Before
	public void before(){
		// This is a test for the JSONObjectAdapterImpl
		adapter = new JSONObjectAdapterImpl();
		propertyKey = "propKey";
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
					Date result = adapter.convertStringToDate(format, formatedString);
					assertNotNull("FORMAT: "+format.name()+" String: "+formatedString,result);
					assertEquals(expectedDate.getTime(), result.getTime());
					// Now make sure we can get a string.
					String stringResult = adapter.convertDateToString(format, result);
					assertNotNull("FORMAT: "+format.name()+" String: "+formatedString, stringResult);
					// Since there are multiple ways write these strings it might not match the expected.
					// However, we should get the same date for this string.
					result = adapter.convertStringToDate(format, stringResult);
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
		String dateString = adapter.convertDateToString(FORMAT.DATE_TIME, toConvert);
		assertNotNull(dateString);
		System.out.println(dateString);
		// Now convert it back to a date
		Date convertedDate = adapter.convertStringToDate(FORMAT.DATE_TIME, dateString);
		assertNotNull(convertedDate);
		assertEquals(toConvert.getTime(), convertedDate.getTime());
	}
	
	@Test
	public void testLongRoundTrip() throws JSONObjectAdapterException{
		long value = 123;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertEquals(value, adapter.getLong(propertyKey));
	}
	
	@Test
	public void testStringRoundTrip() throws JSONObjectAdapterException{
		String value = "some string";
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertEquals(value, adapter.getString(propertyKey));
	}
	
	@Test
	public void testDoubleRoundTrip() throws JSONObjectAdapterException{
		double value = 12.5565;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertEquals(Double.doubleToLongBits(value), Double.doubleToLongBits(adapter.getDouble(propertyKey)));
	}
	
	@Test
	public void testBooleanRoundTrip() throws JSONObjectAdapterException{
		boolean value = true;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertEquals(value, adapter.getBoolean(propertyKey));
	}
	
	@Test
	public void testIntRoundTrip() throws JSONObjectAdapterException{
		int value = 1232334;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertEquals(value, adapter.getInt(propertyKey));
	}
	
	@Test
	public void testJSONObjectAdapterRoundTrip() throws JSONObjectAdapterException{
		JSONObjectAdapter value = adapter.createNew();
		value.put("someValue", 123);
		assertNotNull(value);
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertNotNull(adapter.getJSONObject(propertyKey));
		assertEquals(value.toJSONString(), adapter.getJSONObject(propertyKey).toJSONString());
	}
	
	@Test
	public void testJSONArrayAdapterRoundTrip() throws JSONObjectAdapterException{
		JSONArrayAdapter array = adapter.createNewArray();
		// Add one object to the array
		array.put(0, 123);
		array.put(1, 345);
		assertEquals(2, array.length());
		// add the array to the value
		adapter.put(propertyKey, array);
		assertTrue(adapter.has(propertyKey));
		assertNotNull(adapter.getJSONArray(propertyKey));
		System.out.println(adapter.getJSONArray(propertyKey).toJSONString());
		assertEquals(array.toJSONString(), adapter.getJSONArray(propertyKey).toJSONString());
	}
	
	@Test
	public void testIterator() throws JSONObjectAdapterException{
		// add the array to the value
		HashSet<String> expecteKeys = new HashSet<String>(Arrays.asList(new String[]{"longKey", "doubleKey", "stringKey"}));
		adapter.put("longKey", 123);
		adapter.put("doubleKey", 34.5);
		adapter.put("stringKey", "I am a great string!");
		Iterator<String> it = adapter.keys();
		assertNotNull(it);
		while(it.hasNext()){
			String key = it.next();
			assertTrue(expecteKeys.contains(key));
			expecteKeys.remove(key);
		}
		assertEquals("All keys were not matched and removed", 0, expecteKeys.size());
		
	}
	
	@Test
	public void testIsNull() throws JSONObjectAdapterException{
		// add the array to the value
		assertTrue(adapter.isNull(propertyKey));
		adapter.put(propertyKey, "someValue");
		assertFalse(adapter.isNull(propertyKey));
		adapter.put(propertyKey, (String)null);
		assertTrue(adapter.isNull(propertyKey));
	}
	

	@Test
	public void testNullInput() throws JSONObjectAdapterException{
		adapter =  JSONObjectAdapterImpl.createAdapterFromJSONString("{\"name\":\"testAnonymousGet\",\"annotations\":null,\"id\":null,}");
		assertTrue(adapter.has("name"));
		assertFalse(adapter.isNull("name"));
		
		assertTrue(adapter.isNull("annotations"));
		assertTrue(adapter.isNull("id"));
	}
	
	@Test
	public void testToString() throws JSONObjectAdapterException{
		adapter.put("longKey", 123);
		adapter.put("doubleKey", 34.5);
		adapter.put("stringKey", "I am a great string!");
		JSONObjectAdapter object = adapter.createNew();
		object.put("childKeyOne", true);
		adapter.put("objecKey", object);
		System.out.println(adapter.toJSONString());
		assertEquals(adapter.toJSONString(), adapter.toString());
	}

}
