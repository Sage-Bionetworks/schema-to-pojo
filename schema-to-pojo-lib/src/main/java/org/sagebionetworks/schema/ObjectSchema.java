package org.sagebionetworks.schema;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

public interface ObjectSchema extends JSONEntity {

	public static final String JSON_DEPENDENCIES = "dependencies";
	public static final String JSON_SCHEMA = "schema";
	public static final String JSON_DISALLOW = "disallow";
	public static final String JSON_DIVISIBLE_BY = "divisibleBy";
	public static final String JSON_TITLE = "title";
	public static final String JSON_MAX_LENGTH = "maxLength";
	public static final String JSON_MIN_LENGTH = "minLength";
	public static final String JSON_PATTERN = "pattern";
	public static final String JSON_MAX_ITEMS = "maxItems";
	public static final String JSON_MIN_ITEMS = "minItems";
	public static final String JSON_TRANSIENT = "transient";
	public static final String JSON_ID = "id";
	public static final String JSON_NAME = "name";
	public static final String JSON_TYPE = "type";
	public static final String JSON_PROPERTIES = "properties";
	public static final String JSON_ADDITIONAL_PROPERTIES = "additionalProperties";
	public static final String JSON_ITEMS = "items";
	public static final String JSON_UNIQUE_ITEMS = "uniqueItems";
	public static final String JSON_ADDITIONAL_ITEMS = "additionalItems";
	public static final String JSON_KEY = "key";
	public static final String JSON_VALUE = "value";
	public static final String JSON_REQUIRED = "required";
	public static final String JSON_MINIMUM = "minimum";
	public static final String JSON_MAXIMUM = "maximum";
	public static final String JSON_DESCRIPTION = "description";
	public static final String JSON_IMPLEMENTS = "implements";
	public static final String JSON_$REF = "$ref";
	public static final String JSON_EXTENDS = "extends";
	public static final String JSON_FORMAT = "format";
	public static final String JSON_ENUM = "enum";
	public static final String JSON_CONTENT_ENCODING = "contentEncoding";
	public static final String SELF_REFERENCE = "#";
	public static final String JSON_DEFAULT = "default";
	public static final String JSON_LINKS = "links";
	public static final String JSON_RECURSIVE_ANCHOR = "$recursiveAnchor";
	public static final String JSON_RECURSIVE_REF = "$recursiveRef";
	
	/**
	 * For the case where a POJO's fields types are an interfaces or abstract class,
	 * we need to know the type of the concrete class used at runtime, to support parsing
	 * these POJOs from JSON.  To support such abstractions the 'concreteType' property
	 * is used to capture the concrete type used.
	 */
	public static final String CONCRETE_TYPE = "concreteType";

	/**
	 * For the case where a POJO is created from a new version with additional fields, we want to preserve those
	 * additional fields and re-emit them if necessary. For that we keep an optional list of JSONObjects
	 */
	public static final String EXTRA_FIELDS = "extraFieldsFromNewerVersion";
	
	/**
	 * Error message for null adapter.
	 */
	public static final String OBJECT_ADAPTER_CANNOT_BE_NULL = "org.sagebionetworks.schema.adapter.JSONObjectAdapter cannot be null";
	
	/**
	 * Name of the String[] containing all keys used by a class.
	 */
	public static final String ALL_KEYS_NAME = "_ALL_KEYS";
	/**
	 * Template used to create key constants for property names.
	 */
	public static final String KEY_PREFIX = "_KEY_";
	
	public String get$recursiveRef();

	public void set$recursiveRef(String $recursiveRef);

	public Boolean get$recursiveAnchor();

	public void set$recursiveAnchor(Boolean $recursiveAnchor);

	/**
	 * The links for this schema.
	 * @return
	 */
	public LinkDescription[] getLinks();
	
	public void setLinks(LinkDescription[] links);



	public String getName();

	public void setName(String name);

	/**
	 * 6.4. contentEncoding
	 * 
	 * If the instance property value is a string, this attribute defines that
	 * the string SHOULD be interpreted as binary data and decoded using the
	 * encoding named by this schema property. RFC 2045, Sec 6.1 [RFC2045] lists
	 * the possible values for this property.
	 * 
	 * @param encoding
	 */
	public void setContentEncoding(ENCODING encoding);

