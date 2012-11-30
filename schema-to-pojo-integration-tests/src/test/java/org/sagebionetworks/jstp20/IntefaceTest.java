package org.sagebionetworks.jstp20;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class IntefaceTest {
	
	
	@Test
	public void testInterfaces() throws IOException, JSONObjectAdapterException, ClassNotFoundException{
		
		// do a round trip test
		HasInterfaceField pojo = new HasInterfaceField();
		OneImpl one = new OneImpl();
		one.setFromInterface("abc");
		one.setFromOne("123");
		pojo.setInterfaceField(one);
		
		String json = EntityFactory.createJSONStringForEntity(pojo);
		System.out.println(json);
		HasInterfaceField clonePojo = EntityFactory.createEntityFromJSONString(json, HasInterfaceField.class);
		assertNotNull(clonePojo);
		assertEquals(pojo, clonePojo);
	}
}
