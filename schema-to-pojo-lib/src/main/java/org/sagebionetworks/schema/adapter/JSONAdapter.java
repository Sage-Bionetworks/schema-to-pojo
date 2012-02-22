package org.sagebionetworks.schema.adapter;

import java.util.Date;

import org.sagebionetworks.schema.FORMAT;

public interface JSONAdapter {
	
	/**
	 * Create a new Object.
	 * @return
	 */
	public JSONObjectAdapter createNew();
	
	/**
	 * Create a new Adapter using the passed JSON String
	 * @param json
	 * @return
	 * @throws JSONObjectAdapterException 
	 */
	public JSONObjectAdapter createNew(String json) throws JSONObjectAdapterException;
	
	/**
	 * Create a new array.
	 * @return
	 */
	public JSONArrayAdapter createNewArray();
	
	/**
	 * Write this object to its JSON string.
	 * @return
	 */
	public String toJSONString();
	
	/**
	 * Convert a Date to a string of the given format.
	 * @param format
	 * @param toFormat
	 * @return
	 */
	public String convertDateToString(FORMAT format, Date toFormat);
	
	/**
	 * Convert a String to a Date of the given format.
	 * @param format
	 * @param toFormat
	 * @return
	 */
	public Date convertStringToDate(FORMAT format, String toFormat);
}
