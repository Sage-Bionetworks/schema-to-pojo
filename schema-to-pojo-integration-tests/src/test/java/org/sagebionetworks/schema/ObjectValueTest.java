package org.sagebionetworks.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.sagebionetworks.ObjectValue;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class ObjectValueTest {
	
	@Test
	public void testObjectValueWithBoolean() throws JSONObjectAdapterException {
		ObjectValue original = new ObjectValue();
		original.setObjectValue(Boolean.TRUE);
		String json = EntityFactory.createJSONStringForEntity(original);
		ObjectValue result = EntityFactory.createEntityFromJSONString(json, ObjectValue.class);
		assertEquals(original, result);
	}
	
	@Test
	public void testObjectValueWithString() throws JSONObjectAdapterException {
		ObjectValue original = new ObjectValue();
		original.setObjectValue("some string");
		String json = EntityFactory.createJSONStringForEntity(original);
		ObjectValue result = EntityFactory.createEntityFromJSONString(json, ObjectValue.class);
		assertEquals(original, result);
	}
	
	@Test
	public void testObjectValueWithLong() throws JSONObjectAdapterException {
		ObjectValue original = new ObjectValue();
		original.setObjectValue(Long.MAX_VALUE);
		String json = EntityFactory.createJSONStringForEntity(original);
		ObjectValue result = EntityFactory.createEntityFromJSONString(json, ObjectValue.class);
		assertEquals(original, result);
	}
	
	@Test
	public void testObjectValueWithDouble() throws JSONObjectAdapterException {
		ObjectValue original = new ObjectValue();
		original.setObjectValue(123.456);
		String json = EntityFactory.createJSONStringForEntity(original);
		ObjectValue result = EntityFactory.createEntityFromJSONString(json, ObjectValue.class);
		assertEquals(original, result);
	}
	
	@Test
	public void testObjectValueWithNested() throws JSONObjectAdapterException {
		ObjectValue original = new ObjectValue();
		original.setObjectValue(new ObjectValue().setObjectValue("wrapped"));
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		String message = assertThrows(JSONObjectAdapterException.class, ()->{
			// call under test
			original.writeToJSONObject(adapter);
		}).getMessage();
		assertEquals("Unsupported value of type: 'org.sagebionetworks.ObjectValue' for key: 'objectValue'", message);
	}
}
