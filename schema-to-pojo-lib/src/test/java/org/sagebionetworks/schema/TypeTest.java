package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;


public class TypeTest {
	
	@Test
	public void testDateTypes(){
		HashSet<FORMAT> dateTypes = new HashSet<FORMAT>();
		dateTypes.add(FORMAT.DATE);
		dateTypes.add(FORMAT.DATE_TIME);
		dateTypes.add(FORMAT.TIME);
		dateTypes.add(FORMAT.UTC_MILLISEC);
		for(FORMAT format: FORMAT.values()){
			boolean isDateType = dateTypes.contains(format);
			assertEquals(format.name()+" date formate not expected ",isDateType, format.isDateFormat());
			dateTypes.remove(format);
		}
		assertEquals("Did not match to all date types",0,dateTypes.size());
		
	}

}
