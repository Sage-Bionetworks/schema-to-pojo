package org.sagebionetworks.schema.generator;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import org.sagebionetworks.schema.JavaKeyword;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;

public class PropertyUtils {

	public static JFieldVar getPropertyReference(JDefinedClass classType, String propName) {
		String fieldName = JavaKeyword.determineJavaName(propName);
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
