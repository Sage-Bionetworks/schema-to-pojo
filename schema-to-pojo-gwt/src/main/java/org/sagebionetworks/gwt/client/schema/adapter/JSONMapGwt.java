package org.sagebionetworks.gwt.client.schema.adapter;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONMapAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.binary.Base64;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * An com.google.gwt.json.client.JSONArray implementation of JSONObjectAdapter.
 * 
 * @author John
 * 
 */
public class JSONMapGwt extends GwtAdapterFactory implements JSONMapAdapter {

	protected JSONArray wrapped;
	private Map<Object, JSONObject> wrappedMap = new HashMap<Object, JSONObject>();

	protected JSONMapGwt(JSONArray valueType) throws JSONObjectAdapterException {
		wrapped = valueType;
		unwrap();
	}

	public JSONMapGwt() {
		this.wrapped = new JSONArray();
	}

	private void unwrap() throws JSONObjectAdapterException {
		for (int i = 0; i < wrapped.size(); i++) {
			JSONObject jsonObject = JSONValueUtil.getJSONObjectValue(wrapped.get(i), i);
			Object key = jsonObject.get("key");
			wrappedMap.put(key, jsonObject);
		}
	}

	@Override
	public boolean getBoolean(Object key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getBooleanValue(wrappedMap.get(key), key);
	}

	@Override
	public double getDouble(Object key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getDoubleValue(wrappedMap.get(key), key);
	}

	@Override
	public int getInt(Object key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getIntValue(wrappedMap.get(key), key);
	}

	@Override
	public JSONArrayAdapter getJSONArray(Object key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return new JSONArrayGwt(JSONValueUtil.getArrayValue(wrappedMap.get(key), key));
	}

	@Override
	public long getLong(Object key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getLongValue(wrappedMap.get(key), key);
	}

	@Override
	public String getString(Object key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getStringValue(wrappedMap.get(key), key);
	}

	@Override
	public Object get(Object key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getObjectValue(wrappedMap.get(key), key);
	}

	@Override
	public boolean isNull(Object key) {
		JSONValue value = this.wrappedMap.get(key);
		if (value == null)
			return true;
		return value.isNull() != null;
	}

	@Override
	public int length() {
		return wrapped.size();
	}

	@Override
	public JSONObjectAdapter getJSONObject(Object key) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return new JSONObjectGwt(JSONValueUtil.getJSONObjectValue(wrappedMap.get(key), key));
	}

	private void doPut(Object key, JSONValue value) throws JSONObjectAdapterException {
		String keyString = key.toString();
		JSONObject entry = new JSONObject();
		entry.put("key", new JSONString(keyString));
		entry.put("value", value);
		wrapped.set(wrapped.size(), entry);
		wrappedMap.put(key, entry);
	}

	@Override
	public JSONMapAdapter put(Object key, JSONArrayAdapter value) throws JSONObjectAdapterException {
		if (value == null) {
			doPut(key, null);
		} else {
			JSONMapGwt impl = (JSONMapGwt) value;
			// Pass the wrapped object to the wrapped.
			doPut(key, impl.wrapped);
		}

		return this;
	}

	@Override
	public JSONMapAdapter put(Object key, JSONObjectAdapter value) throws JSONObjectAdapterException {
		if (value == null) {
			doPut(key, null);
		} else {
			JSONObjectGwt impl = (JSONObjectGwt) value;
			doPut(key, impl.wrapped);
		}
		return this;
	}

	@Override
	public JSONMapAdapter put(Object key, String value) throws JSONObjectAdapterException {
		if (value == null) {
			doPut(key, null);
		} else {
			doPut(key, new JSONString(value));
		}
		return this;
	}

	@Override
	public JSONMapAdapter putNull(Object key) throws JSONObjectAdapterException {
		doPut(key, JSONNull.getInstance());
		return this;
	}

	@Override
	public JSONMapAdapter put(Object key, long value) throws JSONObjectAdapterException {
		doPut(key, JSONValueUtil.createJSONNumberForLong(value));
		return this;
	}

	@Override
	public JSONMapAdapter put(Object key, double value) throws JSONObjectAdapterException {
		doPut(key, new JSONNumber(value));
		return this;
	}

	@Override
	public JSONMapAdapter put(Object key, boolean value) throws JSONObjectAdapterException {
		doPut(key, JSONBoolean.getInstance(value));
		return this;
	}

	@Override
	public JSONMapAdapter put(Object key, int value) throws JSONObjectAdapterException {
		doPut(key, new JSONNumber(value));
		return this;
	}

	@Override
	public String toJSONString() {
		return wrapped.toString();
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
	public String convertDateToString(FORMAT format, Date toFormat) {
		return DateUtils.convertDateToString(format, toFormat);
	}

	@Override
	public Date convertStringToDate(FORMAT format, String toFormat) {
		return DateUtils.convertStringToDate(format, toFormat);
	}

	@Override
	public Date getDate(Object key) throws JSONObjectAdapterException {
		// Get the string value
		long longValue = getLong(key);
		return new Date(longValue);
	}

	@Override
	public JSONMapAdapter put(Object key, Date date) throws JSONObjectAdapterException {
		if (date == null)
			throw new IllegalArgumentException("Date cannot be null");
		return put(key, date.getTime());
	}

	@Override
	public JSONMapAdapter put(Object key, byte[] value) throws JSONObjectAdapterException {
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
	public byte[] getBinary(Object key) throws JSONObjectAdapterException {
		try {
			// Get the string value
			String base64String = getString(key);
			return Base64.decodeBase64(base64String.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public Iterable<Object> keys() {
		return wrappedMap.keySet();
	}
}
