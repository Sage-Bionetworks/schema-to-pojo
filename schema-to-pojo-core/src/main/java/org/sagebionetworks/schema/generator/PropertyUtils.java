package org.sagebionetworks.schema.generator;

import javax.lang.model.SourceVersion;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;

public class PropertyUtils {
	/**
	 * Determines the name that will be used in the java field for an ObjectSchema's property name.
	 * Currently, it only disambiguates names that collide with Java's keywords.
	 * @param propertyName
	 * @return Name to be used for a Java field
	 */
	public static String determineJavaFieldName(String propertyName){
		return SourceVersion.isKeyword(propertyName) ? "_" + propertyName : propertyName;
	}

	public static JFieldVar getPropertyReference(JDefinedClass classType, String propName) {
		String fieldName = determineJavaFieldName(propName);
		JFieldVar field = classType.fields().get(fieldName);
		if (field == null)
			throw new IllegalArgumentException(
					"Failed to find the JFieldVar for property: '"
							+ fieldName + "' on class: " + classType.name());
		return field;
	}

	public static TYPE validateNonNullType(JDefinedClass classType, ObjectSchema propSchema) {
		if (propSchema.getType() == null)
			throw new IllegalArgumentException("Property: '" + propSchema
					+ "' has a null TYPE on class: " + classType.name());
		return propSchema.getType();
	}
}