	/**
	 * 6.4. contentEncoding
	 * 
	 * If the instance property value is a string, this attribute defines that
	 * the string SHOULD be interpreted as binary data and decoded using the
	 * encoding named by this schema property. RFC 2045, Sec 6.1 [RFC2045] lists
	 * the possible values for this property.
	 * 
	 * @return
	 */
	public ENCODING getContentEncoding();

	/**
	 * 5.1. type
	 * 
	 * 
	 * This attribute defines what the primitive type or the schema of the
	 * instance MUST be in order to validate. This attribute can take one of two
	 * forms:
	 * 
	 * @return
	 */
	public TYPE getType();
	/**
	 * 5.1. type
	 * 
	 * 
	 * This attribute defines what the primitive type or the schema of the
	 * instance MUST be in order to validate. This attribute can take one of two
	 * forms:
	 * 
	 * @param type
	 */
	public void setType(TYPE type);

	/**
	 * 5.2. properties
	 * 
	 * 
	 * This attribute is an object with property definitions that define the
	 * valid values of instance object property values. When the instance value
	 * is an object, the property values of the instance object MUST conform to
	 * the property definitions in this object. In this object, each property
	 * definition's value MUST be a schema, and the property's name MUST be the
	 * name of the instance property that it defines. The instance property
	 * value MUST be valid according to the schema from the property definition.
	 * Properties are considered unordered, the order of the instance properties
	 * MAY be in any order.
	 * 
	 * @return
	 */
	public Map<String, ObjectSchema> getProperties() ;
	/**
	 * 5.2. properties
	 * 
	 * 
	 * This attribute is an object with property definitions that define the
	 * valid values of instance object property values. When the instance value
	 * is an object, the property values of the instance object MUST conform to
	 * the property definitions in this object. In this object, each property
	 * definition's value MUST be a schema, and the property's name MUST be the
	 * name of the instance property that it defines. The instance property
	 * value MUST be valid according to the schema from the property definition.
	 * Properties are considered unordered, the order of the instance properties
	 * MAY be in any order.
	 * 
	 * @param properties
	 */
	public void putProperty(String key, ObjectSchema property);

	/**
	 * Set the primary properties.
	 * @param properties
	 */
	public void setProperties(LinkedHashMap<String, ObjectSchema> properties);
	/**
	 * 5.4. additionalProperties
	 * 
	 * 
	 * This attribute defines a schema for all properties that are not
	 * explicitly defined in an object type definition. If specified, the value
	 * MUST be a schema or a boolean. If false is provided, no additional
	 * properties are allowed beyond the properties defined in the schema. The
	 * default value is an empty schema which allows any value for additional
	 * properties.
	 * 
	 * @return
	 */
	public Map<String, ObjectSchema> getAdditionalProperties();
	/**
	 * 5.4. additionalProperties
	 * 
	 * 
	 * This attribute defines a schema for all properties that are not
	 * explicitly defined in an object type definition. If specified, the value
	 * MUST be a schema or a boolean. If false is provided, no additional
	 * properties are allowed beyond the properties defined in the schema. The
	 * default value is an empty schema which allows any value for additional
	 * properties.
	 * 
	 * @param additionalProperties
	 */
	public void putAdditionalProperty(String key, ObjectSchema property);

	/**
	 * Set the additional properties.
	 * @param additionalProperties
	 */
	public void setAdditionalProperties(LinkedHashMap<String, ObjectSchema> additionalProperties);
	
	/**
	 * 5.5. items
	 * 
	 * 
	 * This attribute defines the allowed items in an instance array, and MUST
	 * be a schema or an array of schemas. The default value is an empty schema
	 * which allows any value for items in the instance array.
	 * 
	 * When this attribute value is a schema and the instance value is an array,
	 * then all the items in the array MUST be valid according to the schema.
	 * 
	 * When this attribute value is an array of schemas and the instance value
	 * is an array, each position in the instance array MUST conform to the
	 * schema in the corresponding position for this array. This called tuple
	 * typing. When tuple typing is used, additional items are allowed,
	 * disallowed, or constrained by the "additionalItems" (Section 5.6)
	 * attribute using the same rules as "additionalProperties" (Section 5.4)
	 * for objects.
	 * 
	 * @return
	 */
	public ObjectSchema getItems(); 

