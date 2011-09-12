package org.sagebionetworks.schema.generator;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.generator.handler.HandlerFactory;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

/**
 * Drives the generation of POJOs from schema definitions. Since a JSON schema can reference many other JSON schemas
 * the POJO creation process becomes recursive.  The purpose of this driver is to handle the necessary recursion 
 * and push the rest of the work to non-recursive handlers.  Put another way, the driver drives the handlers.
 * 
 * @author jmhill
 *
 */
public class PojoGeneratorDriver {
	
	/*
	 * The factory serves up handlers used to do all of the non-recursive work.
	 */
	HandlerFactory factory = null;
	
	public PojoGeneratorDriver(HandlerFactory factory){
		if(factory == null) throw new IllegalArgumentException("The handler factory cannot be null");
		this.factory = factory;
	}
	
	/**
	 * Create all POJOs from the list of root schemas.
	 * @param codeModel
	 * @param list
	 * @param packageName
	 * @throws ClassNotFoundException
	 */
	public void createAllClasses(JCodeModel codeModel, List<ObjectSchema> list, String packageName) throws ClassNotFoundException{
		// The first step is to register all named types and replace all references with
		// concrete schemas.
		list = preprocessSchemas(list);
		// We are now ready to start creating the classes
		// First create the package
		JPackage _package = codeModel._package(packageName);
		// Now recursively process all of the schema objects
		for(ObjectSchema schema: list){
			// Create each POJO
			createPOJO(_package, schema);
		}
	}
	
