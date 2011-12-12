package org.sagebionetworks.schema.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Utilities for reading and writing collections to adapters.
 * @author John
 *
 */
public class AdapterCollectionUtils {
	
	public static void writeToObject(JSONObjectAdapter adapter, Map<String, Object> map) throws JSONObjectAdapterException{
		for(String key: map.keySet()){
			Object value = map.get(key);
			if(value instanceof String){
				adapter.put(key,(String)value);
			}else if(value instanceof Long){
				adapter.put(key,(Long)value);
			}else if(value instanceof Double){
				adapter.put(key,(Double)value);
			}else if(value instanceof Integer){
				adapter.put(key,(Integer)value);
			}else if(value instanceof Boolean){
				adapter.put(key,(Boolean)value);
			}else if(value == null){
				adapter.putNull(key);
			}else if(value instanceof Iterable ){
				Iterable itable = (Iterable) value;
				JSONArrayAdapter newArray = adapter.createNewArray();
				adapter.put(key, newArray);
				writeToArray(newArray, itable);
			}else if(value instanceof String[] ){
				String[] itable = (String[]) value;
				JSONArrayAdapter newArray = adapter.createNewArray();
				adapter.put(key, newArray);
				writeToArray(newArray, itable);
			}else if(value instanceof Long[] ){
				Long[] itable = (Long[]) value;
				JSONArrayAdapter newArray = adapter.createNewArray();
				adapter.put(key, newArray);
				writeToArray(newArray, itable);
			}else if(value instanceof Double[] ){
				Double[] itable = (Double[]) value;
				JSONArrayAdapter newArray = adapter.createNewArray();
				adapter.put(key, newArray);
				writeToArray(newArray, itable);
			}else if(value instanceof Boolean[] ){
				Boolean[] itable = (Boolean[]) value;
				JSONArrayAdapter newArray = adapter.createNewArray();
				adapter.put(key, newArray);
				writeToArray(newArray, itable);
			}else{
				throw new IllegalArgumentException("Unknown type: "+value.getClass().getName());
			}
		}
	}
	
	public static void writeToArray(JSONArrayAdapter array, Iterable iterable) throws JSONObjectAdapterException{
		int index = 0;
		for(Object value: iterable){
			if(value instanceof String){
				array.put(index,(String)value);
			}else if(value instanceof Long){
				array.put(index,(Long)value);
			}else if(value instanceof Double){
				array.put(index,(Double)value);
			}else if(value instanceof Integer){
				array.put(index,(Integer)value);
			}else if(value instanceof Boolean){
				array.put(index,(Boolean)value);
			}else if(value == null){
				array.putNull(index);
			}else if(value instanceof Map ){
				Map<String, Object> map = (Map<String, Object>) value;
				JSONObjectAdapter object = array.createNew();
				array.put(index, object);
				writeToObject(object, map);
			}else if(value instanceof Iterable ){
				Iterable itable = (Iterable) value;
				JSONArrayAdapter newArray = array.createNewArray();
				array.put(index, newArray);
				writeToArray(newArray, itable);
			}else if(value instanceof String[] ){
				String[] itable = (String[]) value;
				JSONArrayAdapter newArray = array.createNewArray();
				array.put(index, newArray);
				writeToArray(newArray, itable);
			}else if(value instanceof Long[] ){
				Long[] itable = (Long[]) value;
				JSONArrayAdapter newArray = array.createNewArray();
				array.put(index, newArray);
				writeToArray(newArray, itable);
			}else if(value instanceof Double[] ){
				Double[] itable = (Double[]) value;
				JSONArrayAdapter newArray = array.createNewArray();
				array.put(index, newArray);
				writeToArray(newArray, itable);
			}else if(value instanceof Boolean[] ){
				Boolean[] itable = (Boolean[]) value;
				JSONArrayAdapter newArray = array.createNewArray();
				array.put(index, newArray);
				writeToArray(newArray, itable);
			}else{
				throw new IllegalArgumentException("Unknown type: "+value.getClass().getName());
			}
			index++;
		}
	}
	
	public static void writeToArray(JSONArrayAdapter newArray, Boolean[] array) throws JSONObjectAdapterException {
		for(int i=0; i<array.length; i++){
			newArray.put(i, array[i]);
		}
	}

	public static void writeToArray(JSONArrayAdapter newArray, Double[] array) throws JSONObjectAdapterException {
		for(int i=0; i<array.length; i++){
			newArray.put(i, array[i]);
		}
	}

