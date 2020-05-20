package org.sagebionetworks.schema;

import javax.lang.model.SourceVersion;

public class JavaKeyword {

	public static final String PREFIX = "_";

	/**
	 * Determines the name that will be used in java code for a name.
	 * Currently, it only disambiguates names that collide with Java's keywords.
	 * @param propertyName
	 * @return Name to be used for a Java field
	 */
	public static String determineJavaName(String propertyName){
		return propertyName != null && SourceVersion.isKeyword(propertyName) ? PREFIX + propertyName : propertyName;
	}

	/**
	 * Determines the name that will be used to serialize/deserialize JSON
	 * Currently, it only disambiguates names that collide with Java's keywords.
	 * @param propertyName
	 * @return Name to be used to serialize/deserialize JSON
	 */
	public static String determineJsonName(String propertyName){
		if( propertyName != null && !propertyName.isEmpty() && propertyName.startsWith(PREFIX)){
			String withoutUnderscore = propertyName.substring(1);
			if (SourceVersion.isKeyword(withoutUnderscore) ){
				return withoutUnderscore;
			}
		}
		return propertyName;
	}
}
