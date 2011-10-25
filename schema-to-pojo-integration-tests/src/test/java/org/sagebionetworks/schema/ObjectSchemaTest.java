package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
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

}
