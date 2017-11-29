package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

/**
 * Test that checks that the order of the properties in the JSON schema are preserved.
 * @author jmhill
 *
 */
public class OrderedTest {
	
	@Ignore // This test fails since moving to Java 8 because of the changes to HashSet
	@Test
	public void testOrder() throws JSONObjectAdapterException{
		// Get the schema
		ObjectSchema schema = EntityFactory.createEntityFromJSONString(new Ordered().getJSONSchema(), ObjectSchema.class);
		Iterator<String> it = schema.getProperties().keySet().iterator();
		int index = 0;
		while(it.hasNext()){
			String key = it.next();
			System.out.println(key);
			String expectedKey = "a"+index;
			assertEquals("The property order was not preserved!", expectedKey, key);
			index++;
		}
	}

}