	/**
	 * 5.5. items
	 * 
	 * 
	 * This attribute defines the allowed items in an instance array, and MUST
	 * be a schema or an array of schemas. The default value is an empty schema
	 * which allows any value for items in the instance array.
	 * 
	 * When this attribute value is a schema and the instance value is an array,
	 * then all the items in the array MUST be valid according to the schema.
	 * 
	 * When this attribute value is an array of schemas and the instance value
	 * is an array, each position in the instance array MUST conform to the
	 * schema in the corresponding position for this array. This called tuple
	 * typing. When tuple typing is used, additional items are allowed,
	 * disallowed, or constrained by the "additionalItems" (Section 5.6)
	 * attribute using the same rules as "additionalProperties" (Section 5.4)
	 */
	public void setItems(ObjectSchema items);

	/**
	 * 5.6. additionalItems
	 * 
	 * 
	 * This provides a definition for additional items in an array instance when
	 * tuple definitions of the items is provided. This can be false to indicate
	 * additional items in the array are not allowed, or it can be a schema that
	 * defines the schema of the additional items.
	 * 
	 * @return
	 */
	public ObjectSchema getAdditionalItems();

	/**
	 * 5.6. additionalItems
	 * 
	 * 
	 * This provides a definition for additional items in an array instance when
	 * tuple definitions of the items is provided. This can be false to indicate
	 * additional items in the array are not allowed, or it can be a schema that
	 * defines the schema of the additional items.
	 * 
	 * @param additionalItems
	 */
	public void setAdditionalItems(ObjectSchema additionalItems);

	/**
	 * Sage added
	 * 
	 * 
	 * This attribute defines the key type of an instance map, and MUST be a schema.
	 * 
	 * @return
	 */
	public ObjectSchema getKey();

	/**
	 * Sage added
	 * 
	 * 
	 * This attribute defines the key type of an instance map, and MUST be a schema.
	 * 
	 * @param key
	 */
	public void setKey(ObjectSchema key);

	/**
	 * Sage added
	 * 
	 * 
	 * This attribute defines the value type of an instance map, and MUST be a schema.
	 * 
	 * @return
	 */
	public ObjectSchema getValue();
	/**
	 * Sage added
	 * 
	 * 
	 * This attribute defines the value type of an instance map, and MUST be a schema.
	 * 
	 * @param key
	 */
	public void setValue(ObjectSchema value);

	/**
	 * 5.7. required
	 * 
	 * 
	 * This attribute indicates if the instance must have a value, and not be
	 * undefined. This is false by default, making the instance optional.
	 */
	public boolean isRequired();

	/**
	 * 5.7. required
	 * 
	 * 
	 * This attribute indicates if the instance must have a value, and not be
	 * undefined. This is false by default, making the instance optional.
	 * 
	 * @param required
	 */
	public void setRequired(Boolean required);
	
	/**
	 * 5.8. dependencies
	 * 
	 * 
	 * This attribute is an object that defines the requirements of a property
	 * on an instance object. If an object instance has a property with the same
	 * name as a property in this attribute's object, then the instance must be
	 * valid against the attribute's property value (hereafter referred to as
	 * the "dependency value").
	 * 
	 * The dependency value can take one of two forms:
	 * 
	 * Simple Dependency If the dependency value is a string, then the instance
	 * object MUST have a property with the same name as the dependency value.
	 * If the dependency value is an array of strings, then the instance object
	 * MUST have a property with the same name as each string in the dependency
	 * value's array.
	 * 
	 * @return
	 */
	public String[] getDependencies();

	/**
	 * 5.8. dependencies
	 * 
	 * 
	 * This attribute is an object that defines the requirements of a property
	 * on an instance object. If an object instance has a property with the same
	 * name as a property in this attribute's object, then the instance must be
	 * valid against the attribute's property value (hereafter referred to as
	 * the "dependency value").
	 * 
	 * The dependency value can take one of two forms:
	 * 
	 * Simple Dependency If the dependency value is a string, then the instance
	 * object MUST have a property with the same name as the dependency value.
	 * If the dependency value is an array of strings, then the instance object
	 * MUST have a property with the same name as each string in the dependency
	 * value's array.
	 */
	public void setDependencies(String[] dependencies);

