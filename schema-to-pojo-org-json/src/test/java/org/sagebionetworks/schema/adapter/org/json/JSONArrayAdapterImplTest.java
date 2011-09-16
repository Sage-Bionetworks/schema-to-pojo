package org.sagebionetworks.schema.adapter.org.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class JSONArrayAdapterImplTest {
	
	JSONArrayAdapter adapter = null;
	int index = 0;
	
	@Before
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
	}
	
	@Test
	public void testStringRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		String value = "I am a tea pot";
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getString(index));
	}
	
	@Test
	public void testDoubleRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		double value = 34.3;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(Double.doubleToLongBits(value), Double.doubleToLongBits(adapter.getDouble(index)));
	}

	@Test
	public void testBooleanRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		boolean value = true;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getBoolean(index));
	}
	
	@Test
	public void testIntRoundTrip() throws JSONObjectAdapterException{
		// Start off at zero
		assertEquals(0, adapter.length());
		int value = 34;
		adapter.put(index, value);
		assertEquals(1, adapter.length());
		assertEquals(value, adapter.getInt(index));
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
	
}
