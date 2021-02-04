package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.sagebionetworks.ABImpl;
import org.sagebionetworks.InterfaceB;
import org.sagebionetworks.schema.adapter.JSONAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class ABImplTest {
	
	@Test
	public void testEquals() throws JSONObjectAdapterException{
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
		
		JSONAdapter adapter = a.writeToJSONObject(new JSONObjectAdapterImpl());
		
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

	/**
	 * Tests that setter can be chained together in a single line
	 */
	@Test
	public void testSetterChaining(){
		ABImpl impl = new ABImpl()
				.setFromMe("from me value")
				.setFromInterfaceA("from A value")
				.setFromInterfaceB("from B value");

		assertEquals("from me value" , impl.getFromMe());
		assertEquals("from A value", impl.getFromInterfaceA());
		assertEquals("from B value", impl.getFromInterfaceB());
	}


}
