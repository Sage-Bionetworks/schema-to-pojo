package org.sagebionetworks.gwt.client.schema.adapter;

import java.util.Date;

import org.sagebionetworks.schema.FORMAT;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateUtils {
	private static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
	private static final DateTimeFormat TIME_FORMAT = DateTimeFormat.getFormat("HH:mm:ss.SSSZZ");
		
	/**
	 * Convert a date to a string of the given format.
	 * @param format
	 * @param toFormat
	 * @return
	 */
	public static String convertDateToString(FORMAT format, Date toFormat) {
		if(format == null) throw new IllegalArgumentException("FORMAT cannot be null");
		if(toFormat == null) throw new IllegalArgumentException("Date cannot be null");
		if(!format.isDateFormat()) throw new IllegalArgumentException("Not a date format: "+format.name());
		if(FORMAT.DATE_TIME == format){
			return DATE_TIME_FORMAT.format(toFormat);
		}else if(FORMAT.DATE == format){
			return DATE_FORMAT.format(toFormat);
		}else if(FORMAT.TIME == format){
			return TIME_FORMAT.format(toFormat);
		}else if(FORMAT.UTC_MILLISEC == format){
			return ""+toFormat.getTime();
		}else{
			throw new IllegalArgumentException("Unknown date format: "+format.name());
		}
	}

	/**
	 * Convert a string to a date of a given format
	 * @param format
	 * @param toFormat
	 * @return
	 */
	public static Date convertStringToDate(FORMAT format, String toFormat) {		
		if(format == null) throw new IllegalArgumentException("FORMAT cannot be null");
		if(toFormat == null) throw new IllegalArgumentException("Date cannot be null");
		if(!format.isDateFormat()) throw new IllegalArgumentException("Not a date format: "+format.name());
		// GWT doesn't understand Zulu time, replace with GMT 
		// http://stackoverflow.com/questions/4959073/gwt-datetimeformat-throws-illegalargumentexception-when-date-value-contains-z
		if (toFormat.endsWith("Z")) {
			toFormat = toFormat.substring(0, toFormat.length()-1) + "GMT-00:00";
		}
		if(FORMAT.DATE_TIME == format){
			return DATE_TIME_FORMAT.parse(toFormat);
		}else if(FORMAT.DATE == format){
			return DATE_FORMAT.parse(toFormat);
		}else if(FORMAT.TIME == format){
			return TIME_FORMAT.parse(toFormat);
		}else if(FORMAT.UTC_MILLISEC == format){
			long time = Long.parseLong(toFormat);
			return new Date(time);
		}else{
			throw new IllegalArgumentException("Unknown date format: "+format.name());
		}
	}

}
