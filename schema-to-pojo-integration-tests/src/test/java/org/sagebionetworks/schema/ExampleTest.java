package org.sagebionetworks.schema;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.sagebionetworks.schema.util.FileUtil;

public class ExampleTest {
	
	@Test
	public void testLoadExample() throws IOException, JSONException{
		// Load the JSON string from the file.
		String json = FileUtil.loadStringFromClasspathFile(ExampleTest.class.getClassLoader(), "org/sagebionetworks/schema/ExampleSchema.json");
		JSONObject loaded = new JSONObject(json);
		assertNotNull(loaded);
		StringWriter writer = new StringWriter();
		System.out.println("Loaded:");
		System.out.println(loaded.write(writer));
		// Now build up a JSONObject
		JSONObject built = new JSONObject();
		built.put("name", "Product");
		JSONObject properties = new JSONObject();
		built.put("properties", properties);
		// id
		JSONObject property = new JSONObject();
		property.put("type", "number");
		property.put("description", "Product identifier");
		property.put("required", true);
		properties.put("id", property);
		// name
		property = new JSONObject();
		property.put("type", "string");
		property.put("description", "Name of the product");
		property.put("required", true);
		properties.put("name", property);
		// price
		property = new JSONObject();
		property.put("required", true);
		property.put("type", "number");
		property.put("minimum", 0);
		properties.put("price", property);
		// tags
		property = new JSONObject();
		property.put("type", "array");
//		JSONArray items = new JSONArray();
		JSONObject item = new JSONObject();
		item.put("type", "string");
//		items.put(item);
		property.put("items", item);
		properties.put("tags", property);
		
		writer = new StringWriter();
		System.out.println("Built:");
		System.out.println(built.write(writer));
//		
//		JSONObject clone = new JSONObject(built.toString());
//		System.out.println(loaded.toString());
//		System.out.println(clone.toString());
//		assertEquals(loaded.toString(), clone.toString());
	}

}
