package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.sagebionetworks.ABImpl;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class ABImplTest {
	
	@Test
	public void testEquals(){
		ABImpl a = new ABImpl();
		ABImpl b = new ABImpl();
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		
		// Change the values
		a.setAlsoFromInterfaceA(123.76);
		assertFalse(a.equals(b));
		
		// Make them equal
		b.setAlsoFromInterfaceA(a.getAlsoFromInterfaceA());
		assertTrue(a.equals(b));
		
		// Change the values
		a.setAlsoFromInterfaceB(new Date(0));
		assertFalse(a.equals(b));
		
		// Make them equal
		b.setAlsoFromInterfaceB(a.getAlsoFromInterfaceB());
		assertTrue(a.equals(b));
		
		// Change the values
		a.setFromMe("me");
		assertFalse(a.equals(b));
		
		// Make them equal
		b.setFromMe(a.getFromMe());
		assertTrue(a.equals(b));
		
	}
	
	@Test
	public void testRoundTrip() throws JSONObjectAdapterException{
		ABImpl impl = new ABImpl();
		impl.setFromMe("from me value");
		impl.setAlsoFromInterfaceA(123.456);
		impl.setFromInterfaceA("from A value");
		impl.setAlsoFromInterfaceB(new Date(0));
		impl.setFromInterfaceB("from B value");
		
		// Now make the round trip
		String jsonString = EntityFactory.createJSONStringForEntity(impl);
		assertNotNull(jsonString);
		System.out.println(jsonString);
		// Clone it
		ABImpl clone = EntityFactory.createEntityFromJSONString(jsonString, ABImpl.class);
		assertNotNull(clone);
		System.out.println(EntityFactory.createJSONStringForEntity(clone));
		assertNotNull(clone.getFromInterfaceA());
		assertNotNull(clone.getFromInterfaceB());

		assertEquals(impl, clone);
	}
	
	@Test
	public void testGetJSONSchema() throws JSONObjectAdapterException{
		ABImpl impl = new ABImpl();
		String json = impl.getJSONSchema();
		assertNotNull(json);
		ObjectSchema schema = new ObjectSchema(JSONObjectAdapterImpl.createAdapterFromJSONString(json));
		assertNotNull(schema);
		assertNotNull(schema.getProperties());
		assertNotNull(schema.getProperties().get("alsoFromInterfaceA"));
		assertNotNull(schema.getProperties().get("alsoFromInterfaceB"));
		assertNotNull(schema.getProperties().get("fromInterfaceB"));
		assertNotNull(schema.getProperties().get("fromInterfaceA"));
	}

}
