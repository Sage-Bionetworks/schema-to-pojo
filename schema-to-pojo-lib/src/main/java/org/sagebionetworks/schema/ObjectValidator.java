package org.sagebionetworks.schema;

import java.util.Map;

import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

/**
 * Validates JSONObjectAdapter's against their schemas.
 * @author ntiedema
 */
public class ObjectValidator {
	
	/**
	 * Validates a JSONObjectAdapter against it's schema.
	 * @param adapter
	 * @param schema
	 * @throws JSONObjectAdapterException
	 */
	public static void validateObject(JSONObjectAdapter adapter,
			ObjectSchema schema) throws JSONObjectAdapterException{
		//do we have a valid adapter
		if (adapter == null){
			throw new IllegalArgumentException
				("can not validate with a null adapter object");
		}
		
		//do we have a valid schema
		if (schema == null){
			throw new IllegalArgumentException
				("can not validate with a null schema object");
		}
		
		//check that adapter has all properties marked as "required" in schema
		validateRequiredProperties(adapter, schema);
	}
	
	/**
	 * Private helper method to make sure adapter has all properties that are
	 * represented as "required" in schema's properties.  All required 
	 * properties a schema has should translate to an adapter name/value JSON.
	 * @param adapter
	 * @param schema
	 * @throws JSONObjectAdapterException
	 */
	private static void validateRequiredProperties(JSONObjectAdapter adapter, 
			ObjectSchema schema) throws JSONObjectAdapterException {
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

