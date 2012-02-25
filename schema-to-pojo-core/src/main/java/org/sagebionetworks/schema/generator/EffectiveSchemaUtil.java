package org.sagebionetworks.schema.generator;

import java.util.LinkedHashMap;

import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

/**
 * For a schema that inherits properties from another schema (through "extends" or "implements"),
 * the "effective" schema collapses all properties from inheritances or interfaces into first class
 * properties.  This is useful for a client that just wants a simple representation of a schema, without
 * the need for walking class hierarchy, or interfaces.
 * 
 * @author jmhill
 *
 */
public class EffectiveSchemaUtil {
	
	/**
	 * Generate the JSON of the "effective" schema.
	 * @param schema
	 * @return
	 * @throws JSONObjectAdapterException 
	 */
	public static String generateJSONofEffectiveSchema(ObjectSchema schema) throws JSONObjectAdapterException{
		// First create the effective schema, then write it JSON.
		ObjectSchema effective = generateEffectiveSchema(schema);
		JSONObjectAdapter adapter = effective.writeToJSONObject(new JSONObjectAdapterImpl());
		return adapter.toJSONString();
	}
	
	/**
	 * Generate the "effective" schema object.
	 * @param schema
	 * @return
	 * @throws JSONObjectAdapterException 
	 */
	public static ObjectSchema generateEffectiveSchema(ObjectSchema schema) throws JSONObjectAdapterException{
		if(schema == null) throw new IllegalArgumentException("Schema cannot be null");
		// First make a copy of the schema
		JSONObjectAdapter adapter = schema.writeToJSONObject(new JSONObjectAdapterImpl());;
		adapter = new JSONObjectAdapterImpl(adapter.toJSONString());
		ObjectSchema copy = new ObjectSchema(adapter);
		// Clear the extends and and implements
		copy.setExtends(null);
		copy.setImplements(null);
		copy.setProperties((LinkedHashMap<String, ObjectSchema>) schema.getObjectFieldMap());
		ObjectSchema.recursivelyAddAllExtendsProperties(copy.getProperties(), schema);
		// Add any properties from the extends.
		return copy;
	}

}
