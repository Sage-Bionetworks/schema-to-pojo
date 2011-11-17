package org.sagebionetworks.schema;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class PatternObjectTest {

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
	 * Tests that pattern correctly gets set for a patternObject when 
	 * initializeFromJSONObject gets called with an adapter that 
	 * has a valid instance of the patternObject's pattern.
	 */
	@Test
	public void testInitializeFromJSONObjectSetsValidPattern() throws Exception {
		//make patternObject object
		PatternObject objectWithAPattern = new PatternObject();
		
		//verify state of pattern is null
		assertNull(objectWithAPattern.getPatternPropertyOne());
		
		//make an adapter that has a valid instance of the pattern
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		adapter.put("booleanProperty", true);
		adapter.put("patternPropertyOne", "aaaaaaaaaaaaab");
		
		//initialize
		objectWithAPattern.initializeFromJSONObject(adapter);
		
		//verify the pattern property was set
		assertNotNull(objectWithAPattern.getPatternPropertyOne());
		assertEquals("aaaaaaaaaaaaab", objectWithAPattern.getPatternPropertyOne());
	}
	
	/**
	 * Tests that a error gets thrown when a patternObject tries to 
	 * call initializeFromJSONObject and the adapter has an invalid
	 * instance of the pattern.
	 */
	@Test (expected = JSONObjectAdapterException.class)
	public void testInitializeFromJSONObjectWithInvalidAdapterPattern() throws Exception {
		//make patternObject object
		PatternObject objectWithAPattern = new PatternObject();
		
		//verify state of pattern is null
		assertNull(objectWithAPattern.getPatternPropertyOne());
		
		//make an adapter that has a valid instance of the pattern
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl();
		adapter.put("booleanProperty", true);
		adapter.put("patternPropertyOne", "ccaaaaaaaaaaaaab");
		
		//initialize
		objectWithAPattern.initializeFromJSONObject(adapter);
	}
}
