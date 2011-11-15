package org.sagebionetworks.schema;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class DefaultObjectTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Tests that DefaultObject's initializeFromJSONObject correctly
	 * sets defaults when adapter does not have anything set to corresponding
	 * fields as classes member variables.  Also verifies all the fields
	 * that do not have defaults assigned do not get set.
	 * @throws Exception
	 */
	@Test
	public void testDefaultsGetSet() throws Exception {
		//make a defaultObject that has all member variables = null
		DefaultObject defaultObject = new DefaultObject();
		assertNull(defaultObject.getStringWithDefault());
		assertNull(defaultObject.getDoublePropWithDefault());
		assertNull(defaultObject.getLongPropWithDefault());
		assertNull(defaultObject.getBoolPropWithDefault());
		assertNull(defaultObject.getStringWithoutDefault());
		assertNull(defaultObject.getDoublePropWithoutDefault());
		assertNull(defaultObject.getLongPropWithoutDefault());
		assertNull(defaultObject.getBoolePropWithoutDefault());
		
		//create an adapter that had nothing set
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		
		//should set the defaults
		defaultObject.initializeFromJSONObject(adapter);
		
		//verify everything was set to defaults
		assertNotNull(defaultObject.getStringWithDefault());
		assertEquals("hello", defaultObject.getStringWithDefault());
		
		assertNotNull(defaultObject.getDoublePropWithDefault());
		assertEquals((Double)7.77, defaultObject.getDoublePropWithDefault());
		
		assertNotNull(defaultObject.getLongPropWithDefault());
		Long param = defaultObject.getLongPropWithDefault();
		assertEquals(param, defaultObject.getLongPropWithDefault());
		
		assertNotNull(defaultObject.getBoolPropWithDefault());
		assertEquals(false, defaultObject.getBoolPropWithDefault());
		
		//verify nonDefault member variables are still null
		assertNull(defaultObject.getStringWithoutDefault());
		assertNull(defaultObject.getDoublePropWithoutDefault());
		assertNull(defaultObject.getLongPropWithoutDefault());
		assertNull(defaultObject.getBoolePropWithoutDefault());
		
	}
	
	/**
	 * Tests that defaults do no get set when an defaultObject calls
	 * initializeFromJSONObject with an adapter that has values for 
	 * corresponding fields.  Also verifies fields that do not have
	 * default values do not get set.
	 * @throws Exception
	 */
	@Test
	public void testDefaultsDontGetSet() throws Exception {
		//make a defaultObject that has all member variables = null
		DefaultObject defaultObject = new DefaultObject();
		assertNull(defaultObject.getStringWithDefault());
		assertNull(defaultObject.getDoublePropWithDefault());
		assertNull(defaultObject.getLongPropWithDefault());
		assertNull(defaultObject.getBoolPropWithDefault());
		assertNull(defaultObject.getStringWithoutDefault());
		assertNull(defaultObject.getDoublePropWithoutDefault());
		assertNull(defaultObject.getLongPropWithoutDefault());
		assertNull(defaultObject.getBoolePropWithoutDefault());
		
		//make an adapter that has values for all fields
		//that would normally recieve a default value
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		adapter.put("stringWithDefault", "imNotTheDefault");
		adapter.put("doublePropWithDefault", 8.88D);
		adapter.put("longPropWithDefault", 88L);
		adapter.put("boolPropWithDefault", true);
		
		//should set to adapter's values
		defaultObject.initializeFromJSONObject(adapter);
		
		//verify everything is in the correct state
		assertNotNull(defaultObject.getStringWithDefault());
		assertEquals("imNotTheDefault", defaultObject.getStringWithDefault());
		assertNull(defaultObject.getStringWithoutDefault());
		
		assertNotNull(defaultObject.getDoublePropWithDefault());
		Double doubleParam = 8.88;
		assertEquals(doubleParam, defaultObject.getDoublePropWithDefault());
		assertNull(defaultObject.getDoublePropWithoutDefault());
		
		assertNotNull(defaultObject.getLongPropWithDefault());
		Long longParam = (long) 88;
		assertEquals(longParam, defaultObject.getLongPropWithDefault());
		assertNull(defaultObject.getLongPropWithoutDefault());
		
		assertNotNull(defaultObject.getBoolPropWithDefault());
		assertEquals(true, defaultObject.getBoolPropWithDefault());
		assertNull(defaultObject.getBoolePropWithoutDefault());
	}
	
	/**
	 * Tests that when initializeFromJSONObject method gets called
	 * for defaultObject it correctly handles default for string
	 * variable that has a default defined.  Also verifies the 
	 * string variable that does not have a default defined, 
	 * becomes null.
	 * @throws Exception
	 */
	@Test
	public void testStringDefault() throws Exception {
		//create a defaultObject and make sure both string
		//variables are null
		DefaultObject defaultObject = new DefaultObject();
		defaultObject.setStringWithDefault(null);
		defaultObject.setStringWithoutDefault(null);
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		defaultObject.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = JSONObjectAdapterImpl.createAdapterFromJSONString(json);
		DefaultObject clone = new DefaultObject(adapter);
		
		//now verify the clone has the default value for stringWithDefault
		//verify clone has null for the stringWithoutDefault
		assertNull(clone.getStringWithoutDefault());
		assertEquals("hello", clone.getStringWithDefault());		
	}
	
	/**
	 *  Tests that when initializeFromJSONObject method gets called
	 * for defaultObject it correctly handles default for string
	 * variable that has a predefined value.  Also verifies the 
	 * string variable that does not have a default defined, keeps it's
	 * predefined value 
	 * @throws Exception
	 */
	@Test
	public void testStringDefaultWithValues() throws Exception {
		//create a defaultObject that makes sure both 
		//string variables have something defined
		DefaultObject defaultObject = new DefaultObject();
		defaultObject.setStringWithDefault("yo");
		defaultObject.setStringWithoutDefault("same");
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		defaultObject.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = JSONObjectAdapterImpl.createAdapterFromJSONString(json);
		DefaultObject clone = new DefaultObject(adapter);
		
		//now verify clone has the defined value for stringWithDefault
		//verify clone has original "same" string for string withoutDefault
		assertEquals("yo", clone.getStringWithDefault());
		assertEquals("same", clone.getStringWithoutDefault());
	}
	
	/**
	 * Tests that when initializeFromJSONObject method gets called
	 * for defaultObject it correctly handles default for number
	 * variable that has a default defined.  Also verifies the 
	 * number variable that does not have a default defined, 
	 * becomes null.
	 * @throws Exception
	 */
	@Test
	public void testNumberDefault() throws Exception {
		//create a defaultObject and make sure both number
		//variables are null
		DefaultObject defaultObject = new DefaultObject();
		defaultObject.setDoublePropWithDefault(null);
		defaultObject.setDoublePropWithoutDefault(null);
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		defaultObject.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = JSONObjectAdapterImpl.createAdapterFromJSONString(json);
		DefaultObject clone = new DefaultObject(adapter);
		
		//now verify the clone has the default value for double with default
		//verify clone has null for the double without default
		assertNull(clone.getDoublePropWithoutDefault());
		Double doubleParam = 7.77;
		assertEquals(doubleParam, clone.getDoublePropWithDefault());		
	}
	
	/**
	 *  Tests that when initializeFromJSONObject method gets called
	 * for defaultObject it correctly handles default for number
	 * variable that has a predefined value.  Also verifies the 
	 * number variable that does not have a default defined, keeps it's
	 * predefined value 
	 * @throws Exception
	 */
	@Test
	public void testNumberDefaultWithValues() throws Exception {
		//create a defaultObject that makes sure both 
		//number variables have something defined
		DefaultObject defaultObject = new DefaultObject();
		defaultObject.setDoublePropWithDefault(8.88);
		defaultObject.setDoublePropWithoutDefault(9.99);
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		defaultObject.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = JSONObjectAdapterImpl.createAdapterFromJSONString(json);
		DefaultObject clone = new DefaultObject(adapter);
		
		//now verify clone has the defined value for double with default
		//verify clone has original value for double withoutDefault
		Double doubleParam1 = 8.88;
		Double doubleParam2 = 9.99;
		assertEquals(doubleParam1, clone.getDoublePropWithDefault());
		assertEquals(doubleParam2, clone.getDoublePropWithoutDefault());
	}
	
	/**
	 * Tests that when initializeFromJSONObject method gets called
	 * for defaultObject it correctly handles default for integer
	 * variable that has a default defined.  Also verifies the 
	 * integer variable that does not have a default defined, 
	 * becomes null.
	 * @throws Exception
	 */
	@Test
	public void testIntegerDefault() throws Exception {
		//create a defaultObject and make sure both integer
		//variables are null
		DefaultObject defaultObject = new DefaultObject();
		defaultObject.setLongPropWithDefault(null);
		defaultObject.setLongPropWithoutDefault(null);
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		defaultObject.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = JSONObjectAdapterImpl.createAdapterFromJSONString(json);
		DefaultObject clone = new DefaultObject(adapter);
		
		//now verify the clone has the default value for integer with default
		//verify clone has null for the integer without default
		assertNull(clone.getLongPropWithoutDefault());
		Long longParam = (long) 77;
		assertEquals(longParam, clone.getLongPropWithDefault());		
	}
	
	/**
	 *  Tests that when initializeFromJSONObject method gets called
	 * for defaultObject it correctly handles default for integer
	 * variable that has a predefined value.  Also verifies the 
	 * integer variable that does not have a default defined, keeps it's
	 * predefined value 
	 * @throws Exception
	 */
	@Test
	public void testIntegerDefaultWithValues() throws Exception {
		//create a defaultObject that makes sure both 
		//integer variables have something defined
		DefaultObject defaultObject = new DefaultObject();
		Long intProp1 = (long) 88;
		Long intProp2 = (long) 99;
		defaultObject.setLongPropWithDefault(intProp1);
		defaultObject.setLongPropWithoutDefault(intProp2);
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		defaultObject.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = JSONObjectAdapterImpl.createAdapterFromJSONString(json);
		DefaultObject clone = new DefaultObject(adapter);
		
		//now verify clone has the defined value for int with default
		//verify clone has original value for int without default
		assertEquals(intProp1, clone.getLongPropWithDefault());
		assertEquals(intProp2, clone.getLongPropWithoutDefault());
	}
	
	/**
	 * Tests that when initializeFromJSONObject method gets called
	 * for defaultObject it correctly handles default for boolean
	 * variable that has a default defined.  Also verifies the 
	 * boolean variable that does not have a default defined, 
	 * becomes null.
	 * @throws Exception
	 */
	@Test
	public void testBooleanDefault() throws Exception {
		//create a defaultObject and make sure both boolean
		//variables are null
		DefaultObject defaultObject = new DefaultObject();
		defaultObject.setBoolPropWithDefault(null);
		defaultObject.setBoolePropWithoutDefault(null);
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		defaultObject.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = JSONObjectAdapterImpl.createAdapterFromJSONString(json);
		DefaultObject clone = new DefaultObject(adapter);
		
		//now verify the clone has the default value for boolean with default
		//verify clone has null for the boolean without default
		assertNull(clone.getBoolePropWithoutDefault());
		assertEquals(false, clone.getBoolPropWithDefault());		
	}
	
	/**
	 *  Tests that when initializeFromJSONObject method gets called
	 * for defaultObject it correctly handles default for boolean
	 * variable that has a predefined value.  Also verifies the 
	 * boolean variable that does not have a default defined, keeps it's
	 * predefined value 
	 * @throws Exception
	 */
	@Test
	public void testBooleanDefaultWithValues() throws Exception {
		//create a defaultObject that makes sure both 
		//boolean variables have something defined
		DefaultObject defaultObject = new DefaultObject();
		defaultObject.setBoolPropWithDefault(true);
		defaultObject.setBoolePropWithoutDefault(true);
		
		// Now create a clone by going to JSON
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		defaultObject.writeToJSONObject(adapter);
		String json = adapter.toJSONString();
		System.out.println(json);
		// Now make the round trip
		adapter = JSONObjectAdapterImpl.createAdapterFromJSONString(json);
		DefaultObject clone = new DefaultObject(adapter);
		
		//now verify clone has the defined value for boolean with default
		//verify clone has original value for boolean without default
		assertEquals(true, clone.getBoolPropWithDefault());
		assertEquals(true, clone.getBoolePropWithoutDefault());
	}
}