	/**
	 * Create a complete class for a given schema.
	 * 
	 * @param _package
	 * @param schema
	 * @return
	 * @throws ClassNotFoundException
	 */
	public JDefinedClass createPOJO(JPackage _package, ObjectSchema schema) throws ClassNotFoundException{
		// First create the type for this schema
		JType type = createOrGetType(_package, schema);
		if(!(type instanceof JDefinedClass)) return null;
		JDefinedClass classType = (JDefinedClass) type;
		// Process the properties
		if(schema.getProperties() != null){
			Iterator<String> it = schema.getProperties().keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				ObjectSchema propertySchema  = schema.getProperties().get(key);
				// Get type type for this property
				JType propertyType = createOrGetType(_package, propertySchema);
				// Create this property
				factory.getPropertyHandler().createProperty(propertySchema, classType,key, propertyType);
			}
		}
		// Add the JSON marshaling
		factory.getJSONMArshalingHandler().addJSONMarshaling(schema, classType);
		// Add hash and equals
		factory.getHashAndEqualsHandler().addHashAndEquals(schema, classType);
		return classType;
	}

	/**
	 * Pre-process all schema objects.  This will replace all references with concrete 
	 * schema objects.
	 * @param list
	 * @return
	 */
	List<ObjectSchema> preprocessSchemas(List<ObjectSchema> list) {
		Map<URI, ObjectSchema> register = registerAllIdentifiedObjectSchemas(list);
		// Use the register to replace all references with their concrete objects
		list = findAndReplaceAllReferencesSchemas(register, list);
		return list;
	}
	
	/**
	 * Create or Get a type for given schema object.
	 * @param _package
	 * @param schema
	 * @return
	 * @throws ClassNotFoundException
	 */
	public JType createOrGetType(JPackage _package, ObjectSchema schema) throws ClassNotFoundException {
		// The purpose of the driver is to do all of the recursion for the handlers
		JType superType = _package.owner()._ref(Object.class);
		if (schema.getExtends() != null) {
			superType = createOrGetType(_package, schema.getExtends());
		}
		JType arrayType = null;
		if( schema.getItems() != null){
			arrayType = createOrGetType(_package, schema.getItems());
		}
		// Let the handler do most of the work.
		return factory.getTypeCreatorHandler().handelCreateType(_package, schema, superType, arrayType);
	}
	
	/**
	 * Build up a map of all identified schemas in the list.
	 * Note: This will Recursively walk all sub-schemas of each object.
	 * @param list
	 * @return
	 * @throws IllegalArgumentException when duplicate ids are found.
	 */
	protected static Map<URI, ObjectSchema> registerAllIdentifiedObjectSchemas(List<ObjectSchema> list){
		Map<URI, ObjectSchema> map = new HashMap<URI, ObjectSchema>();
		// Walk over all schemas and build up the map.
		for(ObjectSchema schema: list){
			registerAllIdentifiedObjectSchemas(map, schema);
		}
		return map;
	}
	
	/**
	 * Recursively walk all objects
	 * @param map
	 * @param schemas
	 */
	protected static void registerAllIdentifiedObjectSchemas(Map<URI, ObjectSchema> map, ObjectSchema schema){
		// first add this object to the map if it has an id
		if(schema.getId() != null){
			ObjectSchema duplicate = map.put(schema.getId(), schema);
			if(duplicate != null) throw new IllegalArgumentException("More than one schema was found with id="+duplicate.getId());
		}
		// Now add all sub-schemas
		Iterator<ObjectSchema> it = schema.getSubSchemaIterator();
		while(it.hasNext()){
			ObjectSchema sub = it.next();
			registerAllIdentifiedObjectSchemas(map, sub);
		}
	}
	
	/**
	 * A schema can be a reference to another schema.  This function will find all references and replace them with the
	 * actual schema.  Will throw
	 * @param map
	 * @param list
	 * @throws IllegalArgumentException if a reference cannot be resolved.
	 */
	protected static List<ObjectSchema> findAndReplaceAllReferencesSchemas(Map<URI, ObjectSchema> map, List<ObjectSchema> list){
		List<ObjectSchema> results = new ArrayList<ObjectSchema>();
		for(ObjectSchema schema: list){
			// If this schema is a reference then replace it.
			schema = replaceRefrence(map, schema, schema);
			results.add(schema);
			// Replace all references in this schema
			recursiveFindAndReplaceAllReferencesSchemas(map, schema);
		}
		return results;
	}
	/**
	 * @param map
	 * @param schema
	 */
	protected static void recursiveFindAndReplaceAllReferencesSchemas(Map<URI, ObjectSchema> map, ObjectSchema schema){
		// First replace for each child
		Iterator<ObjectSchema> it = schema.getSubSchemaIterator();
		while(it.hasNext()){
			ObjectSchema sub = it.next();
			recursiveFindAndReplaceAllReferencesSchemas(map, sub);
		}
		// Now do the replace for this object
		findAndReplaceAllReferencesSchemas(map,schema);
	}
	
	/**
	 * Find and replace all references found in this schema.
	 * @param map
	 * @param schema
	 */
	protected static void findAndReplaceAllReferencesSchemas(Map<URI, ObjectSchema> map, ObjectSchema schema){
		// Properties
		if(schema.getProperties() != null){
			schema.setProperties(findAndReplaceAllReferencesSchemas(map, schema.getProperties(), schema));
		}
		// Additional
		if(schema.getAdditionalProperties() != null){
			schema.setAdditionalProperties(findAndReplaceAllReferencesSchemas(map, schema.getAdditionalProperties(), schema));
		}
		// Items
		if(schema.getItems() != null){
			schema.setItems(replaceRefrence(map, schema.getItems(), schema));
		}
		// AdditionItems
		if(schema.getAdditionalItems() != null){
			schema.setAdditionalItems(replaceRefrence(map, schema.getAdditionalItems(), schema));
		}
		// Extends
		if(schema.getExtends() != null){
			schema.setExtends(replaceRefrence(map, schema.getExtends(), schema));
		}
	}
	
	/**
	 * Rebuild the passed map replacing all references.
	 * @param registry
	 * @param toCheck
	 * @param self
	 * @return
	 */
	protected static Map<String, ObjectSchema> findAndReplaceAllReferencesSchemas(Map<URI, ObjectSchema> registry, Map<String, ObjectSchema> toCheck, ObjectSchema self){
		HashMap<String, ObjectSchema> newMap = new HashMap<String, ObjectSchema>();
		Iterator<String> it = toCheck.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			ObjectSchema schema = toCheck.get(key);
			schema = replaceRefrence(registry, schema, self);
			newMap.put(key, schema);
		}
		return newMap;
	}
	
	/**
	 * If the passed object has a reference then it will be replaced either with self, or from the registry.
	 * @param registry
	 * @param toCheck
	 * @param self
	 * @return
	 */
	protected static ObjectSchema replaceRefrence(Map<URI, ObjectSchema> registry, ObjectSchema toCheck, ObjectSchema self) {
		// Nothing to do if it is not a reference.
		if (toCheck.getRef() == null)
			return toCheck;
		// Is it a self reference?
		if (ObjectSchema.SELF_REFERENCE.equals(toCheck.getRef().toString())) {
			return self;
		} else {
			// Find it in the registry
			ObjectSchema fromRegistry = registry.get(toCheck.getRef());
			if (fromRegistry == null) throw new IllegalArgumentException("Cannot find the referenced schema: "+ toCheck.getRef());
			return fromRegistry;
		}
	}


}