	/**
	 * 5.9. minimum
	 * 
	 * 
	 * This attribute defines the minimum value of the instance property when
	 * the type of the instance value is a number
	 * 
	 * @return
	 */
	public Number getMinimum();
	/**
	 * 5.9. minimum
	 * 
	 * 
	 * This attribute defines the minimum value of the instance property when
	 * the type of the instance value is a number
	 * 
	 * @param minimum
	 */
	public void setMinimum(Number minimum);
	/**
	 * 5.10. maximum
	 * 
	 * 
	 * This attribute defines the maximum value of the instance property when
	 * the type of the instance value is a number.
	 * 
	 * @return
	 */
	public Number getMaximum();

	/**
	 * 5.10. maximum
	 * 
	 * 
	 * This attribute defines the maximum value of the instance property when
	 * the type of the instance value is a number.
	 * 
	 * @param maximum
	 */
	public void setMaximum(Number maximum);

	/**
	 * 5.12. exclusiveMaximum
	 * 
	 * 
	 * This attribute indicates if the value of the instance (if the instance is
	 * a number) can not equal the number defined by the "maximum" attribute.
	 * This is false by default, meaning the instance value can be less then or
	 * equal to the maximum value.
	 * 
	 * @return
	 */
	public Number getExclusiveMinimum();

	/**
	 * 5.12. exclusiveMaximum
	 * 
	 * 
	 * This attribute indicates if the value of the instance (if the instance is
	 * a number) can not equal the number defined by the "maximum" attribute.
	 * This is false by default, meaning the instance value can be less then or
	 * equal to the maximum value.
	 * 
	 * @param exclusiveMinimum
	 */
	public void setExclusiveMinimum(Number exclusiveMinimum);

	/**
	 * 5.11. exclusiveMinimum
	 * 
	 * 
	 * This attribute indicates if the value of the instance (if the instance is
	 * a number) can not equal the number defined by the "minimum" attribute.
	 * This is false by default, meaning the instance value can be greater then
	 * or equal to the minimum value.
	 * 
	 * @return
	 */
	public Number getExclusiveMaximum();

	/**
	 * 5.11. exclusiveMinimum
	 * 
	 * 
	 * This attribute indicates if the value of the instance (if the instance is
	 * a number) can not equal the number defined by the "minimum" attribute.
	 * This is false by default, meaning the instance value can be greater then
	 * or equal to the minimum value.
	 */
	public void setExclusiveMaximum(Number exclusiveMaximum);

	/**
	 * 5.13. minItems
	 * 
	 * 
	 * This attribute defines the minimum number of values in an array when the
	 * array is the instance value.
	 * 
	 * @return
	 */
	public Long getMinItems();

	/**
	 * 5.13. minItems
	 * 
	 * 
	 * This attribute defines the minimum number of values in an array when the
	 * array is the instance value.
	 * 
	 * @param minItems
	 */
	public void setMinItems(Long minItems);

	/**
	 * 5.14. maxItems
	 * 
	 * 
	 * This attribute defines the maximum number of values in an array when the
	 * array is the instance value.
	 * 
	 * @return
	 */
	public Long getMaxItems();
	/**
	 * 5.14. maxItems
	 * 
	 * 
	 * This attribute defines the maximum number of values in an array when the
	 * array is the instance value.
	 */
	public void setMaxItems(Long maxItems);

	/**
	 * 5.15. uniqueItems
	 * 
	 * 
	 * This attribute indicates that all items in an array instance MUST be
	 * unique (contains no two identical values).
	 * 
	 * Two instance are consider equal if they are both of the same type and:
	 * 
	 * are null; or
	 * 
	 * @return
	 */
	public boolean getUniqueItems();

	/**
	 * 5.15. uniqueItems
	 * 
	 * 
	 * This attribute indicates that all items in an array instance MUST be
	 * unique (contains no two identical values).
	 * 
	 * Two instance are consider equal if they are both of the same type and:
	 * 
	 * are null; or
	 * 
	 * @param uniqueItems
	 */
	public void setUniqueItems(Boolean uniqueItems);

