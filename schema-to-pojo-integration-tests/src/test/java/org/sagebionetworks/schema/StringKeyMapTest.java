package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.Test;
import org.sagebionetworks.ABImpl;
import org.sagebionetworks.ABImpl2;
import org.sagebionetworks.InterfaceA;
import org.sagebionetworks.MapOfStringToInterface;
import org.sagebionetworks.StringKeyMap;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class StringKeyMapTest {

	@Test
	public void testRoundTrip() throws JSONObjectAdapterException {
		StringKeyMap map = new StringKeyMap();

		map.setStrKeyMapOfInterface(new HashMap<>());
		ABImpl value = new ABImpl();
		value.setFromMe("aa");
		map.getStrKeyMapOfInterface().put("a", value);
		ABImpl2 value2 = new ABImpl2();
		value2.setFromMe2("bb");
		map.getStrKeyMapOfInterface().put("b", value2);

		map.setStrKeyMapOfObject(new HashMap<>());
		ABImpl concreteValue = new ABImpl();
		concreteValue.setFromMe("cc");
		map.getStrKeyMapOfObject().put("c", concreteValue);
		ABImpl concreteValue2 = new ABImpl();
		concreteValue2.setFromMe("dd");
		map.getStrKeyMapOfObject().put("d", concreteValue2);

		map.setStrKeyMapOfInt(new HashMap<>());
		map.getStrKeyMapOfInt().put("firstInt", 5L);
		map.getStrKeyMapOfInt().put("secondInt", 58L);

		map.setStrKeyMapOfString(new HashMap<>());
		map.getStrKeyMapOfString().put("firstStr", "value A");
		map.getStrKeyMapOfString().put("secondStr", "value B");

		// Now make the round trip
		String jsonString = EntityFactory.createJSONStringForEntity(map);
		assertNotNull(jsonString);
		System.out.println(jsonString);

		// Clone it
		StringKeyMap clone = EntityFactory.createEntityFromJSONString(jsonString, StringKeyMap.class);
		assertNotNull(clone);
		System.out.println(EntityFactory.createJSONStringForEntity(clone));

		assertEquals(map, clone);
	}
}
