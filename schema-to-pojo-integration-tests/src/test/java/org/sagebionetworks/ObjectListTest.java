package org.sagebionetworks;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class ObjectListTest {

	@Test
	public void testCommplexList() throws JSONObjectAdapterException {
		String json = "{\"cellValues\":[[1,2,3],{\"a\":true,\"b\":[4,5,6]},null,false]}";
		ObjectList list = EntityFactory.createEntityFromJSONString(json, ObjectList.class);
		assertNotNull(list);
		assertNotNull(list.getCellValues());
		assertEquals(4, list.getCellValues().size());
		String jsonResult = EntityFactory.createJSONStringForEntity(list);
		assertEquals(json, jsonResult);
	}
	
	@Test
	public void testCommplexListWithJSONObjects() throws JSONObjectAdapterException {
		ObjectList list = new ObjectList().setCellValues(List.of(new JSONArray("[1,2,3]"), new JSONObject("{\"a\":true}")));
		assertNotNull(list);
		assertNotNull(list.getCellValues());
		assertEquals(2, list.getCellValues().size());
		String jsonResult = EntityFactory.createJSONStringForEntity(list);
		assertEquals("{\"cellValues\":[[1,2,3],{\"a\":true}]}", jsonResult);
	}

}
