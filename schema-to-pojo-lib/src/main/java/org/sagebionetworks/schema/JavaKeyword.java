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
	 * Determines the name used in java code for a schema <i>property</i>. A property name mirrors an
	 * external JSON key, which may contain characters that are legal in JSON but not in a Java
	 * identifier (e.g. the hyphen in OpenSearch's {@code normalization-processor}). Each such
	 * character is dropped and the following character upper-cased, so
	 * {@code normalization-processor} becomes {@code normalizationProcessor}. The JSON key itself is
	 * unaffected &mdash; it is carried separately as the generated {@code _KEY_*} constant's value.
	 * <p>
	 * Applied only on the property path. Enum values keep {@link #determineJavaName} because
	 * {@link #determineJsonName} must invert it to recover the serialized value, and this mapping is
	 * not invertible.
	 *
	 * @param propertyName
	 * @return Name to be used for a Java field, getter, and setter
	 */
	public static String determineJavaPropertyName(String propertyName){
		return determineJavaName(toJavaIdentifier(propertyName));
	}

	private static String toJavaIdentifier(String propertyName){
		if (propertyName == null || propertyName.isEmpty()) {
			return propertyName;
		}
		StringBuilder builder = new StringBuilder(propertyName.length());
		boolean upperCaseNext = false;
		for (int i = 0; i < propertyName.length(); i++) {
			char character = propertyName.charAt(i);
			// The first character has a stricter rule than the rest; '_' and '$' remain legal in both
			// positions, so an existing leading-underscore property such as _score is untouched.
			boolean legal = i == 0 ? Character.isJavaIdentifierStart(character)
					: Character.isJavaIdentifierPart(character);
			if (legal) {
				builder.append(upperCaseNext ? Character.toUpperCase(character) : character);
				upperCaseNext = false;
			} else {
				upperCaseNext = builder.length() > 0;
			}
		}
		return builder.toString();
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
