package org.sagebionetworks.schema.adapter.org.json;

import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

/**
 * An org.json.JSONObject Implementation of JSONObjectAdapter.
 * 
 * @author John
 *
 */
public class JSONObjectAdapterImpl implements JSONObjectAdapter {
	
	protected JSONObject wrapped;
	
	public JSONObjectAdapterImpl(){
		wrapped = new JSONObject();
	}
	
	/**
	 * Create a new adapter from a JSON string
	 * @param json
	 * @throws JSONObjectAdapterException 
	 */
	private JSONObjectAdapterImpl(String json) throws JSONObjectAdapterException{
		try {
			wrapped = new JSONObject(json);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}
	
	/**
	 * Create a new adapter from a JSON string.
	 * @param json
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static JSONObjectAdapter createAdapterFromJSONString(String json) throws JSONObjectAdapterException{
		return  new JSONObjectAdapterImpl(json);
	}

	public JSONObjectAdapterImpl(JSONObject jsonObject) {
		wrapped = jsonObject;
	}

	@Override
	public JSONObjectAdapter createNew() {
		return new JSONObjectAdapterImpl();
	}
	
	@Override
	public JSONObjectAdapter createNew(String json)	throws JSONObjectAdapterException {
		return JSONObjectAdapterImpl.createAdapterFromJSONString(json);
	}

	@Override
	public String toJSONString() {
		return wrapped.toString();
	}
	
	@Override
	public Object get(String key) throws JSONObjectAdapterException {
		try {
			Object result = wrapped.get(key);
			if(JSONObject.NULL == result) return null;
			if(result instanceof JSONObject){
				return new JSONObjectAdapterImpl((JSONObject) result);
			}else if(result instanceof JSONArray){
				return new JSONArrayAdapterImpl((JSONArray) result);
			}
			return result;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public String getString(String key) throws JSONObjectAdapterException {
		try {
			return wrapped.getString(key);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}


	@Override
	public long getLong(String key) throws JSONObjectAdapterException {
		try {
			return wrapped.getLong(key);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public boolean getBoolean(String key) throws JSONObjectAdapterException {
		try {
			return wrapped.getBoolean(key);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public double getDouble(String key) throws JSONObjectAdapterException {
		try {
			return wrapped.getDouble(key);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public int getInt(String key) throws JSONObjectAdapterException {
		try {
			return wrapped.getInt(key);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONArrayAdapter getJSONArray(String key) throws JSONObjectAdapterException {
		try {
			return new JSONArrayAdapterImpl(wrapped.getJSONArray(key));
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONObjectAdapter getJSONObject(String key)
			throws JSONObjectAdapterException {
		try {
			return new JSONObjectAdapterImpl(wrapped.getJSONObject(key));
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public boolean has(String key) {
		return wrapped.has(key);
	}

	@Override
	public boolean isNull(String key) {
		return wrapped.isNull(key);
	}

	@Override
	public JSONObjectAdapter put(String key, JSONObjectAdapter value)
			throws JSONObjectAdapterException {
		JSONObjectAdapterImpl impl = (JSONObjectAdapterImpl) value;
		try {
			wrapped.put(key, impl.wrapped);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONArrayAdapter createNewArray() {
		return new JSONArrayAdapterImpl();
	}

	@Override
	public Iterator<String> keys() {
		return wrapped.keys();
	}

	@Override
	public JSONObjectAdapter put(String key, boolean value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(key, value);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONObjectAdapter put(String key, String value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(key, value);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}
	
	@Override
	public JSONObjectAdapter putNull(String key)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(key, JSONObject.NULL);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONObjectAdapter put(String key, double value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(key, value);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONObjectAdapter put(String key, int value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(key, value);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONObjectAdapter put(String key, long value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(key, value);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONObjectAdapter put(String key, JSONArrayAdapter value)
			throws JSONObjectAdapterException {
		JSONArrayAdapterImpl impl = (JSONArrayAdapterImpl) value;
		try {
			wrapped.put(key, impl.wrapped);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
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
	public String toString() {
		return wrapped.toString();
	}

	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return wrapped.equals(obj);
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
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(property);
		return m.matches();
	}

}
