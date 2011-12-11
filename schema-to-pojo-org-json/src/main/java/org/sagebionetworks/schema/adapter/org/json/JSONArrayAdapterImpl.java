package org.sagebionetworks.schema.adapter.org.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

/**
 * An org.json.JSONObject Implementation of JSONObjectAdapter.
 * 
 * @author John
 * 
 */
public class JSONArrayAdapterImpl implements JSONArrayAdapter {

	protected JSONArray wrapped;

	public JSONArrayAdapterImpl() {
		wrapped = new JSONArray();
	}

	public JSONArrayAdapterImpl(JSONArray array) {
		wrapped = array;
	}
	
	public JSONArrayAdapterImpl(String jsonString) throws JSONException{
		wrapped = new JSONArray(jsonString);
	}

	@Override
	public boolean getBoolean(int index) throws JSONObjectAdapterException {
		try {
			return wrapped.getBoolean(index);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public double getDouble(int index) throws JSONObjectAdapterException {
		try {
			return wrapped.getDouble(index);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public int getInt(int index) throws JSONObjectAdapterException {
		try {
			return wrapped.getInt(index);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONArrayAdapter getJSONArray(int index)
			throws JSONObjectAdapterException {
		try {
			return new JSONArrayAdapterImpl(wrapped.getJSONArray(index));
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public long getLong(int index) throws JSONObjectAdapterException {
		try {
			return wrapped.getLong(index);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public String getString(int index) throws JSONObjectAdapterException {
		try {
			return wrapped.getString(index);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}
	
	@Override
	public Object get(int index) throws JSONObjectAdapterException {
		try {
			return wrapped.get(index);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public boolean isNull(int index) {
		return wrapped.isNull(index);
	}

	@Override
	public int length() {
		return wrapped.length();
	}

	@Override
	public JSONObjectAdapter getJSONObject(int index)
			throws JSONObjectAdapterException {
		try {
			return new JSONObjectAdapterImpl(wrapped.getJSONObject(index));
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public String toJSONString() {
		return wrapped.toString();
	}

	@Override
	public JSONObjectAdapter createNew() {
		return new JSONObjectAdapterImpl();
	}

	@Override
	public JSONArrayAdapter createNewArray() {
		return new JSONArrayAdapterImpl();
	}
	
	@Override
	public JSONObjectAdapter createNew(String json) throws JSONObjectAdapterException {
		return JSONObjectAdapterImpl.createAdapterFromJSONString(json);
	}

	@Override
	public JSONArrayAdapter put(int index, JSONArrayAdapter value)
			throws JSONObjectAdapterException {
		JSONArrayAdapterImpl impl = (JSONArrayAdapterImpl) value;
		try {
			wrapped.put(index, impl.wrapped);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONArrayAdapter put(int index, JSONObjectAdapter value)	throws JSONObjectAdapterException {
		JSONObjectAdapterImpl impl = (JSONObjectAdapterImpl) value;
		try {
			wrapped.put(index, impl.wrapped);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

	@Override
	public JSONArrayAdapter put(int index, String value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(index, value);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, long value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(index, value);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, double value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(index, value);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, boolean value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(index, value);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
		return this;
	}

	@Override
	public JSONArrayAdapter put(int index, int value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(index, value);
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
		return this;
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




}