	/**
	 * Transient data is not persistent. The default is false.
	 * 
	 * @return
	 */
	public boolean isTransient();
	
	/**
	 * Transient data is not persistent.
	 * 
	 * @param trans
	 */
	public void setTransient(Boolean trans);
	
	/**
	 * 5.16. pattern
	 * 
	 * 
	 * When the instance value is a string, this provides a regular expression
	 * that a string instance MUST match in order to be valid. Regular
	 * expressions SHOULD follow the regular expression specification from ECMA
	 * 262/Perl 5
	 * 
	 * @return
	 */
	public String getPattern();

	/**
	 * 5.16. pattern
	 * 
	 * 
	 * When the instance value is a string, this provides a regular expression
	 * that a string instance MUST match in order to be valid. Regular
	 * expressions SHOULD follow the regular expression specification from ECMA
	 * 262/Perl 5
	 * 
	 * @param pattern
	 */
	public void setPattern(String pattern);

	/**
	 * 5.17. minLength
	 * 
	 * 
	 * When the instance value is a string, this defines the minimum length of
	 * the string.
	 * 
	 * @return
	 */
	public Integer getMinLength();

	/**
	 * 5.17. minLength
	 * 
	 * 
	 * When the instance value is a string, this defines the minimum length of
	 * the string.
	 * 
	 * @param minLength
	 */
	public void setMinLength(Integer minLength);

	/**
	 * 5.18. maxLength
	 * 
	 * 
	 * When the instance value is a string, this defines the maximum length of
	 * the string.
	 * 
	 * @return
	 */
	public Integer getMaxLength();

	/**
	 * 5.18. maxLength
	 * 
	 * 
	 * When the instance value is a string, this defines the maximum length of
	 * the string.
	 * 
	 * @param maxLength
	 */
	public void setMaxLength(Integer maxLength);

	/**
	 * 5.19. enum
	 * 
	 * 
	 * This provides an enumeration of all possible values that are valid for
	 * the instance property. This MUST be an array, and each item in the array
	 * represents a possible value for the instance value. If this attribute is
	 * defined, the instance value MUST be one of the values in the array in
	 * order for the schema to be valid. Comparison of enum values uses the same
	 * algorithm as defined in "uniqueItems" (Section 5.15).
	 * 
	 * @return
	 */
	public EnumValue[] getEnum();

	/**
	 * 5.19. enum
	 * 
	 * 
	 * This provides an enumeration of all possible values that are valid for
	 * the instance property. This MUST be an array, and each item in the array
	 * represents a possible value for the instance value. If this attribute is
	 * defined, the instance value MUST be one of the values in the array in
	 * order for the schema to be valid. Comparison of enum values uses the same
	 * algorithm as defined in "uniqueItems" (Section 5.15).
	 * 
	 * @param _enum
	 */
	public void setEnum(EnumValue[] _enum) ;

	/**
	 * 5.20. default
	 * 
	 * 
	 * This attribute defines the default value of the instance when the
	 * instance is undefined.
	 * 
	 * @return
	 */
	public Object getDefault();

	/**
	 * 5.20. default
	 * 
	 * 
	 * This attribute defines the default value of the instance when the
	 * instance is undefined.
	 * 
	 * @param _default
	 */
	public void setDefault(Object _default);

	/**
	 * 5.21. title
	 * 
	 * 
	 * This attribute is a string that provides a short description of the
	 * instance property.
	 * 
	 * @return
	 */
	public String getTitle();

	/**
	 * 5.21. title
	 * 
	 * 
	 * This attribute is a string that provides a short description of the
	 * instance property.
	 * 
	 * @param title
	 */
	public void setTitle(String title);

	/**
	 * 5.22. description
	 * 
	 * 
	 * This attribute is a string that provides a full description of the of
	 * purpose the instance property.
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * 5.22. description
	 * 
	 * 
	 * This attribute is a string that provides a full description of the of
	 * purpose the instance property.
	 * 
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 * 5.23. format
	 * 
	 * 
	 * This property defines the type of data, content type, or microformat to
	 * be expected in the instance property values. A format attribute MAY be
	 * one of the values listed below, and if so, SHOULD adhere to the semantics
	 * describing for the format. A format SHOULD only be used to give meaning
	 * to primitive types (string, integer, number, or boolean). Validators MAY
	 * (but are not required to) validate that the instance values conform to a
	 * format. The following formats are predefined:
	 * 
	 * @return
	 */
	public FORMAT getFormat();

