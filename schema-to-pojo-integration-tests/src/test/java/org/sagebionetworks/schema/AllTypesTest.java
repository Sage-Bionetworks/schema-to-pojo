package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sagebionetworks.AllTypes;
import org.sagebionetworks.PetType;
import org.sagebionetworks.StandaloneEnum;
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
		allTypes.setLongAsDate(new Date(System.currentTimeMillis()));
		allTypes.setListOfStrings(new ArrayList<String>());
		assertNotNull(allTypes.getListOfStrings());
		allTypes.getListOfStrings().add("list value");
		allTypes.setSetOfStrings(new HashSet<String>());
		assertNotNull(allTypes.getSetOfStrings());
		allTypes.getSetOfStrings().add("set value");
		// Date list
		List<Date> dateList = new ArrayList();
		dateList.add(new Date(System.currentTimeMillis()));
		dateList.add(new Date(System.currentTimeMillis()+2324));
		allTypes.setDateList(dateList);
		// Date 2
		dateList = new ArrayList();
		dateList.add(new Date(System.currentTimeMillis()-23234));
		dateList.add(new Date(System.currentTimeMillis()-1232));
		allTypes.setDateList2(dateList);
		
		allTypes.setLongAsDate(new Date(System.currentTimeMillis()));
		// Long list
		allTypes.setLongList(new ArrayList<Long>());
		allTypes.getLongList().add(new Long(99));
		// Double List
		allTypes.setDoubleList(new ArrayList<Double>());
		allTypes.getDoubleList().add(new Double(99.77));
		// string integer map
		Map<String, Long> stringIntegerMap = new HashMap<String, Long>();
		stringIntegerMap.put("a", 20L);
		stringIntegerMap.put("b", 30L);
		allTypes.setStringIntegerMap(stringIntegerMap);
		// object boolean map
		Map<PetType, Boolean> enumBooleanMap = new HashMap<PetType, Boolean>();
		enumBooleanMap.put(PetType.DOG, true);
		enumBooleanMap.put(PetType.CAT, false);
		allTypes.setEnumBooleanMap(enumBooleanMap);
		Map<Object, StandaloneEnum> objectEnumMap = new HashMap<Object, StandaloneEnum>();
		objectEnumMap.put("a", StandaloneEnum.four);
		objectEnumMap.put(20, StandaloneEnum.two);
		allTypes.setObjectEnumMap(objectEnumMap);
	
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
