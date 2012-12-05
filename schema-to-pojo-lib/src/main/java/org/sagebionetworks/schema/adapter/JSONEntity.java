package org.sagebionetworks.schema.adapter;

/**
 * A JSONEntity can marshal itself to/from JSON using a JSONObjectAdapter
 *
 */
public interface JSONEntity {
	
	public static final String EFFECTIVE_SCHEMA = "EFFECTIVE_SCHEMA";
	
	/**
	 * Fully initialize this object from a JSONObjectAdapter.
	 * @param toInitFrom
	 */
	public JSONObjectAdapter initializeFromJSONObject(JSONObjectAdapter toInitFrom) throws JSONObjectAdapterException;
	
	/**
	 * Fully write this object to a JSONObjectAdapter.
	 * @param writeTo
	 */
	public JSONObjectAdapter writeToJSONObject(JSONObjectAdapter writeTo) throws JSONObjectAdapterException;
	
	/**
	 * Get the JSON schema for this entity.
	 * @return
	 */
	public String getJSONSchema();
	

}
