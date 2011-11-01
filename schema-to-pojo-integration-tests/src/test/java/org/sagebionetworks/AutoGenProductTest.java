package org.sagebionetworks;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.sagebionetworks.schema.Product;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

/**
 * This is a test for an auto-generated class.
 * 
 * @author jmhill
 *
 */
public class AutoGenProductTest {
	
	@Test
	public void testRoundTrip() throws JSONObjectAdapterException{
		// Product is an auto-generated class so make sure it works as expected.
		Product sample = new Product();
		sample.setId(new Long(123));
		sample.setName("myName");
		sample.setPrice(2.34);
		sample.setTags(Arrays.asList(new String[]{"tag1", "tag2"}));
		// Now make sure we can go to JSON and back
		String json = EntityFactory.createJSONStringForEntity(sample);
		assertNotNull(json);
		System.out.println(json);
		// Now make clone from the json
		Product clone = EntityFactory.createEntityFromJSONString(json, Product.class);
		assertNotNull(clone);
		// The clone should match the new object
		assertEquals(sample, clone);
	}

}
