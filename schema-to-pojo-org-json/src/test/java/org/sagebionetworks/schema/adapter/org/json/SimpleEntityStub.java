package org.sagebionetworks.schema.adapter.org.json;

import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

/**
 * This is a very simple stub implementation of the JSONEntity for testing.
 * 
 * @author jmhill
 *
 */
public class SimpleEntityStub implements SimpleInterface{
	
	private String value;
	private String concreteType = SimpleEntityStub.class.getName();

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
		writeTo.put(ObjectSchema.CONCRETE_TYPE, concreteType);
		return writeTo;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getConcreteType(){
		return this.concreteType;
	}
	
	public void setConcreteType(String type){
		this.concreteType = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((concreteType == null) ? 0 : concreteType.hashCode());
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
		if (concreteType == null) {
			if (other.concreteType != null)
				return false;
		} else if (!concreteType.equals(other.concreteType))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleEntityStub [value=" + value + ", concreteType="
				+ concreteType + "]";
	}
	
	
}