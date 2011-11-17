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
		//make sure all properties that have a pattern defined, check
		//that adapter's corresponding property is a valid instance of
		//that pattern
		validatePatternProperties(schema, adapter);
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
					if (adapter.isNull(propertyName)){
						throw new JSONObjectAdapterException("The property: " + propertyName + " is required, " +
							"and was not found in the adapter");
					}
				}
			}
		}
	}
	
	/**
	 * Verifies that all schema properties that have a "pattern" defined, 
	 * becomes a pattern which adapter's property is verified against.  If
	 * adapter property is a valid instance of the pattern, then it is fine.
	 * If adapter property is not a valid instance of the pattern then an
	 * exception is thrown.
	 * @throws JSONObjectAdapterException 
	 */
	public static void validatePatternProperties(ObjectSchema schema, JSONObjectAdapter adapter) throws JSONObjectAdapterException{
		if (schema == null){
			throw new IllegalArgumentException("can't validate patterns against a null schema");
		}
		if (adapter == null){
			throw new IllegalArgumentException("cant' validate patterns for schema "
					+ schema + " against a null adapter");
		}
		//obtain all the schema's properties
		Map<String, ObjectSchema> schemaProperties = schema.getProperties();
		if (schemaProperties != null){
			for (String nextPropertyName : schemaProperties.keySet()){
				//obtain next property
				ObjectSchema nextProperty = schemaProperties.get(nextPropertyName);
			
				//check if nextProperty has a pattern defined
				if (nextProperty.getPattern() != null){
					//make sure all properties that have a pattern defined are of type string
					if (TYPE.STRING != nextProperty.getType()){
						throw new JSONObjectAdapterException("pattern for property " 
								+ nextProperty + 
								" can only exist for a property that has a String type");
					}
					
					//obtain pattern
					String propertysPattern = nextProperty.getPattern();
					
					//verify adapter has that property
					if (!adapter.has(nextPropertyName)){
						throw new IllegalArgumentException("can't validate Pattern for property " 
								+ nextProperty + " because adapter " + adapter + 
								" had no corresponding property with name " + nextPropertyName);
					}
					
					//obtain adapter's instance of the pattern
					String mustMatch = adapter.getString(nextPropertyName);
	
// The following code does not work with GWT.
					
					//make pattern and matcher for regular expression checking
//					Pattern regexPattern = Pattern.compile(propertysPattern);
//					Matcher matcher = regexPattern.matcher(mustMatch);
//					
//					//check if the adapter matches pattern
//					if (!matcher.matches()){
//						throw new JSONObjectAdapterException ("pattern from property "
//								+ nextProperty + " has a regularExpression pattern" +
//										" that does not match adapter " + adapter);	
//					}
				}
			}
		}
	}
}

