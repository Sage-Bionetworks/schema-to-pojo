package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sagebionetworks.schema.adapter.AdapterCollectionUtils;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONArrayAdapterImpl;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class AdapterCollectionUtilsTest {
	
	@Test
	public void testStringArrayRoundTrip() throws JSONObjectAdapterException{
		String[] array = new String[] {"a", "b", "c"};
		JSONArrayAdapterImpl arrayAdapter = new JSONArrayAdapterImpl();
		AdapterCollectionUtils.writeToArray(arrayAdapter, array);
		List<String> back = AdapterCollectionUtils.readListOfStrings(arrayAdapter);
		assertNotNull(back);
		String[] backArray = back.toArray(new String[array.length]);
		assertTrue(Arrays.equals(array, backArray));
	}
	
	@Test
	public void testMapWithStringArray() throws JSONObjectAdapterException{
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("key array", new String[]{"one", "two", "three"});
		// Adapter
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		AdapterCollectionUtils.writeToObject(adapter, row);
		Map<String, Object> back = AdapterCollectionUtils.readMapFromObject(adapter);
		assertNotNull(back);
		List<String> list = (List<String>) back.get("key array");
		assertEquals(3, list.size());
		assertEquals("one", list.get(0));
	}
	
	@Test
	public void testMapPrimitivesy() throws JSONObjectAdapterException{
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("string", "stringValue");
		row.put("integer", new Integer(123));
		row.put("long", new Long(1234));
		row.put("double", new Double(123.56));
		// Adapter
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		AdapterCollectionUtils.writeToObject(adapter, row);
		System.out.println(adapter.toJSONString());
		Map<String, Object> back = AdapterCollectionUtils.readMapFromObject(adapter);
		assertNotNull(back);
		assertEquals(row, back);
	}
	
	@Test
	public void testMapWithStringListy() throws JSONObjectAdapterException{
		Map<String, Object> row = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		row.put("key List", list);
		
		// Adapter
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		AdapterCollectionUtils.writeToObject(adapter, row);
		
		Map<String, Object> back = AdapterCollectionUtils.readMapFromObject(adapter);
		assertNotNull(back);
		assertEquals(list, back.get("key List"));
		
	}

}
