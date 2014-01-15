package org.sagebionetworks.gwt.client.schema.adapter;

import org.sagebionetworks.schema.adapter.AdapterFactory;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONMapAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * The GWT version of the adapter factory.
 * @author John
 *
 */
public class GwtAdapterFactory implements AdapterFactory {

	@Override
	public JSONObjectAdapter createNew() {
		// Create a new Adapter with a GWT JSONObject core.
		return new JSONObjectGwt(new JSONObject());
	}

	@Override
	public JSONObjectAdapter createNew(String json)	throws JSONObjectAdapterException {
		// Parse the passed string
		JSONValue value = JSONParser.parseStrict(json);
		return new JSONObjectGwt(value.isObject());
	}

	@Override
	public JSONArrayAdapter createNewArray() {
		return new JSONArrayGwt(new JSONArray());
	}

	@Override
	public JSONArrayAdapter createNewArray(String json)	throws JSONObjectAdapterException {
		// Parse the passed string
		JSONValue value = JSONParser.parseStrict(json);
		return new JSONArrayGwt(value.isArray());
	}

	@Override
	public JSONMapAdapter createNewMap() {
		return new JSONMapGwt();
	}

	@Override
	public JSONMapAdapter createNewMap(String json) throws JSONObjectAdapterException {
		// Parse the passed string
		JSONValue value = JSONParser.parseStrict(json);
		return new JSONMapGwt(value.isArray());
	}
}
