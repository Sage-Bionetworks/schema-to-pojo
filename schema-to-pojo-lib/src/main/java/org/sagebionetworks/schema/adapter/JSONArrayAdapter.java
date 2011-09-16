package org.sagebionetworks.schema.adapter;



public interface JSONArrayAdapter extends JSONAdapter{
	
    /**
     * Get the boolean value associated with an index.
     * The string values "true" and "false" are converted to boolean.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      The truth.
     * @throws JSONObjectAdapterException If there is no value for the index or if the
     *  value is not convertible to boolean.
     */
    public boolean getBoolean(int index) throws JSONObjectAdapterException;
    
    /**
     * Get the double value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      The value.
     * @throws   JSONObjectAdapterException If the key is not found or if the value cannot
     *  be converted to a number.
     */
    public double getDouble(int index) throws JSONObjectAdapterException ;


    /**
     * Get the int value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      The value.
     * @throws   JSONObjectAdapterException If the key is not found or if the value cannot
     *  be converted to a number.
     *  if the value cannot be converted to a number.
     */
    public int getInt(int index) throws JSONObjectAdapterException ;


    /**
     * Get the JSONArray associated with an index.
     * @param index The index must be between 0 and length() - 1.
     * @return      A JSONArray value.
     * @throws JSONObjectAdapterException If there is no value for the index. or if the
     * value is not a JSONArray
     */
    public JSONArrayAdapter getJSONArray(int index) throws JSONObjectAdapterException ;
    
    
    /**
     * Get the JSONObjectAdapter associated with an index.
     * @param index
     * @return
     * @throws JSONObjectAdapterException
     */
    public JSONObjectAdapter getJSONObject(int index)throws JSONObjectAdapterException ;

    /**
     * 
     * @param index
     * @param value
     * @return
     * @throws JSONObjectAdapterException
     */
    public JSONArrayAdapter put(int index, JSONArrayAdapter value) throws JSONObjectAdapterException;
    
    public JSONArrayAdapter put(int index, JSONObjectAdapter value) throws JSONObjectAdapterException;
    
    public JSONArrayAdapter put(int index, String value) throws JSONObjectAdapterException;
    
    public JSONArrayAdapter put(int index, long value) throws JSONObjectAdapterException;
    
    public JSONArrayAdapter put(int index, double value) throws JSONObjectAdapterException;
    
    public JSONArrayAdapter put(int index, boolean value) throws JSONObjectAdapterException;
    
    public JSONArrayAdapter put(int index, int value) throws JSONObjectAdapterException;

    /**
     * Get the JSONObject associated with an index.
     * @param index subscript
     * @return      A JSONObject value.
     * @throws JSONOjbectAdaptorException If there is no value for the index or if the
     * value is not a JSONObject
     */


    /**
     * Get the long value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return      The value.
     * @throws   JSONObjectAdapterException If the key is not found or if the value cannot
     *  be converted to a number.
     */
    public long getLong(int index) throws JSONObjectAdapterException ;
    /**
     * Get the string associated with an index.
     * @param index The index must be between 0 and length() - 1.
     * @return      A string value.
     * @throws JSONObjectAdapterException If there is no value for the index.
     */
    public String getString(int index) throws JSONObjectAdapterException ;

    /**
     * Determine if the value is null.
     * @param index The index must be between 0 and length() - 1.
     * @return true if the value at the index is null, or if there is no value.
     */
    public boolean isNull(int index) ;
	
    /**
     * Get the number of elements in the JSONArray, included nulls.
     *
     * @return The length (or size).
     */
    public int length() ;
    
    /**
     * Convert the array to a JSON String
     * @return
     */
    public String toJSONString();
    

}
