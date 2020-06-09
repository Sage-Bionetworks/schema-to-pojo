package org.sagebionetworks.schema;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JavaKeyword {

	// Copied list of keywords from  javax.lang.model.SourceVersion
	// because GWT can't translate that class
	private final static Set<String> JAVA_KEYWORDS;
	static {
		Set<String> s = new HashSet<String>(Arrays.asList(
				"abstract", "continue",     "for",          "new",          "switch",
				"assert",   "default",      "if",           "package",      "synchronized",
				"boolean",  "do",           "goto",         "private",      "this",
				"break",    "double",       "implements",   "protected",    "throw",
				"byte",     "else",         "import",       "public",       "throws",
				"case",     "enum",         "instanceof",   "return",       "transient",
				"catch",    "extends",      "int",          "short",        "try",
				"char",     "final",        "interface",    "static",       "void",
				"class",    "finally",      "long",         "strictfp",     "volatile",
				"const",    "float",        "native",       "super",        "while",
				// literals
				"null",     "true",         "false"
		));
		JAVA_KEYWORDS = Collections.unmodifiableSet(s);
	}

	public static final String PREFIX = "_";

	/**
	 * Determines the name that will be used in java code for a name.
	 * Currently, it only disambiguates names that collide with Java's keywords.
	 * @param propertyName
	 * @return Name to be used for a Java field
	 */
	public static String determineJavaName(String propertyName){
		return propertyName != null && JAVA_KEYWORDS.contains(propertyName) ? PREFIX + propertyName : propertyName;
	}

	/**
	 * Determines the name that will be used to serialize/deserialize JSON
	 * Currently, it only disambiguates names that collide with Java's keywords.
	 * @param propertyName
	 * @return Name to be used to serialize/deserialize JSON
	 */
	public static String determineJsonName(String propertyName){
		if( propertyName != null && !propertyName.isEmpty() && propertyName.startsWith(PREFIX)){
			String withoutUnderscore = propertyName.substring(PREFIX.length());
			if (JAVA_KEYWORDS.contains(withoutUnderscore) ){
				return withoutUnderscore;
			}
		}
		return propertyName;
	}
}
