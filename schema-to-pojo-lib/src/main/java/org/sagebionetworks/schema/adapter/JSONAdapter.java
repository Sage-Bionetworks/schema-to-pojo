package org.sagebionetworks.schema.adapter;

public interface JSONAdapter {
	
	/**
	 * Create a new Object.
	 * @return
	 */
	public JSONObjectAdapter createNew();
	
	/**
	 * Create a new Adapter using the passed JSON String
	 * @param json
	 * @return
	 * @throws JSONObjectAdapterException 
	 */
	public JSONObjectAdapter createNew(String json) throws JSONObjectAdapterException;
	
	/**
	 * Create a new array.
	 * @return
	 */
	public JSONArrayAdapter createNewArray();
	
	/**
	 * Write this object to its JSON string.
	 * @return
	 */
	public String toJSONString();
}