	/**
	 * 5.23. format
	 * 
	 * 
	 * This property defines the type of data, content type, or microformat to
	 * be expected in the instance property values. A format attribute MAY be
	 * one of the values listed below, and if so, SHOULD adhere to the semantics
	 * describing for the format. A format SHOULD only be used to give meaning
	 * to primitive types (string, integer, number, or boolean). Validators MAY
	 * (but are not required to) validate that the instance values conform to a
	 * format. The following formats are predefined:
	 * 
	 * @param format
	 */
	public void setFormat(FORMAT format);

	/**
	 * 5.24. divisibleBy
	 * 
	 * 
	 * This attribute defines what value the number instance must be divisible
	 * by with no remainder (the result of the division must be an integer.) The
	 * value of this attribute SHOULD NOT be 0.
	 * 
	 * @return
	 */
	public Number getDivisibleBy();

	/**
	 * 5.24. divisibleBy
	 * 
	 * 
	 * This attribute defines what value the number instance must be divisible
	 * by with no remainder (the result of the division must be an integer.) The
	 * value of this attribute SHOULD NOT be 0.
	 * 
	 * @param divisibleBy
	 */
	public void setDivisibleBy(Number divisibleBy);

	/**
	 * 5.25. disallow
	 * 
	 * 
	 * This attribute takes the same values as the "type" attribute, however if
	 * the instance matches the type or if this value is an array and the
	 * instance matches any type or schema in the array, then this instance is
	 * not valid.
	 * 
	 * @return
	 */
	public TYPE getDisallow();

	/**
	 * 5.25. disallow
	 * 
	 * 
	 * This attribute takes the same values as the "type" attribute, however if
	 * the instance matches the type or if this value is an array and the
	 * instance matches any type or schema in the array, then this instance is
	 * not valid.
	 * 
	 * @param disallow
	 */
	public void setDisallow(TYPE disallow) ;

	/**
	 * 5.26. extends
	 * 
	 * 
	 * The value of this property MUST be another schema which will provide a
	 * base schema which the current schema will inherit from. The inheritance
	 * rules are such that any instance that is valid according to the current
	 * schema MUST be valid according to the referenced schema. This MAY also be
	 * an array, in which case, the instance MUST be valid for all the schemas
	 * in the array. A schema that extends another schema MAY define additional
	 * attributes, constrain existing attributes, or add other constraints.
	 * 
	 * Conceptually, the behavior of extends can be seen as validating an
	 * instance against all constraints in the extending schema as well as the
	 * extended schema(s). More optimized implementations that merge schemas are
	 * possible, but are not required. An example of using "extends":
	 * 
	 * { "description":"An adult", "properties":{"age":{"minimum": 21}},
	 * "extends":"person" }
	 * 
	 * @return
	 */
	public ObjectSchema getExtends();
	
	/**
	 * 5.26. extends
	 * 
	 * 
	 * The value of this property MUST be another schema which will provide a
	 * base schema which the current schema will inherit from. The inheritance
	 * rules are such that any instance that is valid according to the current
	 * schema MUST be valid according to the referenced schema. This MAY also be
	 * an array, in which case, the instance MUST be valid for all the schemas
	 * in the array. A schema that extends another schema MAY define additional
	 * attributes, constrain existing attributes, or add other constraints.
	 * 
	 * Conceptually, the behavior of extends can be seen as validating an
	 * instance against all constraints in the extending schema as well as the
	 * extended schema(s). More optimized implementations that merge schemas are
	 * possible, but are not required. An example of using "extends":
	 * 
	 * { "description":"An adult", "properties":{"age":{"minimum": 21}},
	 * "extends":"person" }
	 * 
	 * @param _extends
	 */
	public void setExtends(ObjectSchema _extends);
	
	public void setImplements(ObjectSchema[] impSchemas);
	
	public ObjectSchema[] getImplements();

