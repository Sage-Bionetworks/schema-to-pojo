package org.sagebionetworks.schema;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

/**
 * The Schema of a single object. A schema defines an object that can also be composed
 * of objects.
 * 
 * This object is indented to be an implementation of the
 * draft-zyp-json-schema-03.
 * 
 * @see <a
 *      href="http://tools.ietf.org/html/draft-zyp-json-schema-03">http://tools.ietf.org/html/draft-zyp-json-schema-03</a>
 * 
 * @author jmhill
 * 
 */
public class ObjectSchema implements JSONEntity{

	public static final String JSON_TRANSIENT = "transient";
	public static final String JSON_ID = "id";
	public static final String JSON_NAME = "name";
	public static final String JSON_TYPE = "type";
	public static final String JSON_PROPERTIES = "properties";
	public static final String JSON_ADDITIONAL_PROPERTIES = "additionalProperties";
	public static final String JSON_ITEMS = "items";
	public static final String JSON_UNIQUE_ITEMS = "uniqueItems";
	public static final String JSON_ADDITIONAL_ITEMS = "additionalItems";
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
	/*
	 * The name of this object.
	 */
	private String name;

	/*
	 * 5.1. type
	 * 
	 * 
	 * This attribute defines what the primitive type or the schema of the
	 * instance MUST be in order to validate. This attribute can take one of two
	 * forms:
	 */
	private TYPE type;
	/*
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
	 */
	private Map<String, ObjectSchema> properties;
	/*
	 * 5.4. additionalProperties
	 * 
	 * 
	 * This attribute defines a schema for all properties that are not
	 * explicitly defined in an object type definition. If specified, the value
	 * MUST be a schema or a boolean. If false is provided, no additional
	 * properties are allowed beyond the properties defined in the schema. The
	 * default value is an empty schema which allows any value for additional
	 * properties.
	 */
	private Map<String, ObjectSchema> additionalProperties;
	/*
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
	 */
	private ObjectSchema items;
	/*
	 * 5.6. additionalItems
	 * 
	 * 
	 * This provides a definition for additional items in an array instance when
	 * tuple definitions of the items is provided. This can be false to indicate
	 * additional items in the array are not allowed, or it can be a schema that
	 * defines the schema of the additional items.
	 */
	private ObjectSchema additionalItems;

	/*
	 * 5.7. required
	 * 
	 * 
	 * This attribute indicates if the instance must have a value, and not be
	 * undefined. This is false by default, making the instance optional.
	 */
	private Boolean required;
	/*
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
	private String[] dependencies;
	/*
	 * 5.9. minimum
	 * 
	 * 
	 * This attribute defines the minimum value of the instance property when
	 * the type of the instance value is a number
	 */
	private Number minimum;
	/*
	 * 5.10. maximum
	 * 
	 * 
	 * This attribute defines the maximum value of the instance property when
	 * the type of the instance value is a number.
	 */
	private Number maximum;
	/*
	 * 5.11. exclusiveMinimum
	 * 
	 * 
	 * This attribute indicates if the value of the instance (if the instance is
	 * a number) can not equal the number defined by the "minimum" attribute.
	 * This is false by default, meaning the instance value can be greater then
	 * or equal to the minimum value.
	 */
	private Number exclusiveMinimum;
	/*
	 * 5.12. exclusiveMaximum
	 * 
	 * 
	 * This attribute indicates if the value of the instance (if the instance is
	 * a number) can not equal the number defined by the "maximum" attribute.
	 * This is false by default, meaning the instance value can be less then or
	 * equal to the maximum value.
	 */
	private Number exclusiveMaximum;
	/*
	 * 5.13. minItems
	 * 
	 * 
	 * This attribute defines the minimum number of values in an array when the
	 * array is the instance value.
	 */
	private Long minItems;
	/*
	 * 5.14. maxItems
	 * 
	 * 
	 * This attribute defines the maximum number of values in an array when the
	 * array is the instance value.
	 */
	private Long maxItems;
	/*
	 * 5.15. uniqueItems
	 * 
	 * 
	 * This attribute indicates that all items in an array instance MUST be
	 * unique (contains no two identical values).
	 * 
	 * Two instance are consider equal if they are both of the same type and:
	 * 
	 * are null; or
	 */
	private Boolean uniqueItems;
	
	/* 
	 * This is an additional option to indicate that this item is transient
	 * and therefore does not require persistence.
	 */
	private Boolean _transient;
	
	/*
	 * 5.16. pattern
	 * 
	 * 
	 * When the instance value is a string, this provides a regular expression
	 * that a string instance MUST match in order to be valid. Regular
	 * expressions SHOULD follow the regular expression specification from ECMA
	 * 262/Perl 5
	 */
	private String pattern;

	/*
	 * 5.17. minLength
	 * 
	 * 
	 * When the instance value is a string, this defines the minimum length of
	 * the string.
	 */
	private Integer minLength;
	/*
	 * 5.18. maxLength
	 * 
	 * 
	 * When the instance value is a string, this defines the maximum length of
	 * the string.
	 */
	private Integer maxLength;

