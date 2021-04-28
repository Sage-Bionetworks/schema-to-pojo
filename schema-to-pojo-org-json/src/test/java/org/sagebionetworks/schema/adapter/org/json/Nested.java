package org.sagebionetworks.schema.adapter.org.json;

import java.util.Objects;

/**
 * Simple nested class.
 */
public class Nested {
	private String childValue;

	public Nested(String childValue) {
		super();
		this.childValue = childValue;
	}

	public String getChildValue() {
		return childValue;
	}

	public void setChildValue(String childValue) {
		this.childValue = childValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(childValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Nested)) {
			return false;
		}
		Nested other = (Nested) obj;
		return Objects.equals(childValue, other.childValue);
	}
	
}