	/**
	 * 5.27. id
	 * 
	 * 
	 * This attribute defines the current URI of this schema (this attribute is
	 * effectively a "self" link). This URI MAY be relative or absolute. If the
	 * URI is relative it is resolved against the current URI of the parent
	 * schema it is contained in. If this schema is not contained in any parent
	 * schema, the current URI of the parent schema is held to be the URI under
	 * which this schema was addressed. If id is missing, the current URI of a
	 * schema is defined to be that of the parent schema. The current URI of the
	 * schema is also used to construct relative references such as for $ref.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 5.27. id
	 * 
	 * 
	 * This attribute defines the current URI of this schema (this attribute is
	 * effectively a "self" link). This URI MAY be relative or absolute. If the
	 * URI is relative it is resolved against the current URI of the parent
	 * schema it is contained in. If this schema is not contained in any parent
	 * schema, the current URI of the parent schema is held to be the URI under
	 * which this schema was addressed. If id is missing, the current URI of a
	 * schema is defined to be that of the parent schema. The current URI of the
	 * schema is also used to construct relative references such as for $ref.
	 * 
	 * @param id
	 */
	public void setId(String id);

	/**
	 * 5.28. $ref
	 * 
	 * 
	 * This attribute defines a URI of a schema that contains the full
	 * representation of this schema. When a validator encounters this
	 * attribute, it SHOULD replace the current schema with the schema
	 * referenced by the value's URI (if known and available) and re- validate
	 * the instance. This URI MAY be relative or absolute, and relative URIs
	 * SHOULD be resolved against the URI of the current schema.
	 * 
	 * @return
	 */
	public String getRef();

	/**
	 * 5.28. $ref
	 * 
	 * 
	 * This attribute defines a URI of a schema that contains the full
	 * representation of this schema. When a validator encounters this
	 * attribute, it SHOULD replace the current schema with the schema
	 * referenced by the value's URI (if known and available) and re- validate
	 * the instance. This URI MAY be relative or absolute, and relative URIs
	 * SHOULD be resolved against the URI of the current schema.
	 * 
	 * @param $ref
	 */
	public void setRef(String $ref);

	/**
	 * 5.29. $schema
	 * 
	 * 
	 * This attribute defines a URI of a JSON Schema that is the schema of the
	 * current schema. When this attribute is defined, a validator SHOULD use
	 * the schema referenced by the value's URI (if known and available) when
	 * resolving Hyper Schema (Section 6) links (Section 6.1).
	 * 
	 * A validator MAY use this attribute's value to determine which version of
	 * JSON Schema the current schema is written in, and provide the appropriate
	 * validation features and behavior. Therefore, it is RECOMMENDED that all
	 * schema authors include this attribute in their schemas to prevent
	 * conflicts with future JSON Schema specification changes.
	 * 
	 * @return
	 */
	public String getSchema();

	/**
	 * 5.29. $schema
	 * 
	 * 
	 * This attribute defines a URI of a JSON Schema that is the schema of the
	 * current schema. When this attribute is defined, a validator SHOULD use
	 * the schema referenced by the value's URI (if known and available) when
	 * resolving Hyper Schema (Section 6) links (Section 6.1).
	 * 
	 * A validator MAY use this attribute's value to determine which version of
	 * JSON Schema the current schema is written in, and provide the appropriate
	 * validation features and behavior. Therefore, it is RECOMMENDED that all
	 * schema authors include this attribute in their schemas to prevent
	 * conflicts with future JSON Schema specification changes.
	 * 
	 * @param $schema
	 */
	public void setSchema(String $schema);
	

	public String toJSONString(JSONObjectAdapter adapter) throws JSONObjectAdapterException;


	/**
	 * An iterator that can be used to inspect all sub-schemas in this schema.
	 * 
	 * @return
	 */
	public Iterator<ObjectSchema> getSubSchemaIterator();

	/**
	 * The fields that make up the final object are from a combination of the
	 * properties of this schema plus the properties of any interface schema
	 * that this implemented by this schema.
	 * 
	 * @return
	 */
	public Map<String, ObjectSchema> getObjectFieldMap();


	/**
	 * Get the package name for this object.
	 * 
	 * @return
	 */
	public String getPackageName();

}
