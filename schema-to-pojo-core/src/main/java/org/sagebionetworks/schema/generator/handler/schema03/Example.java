package org.sagebionetworks.schema.generator.handler.schema03;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public class Example implements JSONEntity {
	
	Map<String, String> stringMap;
	Map<String, byte[]> byteArrayMap;

	@Override
	public JSONObjectAdapter initializeFromJSONObject(JSONObjectAdapter toInitFrom) throws JSONObjectAdapterException {

		if(toInitFrom.has("stringMap")){
			stringMap = new LinkedHashMap<String, String>();
			JSONObjectAdapter jsonObject = toInitFrom.getJSONObject("stringMap");
			Iterator<String> keys =  jsonObject.keys();
			while(keys.hasNext()){
				String key = keys.next();
				String value = jsonObject.getString(key);
				stringMap.put(key, value);
			}
		}else{
			stringMap = null;
		}	
		if(toInitFrom.has("byteArrayMap")){
			byteArrayMap = new LinkedHashMap<String, byte[]>();
			JSONObjectAdapter jsonObject = toInitFrom.getJSONObject("byteArrayMap");
			Iterator<String> keys =  jsonObject.keys();
			while(keys.hasNext()){
				String key = keys.next();
				byte[] value = jsonObject.getBinary(key);
				byteArrayMap.put(key, value);
			}
		}else{
			stringMap = null;
		}
		
		return toInitFrom;
	}

	@Override
	public JSONObjectAdapter writeToJSONObject(JSONObjectAdapter writeTo)	throws JSONObjectAdapterException {
		if(stringMap != null){
			JSONObjectAdapter jsonObject = writeTo.createNew();
			for(String key: stringMap.keySet()){
				String value = stringMap.get(key);
				jsonObject.put(key, value);
			}
			writeTo.put("stringMap", jsonObject);
		}
		
		if(byteArrayMap != null){
			JSONObjectAdapter jsonObject = writeTo.createNew();
			for(String key: byteArrayMap.keySet()){
				byte[] value = byteArrayMap.get(key);
				jsonObject.put(key, value);
			}
			writeTo.put("byteArrayMap", jsonObject);
		}
		return writeTo;
	}

	@Override
	public String getJSONSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getStringMap() {
		return stringMap;
	}

	public void setStringMap(Map<String, String> stringMap) {
		this.stringMap = stringMap;
	}

	public Map<String, byte[]> getByteArrayMap() {
		return byteArrayMap;
	}

	public void setByteArrayMap(Map<String, byte[]> byteArrayMap) {
		this.byteArrayMap = byteArrayMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((byteArrayMap == null) ? 0 : byteArrayMap.hashCode());
		result = prime * result
				+ ((stringMap == null) ? 0 : stringMap.hashCode());
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
		Example other = (Example) obj;
		if (byteArrayMap == null) {
			if (other.byteArrayMap != null)
				return false;
		} else if (!byteArrayMap.equals(other.byteArrayMap))
			return false;
		if (stringMap == null) {
			if (other.stringMap != null)
				return false;
		} else if (!stringMap.equals(other.stringMap))
			return false;
		return true;
	}
	
	

}
