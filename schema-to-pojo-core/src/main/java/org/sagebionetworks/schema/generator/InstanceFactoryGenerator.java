package org.sagebionetworks.schema.generator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

/**
 * Generates a factory that can be used to create new instances of a particular
 * interface. Each interface will have its own factory.
 * 
 * @author jmhill
 * 
 */
public class InstanceFactoryGenerator {

	JCodeModel codeModel;
	List<ObjectSchema> allObjects;
	Map<String, JDefinedClass> interfaceMap = new HashMap<String, JDefinedClass>();
	
	/**
	 * 
	 * @param codeModel The code model.
	 * @param allObjects This list should contain all schemas in the namespaces.
	 * 
	 */
	public InstanceFactoryGenerator(JCodeModel codeModel,
			List<ObjectSchema> allObjects) {
		super();
		this.codeModel = codeModel;
		this.allObjects = allObjects;
		buildAll();
	}
	
	/**
	 * Build up each instance factory.
	 */
	private void buildAll(){
		// For each interface build up a list of schemas that implement that interface.
		// A factory will be created for each of the interfaces.
		Map<String, List<ObjectSchema>> map = new HashMap<String, List<ObjectSchema>>();
		for(ObjectSchema schema: allObjects){
			// Look at each concrete type.
			ObjectSchema[] implementsArray = schema.getImplements();
			if((!TYPE.INTERFACE.equals(schema.getType()) && implementsArray != null)){
				// walk each interface
				for(ObjectSchema implementz: implementsArray){
					List<ObjectSchema> instanceList = map.get(implementz.getId());
					if(instanceList == null){
						instanceList = new LinkedList<ObjectSchema>();
						map.put(implementz.getId(), instanceList);
					}
					// Add this schema to the list
					instanceList.add(schema);
				}
			}
		}
		// Now create a factory for each interface
		for(String key: map.keySet()){
			List<ObjectSchema> instanceLis =  map.get(key);
			JDefinedClass factoryClass = RegisterGenerator.createClassFromFullName(codeModel, key+"InstatanceFactory");
			RegisterGenerator.createRegister(codeModel, instanceLis, factoryClass);
			// Map the interface to the factory
			this.interfaceMap.put(key, factoryClass);
		}
	}
	
	/**
	 * Get the factory that can be used to instantiate instances of the given interface.
	 * @param type ID
	 * @return
	 */
	public JDefinedClass getFactoryClass(String id) {
		JDefinedClass clazz = interfaceMap.get(id);
		if(clazz == null){
			throw new IllegalArgumentException("Cannot find a factory for: "+id+". Factories are only created for interfaces or abstract classes");
		}
		return clazz;
	}

	/**
	 * Get the factory that can be used to instantiate instances of the given interface.
	 * @param type
	 * @return
	 */
	public JDefinedClass getFactoryClass(JClass type) {
		return getFactoryClass(type.fullName());
	}
	
}
