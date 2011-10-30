package org.sagebionetworks.schema.adapter.org.json;

import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

/**
 * This is a very simple stub implementation of the JSONEntity for testing.
 * 
 * @author jmhill
 *
 */
public class SimpleEntityStub implements JSONEntity{
	
	private String value;

	@Override
	public JSONObjectAdapter initializeFromJSONObject(JSONObjectAdapter toInitFrom) throws JSONObjectAdapterException {
		if(toInitFrom.has("value")){
			value = toInitFrom.getString("value");
		}
		return toInitFrom;
	}

	@Override
	public JSONObjectAdapter writeToJSONObject(JSONObjectAdapter writeTo)
			throws JSONObjectAdapterException {
		if(value != null){
			writeTo.put("value", value);
		}
		return writeTo;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleEntityStub other = (SimpleEntityStub) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleEntityStub [value=" + value + "]";
	}

	@Override
	public String getJSONSchema() {
		// TODO Auto-generated method stub
		return null;
	}
}