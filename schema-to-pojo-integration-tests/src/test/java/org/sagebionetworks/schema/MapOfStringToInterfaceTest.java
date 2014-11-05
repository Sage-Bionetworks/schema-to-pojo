package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.sagebionetworks.ABImpl;
import org.sagebionetworks.ABImpl2;
import org.sagebionetworks.InterfaceA;
import org.sagebionetworks.MapOfStringToInterface;
import org.sagebionetworks.schema.adapter.JSONAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class MapOfStringToInterfaceTest {
	
	@Test
	public void testRoundTrip() throws Exception {
		MapOfStringToInterface map = new MapOfStringToInterface();

		map.setMap(new HashMap<String, InterfaceA>());
		ABImpl value = new ABImpl();
		value.setFromMe("aa");
		map.getMap().put("a", value);
		ABImpl2 value2 = new ABImpl2();
		value2.setFromMe2("bb");
		map.getMap().put("b", value);

		map.setMapConcrete(new HashMap<String, ABImpl>());
		ABImpl concreteValue = new ABImpl();
		concreteValue.setFromMe("cc");
		map.getMapConcrete().put("c", concreteValue);

		// Now make the round trip
		String jsonString = EntityFactory.createJSONStringForEntity(map);
		assertNotNull(jsonString);
		System.out.println(jsonString);

		// Clone it
		MapOfStringToInterface clone = EntityFactory.createEntityFromJSONString(jsonString, MapOfStringToInterface.class);
		assertNotNull(clone);
		System.out.println(EntityFactory.createJSONStringForEntity(clone));

		assertEquals(map, clone);
	}

	@Test
	public void testRoundTripWithNulls() throws Exception {
		MapOfStringToInterface map = new MapOfStringToInterface();

		map.setMap(new HashMap<String, InterfaceA>());
		ABImpl value = new ABImpl();
		value.setFromMe("aa");
		map.getMap().put("a", value);
		ABImpl2 value2 = new ABImpl2();
		value2.setFromMe2("bb");
		map.getMap().put("b", null);

		map.setMapConcrete(new HashMap<String, ABImpl>());
		ABImpl concreteValue = new ABImpl();
		concreteValue.setFromMe("cc");
		map.getMapConcrete().put("c", concreteValue);
		map.getMapConcrete().put("d", null);

		// Now make the round trip
		String jsonString = EntityFactory.createJSONStringForEntity(map);
		assertNotNull(jsonString);
		System.out.println(jsonString);

		// Clone it
		MapOfStringToInterface clone = EntityFactory.createEntityFromJSONString(jsonString, MapOfStringToInterface.class);
		assertNotNull(clone);
		System.out.println(EntityFactory.createJSONStringForEntity(clone));

		assertEquals(map, clone);
	}
}
