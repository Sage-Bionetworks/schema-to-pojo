package org.sagebionetworks.schema.adapter.org.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.ObjectSchemaImpl;
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
	 * Create a JSON String representing the passed entity.
	 * @param entity
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static String createJSONStringForEntity(JSONEntity entity) throws JSONObjectAdapterException{
		if(entity == null) throw new IllegalArgumentException("Entity cannot be null");
		JSONObjectAdapterImpl adapter = writeEntityToAdapter(entity);
		return adapter.toJSONString();
	}
	
	/**
	 * Create a JSON String representing the passed entity.
	 * @param entity
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static JSONObject createJSONObjectForEntity(JSONEntity entity) throws JSONObjectAdapterException{
		if(entity == null) throw new IllegalArgumentException("Entity cannot be null");
		JSONObjectAdapterImpl adapter = writeEntityToAdapter(entity);
		return adapter.wrapped;
	}

	/**
	 * Write the passed entity to an adapter.
	 * @param entity
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	private static JSONObjectAdapterImpl writeEntityToAdapter(JSONEntity entity)
			throws JSONObjectAdapterException {
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		entity.writeToJSONObject(adapter);
		return adapter;
	}
	
	/**
	 * Create an entity from a JSON String.
	 * @param <T>
	 * @param jsonEntity
	 * @param clazz
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static <T extends JSONEntity> T createEntityFromJSONString(String jsonString, Class<? extends T> clazz) throws JSONObjectAdapterException{
		if(jsonString == null) throw new IllegalArgumentException("JSON string cannot be null");
		if(clazz == null) throw new IllegalArgumentException("JSONEntity class cannot be null");
		// First create an adapter with the datat
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl(jsonString);
		return createEntityFromAdapter(clazz, adapter); 
	}
	
	
	/**
	 * Create an entity from a JSON String.
	 * @param <T>
	 * @param jsonEntity
	 * @param clazz
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static <T extends JSONEntity> T createEntityFromJSONObject(JSONObject jsonEntity, Class<? extends T> clazz) throws JSONObjectAdapterException{
		if(jsonEntity == null) throw new IllegalArgumentException("JSONObject cannot be null");
		if(clazz == null) throw new IllegalArgumentException("JSONEntity class cannot be null");
		// First create an adapter with the datat
		JSONObjectAdapter adapter = new JSONObjectAdapterImpl(jsonEntity);
		return createEntityFromAdapter(clazz, adapter); 
	}

	/**
	 * Read the provided JSON array string into a list of JSONEntites.
	 * 
	 * @param <T>
	 * @param json  Must be a JSON array.
	 * @param clazz
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static <T extends JSONEntity> ArrayList<T> readFromJSONArrayString(String json,
			Class<? extends T> clazz) throws JSONObjectAdapterException {
		if (json == null) {
			throw new IllegalArgumentException("json cannot be null");
		}
		if (clazz == null) {
			throw new IllegalArgumentException("JSONEntity class cannot be null");
		}
		JSONArrayAdapterImpl array = new JSONArrayAdapterImpl(json);
		ArrayList<T> list = new ArrayList<T>(array.length());
		for (int i = 0; (i < array.length()); i++) {
			T item = (array.isNull(i)) ? null : createEntityFromAdapter(clazz, array.getJSONObject(i));
			list.add(item);
		}
		return list;
	}
	
	/**
	 * Write the given collection of JSONEntites to JSON array string.
	 * 
	 * @param list
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static String writeToJSONArrayString(Collection<? extends JSONEntity> list)
			throws JSONObjectAdapterException {
		if (list == null) {
			throw new IllegalArgumentException("list cannot be null");
		}
		JSONArrayAdapterImpl adapter = new JSONArrayAdapterImpl();
		int i = 0;
		Iterator<? extends JSONEntity> it = list.iterator();
		while (it.hasNext()) {
			JSONEntity item = it.next();
			if (item == null) {
				adapter.putNull(i);
			} else {
				adapter.put(i, item.writeToJSONObject(new JSONObjectAdapterImpl()));
			}
			i++;
		}
		return adapter.toJSONString();
	}

	/**
	 * Given an adapter and class, create an instance of the entity.
	 * @param <T>
	 * @param clazz
	 * @param adapter
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	@SuppressWarnings("unchecked")
	private static <T extends JSONEntity> T createEntityFromAdapter(Class<? extends T> clazz, JSONObjectAdapter adapter) throws JSONObjectAdapterException {
		// Now create a new instance of the class
		try {
			T newInstance = null;
			if(clazz.isInterface()){
				String concreteType = extractConcreteType(adapter, clazz);
				// Use the concrete type to instantiate the object.
				try {
					newInstance = (T) Class.forName(concreteType).newInstance();
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException(String.format("Unknown %s : '%s'",ObjectSchema.CONCRETE_TYPE, concreteType), e);
				}
			}else{
				newInstance = clazz.newInstance();
			}
			newInstance.initializeFromJSONObject(adapter);
			return newInstance;
		}  catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONObjectAdapterException(e);
		}
	}
	
	private static <T extends JSONEntity> String extractConcreteType(JSONObjectAdapter adapter, Class<? extends T> clazz) throws JSONObjectAdapterException {
		if (!adapter.isNull(ObjectSchema.CONCRETE_TYPE)) {
			return adapter.getString(ObjectSchema.CONCRETE_TYPE);
		}
		
		try {
			Field defaultConcreteTypeField = clazz.getDeclaredField(ObjectSchema.DEFAULT_CONCRETE_TYPE_NAME);
			return (String) defaultConcreteTypeField.get(clazz);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new JSONObjectAdapterException(ObjectSchemaImpl.createMissingConcreteTypeMessage(clazz), e);
		}
	}

}
