package org.sagebionetworks.schema;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.Period;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONArrayAdapterImpl;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class ObjectSchemaTest {
	
	@Test
	public void testRoundTrip() throws JSONObjectAdapterException, JSONException{
		// Create a ObjectModel from the spec example.
		ObjectSchema example = new ObjectSchema();
		example.setName("Product");
		example.setId(new String("someId"));
		// Add the properties
		// id
		ObjectSchema property = new ObjectSchema();
		property.setType(TYPE.NUMBER);
		property.setDescription("Product identifier");
		property.setRequired(true);
		//yo I added and will need to delete
		double requiredPI = 7.77;
		property.setDefault(requiredPI);
		example.putProperty("id", property);
		// Name
		property = new ObjectSchema();
		property.setType(TYPE.STRING);
		property.setFormat(FORMAT.DATE_TIME);
		property.setDescription("Name of the product");
		property.setRequired(true);
		example.putProperty("name", property);
		// price
		property = new ObjectSchema();
		property.setType(TYPE.NUMBER);
		property.setMinimum(0);
		property.setRequired(true);
		// Add an array
		property = new ObjectSchema();
		property.setType(TYPE.ARRAY);
		property.setUniqueItems(true);
		ObjectSchema arrayType  = new ObjectSchema();
		arrayType.setType(TYPE.INTEGER);
		property.setAdditionalItems(arrayType);
		example.putProperty("array", property);
		
		// Add an additional property
		property = new ObjectSchema();
		property.setType(TYPE.STRING);
		example.putAdditionalProperty("label", property);
		
		// Extends a reference
		ObjectSchema _extends = new ObjectSchema(TYPE.OBJECT);
		example.setExtends(_extends);
		
		// Implements
		ObjectSchema[] _implements = new ObjectSchema[1];
		example.setImplements(_implements);
		_implements[0] = new ObjectSchema();
		_implements[0].setType(TYPE.INTERFACE);

		// Add an array
		property = new ObjectSchema();
		property.setType(TYPE.ARRAY);
		arrayType  = new ObjectSchema();
		arrayType.setType(TYPE.BOOLEAN);
		property.setAdditionalItems(arrayType);
		example.putAdditionalProperty("array", property);
		
		// Encoding
		example.setContentEncoding(ENCODING.BINARY);
		
		// Transient
		example.setTransient(true);
		
		// An enum
		example.setEnum(new String[]{"a","b","c"});
		
		// Now go to the JSONString
		String json = example.toJSONString( new JSONObjectAdapterImpl());
		System.out.println(json);
		// Now create a new object using the JSON String
		ObjectSchema clone = new  ObjectSchema(new JSONObjectAdapterImpl(new JSONObject(json)));
		assertNotNull(clone);
		System.out.println(clone.toJSONString(new JSONObjectAdapterImpl()));
		assertEquals(example, clone);
	}
	
	@Test
	public void testRoundTripRef() throws JSONObjectAdapterException, JSONException{
		// Create a ObjectModel from the spec example.
		ObjectSchema example = new ObjectSchema();
		example.setRef(new String("someId"));
		
		// Now go to the JSONString
		String json = example.toJSONString( new JSONObjectAdapterImpl());
		System.out.println(json);
		// Now create a new object using the JSON String
		ObjectSchema clone = new  ObjectSchema(new JSONObjectAdapterImpl(new JSONObject(json)));
		assertNotNull(clone);
		System.out.println(clone.toJSONString(new JSONObjectAdapterImpl()));
		assertEquals(example, clone);
	}
	

	
	@Test
	public void testGetNumberBasedOnType() throws JSONException, JSONObjectAdapterException{
		JSONObject json = new JSONObject();
		String key = "someInteger";
		json.put(key, new Long(33));
		// Now fetch the number
		Number result = ObjectSchema.getNumberBasedOnType(TYPE.NUMBER, new JSONObjectAdapterImpl(json), key);
		// Number should be mapped to doubles
		assertTrue(result instanceof Double);
		
		result = ObjectSchema.getNumberBasedOnType(TYPE.INTEGER, new JSONObjectAdapterImpl(json), key);
		// Integers should be mapped to Long
		assertTrue(result instanceof Long);
	}
	
	@Test
	public void testGetNumberBasedOnTypeDouble() throws JSONException, JSONObjectAdapterException{
		JSONObject json = new JSONObject();
		String key = "someDouble";
		json.put(key, new Double(33.44));
		// Now fetch the number
		Number result = ObjectSchema.getNumberBasedOnType(TYPE.NUMBER, new JSONObjectAdapterImpl(json), key);
		// Number should be mapped to doubles
		assertTrue(result instanceof Double);
		System.out.println(result);
		
		result = ObjectSchema.getNumberBasedOnType(TYPE.INTEGER, new JSONObjectAdapterImpl(json), key);
		// Integers should be mapped to Long
		assertTrue(result instanceof Long);
		System.out.println(result);
	}
	
	/**
	 * Tests that setDefaultType correctly sets adapter's default from 
	 * a object that represents a supported JSON type.
	 */
	@Test
	public void testSetDefaultType() throws Exception {
		//make a adapter
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		
		//make an object that represents a string
		String str = "hello";
		Object strObject = str;
		ObjectSchema.setDefaultType(strObject, adapter);
		
		//verify adapter has a default set and that it's of type string
		assertNotNull(adapter.getString("default"));
		
		adapter = new JSONObjectAdapterImpl();
	
		//make an object that represents a number
		Double doub = 7.77;
		Object doubObject = doub;
		ObjectSchema.setDefaultType(doubObject, adapter);
		
		//verify adapter has a default set and that it's of type double
		assertNotNull(adapter.getDouble("default"));
		
		adapter = new JSONObjectAdapterImpl();
		
		//make an object that represents a integer
		Long theLong = (long) 77;
		Object longObject = theLong;
		ObjectSchema.setDefaultType(longObject, adapter);
		
		//verify adapter has a default set and that it's of type integer
		assertNotNull(adapter.getLong("default"));
		
		adapter = new JSONObjectAdapterImpl();
		
		//make an object that represents a boolean
		Boolean boole = true;
		Object booleObject = boole;
		ObjectSchema.setDefaultType(booleObject, adapter);
		
		//verify adapter has a default set and that it's of type boolean
		assertNotNull(adapter.getBoolean("default"));
		
		adapter = new JSONObjectAdapterImpl();

		//make an object that represents a JSONArrayAdapter 
		JSONArrayAdapter arrayAdapter = new JSONArrayAdapterImpl();
		arrayAdapter.put(0, "yoHello");
		Object arrayObject = arrayAdapter;
		ObjectSchema.setDefaultType(arrayObject, adapter);
		
		//verify adapter has a default set and that it's of type array
		assertNotNull(adapter.getJSONArray("default"));
		
		adapter = new JSONObjectAdapterImpl();
		
		//make an object that represents a JSONObjectAdapter
		JSONObjectAdapter adap = new JSONObjectAdapterImpl();
		adap.put("yoHelloKey", "yoHello");
		Object adapObject = adap;
		ObjectSchema.setDefaultType(adapObject, adapter);
		
		//verify adapter has a default set and that it's of type object
		assertNotNull(adapter.getJSONObject("default"));
	}
	
	/**
	 * Tests that setDefaultType correctly throws exception when the
	 * object sent is not of a supported JSON type.
	 */
	@Test (expected = RuntimeException.class)
	public void testSetDefaultTypeForInvalidObject() throws Exception {
		//make adapter
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		
		//make object that is not of supported type
		Period rentalPeriod = new Period().withDays(2).withHours(12);
		Object nonJSONObject = rentalPeriod;
		ObjectSchema.setDefaultType(nonJSONObject, adapter);
	}
	
	/**
	 * Tests that setDefaultType correctly throws exception when the
	 * object sent is null.
	 */
	@Test (expected = RuntimeException.class)
	public void testSetDefaultTypeForNullObject() throws Exception {
		//make adapter
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		
		Object lameNullObject = null;
		ObjectSchema.setDefaultType(lameNullObject, adapter);
	}
	
	
	/**
	 * Tests that setDefaultObject works for situations where an adapter's
	 * type corresponds to the class type of object in adapter's default.
	 * @throws Exception
	 */
	public void testSetDefaultObject() throws Exception {
		//make ObjectSchema
		ObjectSchema schema = new ObjectSchema();
		
		//make adapter whose type is set to string
		//must have corresponding string object as default
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		adapter.put("type", "string");
		String adaptersDefault = "imADefaultString";
		adapter.put("default", adaptersDefault);
		
		ObjectSchema.setDefaultObject(schema, adapter);
		
		//verify that ObjectSchema has a stringObject as it's default
		assertNotNull(schema.getDefault());
		Object osDefault = schema.getDefault();
		assertEquals(String.class.getName(), osDefault.getClass().getName());
		
		//make adapter whose type is set to number
		//must have a corresponding Double object as default
		adapter = new JSONObjectAdapterImpl();
		adapter.put("type", "number");
		Double adapterDouble = 7.77;
		adapter.put("default", adapterDouble);
		
		ObjectSchema.setDefaultObject(schema, adapter);
		
		//verify that ObjectSchema has a doubleObject as it's default
		Object osDouble = schema.getDefault();
		assertEquals(Double.class.getName(), osDouble.getClass().getName());
		
		//make adapter whose type is set to integer
		//must have a corresponding Long object as default
		adapter = new JSONObjectAdapterImpl();
		adapter.put("type", "integer");
		Long adapterLong = (long) 77;
		adapter.put("default", adapterLong);
		
		ObjectSchema.setDefaultObject(schema, adapter);
		
		//verify that ObjectSchema has a longObject as it's default
		Object osLong = schema.getDefault();
		assertEquals(Long.class.getName(), osLong.getClass().getName());
		
		//make adapter whose type is set to boolean
		//must have a corresponding boolean object as default
		adapter = new JSONObjectAdapterImpl();
		adapter.put("type", "boolean");
		Boolean adapterBool = true;
		adapter.put("default", adapterBool);
		
		ObjectSchema.setDefaultObject(schema, adapter);
		
		//verify that ObjectSchema has a boolean object as it's default
		Object osBool = schema.getDefault();
		assertEquals(Boolean.class.getName(), osBool.getClass().getName());
		
		//make adapter whose type is set to object
		//must have a corresponding adapter object as default
		adapter = new JSONObjectAdapterImpl();
		adapter.put("type", "object");
		JSONObjectAdapter defaultAdapter = new JSONObjectAdapterImpl();
		defaultAdapter.put("name", "matilda");
		adapter.put("default", defaultAdapter);
		
		ObjectSchema.setDefaultObject(schema, adapter);
		
		//verify that ObjectSchema has a adapter object as it's default
		Object osAdapter = schema.getDefault();
		assertTrue(osAdapter instanceof JSONObjectAdapter);
		
		//make an adapter whose type is set to array
		//must have a corresponding JSONArrayAdapter as default
		adapter = new JSONObjectAdapterImpl();
		adapter.put("type", "array");
		JSONArrayAdapter defaultArray = new JSONArrayAdapterImpl();
		defaultArray.put(0, "imInAnArray");
		adapter.put("default", defaultArray);
		
		ObjectSchema.setDefaultObject(schema, adapter);
		
		//verify that ObjectSchema has a array object as it's default
		Object osArray = schema.getDefault();
		assertTrue(osArray instanceof JSONArrayAdapter);
	}
	
	/**
	 * Tests that setDefaultObject correctly throws exception when adapter
	 * does not have a type in it's JSON
	 * @throws Exception
	 */
	@Test (expected = JSONObjectAdapterException.class)
	public void testSetDefaultObjectWhenAdapterHasNoType() throws Exception {
		//make ObjectSchema
		ObjectSchema schema = new ObjectSchema();
		
		//make adapter who does not have type in it's JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		String adaptersDefault = "imADefaultString";
		adapter.put("default", adaptersDefault);
		
		ObjectSchema.setDefaultObject(schema, adapter);
	}
	
	/**
	 * Tests that setDefaultObject correctly throws exception when adapter
	 * has a null in it's type.
	 * @throws
	 */
	@Test (expected = JSONObjectAdapterException.class)
	public void testSetDefaultObjectWhenAdapterHasNullType() throws Exception {
		//make ObjectSchema
		ObjectSchema schema = new ObjectSchema();
		
		//make adapter who has null type
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		String nullString = null;
		adapter.put("type", nullString);
		adapter.put("default", "defaultString");
		
		ObjectSchema.setDefaultObject(schema, adapter);		
	}
	
	/**
	 * Tests that setDefaultObject correctly throws exception when adapter
	 * does not have a default defined
	 */
	@Test (expected = JSONObjectAdapterException.class)
	public void setDefaultObjectWhenAdapterHasNoDefault() throws Exception {
		//make ObjectSchema
		ObjectSchema schema = new ObjectSchema();
		
		//make adapter who has no default
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		adapter.put("type", "string");
		
		ObjectSchema.setDefaultObject(schema, adapter);
	}
}
