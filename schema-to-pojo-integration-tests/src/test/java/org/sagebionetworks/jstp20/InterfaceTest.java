package org.sagebionetworks.jstp20;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

/**
 * Tests for JSTP-20 wich supports fields declared as interfaces or abstract classes.
 * 
 * @author John
 *
 */
public class InterfaceTest {
	
	
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
	
	@Test
	public void testArraOfInterfaces() throws JSONObjectAdapterException{
		// This pojo has a list of interfaces with different implement types
		HasListOfInterface pojo = new HasListOfInterface();
		// First implementation
		OneImpl one = new OneImpl();
		one.setFromInterface("abc");
		one.setFromOne("123");
		// second implementation
		TwoImpl two = new TwoImpl();
		two.setFromInterface("xyz");
		two.setFromTwo("456");
		// Add them both to the list
		pojo.setList(new LinkedList<SomeInterface>());
		pojo.getList().add(one);
		pojo.getList().add(two);
		
		// Make the round trip
		String json = EntityFactory.createJSONStringForEntity(pojo);
		System.out.println(json);
		HasListOfInterface clonePojo = EntityFactory.createEntityFromJSONString(json, HasListOfInterface.class);
		assertNotNull(clonePojo);
		assertEquals(pojo, clonePojo);
	}
}
