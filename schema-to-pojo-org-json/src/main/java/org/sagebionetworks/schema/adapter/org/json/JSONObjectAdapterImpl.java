package org.sagebionetworks.schema.adapter.org.json;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
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
	
	JSONObject wrapped;
	
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
	public String toJSONString() {
		return wrapped.toString();
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
	public Iterator keys() {
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
	public JSONObjectAdapter put(String key, Object value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(key, value);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
	}

}
