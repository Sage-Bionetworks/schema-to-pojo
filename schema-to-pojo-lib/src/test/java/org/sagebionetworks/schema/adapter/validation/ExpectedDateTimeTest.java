package org.sagebionetworks.schema.adapter.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.sagebionetworks.schema.FORMAT;

public class ExpectedDateTimeTest {
	
	@Test
	public void testDateTimeConversion(){	
		// Test each format
		Iterator<String> it = ExpectedDateTime.getExpectedFormatedString(FORMAT.DATE_TIME);
		while(it.hasNext()){
			String dateTimeStrings = it.next();
			Date expectedDate = ExpectedDateTime.getExpectedDateForString(FORMAT.DATE_TIME, dateTimeStrings);
			assertNotNull(expectedDate);
			System.out.println(dateTimeStrings);
			System.out.println(expectedDate.getTime());
			// Make sure we get the same conversion
			DateTime dateTime = ISODateTimeFormat.dateTime().parseDateTime(dateTimeStrings);
			Date resultDate = dateTime.toDate();
			assertEquals(resultDate, expectedDate);
		}
	}
	
	@Test
	public void testDateConversion(){
		Iterator<String> it = ExpectedDateTime.getExpectedFormatedString(FORMAT.DATE);
		while(it.hasNext()){
			String dateStrings = it.next();
			Date expectedDate = ExpectedDateTime.getExpectedDateForString(FORMAT.DATE, dateStrings);
			assertNotNull(expectedDate);
			System.out.println(dateStrings);
			System.out.println(expectedDate.getTime());
			// Make sure we get the same conversion
			DateTime dateTime = ISODateTimeFormat.date().parseDateTime(dateStrings);
			Date resultDate = dateTime.toDate();
			System.out.println(resultDate.getTime());
			assertEquals(resultDate, expectedDate);
		}
	}
	
	@Test
	public void testTimeConversion(){
		// Used to create dates. conversion test.
	
		// Test each format
		Iterator<String> it = ExpectedDateTime.getExpectedFormatedString(FORMAT.TIME);
		while(it.hasNext()){
			String dateTimeStrings = it.next();
			Date expectedDate = ExpectedDateTime.getExpectedDateForString(FORMAT.TIME, dateTimeStrings);
			assertNotNull(expectedDate);
			System.out.println(dateTimeStrings);
			System.out.println(expectedDate.getTime());
			// Make sure we get the same conversion
			DateTime dateTime = ISODateTimeFormat.time().parseDateTime(dateTimeStrings);
			Date resultDate = dateTime.toDate();
			System.out.println(resultDate.getTime());
			assertEquals(resultDate, expectedDate);
		}
	}

}
