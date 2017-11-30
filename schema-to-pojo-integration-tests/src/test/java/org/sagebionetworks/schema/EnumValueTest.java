package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class EnumValueTest {

	@Test
	public void testToAndFromJSONAllValues() throws JSONObjectAdapterException {
		EnumValue value = new EnumValue();
		value.setName("valueOne");
		value.setDescription("Description for value one.");
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		value.writeToJSONObject(adapter);
		EnumValue copy = new EnumValue();
		copy.initializeFromJSONObject(adapter);
		assertEquals(value, copy);
	}
	
	@Test
	public void testToAndFromJSONNoValues() throws JSONObjectAdapterException {
		EnumValue value = new EnumValue();
		value.setName(null);
		value.setDescription(null);
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		value.writeToJSONObject(adapter);
		EnumValue copy = new EnumValue();
		copy.initializeFromJSONObject(adapter);
		assertEquals(value, copy);
	}
}
