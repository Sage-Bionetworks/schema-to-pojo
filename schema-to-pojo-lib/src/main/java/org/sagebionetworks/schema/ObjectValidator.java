package org.sagebionetworks.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

/**
 * Utility to help validate an instance (as an JSONObjectAdapter) against a schema.
 * 
 * @author John
 *
 */
public class ObjectValidator {
	/**
	 * Private schema cache.
	 * NOTE: We cannot use Collections.synchronizedMap because GWT does not support it.
	 */
	private static Map<Class<? extends JSONEntity>, ObjectSchema> cache = new HashMap<Class<? extends JSONEntity>, ObjectSchema>();
	
	/**
	 * Get the ObjectSchema for a JSONEntity.
	 * @param entity
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	private static ObjectSchema getSchema(String schemaJSON, JSONObjectAdapter adapter, Class<? extends JSONEntity> clazz) {
		if(schemaJSON == null) throw new IllegalArgumentException("Schema string cannot be null");
		if(adapter == null) throw new IllegalArgumentException("Adapter cannot be null");
		if(clazz == null) throw new IllegalArgumentException("Adapter cannot be null");
		ObjectSchema schema = cache.get(clazz);
		if(schema == null){
			try {
				schema = new ObjectSchema(adapter.createNew(schemaJSON));
			} catch (JSONObjectAdapterException e) {
				// convert this to a runtime.
				throw new RuntimeException(e);
			}
			cache.put(clazz, schema);
		}
		return schema;
	}
	
	/**
	 * Validate.
	 * @param schemaJSON
	 * @param adapter
	 * @param clazz
	 */
	public static void validateEntity(String schemaJSON, JSONObjectAdapter adapter, Class<? extends JSONEntity> clazz) throws JSONObjectAdapterException{
		if(schemaJSON == null) throw new IllegalArgumentException("Schema string cannot be null");
		if(adapter == null) throw new IllegalArgumentException("Adapter cannot be null");
		if(clazz == null) throw new IllegalArgumentException("Adapter cannot be null");
		// First fetch the schema.
		ObjectSchema schema = getSchema(schemaJSON, adapter, clazz);
		// Validate it against the schema
		validateEntity(schema, adapter);
	}
	
	/**
	* Validate the passed adapter against the given schema.
	* @param schema
	* @param adapter
	* @throws JSONObjectAdapterException
	*/
	public static void validateEntity(ObjectSchema schema, JSONObjectAdapter adapter) throws JSONObjectAdapterException{
		if(schema == null) throw new IllegalArgumentException("Schema cannot be null");
		if(adapter == null) throw new IllegalArgumentException("Adapter cannot be null");
		// First fetch the schema.
		validateAllKeysAreDefined(schema, adapter);
		//make sure all required properties are represented in adapter
		validateRequiredProperties(schema, adapter);
	}
	
	/**
	 * Validate that all of the keys are defined.
	 * @param schema
	 * @param adapter
	 * @throws JSONObjectAdapterException 
	 */
	private static void validateAllKeysAreDefined(ObjectSchema schema, JSONObjectAdapter adapter) throws JSONObjectAdapterException{
		Iterator<String> it = adapter.keys();
		Map<String, ObjectSchema> props = schema.getProperties();
		if(props == null){
			if(it.hasNext()) throw new JSONObjectAdapterException("The schema has not properties so the adapter cannot have any data");
			return;
		}
		while(it.hasNext()){
			String key = it.next();
			if(!props.containsKey(key)){
				throw new JSONObjectAdapterException("Found property: "+key+", but this property is not defined in the schema: "+schema.getId());
			}
		}
	}
	
	/**
	 * Private helper method to make sure adapter has all properties that are
	 * represented as "required" in schema's properties.  All required 
	 * properties a schema has should translate to an adapter name/value JSON.
	 * @param adapter
	 * @param schema
	 * @throws JSONObjectAdapterException
	 */
	private static void validateRequiredProperties(ObjectSchema schema, 
			JSONObjectAdapter adapter) throws JSONObjectAdapterException {
		//obtain schema's properties
		Map<String, ObjectSchema> schemaProperties = schema.getProperties();
		if (schemaProperties != null){
			for (String propertyName : schemaProperties.keySet()){
				//check each schemaProperty and if it is required, verify
				//adapter has corresponding property
				ObjectSchema nextPropertySchema = schemaProperties.get(propertyName);
				if (nextPropertySchema.isRequired()){
					if (!adapter.has(propertyName)){
						throw new JSONObjectAdapterException("The property: " + propertyName + " is required, " +
							"and was not found in the adapter");
					}
					//validate that adapter has correct "type"
					validatePropertyType(adapter, nextPropertySchema, propertyName);
				}
			}
		}
	}
	
	/**
	 * Validates a JSONObjectAdapter has name/values in the correct TYPE for 
	 * all required schema properties.
	 * @param adapter
	 * @throws JSONObjectAdapterException 
	 */
	protected static void validatePropertyType(JSONObjectAdapter adapter, ObjectSchema property, String propertyKey) throws JSONObjectAdapterException {
		if (TYPE.STRING == property.getType()){
			if (adapter.getString(propertyKey) == null){
				throw new JSONObjectAdapterException(propertyKey + " is not a valid string");
			}
		}
		else if (TYPE.NUMBER == property.getType()){
			adapter.getDouble(propertyKey);
		}
		else if (TYPE.INTEGER == property.getType()){
			adapter.getLong(propertyKey);
		}
		else if (TYPE.BOOLEAN == property.getType()){
			adapter.getBoolean(propertyKey);
		}
		else if (TYPE.OBJECT == property.getType() || TYPE.ANY == property.getType() || TYPE.INTERFACE == property.getType()){
			if (adapter.isNull(propertyKey)){
				throw new JSONObjectAdapterException(propertyKey + " is not a valid JSONObjectAdapter");
			}
			//we verified that it's not a JSONObject.NULL, but also need to verify
			//adapter has that property in the correct type
			adapter.getJSONObject(propertyKey);
		}
		else if (TYPE.ARRAY == property.getType()){
			if (adapter.getJSONArray(propertyKey) == null){
				throw new JSONObjectAdapterException(propertyKey + " is not a valid JSONObjectArray");
			}
		}
		else if (TYPE.NULL == property.getType()){
			if (adapter.getJSONObject(propertyKey) != null){
				throw new JSONObjectAdapterException(propertyKey + " is not a null JSONObjectAdapter");
			}
		}
		else {
			throw new JSONObjectAdapterException(propertyKey + "is not a supported JSONObject type");
		}
	}
}

