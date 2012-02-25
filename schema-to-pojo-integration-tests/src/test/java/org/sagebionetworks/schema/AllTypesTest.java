package org.sagebionetworks.schema;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import org.junit.Test;
import org.sagebionetworks.AllTypes;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class AllTypesTest {
	
	@Test
	public void testAllTypesRoundTrip() throws JSONObjectAdapterException{
		AllTypes allTypes = new AllTypes();
		allTypes.setStringProp("string");
		allTypes.setDoubleProp(123.4);
		allTypes.setBooleanProp(true);
		allTypes.setStringAsDate(new Date());
		allTypes.setListOfStrings(new ArrayList<String>());
		assertNotNull(allTypes.getListOfStrings());
		allTypes.getListOfStrings().add("list value");
		allTypes.setSetOfStrings(new HashSet<String>());
		assertNotNull(allTypes.getSetOfStrings());
		allTypes.getSetOfStrings().add("set value");
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		allTypes.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = new JSONObjectAdapterImpl(json);
		AllTypes clone = new AllTypes(adapter);
		assertEquals(allTypes, clone);
		// Try equals both ways
		assertTrue(clone.equals(allTypes));
		assertTrue(allTypes.equals(clone));
		// Change the clone and test again
		clone.setBooleanProp(false);
		assertFalse(allTypes.equals(clone));
		assertFalse(clone.equals(allTypes));
		
	}

}
