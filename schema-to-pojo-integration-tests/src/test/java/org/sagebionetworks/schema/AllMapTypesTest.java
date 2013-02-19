package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;

import org.junit.Test;
import org.sagebionetworks.AllMapTypes;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class AllMapTypesTest {

	@Test
	public void testAllMapTypesRoundTrip() throws JSONObjectAdapterException{
		AllMapTypes amt = new AllMapTypes();
		amt.setStringMap(new LinkedHashMap<String, String>());
		amt.getStringMap().put("stringOne", "one");
		amt.getStringMap().put("stringTwo", "two");
		
		// to JSON
		String jsonString = EntityFactory.createJSONStringForEntity(amt);
		System.out.println(jsonString);
		AllMapTypes clone = EntityFactory.createEntityFromJSONString(jsonString, AllMapTypes.class);
		assertEquals(amt, clone);
	}
}
