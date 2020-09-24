package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.DefaultConcreteTypeImpl;
import org.sagebionetworks.InterfaceWithDefaultConcreteType;
import org.sagebionetworks.NonDefaultConcreteTypeImpl;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class DefaultConcreteTypeIntegrationTest {
	
	@Test
	public void testRoundTrip() throws JSONObjectAdapterException {
		NonDefaultConcreteTypeImpl instance = new NonDefaultConcreteTypeImpl();
		
		instance.setSomeProperty("Some Property");
		instance.setSomeOtherProperty("Some Other Property");
		
		String expectedJson = "{" 
				+ "\"someProperty\":\"Some Property\","
				+ "\"someOtherProperty\":\"Some Other Property\"" 
				+ "}";
		
		String jsonString = EntityFactory.createJSONStringForEntity(instance);
		
		assertEquals(expectedJson, jsonString);
		
		InterfaceWithDefaultConcreteType result = EntityFactory.createEntityFromJSONString(jsonString, InterfaceWithDefaultConcreteType.class);
	
		assertEquals(DefaultConcreteTypeImpl.class, result.getClass());
		
	}

}
