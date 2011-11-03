package org.sagebionetworks.gwt.client.schema.adapter;

import java.util.Date;
import java.util.Iterator;

import org.gwttime.time.DateTime;
import org.gwttime.time.format.ISODateTimeFormat;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * An com.google.gwt.json.client.JSONObject implementation of JSONObjectAdapter.
 * @author John
 *
 */
public class JSONObjectGwt implements JSONObjectAdapter {
	

	protected JSONObject wrapped = null;

	@Override
	public JSONObjectAdapter createNew() {
		return createNewAdapter();
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
	public JSONArrayAdapter createNewArray() {
		return new JSONArrayGwt(new JSONArray());
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
		return new JSONObjectGwt(JSONValueUtil.getObjectValue(wrapped.get(key), key));
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
		if(format == null) throw new IllegalArgumentException("FORMAT cannot be null");
		if(toFormat == null) throw new IllegalArgumentException("Date cannot be null");
		if(!format.isDateFormat()) throw new IllegalArgumentException("Not a date format: "+format.name());
		if(FORMAT.DATE_TIME == format){
			DateTime dt = new DateTime(toFormat.getTime());
			return ISODateTimeFormat.dateTime().print(dt);
		}else if(FORMAT.DATE == format){
			DateTime dt = new DateTime(toFormat.getTime());
			return ISODateTimeFormat.date().print(dt);
		}else if(FORMAT.TIME == format){
			DateTime dt = new DateTime(toFormat.getTime());
			return ISODateTimeFormat.time().print(dt);
		}else{
			throw new IllegalArgumentException("Unknown date format: "+format.name());
		}
	}

	@Override
	public Date convertStringToDate(FORMAT format, String toFormat) {
		if(format == null) throw new IllegalArgumentException("FORMAT cannot be null");
		if(toFormat == null) throw new IllegalArgumentException("Date cannot be null");
		if(!format.isDateFormat()) throw new IllegalArgumentException("Not a date format: "+format.name());
		if(FORMAT.DATE_TIME == format){
			DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(toFormat);
			return dt.toDate();
		}else if(FORMAT.DATE == format){
			DateTime dt = ISODateTimeFormat.date().parseDateTime(toFormat);
			return dt.toDate();
		}else if(FORMAT.TIME == format){
			DateTime dt = ISODateTimeFormat.time().parseDateTime(toFormat);
			return dt.toDate();
		}else{
			throw new IllegalArgumentException("Unknown date format: "+format.name());
		}
	}

	@Override
	public JSONObjectAdapter createNew(String json)
			throws JSONObjectAdapterException {
		JSONValue value = JSONParser.parseStrict(json);
		return new JSONObjectGwt(value.isObject());
	}


}
