package org.sagebionetworks.schema.adapter.org.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class EntityFactoryTest {

	@Test
	public void testCreateJSONStringForEntityNull() throws JSONObjectAdapterException {
		assertThrows(IllegalArgumentException.class, () -> {
			// null is not allowed
			EntityFactory.createJSONStringForEntity(null);
		});
	}

	@Test
	public void testCreateJSONObjectForEntityNull() throws JSONObjectAdapterException {
		assertThrows(IllegalArgumentException.class, () -> {
			EntityFactory.createJSONObjectForEntity(null);
		});
	}

	@Test
	public void testCreateEntityFromJSONStringStringNull() throws JSONObjectAdapterException {
		assertThrows(IllegalArgumentException.class, () -> {
			EntityFactory.createEntityFromJSONString(null, SimpleEntityStub.class);
		});
	}

	@Test
	public void testCreateEntityFromJSONStringClassNull() throws JSONObjectAdapterException {
		assertThrows(IllegalArgumentException.class, () -> {
			EntityFactory.createEntityFromJSONString("{\"value\":\"This value should make a round trip\"}", null);
		});
	}

	@Test
	public void testCreateEntityFromJSONObjectStringNull() throws JSONObjectAdapterException {
		assertThrows(IllegalArgumentException.class, () -> {
			EntityFactory.createEntityFromJSONObject(null, SimpleEntityStub.class);
		});
	}

	@Test
	public void testCreateEntityFromJSONObjectClassNull() throws JSONObjectAdapterException {
		assertThrows(IllegalArgumentException.class, () -> {
			EntityFactory.createEntityFromJSONObject(new JSONObject(), null);
		});
	}

	@Test
	public void testJSONStringRoundTrip() throws JSONObjectAdapterException {
		SimpleEntityStub stub = new SimpleEntityStub();
		stub.setValue("This value should make a round trip");
		String json = EntityFactory.createJSONStringForEntity(stub);
		assertNotNull(json);
		// Make sure we can make a round trip
		SimpleEntityStub clone = EntityFactory.createEntityFromJSONString(json, SimpleEntityStub.class);
		assertNotNull(clone);
		// The stub and clone should be the same
		assertEquals(stub, clone);
	}

	@Test
	public void testJSONObjectRoundTrip() throws JSONObjectAdapterException {
		SimpleEntityStub stub = new SimpleEntityStub();
		stub.setValue("This value should make a round trip");
		JSONObject object = EntityFactory.createJSONObjectForEntity(stub);
		assertNotNull(object);
		// Make sure we can make a round trip
		SimpleEntityStub clone = EntityFactory.createEntityFromJSONObject(object, SimpleEntityStub.class);
		assertNotNull(clone);
		// The stub and clone should be the same
		assertEquals(stub, clone);
	}

	/**
	 * For this case we are testing that we can instantiate the POJO using the
	 * interfaces as the type.
	 * 
	 * @throws JSONObjectAdapterException
	 */
	@Test
	public void testRoundTripInterface() throws JSONObjectAdapterException {
		SimpleEntityStub stub = new SimpleEntityStub();
		stub.setValue("This value should make a round trip");
		String json = EntityFactory.createJSONStringForEntity(stub);
		assertNotNull(json);
		// Make sure we can use the interface as the type and that we get what we
		// expect.
		SimpleInterface clone = EntityFactory.createEntityFromJSONString(json, SimpleInterface.class);
		assertNotNull(clone);
		// The stub and clone should be the same
		assertEquals(stub, clone);
	}

	@Test
	public void testCreateEntityFromJSONStringWithDefaultConcreteType() throws JSONObjectAdapterException {

		// A json string without concrete type, the interface has a
		// _DEFAULT_CONCRETE_TYPE field
		String json = "{\"value\":\"This value should make a round trip\"}";

		SimpleEntityStub expected = new SimpleEntityStub();
		expected.setValue("This value should make a round trip");

		// Make sure we can use the interface even though a concrete type is not
		// specified, using the annotation value
		SimpleInterfaceWithDefaultConcreteType clone = EntityFactory.createEntityFromJSONString(json,
				SimpleInterfaceWithDefaultConcreteType.class);

		// The stub and clone should be the same
		assertEquals(expected, clone);
	}

	@Test
	public void testCreateEntityFromJSONStringWithMissingConcreteType() throws JSONObjectAdapterException {

		// A json string without concrete type,
		String json = "{\"value\":\"This value should make a round trip\"}";

		JSONObjectAdapterException ex = assertThrows(JSONObjectAdapterException.class, () -> {
			// Call under test
			EntityFactory.createEntityFromJSONString(json, SimpleInterface.class);
		});

		assertEquals("Missing 'concreteType' property, cannot discriminate polymorphic type "
				+ "org.sagebionetworks.schema.adapter.org.json.SimpleInterface", ex.getCause().getMessage());

	}
	
	@Test
	public void testWriteAndReadJSONArrayString() throws JSONObjectAdapterException {
		List<SimpleEntityStub> list = Arrays.asList(new SimpleEntityStub().withValue("1"), null,
				new SimpleEntityStub().withValue("2"));
		// call under test
		String json = EntityFactory.writeToJSONArrayString(list);
		// call under test
		List<SimpleEntityStub> clone = EntityFactory.readFromJSONArrayString(json, SimpleEntityStub.class);
		assertEquals(list, clone);
	}

	@Test
	public void testWriteToJSONArrayString() throws JSONObjectAdapterException {
		List<SimpleEntityStub> list = Arrays.asList(new SimpleEntityStub().withValue("1"), null,
				new SimpleEntityStub().withValue("2"));
		// call under test
		String json = EntityFactory.writeToJSONArrayString(list);
		assertEquals(
				"[{\"value\":\"1\",\"concreteType\":\"org.sagebionetworks.schema.adapter.org.json.SimpleEntityStub\"}"
						+ ",null"
						+ ",{\"value\":\"2\",\"concreteType\":\"org.sagebionetworks.schema.adapter.org.json.SimpleEntityStub\"}]",
				json);
	}

	@Test
	public void testWriteToJSONArrayStringWithEmpty() throws JSONObjectAdapterException {
		List<SimpleEntityStub> list = Collections.emptyList();
		// call under test
		String json = EntityFactory.writeToJSONArrayString(list);
		assertEquals("[]", json);
	}

	@Test
	public void testWriteToJSONArrayStringWithNullList() throws JSONObjectAdapterException {
		List<SimpleEntityStub> list = null;
		String message = assertThrows(IllegalArgumentException.class, () -> {
			// call under test
			EntityFactory.writeToJSONArrayString(list);
		}).getMessage();
		assertEquals("list cannot be null", message);
	}

	@Test
	public void testReadFromJSONArrayString() throws JSONObjectAdapterException {
		String json = "[{\"value\":\"1\",\"concreteType\":\"org.sagebionetworks.schema.adapter.org.json.SimpleEntityStub\"}"
				+ ",null"
				+ ",{\"value\":\"2\",\"concreteType\":\"org.sagebionetworks.schema.adapter.org.json.SimpleEntityStub\"}]";
		// call under test
		List<SimpleEntityStub> list = EntityFactory.readFromJSONArrayString(json, SimpleEntityStub.class);
		List<SimpleEntityStub> expected = Arrays.asList(new SimpleEntityStub().withValue("1"), null,
				new SimpleEntityStub().withValue("2"));
		assertEquals(expected, list);
	}
	
	@Test
	public void testReadFromJSONArrayStringWithEmpty() throws JSONObjectAdapterException {
		String json = "[]";
		// call under test
		List<SimpleEntityStub> list = EntityFactory.readFromJSONArrayString(json, SimpleEntityStub.class);
		List<SimpleEntityStub> expected = Collections.emptyList();
		assertEquals(expected, list);
	}

	@Test
	public void testReadFromJSONArrayStringWithNullList() throws JSONObjectAdapterException {
		String json = null;
		String message = assertThrows(IllegalArgumentException.class, () -> {
			// call under test
			EntityFactory.readFromJSONArrayString(json, SimpleEntityStub.class);
		}).getMessage();
		assertEquals("json cannot be null", message);
	}
	
	@Test
	public void testReadFromJSONArrayStringWithNullClazz() throws JSONObjectAdapterException {
		Class<? extends JSONEntity> clazz = null;
		String message = assertThrows(IllegalArgumentException.class, () -> {
			// call under test
			EntityFactory.readFromJSONArrayString("[]", clazz);
		}).getMessage();
		assertEquals("JSONEntity class cannot be null", message);
	}
	
	/**
	 * Test added for PLFM-6906
	 * @throws JSONObjectAdapterException
	 */
	@Test
	public void testCreateEntityFromJSONStringWithClassNotFound() throws JSONObjectAdapterException {
		JSONObject jsonEntity = new JSONObject();
		jsonEntity.put(ObjectSchema.CONCRETE_TYPE, "not.a.real.class");
		String message = assertThrows(IllegalArgumentException.class, ()->{
			EntityFactory.createEntityFromJSONObject(jsonEntity, SimpleInterface.class);
		}).getMessage();
		assertEquals("Unknown concreteType : 'not.a.real.class'", message);
	}
}
