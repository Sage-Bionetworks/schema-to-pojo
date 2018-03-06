package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.NoPropertiesImpl;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class NoPropertiesImplTest {

	
	/**
	 * Extra fields should work with a class that has no properties.
	 * @throws JSONObjectAdapterException
	 */
	@Test
	public void testNoPropertiesExtraFields() throws JSONObjectAdapterException {
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		adapter.put("foo", "bar");
		NoPropertiesImpl noProps = new NoPropertiesImpl();
		noProps.initializeFromJSONObject(adapter);
		JSONObjectAdapterImpl clone = new JSONObjectAdapterImpl();
		noProps.writeToJSONObject(clone);
		// extra property should be in the clone.
		assertEquals("{\"foo\":\"bar\"}", clone.toJSONString());
	}
}
