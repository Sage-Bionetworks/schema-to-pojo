package org.sagebionetworks.schema.generator.handler.schema03;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.generator.handler.TypeCreatorHandler;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

/**
 * Handles type creation for version 03 
 * @see <a
 *      href="http://tools.ietf.org/html/draft-zyp-json-schema-03">http://tools.ietf.org/html/draft-zyp-json-schema-03</a>
 * @author jmhill
 *
 */
public class TypeCreatorHandlerImpl03 implements TypeCreatorHandler {
	
	public static final String AUTO_GENERATED_MESSAGE = "Note: This class was auto-generated, and should not be directly modified.";


	@Override
	public JType handelCreateType(JPackage _package, ObjectSchema schema, JType superType, JType arrayType) throws ClassNotFoundException {
		if(superType.isPrimitive()) return superType;
		
		// Determine the type of this class
		if(schema.getType() == null) throw new IllegalArgumentException("TYPE cannot be null");
		
		// First check to see if this is a date format
		if(schema.getFormat() != null){
			if(schema.getFormat().isDateFormat()){
				// If the format is a date then the java type is a date.
				return _package.owner().ref(Date.class);
			}
		}
		
		// Handle primitives
		if(schema.getType().isPrimitive()){
			// This is a primitive
			return _package.owner().parseType(schema.getType().getJavaType());
		}
		// Strings
		if(TYPE.STRING == schema.getType()){
			return _package.owner().ref(String.class);
		}
		// Any is treated as a generic object
		if(TYPE.ANY == schema.getType()){
			return _package.owner().ref(Object.class);
		}
		// Null is treated as object
		if(TYPE.NULL == schema.getType()){
			return _package.owner().ref(Object.class);
		}
		if(TYPE.ARRAY == schema.getType()){
			// We must have Items
			if(arrayType == null) throw new IllegalArgumentException("A schema with TYPE.ARRAY must have a items that defines the type of the array");
			// Get the array type
			if(schema.getUniqueItems()){
				// This is a set
				return _package.owner().ref(Set.class).narrow(arrayType);
			}else{
				// This is a list
				return _package.owner().ref(List.class).narrow(arrayType);
			}
		}
		// The last category is objects.
		if(TYPE.OBJECT == schema.getType()){
			// A named object is a new class while a null name is a generic object
			if(schema.getName() == null){
				return _package.owner().ref(Object.class);
			}
			// Create a new class with this name
			try {
				JDefinedClass newClass = _package._class(schema.getName());
				if(superType != null){
					newClass._extends((JClass) superType);
				}
				// Add the comments to the class
				JDocComment docs = newClass.javadoc();
				if(schema.getTitle() != null){
					docs.add(schema.getTitle());
					docs.add("\n\n");
				}
				if(schema.getDescription() != null){
					docs.add(schema.getDescription());
					docs.add("\n\n");
				}
				// Add the auto-generated message
				docs.add(AUTO_GENERATED_MESSAGE);
				return newClass;
			} catch (JClassAlreadyExistsException e) {
				return e.getExistingClass();
			}
		}
		// Any other type is a failure
		throw new IllegalArgumentException("Unknown type: "+schema.getType());
	}

}
