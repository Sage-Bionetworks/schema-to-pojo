package org.sagebionetworks.schema.adapter.org.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.ObjectSchemaImpl;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.validation.ExpectedDateTime;

public class JSONObjectAdapterImplTest {
	
	JSONObjectAdapter adapter = null;
	String propertyKey = null;
	
	public static final String[] validURIs = new String[] {
		"foo://username:password@example.com:8042/over/there/index.dtb?type=animal&name=narwhal#nose",
		"ftp://ftp.is.co.za/rfc/rfc1808.txt",
		"http://www.ietf.org/rfc/rfc2396.txt",
		"ldap://[2001:db8::7]/c=GB?objectClass?one",
		"mailto:John.Doe@example.com",
		"news:comp.infosystems.www.servers.unix",
		"tel:+1-816-555-1212",
		"telnet://192.0.2.16:80/",
		"urn:oasis:names:specification:docbook:dtd:xml:4.1.2", 
		};
	
	public static final String[] invalidURIs = new String[] {
		"foo#username:password@example.com:8042/over/there/index.dtb?type=animal&name=narwhal#nose",
		"This is not a URI",
		};
	
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
		// Make sure we can also get it as an object
		assertEquals(value, adapter.get(propertyKey));
	}
	
	@Test
	public void testStringRoundTrip() throws JSONObjectAdapterException{
		String value = "some string";
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertEquals(value, adapter.getString(propertyKey));
		// Make sure we can also get it as an object
		assertEquals(value, adapter.get(propertyKey));
	}
	
	@Test
	public void testNullRoundTrip() throws JSONObjectAdapterException{
		adapter.putNull(propertyKey);
		assertTrue(adapter.has(propertyKey));
		assertTrue(adapter.isNull(propertyKey));
		assertEquals(null, adapter.get(propertyKey));
	}
	
	@Test
	public void testDoubleRoundTrip() throws JSONObjectAdapterException{
		double value = 12.5565;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertEquals(Double.doubleToLongBits(value), Double.doubleToLongBits(adapter.getDouble(propertyKey)));
		// Make sure we can also get it as an object
		assertEquals(Double.doubleToLongBits(value), Double.doubleToLongBits((Double)adapter.get(propertyKey)));
	}
	
	@Test
	public void testDoubleNaNRoundTrip() throws JSONObjectAdapterException{
		double value = Double.NaN;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertTrue(Double.isNaN(adapter.getDouble(propertyKey)));
		// Make sure we can also get it as an object
		assertTrue(Double.isNaN(Double.parseDouble((String)adapter.get(propertyKey))));
	}
	
	@Test
	public void testDoubleInfinityRoundTrip() throws JSONObjectAdapterException{
		double value = Double.POSITIVE_INFINITY;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertTrue(Double.isInfinite(adapter.getDouble(propertyKey)));
		assertEquals(Double.POSITIVE_INFINITY, adapter.getDouble(propertyKey), 0d);
		// Make sure we can also get it as an object
		assertTrue(Double.isInfinite(Double.parseDouble((String)adapter.get(propertyKey))));
	}
	
	@Test
	public void testDoubleNegativeInfinityRoundTrip() throws JSONObjectAdapterException{
		double value = Double.NEGATIVE_INFINITY;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertTrue(Double.isInfinite(adapter.getDouble(propertyKey)));
		assertEquals(Double.NEGATIVE_INFINITY, adapter.getDouble(propertyKey), 0d);
		// Make sure we can also get it as an object
		assertTrue(Double.isInfinite(Double.parseDouble((String)adapter.get(propertyKey))));
	}
	
	@Test
	public void testBooleanRoundTrip() throws JSONObjectAdapterException{
		boolean value = true;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertEquals(value, adapter.getBoolean(propertyKey));
		// Make sure we can also get it as an object
		assertEquals(value, adapter.get(propertyKey));
	}
	
	@Test
	public void testIntRoundTrip() throws JSONObjectAdapterException{
		int value = 1232334;
		adapter.put(propertyKey, value);
		assertTrue(adapter.has(propertyKey));
		assertEquals(value, adapter.getInt(propertyKey));
		// Make sure we can also get it as an object
		assertEquals(value, adapter.get(propertyKey));
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
		adapter =  new  JSONObjectAdapterImpl("{\"name\":\"testAnonymousGet\",\"annotations\":null,\"id\":null,}");
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
	
	/**
	 * Tests that validatePatternProperty works for a valid pattern
	 * and valid string property.
	 * @throws Exception
	 */
	@Test
	public void testValidatePatternProperty() throws Exception {
		String pattern = "a*b";
		String property = "aaaab";
		assertTrue(adapter.validatePatternProperty(pattern, property));
	}
	
	/**
	 * Tests that validatePatternProperty correctly handles when 
	 * an invalid property is sent for the pattern.
	 */
	@Test
	public void testValidatePatternPropertyForInvalidProperty() throws Exception {
		String pattern = "a*b";
		String badProperty = "caaaaaaaaab";
		assertFalse(adapter.validatePatternProperty(pattern, badProperty));
	}
	
	@Test
	public void testValidateValidURI() throws JSONObjectAdapterException{
		// Validate all of the valid URIs.
		for(String toTest: validURIs){
			assertTrue("This URI was valid: "+toTest, adapter.validateURI(toTest));
		}
	}
	
	@Test
	public void testValidateInvalidURI() {
		// Validate all of the valid URIs.
		for(String toTest: invalidURIs){
			try {
				assertTrue(adapter.validateURI(toTest));
				fail("This uri was invalid: "+toTest);
			} catch (JSONObjectAdapterException e) {
				// Expected
			}
		}
	}
	
	@Test
	public void testDateRoundTrip() throws JSONObjectAdapterException{
		Date dateValue = new Date(System.currentTimeMillis());
		adapter.put("key", dateValue);
		System.out.println(adapter.toJSONString());
		Date clone = adapter.getDate("key");
		assertEquals(dateValue, clone);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testDateNull() throws JSONObjectAdapterException{
		Date dateValue = new Date(System.currentTimeMillis());
		adapter.put(null, dateValue);
	}
	
	@Test (expected=JSONObjectAdapterException.class)
	public void testDateNullValue() throws JSONObjectAdapterException{
		Date value = adapter.getDate("key");
	}
	
	@Test
	public void binary() throws UnsupportedEncodingException, JSONException{
		JSONObject object =new JSONObject();
		object.put("test", "To a byte array".getBytes("UTF-8"));
		System.out.println(object.toString());
	}

	@Test
	public void binaryRoundTrip() throws JSONObjectAdapterException, UnsupportedEncodingException {
		// Make sure we can use base 64
		String startString = "This string will be encoded";
		byte[] value = startString.getBytes("UTF-8");
		adapter.put("binary", value);
		System.out.println(adapter.toJSONString());
		// Get the value out
		byte[] cloneArray = adapter.getBinary("binary");
		String clone = new String(cloneArray, "UTF-8");
		assertEquals(startString, clone);
	}
	
	@Test
	public void testRecursiveJSONRoundTrip() throws JSONObjectAdapterException {
		ObjectSchema schema = new ObjectSchemaImpl();
		schema.setTitle("Recrusive");
		schema.set$recursiveAnchor(Boolean.TRUE);
		
		ObjectSchema recursiveRef = new ObjectSchemaImpl();
		recursiveRef.set$recursiveRef("#");
		
		ObjectSchema array = new ObjectSchemaImpl();
		array.setType(TYPE.ARRAY);
		array.setItems(recursiveRef);
		
		LinkedHashMap<String, ObjectSchema> properties = new LinkedHashMap<String, ObjectSchema>();
		properties.put("listOfRecursive", array);
		schema.setProperties(properties);
		
		schema.writeToJSONObject(adapter);
		
		String resultJson = adapter.toJSONString();
		System.out.println(resultJson);
		ObjectSchema clone = new ObjectSchemaImpl();
		clone.initializeFromJSONObject(new JSONObjectAdapterImpl(resultJson));
		assertEquals(clone, schema);
	}
	
	@Test
	public void testGetWithInvalidObject() throws JSONObjectAdapterException {
		Nested invalidValue = new Nested("contained value");
		JSONObject wrapped = new JSONObject();
		this.propertyKey = "invalidKey";
		wrapped.put(propertyKey, invalidValue);
		adapter = new JSONObjectAdapterImpl(wrapped);
		String message = assertThrows(JSONObjectAdapterException.class, ()->{
			// call under test
			adapter.get(propertyKey);
		}).getMessage();
		assertEquals("Unsupported value type: 'class org.sagebionetworks.schema.adapter.org.json.Nested' for key: 'invalidKey'", message);
	}
	
	@Test
	public void testGetWithString() throws JSONObjectAdapterException {
		JSONObject wrapped = new JSONObject();
		Object inputValue = "some string";
		wrapped.put(propertyKey, inputValue);
		adapter = new JSONObjectAdapterImpl(wrapped);
		// call under test
		Object value = adapter.get(propertyKey);
		assertEquals(inputValue, value);
	}
	
	@Test
	public void testGetWithInteger() throws JSONObjectAdapterException {
		JSONObject wrapped = new JSONObject();
		Object inputValue = new Integer(123);
		wrapped.put(propertyKey, inputValue);
		adapter = new JSONObjectAdapterImpl(wrapped);
		// call under test
		Object value = adapter.get(propertyKey);
		assertEquals(inputValue, value);
	}
	
	@Test
	public void testGetWithLong() throws JSONObjectAdapterException {
		JSONObject wrapped = new JSONObject();
		Object inputValue = Long.MAX_VALUE;
		wrapped.put(propertyKey, inputValue);
		adapter = new JSONObjectAdapterImpl(wrapped);
		// call under test
		Object value = adapter.get(propertyKey);
		assertEquals(inputValue, value);
	}
	
	@Test
	public void testGetWithDouble() throws JSONObjectAdapterException {
		JSONObject wrapped = new JSONObject();
		Object inputValue = new Double(123.456);
		wrapped.put(propertyKey, inputValue);
		adapter = new JSONObjectAdapterImpl(wrapped);
		// call under test
		Object value = adapter.get(propertyKey);
		assertEquals(inputValue, value);
	}
	
	@Test
	public void testGetWithBoolean() throws JSONObjectAdapterException {
		JSONObject wrapped = new JSONObject();
		Object inputValue = Boolean.FALSE;
		wrapped.put(propertyKey, inputValue);
		adapter = new JSONObjectAdapterImpl(wrapped);
		// call under test
		Object value = adapter.get(propertyKey);
		assertEquals(inputValue, value);
	}
	
	@Test
	public void testGetWithDate() throws JSONObjectAdapterException {
		JSONObject wrapped = new JSONObject();
		Object inputValue = new Date(123);
		wrapped.put(propertyKey, inputValue);
		adapter = new JSONObjectAdapterImpl(wrapped);
		// call under test
		Object value = adapter.get(propertyKey);
		assertEquals(inputValue, value);
	}
	
	@Test
	public void testPutObjectWithString() throws JSONObjectAdapterException {
		adapter = new JSONObjectAdapterImpl();
		Object value = "some string";
		// call under test
		adapter.putObject(propertyKey, value);
		assertEquals(value, adapter.get(propertyKey));
	}
	
	@Test
	public void testPutObjectWithInteger() throws JSONObjectAdapterException {
		adapter = new JSONObjectAdapterImpl();
		Object value = Integer.MAX_VALUE;
		// call under test
		adapter.putObject(propertyKey, value);
		assertEquals(value, adapter.get(propertyKey));
	}
	
	@Test
	public void testPutObjectWithLong() throws JSONObjectAdapterException {
		adapter = new JSONObjectAdapterImpl();
		Object value = Long.MAX_VALUE;
		// call under test
		adapter.putObject(propertyKey, value);
		assertEquals(value, adapter.get(propertyKey));
	}
	
	@Test
	public void testPutObjectWithDouble() throws JSONObjectAdapterException {
		adapter = new JSONObjectAdapterImpl();
		Object value = new Double(123.456);
		// call under test
		adapter.putObject(propertyKey, value);
		assertEquals(value, adapter.get(propertyKey));
	}
	
	@Test
	public void testPutObjectWithDate() throws JSONObjectAdapterException {
		adapter = new JSONObjectAdapterImpl();
		Date value = new Date(123);
		// call under test
		adapter.putObject(propertyKey, value);
		assertEquals(value.getTime(), adapter.get(propertyKey));
	}
	
	@Test
	public void testPutObjectWithNonPrimitiveObject() {
		adapter = new JSONObjectAdapterImpl();
		Object value = new ByteArrayInputStream("some data".getBytes(StandardCharsets.UTF_8));
		String message = assertThrows(JSONObjectAdapterException.class, ()->{
			// call under test
			adapter.putObject(propertyKey, value);
		}).getMessage();
		assertEquals("Unsupported value of type: 'java.io.ByteArrayInputStream' for key: 'propKey'", message);
	}
}
