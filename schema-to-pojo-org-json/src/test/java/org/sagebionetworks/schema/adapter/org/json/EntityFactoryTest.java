package org.sagebionetworks.schema.adapter.org.json;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;


public class EntityFactoryTest {
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateJSONStringForEntityNull() throws JSONObjectAdapterException{
		// null is not allowed
		String json = EntityFactory.createJSONStringForEntity(null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateJSONObjectForEntityNull() throws JSONObjectAdapterException{
		EntityFactory.createJSONObjectForEntity(null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateEntityFromJSONStringStringNull() throws JSONObjectAdapterException{
		EntityFactory.createEntityFromJSONString(null, SimpleEntityStub.class);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testCreateEntityFromJSONStringClassNull() throws JSONObjectAdapterException{
		EntityFactory.createEntityFromJSONString("{\"value\":\"This value should make a round trip\"}",null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateEntityFromJSONObjectStringNull() throws JSONObjectAdapterException{
		EntityFactory.createEntityFromJSONObject(null, SimpleEntityStub.class);
	}
	@Test (expected=IllegalArgumentException.class)
	public void testCreateEntityFromJSONObjectClassNull() throws JSONObjectAdapterException{
		EntityFactory.createEntityFromJSONObject(new JSONObject(),null);
	}
	
	@Test
	public void testJSONStringRoundTrip() throws JSONObjectAdapterException{
		SimpleEntityStub stub = new SimpleEntityStub();
		stub.setValue("This value should make a round trip");
		String json = EntityFactory.createJSONStringForEntity(stub);
		assertNotNull(json);
		System.out.println(json);
		// Make sure we can make a round trip
		SimpleEntityStub clone = EntityFactory.createEntityFromJSONString(json, SimpleEntityStub.class);
		assertNotNull(clone);
		// The stub and clone should be the same
		assertEquals(stub, clone);
	}
	
	@Test
	public void testJSONObjectRoundTrip() throws JSONObjectAdapterException{
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
	 * For this case we are testing that we can instantiate the POJO using the interfaces as the type.
	 * @throws JSONObjectAdapterException
	 */
	@Test
	public void testRoundTripInterface() throws JSONObjectAdapterException{
		SimpleEntityStub stub = new SimpleEntityStub();
		stub.setValue("This value should make a round trip");
		String json = EntityFactory.createJSONStringForEntity(stub);
		assertNotNull(json);
		System.out.println(json);
		// Make sure we can use the interface as the type and that we get what we expect.
		SimpleInterface clone = EntityFactory.createEntityFromJSONString(json, SimpleInterface.class);
		assertNotNull(clone);
		// The stub and clone should be the same
		assertEquals(stub, clone);
	}

}
