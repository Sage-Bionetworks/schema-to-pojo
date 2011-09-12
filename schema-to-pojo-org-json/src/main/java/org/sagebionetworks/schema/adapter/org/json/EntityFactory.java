package org.sagebionetworks.schema.adapter.org.json;

import java.lang.reflect.Constructor;

import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

/**
 * Helper for generating JSONEntity using adapters.
 * @author jmhill
 *
 */
public class EntityFactory {
	
	/**
	 * 
	 * @param entity
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static String createJSONStringForEntity(JSONEntity entity) throws JSONObjectAdapterException{
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		entity.writeToJSONObject(adapter);
		return adapter.toJSONString();
	}
	
	/**
	 * Create an entity from a JSON String.
	 * @param <T>
	 * @param jsonEntity
	 * @param clazz
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static <T> T createEntityFromJSONString(String jsonEntity, Class<? extends T> clazz) throws JSONObjectAdapterException{
		// First create an adapter with the datat
		JSONObjectAdapter adapter = JSONObjectAdapterImpl.createAdapterFromJSONString(jsonEntity);
		// Now create a new instance of the class
		try {
			Constructor<? extends T> con = clazz.getConstructor(JSONObjectAdapter.class);
			return con.newInstance(adapter);
		} catch (Exception e) {
			throw new JSONObjectAdapterException(e);
		} 
	}

}
