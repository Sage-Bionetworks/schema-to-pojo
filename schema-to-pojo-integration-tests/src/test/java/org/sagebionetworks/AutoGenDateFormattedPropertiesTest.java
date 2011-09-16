package org.sagebionetworks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

/**
 * Test for the auto-generated class DateFormattedProperties.json
 * @author jmhill
 *
 */
public class AutoGenDateFormattedPropertiesTest {

	@Test
	public void testRoundTrip() throws JSONObjectAdapterException{
		// Make sure we can make a round trip with this class.
		DateFormattedProperties dfp = new DateFormattedProperties();
		String[] dateStrings = new String[]{"2011-09-16T19:49:01.154Z", "1891-01-01", "20:00:59.000Z", "1985-02-23T06:01:01.999Z"};
		
		// Set a date-time
		dfp.setStringAsDateTime(ISODateTimeFormat.dateTime().parseDateTime(dateStrings[0]).toDate());
		// Set a date
		dfp.setStringAsDate(ISODateTimeFormat.date().parseDateTime(dateStrings[1]).toDate());
		// Set a time
		dfp.setStringAsTime(ISODateTimeFormat.time().parseDateTime(dateStrings[2]).toDate());
		// Set a UTC
		dfp.setStringAsUtcMillisec(ISODateTimeFormat.dateTime().parseDateTime(dateStrings[3]).toDate());
		
		// Now write this to JSON
		// Now make sure we can go to JSON and back
		String json = EntityFactory.createJSONStringForEntity(dfp);		
		assertNotNull(json);
		System.out.println(json);
		// Now make clone from the json
		DateFormattedProperties clone = EntityFactory.createEntityFromJSONString(json, DateFormattedProperties.class);
		assertNotNull(clone);
		// The clone should match the new object
		assertEquals(dfp, clone);
	}
}