	/*
	 * 5.19. enum
	 * 
	 * 
	 * This provides an enumeration of all possible values that are valid for
	 * the instance property. This MUST be an array, and each item in the array
	 * represents a possible value for the instance value. If this attribute is
	 * defined, the instance value MUST be one of the values in the array in
	 * order for the schema to be valid. Comparison of enum values uses the same
	 * algorithm as defined in "uniqueItems" (Section 5.15).
	 */
	private String[] _enum;
	/*
	 * 5.20. default
	 * 
	 * 
	 * This attribute defines the default value of the instance when the
	 * instance is undefined.
	 */
	private Object _default;

	/*
	 * 5.21. title
	 * 
	 * 
	 * This attribute is a string that provides a short description of the
	 * instance property.
	 */
	private String title;

	/*
	 * 5.22. description
	 * 
	 * 
	 * This attribute is a string that provides a full description of the of
	 * purpose the instance property.
	 */
	private String description;
	/*
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
	 */
	private FORMAT format;

	/*
	 * 5.24. divisibleBy
	 * 
	 * 
	 * This attribute defines what value the number instance must be divisible
	 * by with no remainder (the result of the division must be an integer.) The
	 * value of this attribute SHOULD NOT be 0.
	 */
	private Number divisibleBy;

	/*
	 * 5.25. disallow
	 * 
	 * 
	 * This attribute takes the same values as the "type" attribute, however if
	 * the instance matches the type or if this value is an array and the
	 * instance matches any type or schema in the array, then this instance is
	 * not valid.
	 */
	private TYPE disallow;

	/*
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
	 */
	private ObjectSchema _extends;
	
	/*
	 * This is a custom field that is not part of the spec.  It is used to 
	 * indicate interfaces that should be implemented.
	 */
	private ObjectSchema[] _implements;

	/*
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
	 */
	private String id;
	/*
	 * 5.28. $ref
	 * 
	 * 
	 * This attribute defines a URI of a schema that contains the full
	 * representation of this schema. When a validator encounters this
	 * attribute, it SHOULD replace the current schema with the schema
	 * referenced by the value's URI (if known and available) and re- validate
	 * the instance. This URI MAY be relative or absolute, and relative URIs
	 * SHOULD be resolved against the URI of the current schema.
	 */
	private String ref;

	/*
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
	 */
	private String schema;
	

	/*
	 * 6.4. contentEncoding
	 * 
	 * If the instance property value is a string, this attribute defines
	 * that the string SHOULD be interpreted as binary data and decoded
	 * using the encoding named by this schema property.  RFC 2045, Sec 6.1
	 * [RFC2045] lists the possible values for this property.
	 */
	private ENCODING contentEncoding;

	/**
	 * New Object of a given type.
	 * @param type
	 */
	public ObjectSchema(TYPE type) {
		this.type = type;
	}

	/**
	 * New empty object
	 */
	public ObjectSchema() {
	}
	
