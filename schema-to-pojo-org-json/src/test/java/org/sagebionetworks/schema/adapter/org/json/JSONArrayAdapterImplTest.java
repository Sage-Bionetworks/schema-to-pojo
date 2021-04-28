package org.sagebionetworks.schema.adapter.org.json;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class JSONArrayAdapterImplTest {
	
	JSONArrayAdapter adapter = null;
	int index = 0;
	
	@BeforeEach
	public void befor(){
		// This is a test for the JSONObjectAdapterImpl
		adapter = new JSONArrayAdapterImpl();
		index = 0;
	}
	
	@Test
	public void testLongRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		long value = 123;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getLong(index));
		// Make sure we can also get it as an object
		assertEquals(value, adapter.get(index));
	}
	
	@Test
	public void testNullRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		long value = 123;
		adapter.putNull(index);
		assertEquals(1, adapter.length());
		assertTrue(adapter.isNull(index));
		assertEquals(null, adapter.get(index));
	}
	
	@Test
	public void testStringRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		String value = "I am a tea pot";
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getString(index));
		// Make sure we can also get it as an object
		assertEquals(value, adapter.get(index));
	}
	
	@Test
	public void testDoubleRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		double value = 34.3;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(Double.doubleToLongBits(value), Double.doubleToLongBits(adapter.getDouble(index)));
		// Make sure we can also get it as an object
		assertEquals(Double.doubleToLongBits(value), Double.doubleToLongBits((Double)adapter.get(index)));
	}

	@Test
	public void testDoubleNaNRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		double value = Double.NaN;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertTrue(Double.isNaN(adapter.getDouble(index)));
		// Make sure we can also get it as an object
		assertTrue(Double.isNaN(Double.parseDouble((String)adapter.get(index))));
	}

	@Test
	public void testDoubleInfinityRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		double value = Double.POSITIVE_INFINITY;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertTrue(Double.isInfinite(adapter.getDouble(index)));
		// Make sure we can also get it as an object
		assertTrue(Double.isInfinite(Double.parseDouble((String)adapter.get(index))));
	}

	@Test
	public void testDoubleNegativeInfinityRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		double value = Double.NEGATIVE_INFINITY;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertTrue(Double.isInfinite(adapter.getDouble(index)));
		// Make sure we can also get it as an object
		assertTrue(Double.isInfinite(Double.parseDouble((String)adapter.get(index))));
	}

	@Test
	public void testBooleanRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		boolean value = true;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getBoolean(index));
		// Make sure we can also get it as an object
		assertEquals(value, adapter.get(index));
	}
	
	@Test
	public void testIntRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		int value = 34;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getInt(index));
		// Make sure we can also get it as an object
		assertEquals(value, adapter.get(index));
	}
	
	@Test
	public void testJSONObjectRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		JSONObjectAdapter value = adapter.createNew();
		value.put("keyone", 123);
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertNotNull(adapter.getJSONObject(index));
		System.out.print(adapter.toJSONString());
		assertEquals(value.toJSONString(), adapter.getJSONObject(index).toJSONString());
	}
	
	@Test
	public void testJSONArrayRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		JSONArrayAdapter value = adapter.createNewArray();
		value.put(0, 123);
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertNotNull(adapter.getJSONArray(index));
		System.out.print(adapter.toJSONString());
		assertEquals(value.toJSONString(), adapter.getJSONArray(index).toJSONString());
	}
	
	@Test
	public void testToString() throws JSONObjectAdapterException{
		adapter.put(0, 123);
		adapter.put(1, 34.5);
		adapter.put(2, "I am a great string!");
		JSONObjectAdapter object = adapter.createNew();
		object.put("childKeyOne", true);
		adapter.put(3, object);
		System.out.println(adapter.toJSONString());
		assertEquals(adapter.toJSONString(), adapter.toString());
	}
	
	@Test
	public void testDateRoundTrip() throws JSONObjectAdapterException{
		Date dateValue = new Date(System.currentTimeMillis());
		adapter.put(0, dateValue);
		System.out.println(adapter.toJSONString());
		Date clone = adapter.getDate(0);
		assertEquals(dateValue, clone);
	}
	
	@Test
	public void testDateNull() throws JSONObjectAdapterException{
		assertThrows(IllegalArgumentException.class, ()->{
			adapter.put(0, (Date)null);
		});
	}
	
	@Test
	public void testDateNullValue() throws JSONObjectAdapterException{
		assertThrows(JSONObjectAdapterException.class, ()->{
			adapter.getDate(0);
		});
	}
	
	@Test
	public void testGetPutObjectWithString() throws JSONObjectAdapterException {
		Object value = "a string";
		int index = 0;
		// call under test
		adapter.putObject(index, value);
		// call under test
		Object result = adapter.getObject(index);
		assertEquals(value, result);
	}
	
	@Test
	public void testGetPutObjectWithInteger() throws JSONObjectAdapterException {
		Object value = Integer.MAX_VALUE;
		int index = 0;
		// call under test
		adapter.putObject(index, value);
		// call under test
		Object result = adapter.getObject(index);
		assertEquals(value, result);
	}
	
	@Test
	public void testGetPutObjectWithLong() throws JSONObjectAdapterException {
		Object value = Long.MAX_VALUE;
		int index = 0;
		// call under test
		adapter.putObject(index, value);
		// call under test
		Object result = adapter.getObject(index);
		assertEquals(value, result);
	}
	
	@Test
	public void testGetPutObjectWithBoolean() throws JSONObjectAdapterException {
		Object value = Boolean.TRUE;
		int index = 0;
		// call under test
		adapter.putObject(index, value);
		// call under test
		Object result = adapter.getObject(index);
		assertEquals(value, result);
	}
	
	@Test
	public void testGetPutObjectWithDouble() throws JSONObjectAdapterException {
		Object value = new Double(123.456);
		int index = 0;
		// call under test
		adapter.putObject(index, value);
		// call under test
		Object result = adapter.getObject(index);
		assertEquals(value, result);
	}
	
	@Test
	public void testGetPutObjectWithDate() throws JSONObjectAdapterException {
		Date value = new Date(123);
		int index = 0;
		// call under test
		adapter.putObject(index, value);
		// call under test
		Object result = adapter.getObject(index);
		assertEquals(value.getTime(), result);
	}
	
	@Test
	public void testGetPutObjectWithNull() throws JSONObjectAdapterException {
		Object value = null;
		int index = 0;
		// call under test
		adapter.putObject(index, value);
		// call under test
		Object result = adapter.getObject(index);
		assertEquals(value, result);
	}
	
	@Test
	public void testGetObjectWithInvalid() throws JSONObjectAdapterException {
		JSONObjectAdapterImpl value = new JSONObjectAdapterImpl();
		int index = 0;
		adapter.put(index, value);
		String message = assertThrows(JSONObjectAdapterException.class, ()->{
			// call under test
			adapter.getObject(index);
		}).getMessage();
		assertEquals("Unsupported value of type: 'org.json.JSONObject' for index: '0'", message);
	}
	
	@Test
	public void testPutObjectWithInvalid() throws JSONObjectAdapterException {
		JSONObjectAdapterImpl value = new JSONObjectAdapterImpl();
		int index = 0;
		String message = assertThrows(JSONObjectAdapterException.class, ()->{
			// call under test
			adapter.putObject(index, value);
		}).getMessage();
		assertEquals("Unsupported value of type: 'org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl' for index: '0'", message);
	}
}
