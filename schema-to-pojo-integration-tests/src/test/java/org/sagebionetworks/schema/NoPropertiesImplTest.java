package org.sagebionetworks.schema;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
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
		// extra field was ignored and not re-emitted
		assertEquals("{}", clone.toJSONString());
	}
}
