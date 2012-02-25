package org.sagebionetworks.gwt.client.schema.adapter;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * An com.google.gwt.json.client.JSONObject implementation of JSONObjectAdapter.
 * @author John
 *
 */
public class JSONObjectGwt extends GwtAdapterFactory implements JSONObjectAdapter {
	
	protected JSONObject wrapped = null;
	
	public JSONObjectGwt() {
		this(new JSONObject());
	}
	
	public JSONObjectGwt(JSONObject toWrap){
		this.wrapped = toWrap;
	}
	
	/**
	 * Static helper for creating an adapter
	 * @return
	 */
	public static JSONObjectAdapter createNewAdapter(){
		return new JSONObjectGwt(new JSONObject());
	}
	
	@Override
	public int hashCode() {
		return this.wrapped.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.wrapped.equals(obj);
	}

	@Override
	public String toString() {
		return this.wrapped.toString();
	}

	@Override
	public Iterator keys() {
		return this.wrapped.keySet().iterator();
	}

	@Override
	public String toJSONString() {
		return this.wrapped.toString();
	}

	@Override
	public String getString(String key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getStringValue(wrapped.get(key), key);
	}

	@Override
	public JSONObjectAdapter put(String key, JSONObjectAdapter value) throws JSONObjectAdapterException {
		if(value == null){
			this.wrapped.put(key, null);
		}else{
			JSONObjectGwt impl = (JSONObjectGwt) value;
			this.wrapped.put(key, impl.wrapped);
		}
		return this;
	}
	
	@Override
	public Object get(String key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getObjectValue(wrapped.get(key), key);
	}

	@Override
	public long getLong(String key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getLongValue(wrapped.get(key), key);
	}

	@Override
	public boolean getBoolean(String key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getBooleanValue(wrapped.get(key), key);
	}

	@Override
	public double getDouble(String key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getDoubleValue(wrapped.get(key), key);
	}

	@Override
	public int getInt(String key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getIntValue(wrapped.get(key), key);
	}

	@Override
	public JSONArrayAdapter getJSONArray(String key)
			throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return new JSONArrayGwt(JSONValueUtil.getArrayValue(wrapped.get(key), key));
	}

	@Override
	public JSONObjectAdapter getJSONObject(String key)
			throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return new JSONObjectGwt(JSONValueUtil.getJSONObjectValue(wrapped.get(key), key));
	}

	@Override
	public boolean has(String key) {
		return this.wrapped.get(key) != null;
	}

	@Override
	public boolean isNull(String key) {
		JSONValue value = this.wrapped.get(key);
 		if(value == null) return true;
 		return value.isNull() != null;
	}

	@Override
	public JSONObjectAdapter put(String key, boolean value)
			throws JSONObjectAdapterException {
		this.wrapped.put(key, JSONBoolean.getInstance(value));
		return this;
	}

	@Override
	public JSONObjectAdapter put(String key, String value)
			throws JSONObjectAdapterException {
		if(value == null){
			this.wrapped.put(key, null);
		}else{
			this.wrapped.put(key, new JSONString(value));
		}
		return this;
	}
	
	@Override
	public JSONObjectAdapter putNull(String key)
			throws JSONObjectAdapterException {
		this.wrapped.put(key, JSONNull.getInstance());
		return this;
	}

	@Override
	public JSONObjectAdapter put(String key, double value)
			throws JSONObjectAdapterException {
		this.wrapped.put(key, new JSONNumber(value));
		return this;
	}

	@Override
	public JSONObjectAdapter put(String key, int value)
			throws JSONObjectAdapterException {
		// According to GWT a long cannot be represented in JavaScript so we pass it as string.
		// See: http://code.google.com/webtoolkit/doc/1.6/DevGuideCodingBasics.html
		this.wrapped.put(key, new JSONNumber(value));
		return this;
	}

	@Override
	public JSONObjectAdapter put(String key, long value)
			throws JSONObjectAdapterException {
		this.wrapped.put(key, JSONValueUtil.createJSONNumberForLong(value));
		return this;
	}

	@Override
	public JSONObjectAdapter put(String key, JSONArrayAdapter value)
			throws JSONObjectAdapterException {
		if(value == null){
			this.wrapped.put(key, null);
		}else{
			JSONArrayGwt impl = (JSONArrayGwt) value;
			// Pass the wrapped object to the wrapped.
			this.wrapped.put(key, impl.wrapped);
		}
		return this;
	}

	@Override
	public String convertDateToString(FORMAT format, Date toFormat) {
		return DateUtils.convertDateToString(format, toFormat);
	}

	@Override
	public Date convertStringToDate(FORMAT format, String toFormat) {
		return DateUtils.convertStringToDate(format, toFormat);
	}

	/**
	 * Method to validate a regular expression string against a pattern.
	 */
	public boolean validatePatternProperty(String pattern, String property){
		if (pattern == null){
			throw new IllegalArgumentException("can not validatePatternProperty for property " 
					+ property + " because pattern is null");
		}
		if (property == null){
			throw new IllegalArgumentException("can not validatePatternProperty for pattern "
					+ pattern + "because property is null");
		}
		RegExp regExp = RegExp.compile(pattern);
		MatchResult matcher = regExp.exec(property);
		//for the property to MATCH the pattern three things must be true
		//first, matcher can't be null, that means no matches were found
		//second, index  must be 0 which means  match started at first character
		//third, match group must match the input string
		if (matcher == null){
			return false;
		}
		else if (matcher.getIndex() != 0){
			return false;
		}
		else if (!matcher.getGroup(0).equals(matcher.getInput())){
			return false;
		}
		return true;
	}

	@Override
	public boolean validateURI(String uri) throws JSONObjectAdapterException {
		// Currently there is no easy way to do this with GWT, so we accept all URIs.
		return true;
	}
	
	@Override
	public JSONObjectAdapter put(String key, Date date) throws JSONObjectAdapterException {
		if(key == null) throw new IllegalArgumentException("Key cannot be null");
		if(date == null) throw new IllegalArgumentException("Date cannot be null");
		// first convert it to a date string
		return put(key, date.getTime());
	}

	@Override
	public Date getDate(String key) throws JSONObjectAdapterException {
		// Get the string value
		long longValue = getLong(key);
		return new Date(longValue);
	}
	
	@Override
	public JSONObjectAdapter put(String key, byte[] value)	throws JSONObjectAdapterException {
		// Base64 encode the byte array
		try {
			byte[] encoded = Base64.encodeBase64(value);
			String stringValue = new String(encoded, "UTF-8");
			return put(key, stringValue);
		} catch (UnsupportedEncodingException e) {
			throw new JSONObjectAdapterException(e);
		}

	}

	@Override
	public byte[] getBinary(String key) throws JSONObjectAdapterException {
		try {
			// Get the string value
			String base64String = getString(key);
			return Base64.decodeBase64(base64String.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new JSONObjectAdapterException(e);
		}
	}


}
