package org.sagebionetworks.schema.adapter.org.json;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.sagebionetworks.schema.FORMAT;

public class JsonDateUtilsTest {

	@Test
	public void testDateUtilsDateTime(){
		// Make sure we can do a round trip for each date type
		Date now = new Date(System.currentTimeMillis());
		String dateString = JsonDateUtils.convertDateToString(FORMAT.DATE_TIME, now);
		Date clone = JsonDateUtils.convertStringToDate(FORMAT.DATE_TIME, dateString);
		assertEquals(now, clone);
	}
	
	@Test
	public void testDateUtilsUTC(){
		// Make sure we can do a round trip for each date type
		Date now = new Date(System.currentTimeMillis());
		String dateString = JsonDateUtils.convertDateToString(FORMAT.UTC_MILLISEC, now);
		Date clone = JsonDateUtils.convertStringToDate(FORMAT.UTC_MILLISEC, dateString);
		assertEquals(now, clone);
	}
}
