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
	
	JSONArray wrapped;
	
	public JSONArrayAdapterImpl(){
		wrapped = new JSONArray();
	}
	
	public JSONArrayAdapterImpl(JSONArray array){
		wrapped = array;
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
	public boolean isNull(int index) {
		return wrapped.isNull(index);
	}

	@Override
	public int length() {
		return wrapped.length();
	}

	@Override
	public JSONArrayAdapter put(int index, Object value)
			throws JSONObjectAdapterException {
		try {
			wrapped.put(index, value);
			return this;
		} catch (JSONException e) {
			throw new JSONObjectAdapterException(e);
		}
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

}
