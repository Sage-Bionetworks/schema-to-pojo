package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class HasUriTest {
	
	@Test
	public void testRoundTrip() throws JSONObjectAdapterException{
		HasURI source = new HasURI();
		source.setNotRequiredUri("http://localhost:8080/test/something.html");
		source.setRequireUri("//localhost");
		String jsonString = EntityFactory.createJSONStringForEntity(source);
		System.out.println(jsonString);
		HasURI clone = EntityFactory.createEntityFromJSONString(jsonString, HasURI.class);
		assertNotNull(clone);
		assertEquals(source,clone);
	}
	
	@Test (expected=JSONObjectAdapterException.class)
	public void testInvalidURI() throws JSONObjectAdapterException{
		HasURI source = new HasURI();
		source.setRequireUri("#//not a valid URI");
		String jsonString = EntityFactory.createJSONStringForEntity(source);
		System.out.println(jsonString);
		HasURI clone = EntityFactory.createEntityFromJSONString(jsonString, HasURI.class);
		assertNotNull(clone);
		assertEquals(source,clone);
	}
}
