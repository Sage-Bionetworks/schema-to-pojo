package org.sagebionetworks.schema.generator.handler.schema03;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;

import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class ExampleTest {
	
	@Test
	public void testRoundTrip () throws JSONObjectAdapterException{
		Example e = new Example();
		e.setStringMap(new LinkedHashMap<String, String>());
		e.setByteArrayMap(new LinkedHashMap<String, byte[]>());
		e.getStringMap().put("stringOne", "abc");
		e.getStringMap().put("stringTwo", "zyz");
		e.getByteArrayMap().put("bytesOne", "abc".getBytes());
		e.getByteArrayMap().put("bytesTwo", "zyz".getBytes());
		String jsonString = EntityFactory.createJSONStringForEntity(e);
		System.out.println(jsonString);
		Example clone = EntityFactory.createEntityFromJSONString(jsonString, Example.class);
//		assertEquals(e, clone);
	}

}
