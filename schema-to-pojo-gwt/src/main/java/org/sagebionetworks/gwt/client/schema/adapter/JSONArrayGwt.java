package org.sagebionetworks.gwt.client.schema.adapter;

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
 * An com.google.gwt.json.client.JSONArray implementation of JSONObjectAdapter.
 * @author John
 *
 */
public class JSONArrayGwt implements JSONArrayAdapter {
	
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
		return new JSONObjectGwt(JSONValueUtil.getObjectValue(wrapped.get(index), index));
	}

	@Override
	public JSONObjectAdapter createNew() {
		return new JSONObjectGwt(new JSONObject());
	}
	
	@Override
	public JSONObjectAdapter createNew(String json)
			throws JSONObjectAdapterException {
		JSONValue value = JSONParser.parseStrict(json);
		return new JSONObjectGwt(value.isObject());
	}

	@Override
	public JSONArrayAdapter createNewArray() {
		return new JSONArrayGwt(new JSONArray());
	}

	@Override
	public JSONArrayAdapter put(int index, JSONArrayAdapter value)
			throws JSONObjectAdapterException {
		if(value == null){
			this.wrapped.set(index, null);
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
			this.wrapped.set(index, null);
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
			this.wrapped.set(index, null);
		}else{
			this.wrapped.set(index, new JSONString(value));
		}
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, long value)
			throws JSONObjectAdapterException {
		// According to GWT a long cannot be represented in JavaScript so we pass it as string.
		// See: http://code.google.com/webtoolkit/doc/1.6/DevGuideCodingBasics.html
		this.wrapped.set(index, new JSONString(Long.toString(value)));
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, double value)
			throws JSONObjectAdapterException {
		this.wrapped.set(index, new JSONNumber(value));
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, boolean value)
			throws JSONObjectAdapterException {
		this.wrapped.set(index, JSONBoolean.getInstance(value));
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, int value)
			throws JSONObjectAdapterException {
		// According to GWT a long cannot be represented in JavaScript so we pass it as string.
		// See: http://code.google.com/webtoolkit/doc/1.6/DevGuideCodingBasics.html
		this.wrapped.set(index, new JSONString(Integer.toString(value)));
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



}
