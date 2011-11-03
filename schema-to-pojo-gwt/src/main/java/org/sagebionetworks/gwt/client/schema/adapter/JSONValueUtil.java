package org.sagebionetworks.gwt.client.schema.adapter;

import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * Simple helper to do null value checking.
 * 
 * @author jmhill
 *
 */
public class JSONValueUtil {
	
	/**
	 * Helper to get a String from a JSONValue.
	 * @param value
	 * @param key
	 * @return
	 * @throws JSONObjectAdapterException if the value is null or the value is not a string.
	 */
	public static String getStringValue(JSONValue value, Object key) throws JSONObjectAdapterException{
		if(value == null) throw new JSONObjectAdapterException("No value found for key: "+key);
		JSONString stringValue = value.isString();
		if(stringValue == null) throw new JSONObjectAdapterException("Key: "+key+" exists but is not a string. Value class: "+value.getClass().getName());
		return stringValue.stringValue();
	}
	
	/**
	 * Helper to get a Long from a JSONValue.
	 * @param value
	 * @param key
	 * @return
	 * @throws JSONObjectAdapterException if the value is null or the value is not a long.
	 */
	public static long getLongValue(JSONValue value, Object key) throws JSONObjectAdapterException{
		if(value == null) throw new JSONObjectAdapterException("No value found for key: "+key);
		JSONNumber number = value.isNumber();
		if(number == null) throw new JSONObjectAdapterException("Key: "+key+" exists but is not a number. Value class: "+value.getClass().getName());
		return (long) number.doubleValue();
	}
	
	
	/**
	 * Helper to get a Long from a JSONValue.
	 * @param value
	 * @param key
	 * @return
	 * @throws JSONObjectAdapterException if the value is null or the value is not a long.
	 */
	public static JSONNumber createJSONNumberForLong(long value) throws JSONObjectAdapterException{
		// We are forced to store longs as JSONNumber which are backed by doubles.  This means
		// there is a chance for data loss for large longs.
		double temp = value;
		long fromTemp = (long) temp;
		if(value != fromTemp) throw new JSONObjectAdapterException("The long value: "+value+" is too large to store as a GWT JSONNuber.");
		return new JSONNumber(value);
	}
	
	
	/**
	 * Helper to get an integer from a JSONValue.
	 * @param value
	 * @param key
	 * @return
	 * @throws JSONObjectAdapterException if the value is null or the value is not a integer.
	 */
	public static int getIntValue(JSONValue value, Object key) throws JSONObjectAdapterException{
		if(value == null) throw new JSONObjectAdapterException("No value found for key: "+key);
		JSONNumber number = value.isNumber();
		if(number == null) throw new JSONObjectAdapterException("Key: "+key+" exists but is not a number. Value class: "+value.getClass().getName());
		return (int) number.doubleValue();
	}
	
	/**
	 * Helper to get an integer from a JSONValue.
	 * @param value
	 * @param key
	 * @return
	 * @throws JSONObjectAdapterException if the value is null or the value is not a integer.
	 */
	public static boolean getBooleanValue(JSONValue value, Object key) throws JSONObjectAdapterException{
		if(value == null) throw new JSONObjectAdapterException("No value found for key: "+key);
		JSONBoolean valueType = value.isBoolean();
		if(valueType == null) throw new JSONObjectAdapterException("Key: "+key+" exists but is not a boolean. Value class: "+value.getClass().getName());
		return valueType.booleanValue();
	}
	
	/**
	 * Helper to get an integer from a JSONValue.
	 * @param value
	 * @param key
	 * @return
	 * @throws JSONObjectAdapterException if the value is null or the value is not a integer.
	 */
	public static double getDoubleValue(JSONValue value, Object key) throws JSONObjectAdapterException{
		if(value == null) throw new JSONObjectAdapterException("No value found for key: "+key);
		JSONNumber valueType = value.isNumber();
		if(valueType == null) throw new JSONObjectAdapterException("Key: "+key+" exists but is not a number. Value class: "+value.getClass().getName());
		return valueType.doubleValue();
	}
	
	/**
	 * Helper to get an integer from a JSONArray.
	 * @param value
	 * @param key
	 * @return
	 * @throws JSONObjectAdapterException if the value is null or the value is not a JSONArray.
	 */
	public static JSONArray getArrayValue(JSONValue value, Object key) throws JSONObjectAdapterException{
		if(value == null) throw new JSONObjectAdapterException("No value found for key: "+key);
		JSONArray valueType = value.isArray();
		if(valueType == null) throw new JSONObjectAdapterException("Key: "+key+" exists but is not an array. Value class: "+value.getClass().getName());
		return valueType;
	}
	
	/**
	 * Helper to get an integer from a JSONArray.
	 * @param value
	 * @param key
	 * @return
	 * @throws JSONObjectAdapterException if the value is null or the value is not a JSONArray.
	 */
	public static JSONObject getObjectValue(JSONValue value, Object key) throws JSONObjectAdapterException{
		if(value == null) throw new JSONObjectAdapterException("No value found for key: "+key);
		JSONObject valueType = value.isObject();
		if(valueType == null) throw new JSONObjectAdapterException("Key: "+key+" exists but is not an object. Value class: "+value.getClass().getName());
		return valueType;
	}
	
	

}
