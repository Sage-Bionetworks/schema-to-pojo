package org.sagebionetworks.schema.adapter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * For an interface that extends {@link JSONEntity} defines a default concreteType to be used when missing when deserializing.
 * 
 * @author Marco Marasca
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface JSONDefaultConcreteType {

	/**
	 * @return The concreteType to be used when missing during deserialization
	 */
	String value();
	
}
