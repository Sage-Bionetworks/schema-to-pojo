package org.sagebionetworks.schema.generator.handler;

import org.sagebionetworks.schema.ObjectSchema;

import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

/**
 * This handler 
 * @author jmhill
 *
 */
public interface TypeCreatorHandler {
	
	/**
	 * Create a new type for a given object schema.
	 * @param _package
	 * @param schema - The schema to create a type for.
	 * @param superType - If the schema extends another type, this will type will be be provided.
	 * @param arrayType - If the type is an array, the array type will be provided.
	 * @return - The type created.
	 * @throws ClassNotFoundException
	 */
	public JType handelCreateType(JPackage _package, ObjectSchema schema, JType superType, JType arrayType, JType[] interfanceTypes) throws ClassNotFoundException;

}
