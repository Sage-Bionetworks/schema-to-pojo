package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class ObjectSchemaTest {
	
	@Test
	public void testRoundTrip() throws JSONObjectAdapterException, JSONException, URISyntaxException{
		// Create a ObjectModel from the spec example.
		ObjectSchema example = new ObjectSchema();
		example.setName("Product");
		example.setId(new URI("someId"));
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
	public void testRoundTripRef() throws JSONObjectAdapterException, JSONException, URISyntaxException{
		// Create a ObjectModel from the spec example.
		ObjectSchema example = new ObjectSchema();
		example.setRef(new URI("someId"));
		
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
	public void testSubSchemaIterator() throws URISyntaxException{
		Map<URI, ObjectSchema> expectedMap = new HashMap<URI, ObjectSchema>();
		// The root schema to put objects into
		ObjectSchema root = new ObjectSchema();
		// Now add some sub-schema
		
		// Add a property sub-schema
		URI uri = new URI("one");
		ObjectSchema sub = new ObjectSchema();
		sub.setId(uri);
		expectedMap.put(uri, sub);
		root.putProperty("someKey", sub);
		
		// Add an additionalProperty sub-schema
		uri = new URI("two");
		sub = new ObjectSchema();
		sub.setId(uri);
		expectedMap.put(uri, sub);
		root.putAdditionalProperty("addKey", sub);
		
		// Add an item sub
		uri = new URI("three");
		sub = new ObjectSchema();
		sub.setId(uri);
		expectedMap.put(uri, sub);
		root.setItems(sub);
		
		// Add an additional item
		uri = new URI("four");
		sub = new ObjectSchema();
		sub.setId(uri);
		expectedMap.put(uri, sub);
		root.setAdditionalItems(sub);
		
		// Extends
		uri = new URI("five");
		sub = new ObjectSchema();
		sub.setId(uri);
		expectedMap.put(uri, sub);
		root.setExtends(sub);
		
		//Now make sure we find all item with the iterator
		Iterator<ObjectSchema> it = root.getSubSchemaIterator();
		assertNotNull(it);
		// Now check each value
		while(it.hasNext()){
			ObjectSchema subFromIt = it.next();
			assertNotNull(subFromIt);
			assertNotNull(subFromIt.getId());
			ObjectSchema fromMap = expectedMap.remove(subFromIt.getId());
			assertNotNull("The iterator had an unexpected sub-schema", fromMap);
			assertEquals(fromMap, subFromIt);
		}
		// If all items were found the map should be empty
		assertEquals("The iterator missed: "+expectedMap.size()+" sub-scheams", 0, expectedMap.size());
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
