package org.sagebionetworks.gwt.client.schema.adapter;

import java.io.UnsupportedEncodingException;
import java.util.Date;

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

/**
 * An com.google.gwt.json.client.JSONArray implementation of JSONObjectAdapter.
 * @author John
 *
 */
public class JSONArrayGwt extends GwtAdapterFactory implements JSONArrayAdapter {
	
	protected JSONArray wrapped;

	protected JSONArrayGwt(JSONArray valueType) {
		wrapped = valueType;
	}

	public JSONArrayGwt() {
		this.wrapped = new JSONArray();
	}

	@Override
	public boolean getBoolean(int index) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getBooleanValue(wrapped.get(index), index);
	}

	@Override
	public double getDouble(int index) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getDoubleValue(wrapped.get(index), index);
	}

	@Override
	public int getInt(int index) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getIntValue(wrapped.get(index), index);
	}

	@Override
	public JSONArrayAdapter getJSONArray(int index)
			throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return new JSONArrayGwt(JSONValueUtil.getArrayValue(wrapped.get(index), index));
	}


	@Override
	public long getLong(int index) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getLongValue(wrapped.get(index), index);
	}

	@Override
	public String getString(int index) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getStringValue(wrapped.get(index), index);
	}
	
	@Override
	public Object get(int index) throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return JSONValueUtil.getObjectValue(wrapped.get(index), index);
	}

	@Override
	public boolean isNull(int index) {
		JSONValue value = this.wrapped.get(index);
 		if(value == null) return true;
 		return value.isNull() != null;
	}

	@Override
	public int length() {
		return wrapped.size();
	}

	@Override
	public JSONObjectAdapter getJSONObject(int index)
			throws JSONObjectAdapterException {
		// The utility will do the value validation.
		return new JSONObjectGwt(JSONValueUtil.getJSONObjectValue(wrapped.get(index), index));
	}

	@Override
	public JSONArrayAdapter put(int index, JSONArrayAdapter value)
			throws JSONObjectAdapterException {
		if(value == null){
			this.wrapped.set(index, JSONNull.getInstance());
		}else{
			JSONArrayGwt impl = (JSONArrayGwt) value;
			// Pass the wrapped object to the wrapped.
			this.wrapped.set(index, impl.wrapped);
		}

		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, JSONObjectAdapter value)
			throws JSONObjectAdapterException {
		if(value == null){
			this.wrapped.set(index, JSONNull.getInstance());
		}else{
			JSONObjectGwt impl = (JSONObjectGwt) value;
			this.wrapped.set(index, impl.wrapped);
		}
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, String value)
			throws JSONObjectAdapterException {
		if(value == null){
			this.wrapped.set(index, JSONNull.getInstance());
		}else{
			this.wrapped.set(index, new JSONString(value));
		}
		return this;
	}
	
	@Override
	public JSONArrayAdapter putNull(int index)
			throws JSONObjectAdapterException {
		this.wrapped.set(index, JSONNull.getInstance());
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, Long value)
			throws JSONObjectAdapterException {
		if (value == null) {
			this.wrapped.set(index, JSONNull.getInstance());
		} else {
			this.wrapped.set(index, JSONValueUtil.createJSONNumberForLong(value));
		}
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, Double value)
			throws JSONObjectAdapterException {
		if (value == null) {
			this.wrapped.set(index, JSONNull.getInstance());
		} else {
			this.wrapped.set(index, new JSONNumber(value));
		}
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, Boolean value)
			throws JSONObjectAdapterException {
		if (value == null) {
			this.wrapped.set(index, JSONNull.getInstance());
		} else {
			this.wrapped.set(index, JSONBoolean.getInstance(value));
		}
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, Integer value)
			throws JSONObjectAdapterException {
		if (value == null) {
			this.wrapped.set(index, JSONNull.getInstance());
		} else {
			this.wrapped.set(index, new JSONNumber(value));
		}
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
	public String toString(){
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
	public Date getDate(int index) throws JSONObjectAdapterException {
		// Get the string value
		long longValue = getLong(index);
		return new Date(longValue);
	}

	@Override
	public JSONArrayAdapter put(int index, Date date) throws JSONObjectAdapterException {
		if(date == null) throw new IllegalArgumentException("Date cannot be null");
		return put(index, date.getTime());
	}

	@Override
	public JSONArrayAdapter put(int index, byte[] value)throws JSONObjectAdapterException {
		// Base64 encode the byte array
		try {
			byte[] encoded = Base64.encodeBase64(value);
			String stringValue = new String(encoded, "UTF-8");
			return put(index, stringValue);
		} catch (UnsupportedEncodingException e) {
			throw new JSONObjectAdapterException(e);
		}

	}

	@Override
	public byte[] getBinary(int index) throws JSONObjectAdapterException {
		try {
			// Get the string value
			String base64String = getString(index);
			return Base64.decodeBase64(base64String.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new JSONObjectAdapterException(e);
		}
	}


}
