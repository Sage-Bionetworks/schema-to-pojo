package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Test;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.schema.generator.EffectiveSchemaUtil;

/**
 * Test that checks that the order of the properties in the JSON schema are preserved.
 * @author jmhill
 *
 */
public class OrderedTest {
	
	@Test
	public void testOrder() throws Exception {
		// Get the schema
		String schemaJson = EffectiveSchemaUtil.loadEffectiveSchemaFromClasspath(Ordered.class);
		ObjectSchema schema = new ObjectSchemaImpl(new JSONObjectAdapterImpl(schemaJson));
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
