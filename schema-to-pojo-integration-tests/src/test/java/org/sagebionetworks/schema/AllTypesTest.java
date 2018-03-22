package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sagebionetworks.ABImpl;
import org.sagebionetworks.AllTypes;
import org.sagebionetworks.InterfaceA;
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
		List<Date> dateList = new ArrayList<Date>();
		dateList.add(new Date(System.currentTimeMillis()));
		dateList.add(new Date(System.currentTimeMillis()+2324));
		allTypes.setDateList(dateList);
		// Date 2
		dateList = new ArrayList<Date>();
		dateList.add(new Date(System.currentTimeMillis()-23234));
		dateList.add(new Date(System.currentTimeMillis()-1232));
		allTypes.setDateList2(dateList);
		// interfaces
		ABImpl impl1 = new ABImpl();
		impl1.setFromMe("mine1");
		ABImpl impl2 = new ABImpl();
		impl2.setFromMe("mine2");
		List<InterfaceA> interfaces = new ArrayList<InterfaceA>();
		interfaces.add(impl1);
		interfaces.add(impl2);
		allTypes.setInterfaceList(interfaces);
		List<StandaloneEnum> enumList = new ArrayList<StandaloneEnum>();
		enumList.add(StandaloneEnum.one);
		enumList.add(StandaloneEnum.two);
		enumList.add(StandaloneEnum.three);
		allTypes.setEnumList(enumList);

		allTypes.setLongAsDate(new Date(System.currentTimeMillis()));
		// Long list
		allTypes.setLongList(new ArrayList<Long>());
		allTypes.getLongList().add(new Long(99));
		// Double List
		allTypes.setDoubleList(new ArrayList<Double>());
		allTypes.getDoubleList().add(new Double(99.77));
		// string integer map
		Map<String, Long> stringIntegerMap = new LinkedHashMap<String, Long>();
		stringIntegerMap.put("a", 20L);
		stringIntegerMap.put("b", 30L);
		allTypes.setStringIntegerMap(stringIntegerMap);
		// object boolean map
		Map<String, Boolean> enumBooleanMap = new LinkedHashMap<String, Boolean>();
		enumBooleanMap.put("DOG", true);
		enumBooleanMap.put("Cat", false);
		allTypes.setEnumBooleanMap(enumBooleanMap);
		Map<String, StandaloneEnum> objectEnumMap = new LinkedHashMap<String, StandaloneEnum>();
		objectEnumMap.put("a", StandaloneEnum.four);
		objectEnumMap.put("20", StandaloneEnum.two);
		allTypes.setObjectEnumMap(objectEnumMap);
	
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		allTypes.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = new JSONObjectAdapterImpl(json);
		AllTypes clone = new AllTypes(adapter);
		adapter = new JSONObjectAdapterImpl();
		clone.writeToJSONObject(adapter);
		System.out.println(adapter.toJSONString());
		
		assertEquals(allTypes, clone);
		// Try equals both ways
		assertTrue(clone.equals(allTypes));
		assertTrue(allTypes.equals(clone));
		// Change the clone and test again
		clone.setBooleanProp(false);
		assertFalse(allTypes.equals(clone));
		assertFalse(clone.equals(allTypes));
		
	}

	@Test
	public void testAllTypesRoundTripWithDoubleNaN() throws JSONObjectAdapterException{
		AllTypes allTypes = new AllTypes();
		allTypes.setDoubleProp(Double.NaN);
	
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
		
	}

	@Test
	public void testAllTypesRoundTripWithDoubleInfinity() throws JSONObjectAdapterException{
		AllTypes allTypes = new AllTypes();
		allTypes.setDoubleProp(Double.POSITIVE_INFINITY);
	
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
		
	}

	@Test
	public void testAllTypesRoundTripWithDoubleNegativeInfinity() throws JSONObjectAdapterException{
		AllTypes allTypes = new AllTypes();
		allTypes.setDoubleProp(Double.NEGATIVE_INFINITY);
	
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
		
	}

	@Test
	public void testAllTypesRoundTripWithExtraFields() throws JSONObjectAdapterException {
		AllTypes allTypes = new AllTypes();
		allTypes.setStringProp("somevalue");

		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		allTypes.writeToJSONObject(adapter);
		adapter.put("extra_field", "value");
		String json = adapter.toJSONString();
		// Now make the round trip
		adapter = new JSONObjectAdapterImpl(json);
		AllTypes clone = new AllTypes(adapter);
		JSONObjectAdapter cloneAdapter = new JSONObjectAdapterImpl();
		clone.writeToJSONObject(cloneAdapter);
		String cloneJson = cloneAdapter.toJSONString();
		assertEquals("{\"extra_field\":\"value\",\"stringProp\":\"somevalue\"}", cloneJson);
	}

	@Test
	public void testAllTypesRoundTripWithAllNulls() throws JSONObjectAdapterException {
		AllTypes allTypes = new AllTypes();
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		allTypes.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		// Now make the round trip
		adapter = new JSONObjectAdapterImpl(json);
		AllTypes clone = new AllTypes(adapter);
		assertEquals(allTypes, clone);
		assertTrue(clone.equals(allTypes));
		assertTrue(allTypes.equals(clone));
	}

	@Test
	public void testAllTypesRoundTripWithNulls() throws JSONObjectAdapterException {
		AllTypes allTypes = new AllTypes();
		allTypes.setListOfStrings(new ArrayList<String>());
		allTypes.getListOfStrings().add("list value");
		allTypes.getListOfStrings().add(null);
		allTypes.getListOfStrings().add("another value");
		List<Date> dateList = new ArrayList<Date>();
		dateList.add(new Date(System.currentTimeMillis()));
		dateList.add(null);
		allTypes.setDateList(dateList);
		// Long list
		allTypes.setLongList(new ArrayList<Long>());
		allTypes.getLongList().add(new Long(99));
		allTypes.getLongList().add(null);
		// Double List
		allTypes.setDoubleList(new ArrayList<Double>());
		allTypes.getDoubleList().add(new Double(99.77));
		allTypes.getDoubleList().add(new Double(Double.NaN));
		allTypes.getDoubleList().add(new Double(Double.POSITIVE_INFINITY));
		allTypes.getDoubleList().add(new Double(Double.NEGATIVE_INFINITY));
		allTypes.getDoubleList().add(null);
		ABImpl impl1 = new ABImpl();
		impl1.setFromMe("mine1");
		ABImpl impl2 = new ABImpl();
		impl2.setFromMe("mine2");
		List<InterfaceA> interfaces = new ArrayList<InterfaceA>();
		interfaces.add(impl1);
		interfaces.add(null);
		interfaces.add(impl2);
		allTypes.setInterfaceList(interfaces);
		List<StandaloneEnum> enumList = new ArrayList<StandaloneEnum>();
		enumList.add(StandaloneEnum.one);
		enumList.add(null);
		enumList.add(StandaloneEnum.three);
		allTypes.setEnumList(enumList);

		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		allTypes.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		// Now make the round trip
		adapter = new JSONObjectAdapterImpl(json);
		AllTypes clone = new AllTypes(adapter);
		assertEquals(allTypes, clone);
		// Try equals both ways
		assertTrue(clone.equals(allTypes));
		assertTrue(allTypes.equals(clone));
	}
}