	public static void writeToArray(JSONArrayAdapter newArray, Long[] array) throws JSONObjectAdapterException {
		for(int i=0; i<array.length; i++){
			newArray.put(i, array[i]);
		}
	}

	public static void writeToArray(JSONArrayAdapter newArray, String[] array) throws JSONObjectAdapterException {
		for(int i=0; i<array.length; i++){
			newArray.put(i, array[i]);
		}
	}
	
	/**
	 * Get a string array from an adapter.
	 * @param newArray
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static List<String> readListOfStrings(JSONArrayAdapter newArray) throws JSONObjectAdapterException{
		List<String> restuls = new ArrayList<String>();
		for(int i=0; i<newArray.length(); i++){
			restuls.add(newArray.getString(i));
		}
		return restuls;
	}
	
	public static List<Map<String, Object>> readListOfMaps(JSONArrayAdapter newArray) throws JSONObjectAdapterException{
		List<Map<String, Object>> restuls = new ArrayList<Map<String, Object>>();
		for(int i=0; i<newArray.length(); i++){
			JSONObjectAdapter object  = newArray.getJSONObject(i);
			restuls.add(readMapFromObject(object));
		}
		return restuls;
	}
	
	public static Object readObjectFromArray(JSONArrayAdapter array) throws JSONObjectAdapterException{
		if(array.length() < 1) return null;
		Object peak = array.get(0);
		if(peak instanceof String){
			return readStringListFromArray(array);
		}else if(peak instanceof Long){
			return readLongListFromArray(array);
		}else if(peak instanceof Double){
			return readDoubleListFromArray(array);
		}else if(peak instanceof Integer){
			return readIntegerListFromArray(array);
		}else if(peak instanceof Boolean){
			return readBooleanListFromArray(array);
		}else if(peak instanceof JSONObjectAdapter){
			return readListOfMap(array);
		}else{
			throw new IllegalArgumentException("Unsupported array sub type: "+peak.getClass().getName());
		}
	}
	
	public static List<Map<String, Object>> readListOfMap(JSONArrayAdapter array) throws JSONObjectAdapterException {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for(int i=0; i<array.length(); i++){
			JSONObjectAdapter object = array.getJSONObject(i);
			Map<String, Object> map = readMapFromObject(object);
			results.add(map);
		}
		return results;
	}

	public static List<Boolean> readBooleanListFromArray(JSONArrayAdapter array) throws JSONObjectAdapterException {
		List<Boolean> results = new ArrayList<Boolean>();
		for(int i=0; i<array.length(); i++){
			results.add(array.getBoolean(i));
		}
		return results;
	}

	public static List<Integer> readIntegerListFromArray(JSONArrayAdapter array) throws JSONObjectAdapterException {
		List<Integer> results = new ArrayList<Integer>();
		for(int i=0; i<array.length(); i++){
			results.add(array.getInt(i));
		}
		return results;
	}

	private static List<Double> readDoubleListFromArray(JSONArrayAdapter array) throws JSONObjectAdapterException {
		List<Double> results = new ArrayList<Double>();
		for(int i=0; i<array.length(); i++){
			results.add(array.getDouble(i));
		}
		return results;
	}

	public static List<Long> readLongListFromArray(JSONArrayAdapter array) throws JSONObjectAdapterException {
		List<Long> results = new ArrayList<Long>();
		for(int i=0; i<array.length(); i++){
			results.add(array.getLong(i));
		}
		return results;
	}

	public static List<String> readStringListFromArray(JSONArrayAdapter array) throws JSONObjectAdapterException {
		List<String> results = new ArrayList<String>();
		for(int i=0; i<array.length(); i++){
			results.add(array.getString(i));
		}
		return results;
	}

	public static Map<String, Object> readMapFromObject(JSONObjectAdapter adapter) throws JSONObjectAdapterException{
		Map<String, Object> results = new HashMap<String, Object>();
		Iterator<String> keyIt = adapter.keys();
		while(keyIt.hasNext()){
			String key = keyIt.next();
			Object value = adapter.get(key);
			if(value instanceof JSONObjectAdapter){
				Map<String, Object> subMap = readMapFromObject((JSONObjectAdapter)value);
				results.put(key, subMap);
			}else if(value instanceof JSONArrayAdapter){
				results.put(key, readObjectFromArray((JSONArrayAdapter)value));
			}else{
				results.put(key, value);
			}
		}
		return results;
	}


}