	/**
	 * Create a new ObjectSchema with using an ID.
	 * @param id
	 * @return
	 */
	public static ObjectSchema createNewWithId(String id){
		if(id == null) throw new IllegalArgumentException("Id cannot be null");
		ObjectSchema schema = new ObjectSchema();
		schema.setId(id);
		return schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	/**
	 * 6.4. contentEncoding
	 * 
	 * If the instance property value is a string, this attribute defines
	 * that the string SHOULD be interpreted as binary data and decoded
	 * using the encoding named by this schema property.  RFC 2045, Sec 6.1
	 * [RFC2045] lists the possible values for this property.
	 * @param encoding
	 */
	public void setContentEncoding(ENCODING encoding){
		this.contentEncoding = encoding;
	}
	
	/**
	 * 6.4. contentEncoding
	 * 
	 * If the instance property value is a string, this attribute defines
	 * that the string SHOULD be interpreted as binary data and decoded
	 * using the encoding named by this schema property.  RFC 2045, Sec 6.1
	 * [RFC2045] lists the possible values for this property.
	 * @return
	 */
	public ENCODING getContentEncoding(){
		return this.contentEncoding;
	}
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
	public TYPE getType() {
		return type;
	}

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
	public void setType(TYPE type) {
		this.type = type;
	}

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
	public Map<String, ObjectSchema> getProperties() {
		return properties;
	}

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
	public void putProperty(String key, ObjectSchema property) {
		if(properties == null){
			properties = new HashMap<String, ObjectSchema>();
		}
		properties.put(key, property);
	}

	

	public void setProperties(Map<String, ObjectSchema> properties) {
		this.properties = properties;
	}

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
	public Map<String, ObjectSchema> getAdditionalProperties() {
		return additionalProperties;
	}

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
	public void putAdditionalProperty(String key, ObjectSchema property) {
		if(additionalProperties == null){
			additionalProperties = new HashMap<String, ObjectSchema>();
		}
		additionalProperties.put(key, property);
	}
	

	public void setAdditionalProperties(Map<String, ObjectSchema> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

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
	public ObjectSchema getItems() {
		return items;
	}

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
	public void setItems(ObjectSchema items) {
		this.items = items;
	}

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
	public ObjectSchema getAdditionalItems() {
		return additionalItems;
	}

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
	public void setAdditionalItems(ObjectSchema additionalItems) {
		this.additionalItems = additionalItems;
	}

	/**
	 * 5.7. required
	 * 
	 * 
	 * This attribute indicates if the instance must have a value, and not be
	 * undefined. This is false by default, making the instance optional.
	 */
	public boolean isRequired() {
		if(required == null) return false;
		return required;
	}

	/**
	 * 5.7. required
	 * 
	 * 
	 * This attribute indicates if the instance must have a value, and not be
	 * undefined. This is false by default, making the instance optional.
	 * 
	 * @param required
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}

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
	public String[] getDependencies() {
		return dependencies;
	}

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
	public void setDependencies(String[] dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * 5.9. minimum
	 * 
	 * 
	 * This attribute defines the minimum value of the instance property when
	 * the type of the instance value is a number
	 * 
	 * @return
	 */
	public Number getMinimum() {
		return minimum;
	}

	/**
	 * 5.9. minimum
	 * 
	 * 
	 * This attribute defines the minimum value of the instance property when
	 * the type of the instance value is a number
	 * 
	 * @param minimum
	 */
	public void setMinimum(Number minimum) {
		this.minimum = minimum;
	}

	/**
	 * 5.10. maximum
	 * 
	 * 
	 * This attribute defines the maximum value of the instance property when
	 * the type of the instance value is a number.
	 * 
	 * @return
	 */
	public Number getMaximum() {
		return maximum;
	}

	/**
	 * 5.10. maximum
	 * 
	 * 
	 * This attribute defines the maximum value of the instance property when
	 * the type of the instance value is a number.
	 * 
	 * @param maximum
	 */
	public void setMaximum(Number maximum) {
		this.maximum = maximum;
	}

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
	public Number getExclusiveMinimum() {
		return exclusiveMinimum;
	}

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
	public void setExclusiveMinimum(Number exclusiveMinimum) {
		this.exclusiveMinimum = exclusiveMinimum;
	}

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
	public Number getExclusiveMaximum() {
		return exclusiveMaximum;
	}

	/**
	 * 5.11. exclusiveMinimum
	 * 
	 * 
	 * This attribute indicates if the value of the instance (if the instance is
	 * a number) can not equal the number defined by the "minimum" attribute.
	 * This is false by default, meaning the instance value can be greater then
	 * or equal to the minimum value.
	 */
	public void setExclusiveMaximum(Number exclusiveMaximum) {
		this.exclusiveMaximum = exclusiveMaximum;
	}

	/**
	 * 5.13. minItems
	 * 
	 * 
	 * This attribute defines the minimum number of values in an array when the
	 * array is the instance value.
	 * 
	 * @return
	 */
	public Long getMinItems() {
		return minItems;
	}

	/**
	 * 5.13. minItems
	 * 
	 * 
	 * This attribute defines the minimum number of values in an array when the
	 * array is the instance value.
	 * 
	 * @param minItems
	 */
	public void setMinItems(Long minItems) {
		this.minItems = minItems;
	}

	/**
	 * 5.14. maxItems
	 * 
	 * 
	 * This attribute defines the maximum number of values in an array when the
	 * array is the instance value.
	 * 
	 * @return
	 */
	public Long getMaxItems() {
		return maxItems;
	}

	/**
	 * 5.14. maxItems
	 * 
	 * 
	 * This attribute defines the maximum number of values in an array when the
	 * array is the instance value.
	 */
	public void setMaxItems(Long maxItems) {
		this.maxItems = maxItems;
	}

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
	public boolean getUniqueItems() {
		if(uniqueItems == null)return false;
		return uniqueItems;
	}
	

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
	public void setUniqueItems(Boolean uniqueItems) {
		this.uniqueItems = uniqueItems;
	}
	
	/**
	 * Transient data is not persistent.
	 * The default is false.
	 * @return
	 */
	public boolean isTransient(){
		if(_transient == null) return false;
		return _transient;
	}
	
	/**
	 * Transient data is not persistent.
	 * @param trans
	 */
	public void setTransient(Boolean trans){
		this._transient = trans;
	}

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
	public String getPattern() {
		return pattern;
	}

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
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * 5.17. minLength
	 * 
	 * 
	 * When the instance value is a string, this defines the minimum length of
	 * the string.
	 * 
	 * @return
	 */
	public Integer getMinLength() {
		return minLength;
	}

	/**
	 * 5.17. minLength
	 * 
	 * 
	 * When the instance value is a string, this defines the minimum length of
	 * the string.
	 * 
	 * @param minLength
	 */
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	/**
	 * 5.18. maxLength
	 * 
	 * 
	 * When the instance value is a string, this defines the maximum length of
	 * the string.
	 * 
	 * @return
	 */
	public Integer getMaxLength() {
		return maxLength;
	}

	/**
	 * 5.18. maxLength
	 * 
	 * 
	 * When the instance value is a string, this defines the maximum length of
	 * the string.
	 * 
	 * @param maxLength
	 */
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

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
	public String[] getEnum() {
		return _enum;
	}

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
	public void setEnum(String[] _enum) {
		this._enum = _enum;
	}

	/**
	 * 5.20. default
	 * 
	 * 
	 * This attribute defines the default value of the instance when the
	 * instance is undefined.
	 * 
	 * @return
	 */
	public Object getDefault() {
		return _default;
	}

	/**
	 * 5.20. default
	 * 
	 * 
	 * This attribute defines the default value of the instance when the
	 * instance is undefined.
	 * 
	 * @param _default
	 */
	public void setDefault(Object _default) {
		this._default = _default;
	}

	/**
	 * 5.21. title
	 * 
	 * 
	 * This attribute is a string that provides a short description of the
	 * instance property.
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 5.21. title
	 * 
	 * 
	 * This attribute is a string that provides a short description of the
	 * instance property.
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 5.22. description
	 * 
	 * 
	 * This attribute is a string that provides a full description of the of
	 * purpose the instance property.
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 5.22. description
	 * 
	 * 
	 * This attribute is a string that provides a full description of the of
	 * purpose the instance property.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

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
	public FORMAT getFormat() {
		return format;
	}

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
	public void setFormat(FORMAT format) {
		this.format = format;
	}

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
	public Number getDivisibleBy() {
		return divisibleBy;
	}

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
	public void setDivisibleBy(Number divisibleBy) {
		this.divisibleBy = divisibleBy;
	}

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
	public TYPE getDisallow() {
		return disallow;
	}

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
	public void setDisallow(TYPE disallow) {
		this.disallow = disallow;
	}

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
	public ObjectSchema getExtends() {
		return _extends;
	}

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
	public void setExtends(ObjectSchema _extends) {
		this._extends = _extends;
	}
	
	public void setImplements(ObjectSchema[] impSchemas){
		this._implements = impSchemas;
	}
	
	public ObjectSchema[] getImplements(){
		return _implements;
	}

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
	public String getId() {
		return id;
	}

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
	public void setId(String id) {
		this.id = id;
	}

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
	public String getRef() {
		return ref;
	}

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
	public void setRef(String $ref) {
		this.ref = $ref;
	}

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
	public String getSchema() {
		return schema;
	}

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
	public void setSchema(String $schema) {
		this.schema = $schema;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_default == null) ? 0 : _default.hashCode());
		result = prime * result + Arrays.hashCode(_enum);
		result = prime * result
				+ ((_extends == null) ? 0 : _extends.hashCode());
		result = prime * result + Arrays.hashCode(_implements);
		result = prime * result
				+ ((_transient == null) ? 0 : _transient.hashCode());
		result = prime * result
				+ ((additionalItems == null) ? 0 : additionalItems.hashCode());
		result = prime
				* result
				+ ((additionalProperties == null) ? 0 : additionalProperties
						.hashCode());
		result = prime * result
				+ ((contentEncoding == null) ? 0 : contentEncoding.hashCode());
		result = prime * result + Arrays.hashCode(dependencies);
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((disallow == null) ? 0 : disallow.hashCode());
		result = prime * result
				+ ((divisibleBy == null) ? 0 : divisibleBy.hashCode());
		result = prime
				* result
				+ ((exclusiveMaximum == null) ? 0 : exclusiveMaximum.hashCode());
		result = prime
				* result
				+ ((exclusiveMinimum == null) ? 0 : exclusiveMinimum.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result
				+ ((maxItems == null) ? 0 : maxItems.hashCode());
		result = prime * result
				+ ((maxLength == null) ? 0 : maxLength.hashCode());
		result = prime * result + ((maximum == null) ? 0 : maximum.hashCode());
		result = prime * result
				+ ((minItems == null) ? 0 : minItems.hashCode());
		result = prime * result
				+ ((minLength == null) ? 0 : minLength.hashCode());
		result = prime * result + ((minimum == null) ? 0 : minimum.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		result = prime * result
				+ ((required == null) ? 0 : required.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((uniqueItems == null) ? 0 : uniqueItems.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectSchema other = (ObjectSchema) obj;
		if (_default == null) {
			if (other._default != null)
				return false;
		} else if (!_default.equals(other._default))
			return false;
		if (!Arrays.equals(_enum, other._enum))
			return false;
		if (_extends == null) {
			if (other._extends != null)
				return false;
		} else if (!_extends.equals(other._extends))
			return false;
		if (!Arrays.equals(_implements, other._implements))
			return false;
		if (_transient == null) {
			if (other._transient != null)
				return false;
		} else if (!_transient.equals(other._transient))
			return false;
		if (additionalItems == null) {
			if (other.additionalItems != null)
				return false;
		} else if (!additionalItems.equals(other.additionalItems))
			return false;
		if (additionalProperties == null) {
			if (other.additionalProperties != null)
				return false;
		} else if (!additionalProperties.equals(other.additionalProperties))
			return false;
		if (contentEncoding != other.contentEncoding)
			return false;
		if (!Arrays.equals(dependencies, other.dependencies))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (disallow != other.disallow)
			return false;
		if (divisibleBy == null) {
			if (other.divisibleBy != null)
				return false;
		} else if (!divisibleBy.equals(other.divisibleBy))
			return false;
		if (exclusiveMaximum == null) {
			if (other.exclusiveMaximum != null)
				return false;
		} else if (!exclusiveMaximum.equals(other.exclusiveMaximum))
			return false;
		if (exclusiveMinimum == null) {
			if (other.exclusiveMinimum != null)
				return false;
		} else if (!exclusiveMinimum.equals(other.exclusiveMinimum))
			return false;
		if (format != other.format)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (maxItems == null) {
			if (other.maxItems != null)
				return false;
		} else if (!maxItems.equals(other.maxItems))
			return false;
		if (maxLength == null) {
			if (other.maxLength != null)
				return false;
		} else if (!maxLength.equals(other.maxLength))
			return false;
		if (maximum == null) {
			if (other.maximum != null)
				return false;
		} else if (!maximum.equals(other.maximum))
			return false;
		if (minItems == null) {
			if (other.minItems != null)
				return false;
		} else if (!minItems.equals(other.minItems))
			return false;
		if (minLength == null) {
			if (other.minLength != null)
				return false;
		} else if (!minLength.equals(other.minLength))
			return false;
		if (minimum == null) {
			if (other.minimum != null)
				return false;
		} else if (!minimum.equals(other.minimum))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		if (required == null) {
			if (other.required != null)
				return false;
		} else if (!required.equals(other.required))
			return false;
		if (schema == null) {
			if (other.schema != null)
				return false;
		} else if (!schema.equals(other.schema))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		if (uniqueItems == null) {
			if (other.uniqueItems != null)
				return false;
		} else if (!uniqueItems.equals(other.uniqueItems))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ObjectSchema [name=" + name + ", type=" + type
				+ ", properties=" + properties + ", additionalProperties="
				+ additionalProperties + ", items=" + items
				+ ", additionalItems=" + additionalItems + ", required="
				+ required + ", dependencies=" + Arrays.toString(dependencies)
				+ ", minimum=" + minimum + ", maximum=" + maximum
				+ ", exclusiveMinimum=" + exclusiveMinimum
				+ ", exclusiveMaximum=" + exclusiveMaximum + ", minItems="
				+ minItems + ", maxItems=" + maxItems + ", uniqueItems="
				+ uniqueItems + ", _transient=" + _transient + ", pattern="
				+ pattern + ", minLength=" + minLength + ", maxLength="
				+ maxLength + ", _enum=" + Arrays.toString(_enum)
				+ ", _default=" + _default + ", title=" + title
				+ ", description=" + description + ", format=" + format
				+ ", divisibleBy=" + divisibleBy + ", disallow=" + disallow
				+ ", _extends=" + _extends + ", _implements="
				+ Arrays.toString(_implements) + ", id=" + id + ", ref=" + ref
				+ ", schema=" + schema + ", contentEncoding=" + contentEncoding
				+ "]";
	}
	
	public String toJSONString(JSONObjectAdapter adapter) throws JSONObjectAdapterException{
		// Put the data in the adapter and let it write the string.
		return this.writeToJSONObject(adapter).toJSONString();
	}
	
	
	/**
	 * Create a new model using the passed adapter.
	 * @param adapter
	 * @throws JSONObjectAdapterException 
	 */
	public ObjectSchema(JSONObjectAdapter adapter) throws JSONObjectAdapterException{
		initializeFromJSONObject(adapter);
	}
	
	/**
	 * Extract a number based on the type.
	 * @param type
	 * @param adapter
	 * @param key
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	public static Number getNumberBasedOnType(TYPE type, JSONObjectAdapter adapter, String key) throws JSONObjectAdapterException{
		if(type == null) throw new IllegalArgumentException("TYPE cannot be null");
		if(adapter == null) throw new IllegalArgumentException("JSONObjectAdapter cannot be null");
		if(key == null) throw new IllegalArgumentException("Key cannot be null");
		if(TYPE.INTEGER == type){
			return  adapter.getLong(key);
		}else if(TYPE.NUMBER == type){
			return  adapter.getDouble(key);
		}else{
			throw new IllegalArgumentException("Type: "+type+" is not a number type");
		}
	}
	
	
	/**
	 * Create a JSONObjectAdapter that contains all information about this object.
	 * 
	 * @param in
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	@Override
	public JSONObjectAdapter writeToJSONObject(JSONObjectAdapter in) throws JSONObjectAdapterException{
		JSONObjectAdapter copy = in.createNew();
		if(this.name != null){
			copy.put(JSON_NAME, this.name);
		}
		if(this.type != null){
			copy.put(JSON_TYPE, this.type.getJSONValue());
		}
		if(properties != null){
			JSONObjectAdapter props = createJSONObjectAdapter(this.properties, in);
			copy.put(JSON_PROPERTIES, props);	
		}
		if(additionalProperties != null){
			JSONObjectAdapter props = createJSONObjectAdapter(this.additionalProperties, in);
			copy.put(JSON_ADDITIONAL_PROPERTIES, props);		
		}
		if(this.items != null){
			copy.put(JSON_ITEMS, this.items.writeToJSONObject(in));
		}
		if(this.uniqueItems != null){
			copy.put(JSON_UNIQUE_ITEMS, this.uniqueItems.booleanValue());
		}
		if(this.additionalItems != null){
			copy.put(JSON_ADDITIONAL_ITEMS, this.additionalItems.writeToJSONObject(in));
		}
		if(this.required != null){
			copy.put(JSON_REQUIRED, this.required.booleanValue());
		}
		if(dependencies != null){
			JSONArrayAdapter array = in.createNewArray();
			for(int i=0; i<dependencies.length; i++){
				array.put(i, dependencies[i]);
			}
		}
		if(_implements != null){
			JSONArrayAdapter array = in.createNewArray();
			copy.put(JSON_IMPLEMENTS, array);
			for(int i=0; i<_implements.length; i++){
				array.put(i, _implements[i].writeToJSONObject(in));
			}
		}
		if(this.minimum != null){
			putBasedOnType(copy, JSON_MINIMUM, this.minimum, type);
		}
		if(this.maximum != null){
			putBasedOnType(copy, JSON_MAXIMUM, this.maximum, type);
		}
		if(this.description != null){
			copy.put(JSON_DESCRIPTION, this.description);
		}
		if(this.id != null){
			copy.put(JSON_ID, this.id.toString());
		}
		if(this.ref != null){
			copy.put(JSON_$REF, this.ref.toString());
		}
		if(this._extends != null){
			copy.put(JSON_EXTENDS, this._extends.writeToJSONObject(in));
		}
		if(this.format != null){
			copy.put(JSON_FORMAT, this.format.getJSONValue());
		}
		if(this._enum != null){
			JSONArrayAdapter array = in.createNewArray();
			copy.put(JSON_ENUM, array);
			for(int i=0; i<_enum.length; i++){
				array.put(i, _enum[i]);
			}
		}
		if(this.contentEncoding != null){
			copy.put(JSON_CONTENT_ENCODING, this.contentEncoding.getJsonValue());
		}
		if(this._transient != null){
			copy.put(JSON_TRANSIENT, this._transient);
		}
		if(this._default != null){
			setDefaultType(this.getDefault(), copy);
		}
		return copy;
	}
	
	
	/**
	 * How we add values to the adapter depends on the object type for min and max.
	 * @param copy
	 * @param key
	 * @param value
	 * @param type
	 * @throws JSONObjectAdapterException
	 */
	private static void putBasedOnType(JSONObjectAdapter copy, String key, Number value, TYPE type) throws JSONObjectAdapterException{
		if(type == null) throw new IllegalArgumentException("Type cannot be null");
		if(TYPE.INTEGER == type){
			copy.put(key, value.longValue());
		}else if(TYPE.NUMBER == type){
			copy.put(key, value.doubleValue());
		}else{
			throw new IllegalArgumentException("Unknown type: "+type+". Only numeric types can have minimum or maximum attributes");
		}
	}
	
	/**
	 * An iterator that can be used to inspect all sub-schemas in this schema.
	 * @return
	 */
	public Iterator<ObjectSchema> getSubSchemaIterator(){
		List<ObjectSchema> list = new LinkedList<ObjectSchema>();
		// Add all of the properties
		if(this.properties != null){
			list.addAll(this.properties.values());
		}
		// Additional properties.
		if(this.additionalProperties != null){
			list.addAll(this.additionalProperties.values());
		}
		// Items
		if(this.items != null){
			list.add(this.items);
		}
		// Additional items
		if(this.additionalItems != null){
			list.add(this.additionalItems);
		}
		if(this._extends != null){
			list.add(this._extends);
		}
		if(this._implements != null){
			for(ObjectSchema imp: this._implements){
				list.add(imp);
			}
		}
		return list.iterator();
	}
	
	/**
	 * The fields that make up the final object are from a combination 
	 * of the properties of this schema plus the properties of any interface
	 * schema that this implemented by this schema.
	 * @return
	 */
	public Map<String, ObjectSchema> getObjectFieldMap(){
		HashMap<String, ObjectSchema> map = new HashMap<String, ObjectSchema>();
		// First add all of the properties from the interfaces.
		recursivelyAddAllInterfaceProperties(map, this);
		// Now add all of the properties from this object.
		// Note: The order we add the properties to the map is deliberate.
		// By replacing any duplicates from the interfaces with the properties of
		// this class, we will get a compile time error is the types do not match
		// types defined in the interface.
		if(this.properties != null){
			map.putAll(this.properties);
		}
		return map;
	}
	
	/**
	 * Recursively AddAllInterfaceProperties
	 * @param map
	 * @param schema
	 */
	public static void recursivelyAddAllInterfaceProperties(Map<String, ObjectSchema> map, ObjectSchema schema){
		if(schema._implements != null){
			for(ObjectSchema implSchema: schema._implements){
				recursivelyAddAllInterfaceProperties(map, implSchema);
				if(implSchema.properties != null){
					map.putAll(implSchema.properties);
				}
			}
		}
	}
	
	/**
	 * Recursively AddAllInterfaceProperties
	 * @param map
	 * @param schema
	 */
	public static void recursivelyAddAllExtendsProperties(Map<String, ObjectSchema> map, ObjectSchema schema) {
		if (schema._extends != null) {
			recursivelyAddAllExtendsProperties(map, schema._extends);
			if (schema._extends.properties != null) {
				map.putAll(schema._extends.properties);
			}
		}
	}
	
	
	
	/**
	 * Build up a JSONObject from a map.
	 * @param map
	 * @param in
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	private static JSONObjectAdapter createJSONObjectAdapter(Map<String, ObjectSchema> map, JSONObjectAdapter in) throws JSONObjectAdapterException{
		JSONObjectAdapter props = in.createNew();
		// Add each property.
		Iterator<String> keyIt = (Iterator<String>) map.keySet().iterator();
		while(keyIt.hasNext()){
			String key = keyIt.next();
			ObjectSchema child = map.get(key);
			props.put(key, child.writeToJSONObject(in));
		}
		return props;
	}
	
	/**
	 * Create a ObjectModel map from an JSONObjectAdapter
	 * @param in
	 * @return
	 * @throws JSONObjectAdapterException
	 */
	private static Map<String, ObjectSchema> createMapFromAdapter(JSONObjectAdapter in) throws JSONObjectAdapterException{
		HashMap<String, ObjectSchema> map = new HashMap<String, ObjectSchema>();
		Iterator it = in.keys();
		while(it.hasNext()){
			String key = (String) it.next();
			JSONObjectAdapter adapter = in.getJSONObject(key);
			map.put(key, new ObjectSchema(adapter));
		}
		return map;
	}

	@Override
	public JSONObjectAdapter initializeFromJSONObject(
			JSONObjectAdapter adapter) throws JSONObjectAdapterException {
		if(adapter.has(JSON_NAME)){
			this.name = adapter.getString(JSON_NAME);
		}
		if(adapter.has(JSON_TYPE)){
			this.type = TYPE.getTypeFromJSONValue(adapter.getString(JSON_TYPE));
		}
		if(adapter.has(JSON_PROPERTIES)){
			JSONObjectAdapter props = adapter.getJSONObject(JSON_PROPERTIES);
			this.properties = createMapFromAdapter(props);
		}
		if(adapter.has(JSON_ADDITIONAL_PROPERTIES)){
			JSONObjectAdapter props = adapter.getJSONObject(JSON_ADDITIONAL_PROPERTIES);
			this.additionalProperties = createMapFromAdapter(props);
		}
		if(adapter.has(JSON_ITEMS)){
			JSONObjectAdapter items = adapter.getJSONObject(JSON_ITEMS);
			this.items = new ObjectSchema(items);
		}
		if(adapter.has(JSON_UNIQUE_ITEMS)){
			this.uniqueItems =  adapter.getBoolean(JSON_UNIQUE_ITEMS);
		}else{
			this.uniqueItems = null;
		}
		if(adapter.has(JSON_ADDITIONAL_ITEMS)){
			JSONObjectAdapter additionalItems = adapter.getJSONObject(JSON_ADDITIONAL_ITEMS);
			this.additionalItems = new ObjectSchema(additionalItems);
		}
		if(adapter.has(JSON_REQUIRED)){
			this.required = adapter.getBoolean(JSON_REQUIRED);
		}
		if(adapter.has(JSON_MINIMUM)){
			this.minimum = getNumberBasedOnType(type, adapter, JSON_MINIMUM);
		}
		if(adapter.has(JSON_MAXIMUM)){
			this.maximum = getNumberBasedOnType(type, adapter, JSON_MAXIMUM);
		}
		if(adapter.has(JSON_DESCRIPTION)){
			this.description = adapter.getString(JSON_DESCRIPTION);
		}
		if(adapter.has(JSON_ID)){
			this.id = adapter.getString(JSON_ID);
		}
		if(adapter.has(JSON_IMPLEMENTS)){
			JSONArrayAdapter array = adapter.getJSONArray(JSON_IMPLEMENTS);
			this._implements = new ObjectSchema[array.length()];
			for(int i=0; i<array.length(); i++){
				this._implements[i] = new ObjectSchema(array.getJSONObject(i));
			}
		}
		if(adapter.has(JSON_$REF)){
			this.ref = adapter.getString(JSON_$REF);
		}
		if(adapter.has(JSON_EXTENDS)){
			JSONObjectAdapter ex = adapter.getJSONObject(JSON_EXTENDS);
			this._extends = new ObjectSchema(ex);
		}
		if(adapter.has(JSON_FORMAT)){
			this.format = FORMAT.getFormatForJSONValue(adapter.getString(JSON_FORMAT));
		}
		if(adapter.has(JSON_ENUM)){
			JSONArrayAdapter array = adapter.getJSONArray(JSON_ENUM);
			this._enum = new String[array.length()];
			for(int i=0; i<array.length(); i++){
				this._enum[i] = array.getString(i);
			}
		}
		if(adapter.has(JSON_CONTENT_ENCODING)){
			this.contentEncoding = ENCODING.getEncodingForJSONValue(adapter.getString(JSON_CONTENT_ENCODING));
		}
		if(adapter.has(JSON_TRANSIENT)){
			this._transient = adapter.getBoolean(JSON_TRANSIENT);
		}
		if (adapter.has(JSON_DEFAULT)){
			setDefaultObject(this, adapter);		
		}
		return adapter;
	}
	
	/**
	 * Get the package name for this object.
	 * @return
	 */
	public String getPackageName(){
		if(name == null)throw new IllegalArgumentException("Cannot get a package name if the name is null");
		if(id == null) throw new IllegalArgumentException("Cannot get a package name if the id is null");
		String sub = id.substring(0, id.length()-name.length());
		if(sub.endsWith(".")){
			return sub.substring(0, sub.length()-1);
		}else{
			return sub;
		}
	}

	@Override
	public String getJSONSchema() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Set's a adapter's default
	 * @throws JSONObjectAdapterException 
	 */
	public static JSONObjectAdapter setDefaultType(Object defaultObject, JSONObjectAdapter adapter) throws JSONObjectAdapterException{
		//determine which type defaultObject is and set adapter accordingly
		if (defaultObject.getClass().getName().equals(String.class.getName())){
			String defaultString = (String)defaultObject;
			adapter.put(JSON_DEFAULT, defaultString);
		}
		else if (defaultObject.getClass().getName().equals(Double.class.getName())){
			double defaultDouble = (Double)defaultObject;
			adapter.put(JSON_DEFAULT, defaultDouble);
		}
		else if (defaultObject.getClass().getName().equals(Long.class.getName())){
			long defaultLong = (Long)defaultObject;
			adapter.put(JSON_DEFAULT, defaultLong);
		}
		else if (defaultObject.getClass().getName().equals(Boolean.class.getName())){
			boolean defaultBool = (Boolean)defaultObject;
			adapter.put(JSON_DEFAULT, defaultBool);
		}
		else if (defaultObject instanceof JSONObjectAdapter){
			JSONObjectAdapter defaultAdapter = (JSONObjectAdapter)defaultObject;
			adapter.put(JSON_DEFAULT, defaultAdapter);
		}
		else if (defaultObject instanceof JSONArrayAdapter){
			JSONArrayAdapter defaultArray = (JSONArrayAdapter)defaultObject;
			adapter.put(JSON_DEFAULT, defaultArray);
		}
		else {
			throw new RuntimeException("Object " + defaultObject + 
					" can't be added to adapter " + adapter +
					" default as it is not of a supported type");
		}
		return adapter;
	}
	
	/**
	 * Sets a ObjectSchema's default
	 * @throws JSONObjectAdapterException 
	 */
	public static void setDefaultObject(ObjectSchema schema, JSONObjectAdapter adapter) throws JSONObjectAdapterException{
		if (schema == null){
			throw new RuntimeException("can not setDefaultObject when schema paramter is null");
		}
		if (adapter == null){
			throw new RuntimeException("can not setDefaultObject when adapter is null");
		}
		String type = adapter.getString(JSON_TYPE);
		if (type == null){
			throw new RuntimeException("can't add defaultObject to a schema when adapter "
					+ adapter + " has a null type");
		}
		if (type.equals("string")){
			Object defaultString = adapter.getString(JSON_DEFAULT);
			schema.setDefault(defaultString);
		}
		else if (type.equals("number")){
			Object defaultNumber = adapter.getDouble(JSON_DEFAULT);
			schema.setDefault(defaultNumber);
		}
		else if (type.equals("integer")){
			Object defaultInteger = adapter.getLong(JSON_DEFAULT);
			schema.setDefault(defaultInteger);
		}
		else if (type.equals("boolean")){
			Object defaultBoolean = adapter.getBoolean(JSON_DEFAULT);
			schema.setDefault(defaultBoolean);
		}
		else if (type.equals("object")){
			Object defaultJSONObject = adapter.getJSONObject(JSON_DEFAULT);
			schema.setDefault(defaultJSONObject);
		}
		else if (type.equals("array")){
			Object defaultArray = adapter.getJSONArray(JSON_DEFAULT);
			schema.setDefault(defaultArray);
		}
		else {
			throw new RuntimeException("can't add default object to " + schema +
					"because adapter " + "has a invalid type of " + type);
		}
	}
}
