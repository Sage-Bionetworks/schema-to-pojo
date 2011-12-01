package org.sagebionetworks.schema;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ToStringTesterClassTest {

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
	 * Tests that toString works for ToStringTesterClass
	 */
	@Test
	public void testToStringForToStringTesterClass() throws Exception {
		ToStringTesterClass tc = new ToStringTesterClass();
		
		tc.setId((long) 7);
		tc.setName("imTheName");
		tc.setPrice(77.77);
		ArrayList<String> newTags = new ArrayList<String>();
		newTags.add("funTag");
		newTags.add("happyTag");
		newTags.add("sadTag");
		tc.setTags(newTags);
		
		String theClass = tc.toString();
		System.out.println(theClass);
		
		//verify that the string looks as expected
		assertTrue(theClass.indexOf("[id=7 tags=" +
				"[funTag, happyTag, sadTag] price=77.77 " +
				"name=imTheName randomProp=null ]") > 0);
	}
}
