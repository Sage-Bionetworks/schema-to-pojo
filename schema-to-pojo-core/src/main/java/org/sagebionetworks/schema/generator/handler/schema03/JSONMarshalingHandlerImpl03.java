package org.sagebionetworks.schema.generator.handler.schema03;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.sagebionetworks.schema.FORMAT;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.ObjectValidator;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.generator.handler.JSONMarshalingHandler;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JCommentPart;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JWhileLoop;

public class JSONMarshalingHandlerImpl03 implements JSONMarshalingHandler{

	@Override
	public void addJSONMarshaling(ObjectSchema classSchema,	JDefinedClass classType, JDefinedClass createRegister) {
		// There is nothing to do for interfaces.
		if(TYPE.INTERFACE == classSchema.getType()){
			throw new IllegalArgumentException("Cannot add marshaling to an interface");
		}
		// Make sure this class implements JSONEntity
		classType._implements(JSONEntity.class);
		createGetJSONSchemaMethod(classSchema, classType);
	
		// Create the init method
		JMethod initMethod = createMethodInitializeFromJSONObject(classSchema, classType, createRegister);
		// setup a constructor.
		createConstructor(classSchema, classType, initMethod);
		
		// Add the second method.
		createWriteToJSONObject(classSchema, classType);
		
	}
	
	/**
	 * Create the getJSONSchema method.
	 * @param classSchema
	 * @param classType
	 */
	public JMethod createGetJSONSchemaMethod(ObjectSchema classSchema,JDefinedClass classType){
		// Look up the field for this property
		JFieldVar field = classType.fields().get(JSONEntity.EFFECTIVE_SCHEMA);
		if (field == null)
			throw new IllegalArgumentException("Failed to find the JFieldVar for property: '"+ JSONEntity.EFFECTIVE_SCHEMA + "' on class: " + classType.name());
		// Create the get method
		JMethod method = classType.method(JMod.PUBLIC, classType.owner()._ref(String.class), "getJSONSchema");
		method.body()._return(field);
		return method;
	}



	/**
	 * 
	 * @param classSchema
	 * @param classType
	 * @return
	 */
	protected JMethod createConstructor(ObjectSchema classSchema, JDefinedClass classType, JMethod initializeMethod) {
		// We always need a no-args constructor
		classType.constructor(JMod.PUBLIC);
		// Now the constructor that takes a JSONObjectAdapter.
		JMethod constructor  = classType.constructor(JMod.PUBLIC);
		constructor._throws(JSONObjectAdapterException.class);
		// add the parameter
		JVar param = constructor.param(classType.owner()._ref(JSONObjectAdapter.class), "adapter");
		JDocComment docs = constructor.javadoc();
		docs.add("Marshal a new "+classType.name()+" from JSON using the provided implementation of "+JSONObjectAdapter.class.getName());
		JCommentPart part = docs.addParam(param);
		part.add("Data will be read from this adapter to populate this object.");
		docs.addThrows(JSONObjectAdapterException.class);
		// Create the constructor body
        JBlock body = constructor.body();
		// First add a super call
        if(classSchema.getExtends() != null){
        	JInvocation invocation = JExpr.invoke("super").arg(param);
        	body.add(invocation);
        }else{
        	JInvocation invocation = JExpr.invoke("super");
        	body.add(invocation);
        }
        // Make sure the parameter is not null
        body._if(param.eq(JExpr._null()))
        	._then()._throw(createIllegalArgumentException(classType, JSONObjectAdapter.class.getName()+" cannot be null"));
        
        // Now invoke the init method
        body.invoke(initializeMethod).arg(param);
		return constructor;
	}
	
	/**
	 * Create the InitializeFromJSONObject method for the JSONEntity interface.
	 * 
	 * @param classSchema
	 * @param classType
	 * @param createRegister 
	 * @return
	 */
	protected JMethod createMethodInitializeFromJSONObject(ObjectSchema classSchema, JDefinedClass classType, JDefinedClass createRegister) {
		// Now the method that takes a JSONObjectAdapter.
		JMethod method = createBaseMethod(classSchema, classType, "initializeFromJSONObject");
		JVar param = method.params().get(0);
		JBlock body = method.body();
		
		// First validate against the schema
		body.staticInvoke(classType.owner().ref(ObjectValidator.class), "validateEntity").arg(classType.staticRef(JSONEntity.EFFECTIVE_SCHEMA)).arg(param).arg(classType.staticRef("class"));
        
		// Now process each property
		Map<String, ObjectSchema> fieldMap = classSchema.getObjectFieldMap();
		Iterator<String> keyIt = fieldMap.keySet().iterator();
		while (keyIt.hasNext()) {
			String propName = keyIt.next();
			ObjectSchema propSchema = fieldMap.get(propName);
			// Look up the field for this property
			JFieldVar field = classType.fields().get(propName);
			if (field == null)
				throw new IllegalArgumentException(
						"Failed to find the JFieldVar for property: '"
								+ propName + "' on class: " + classType.name());
			// Now process this field
			if (propSchema.getType() == null)
				throw new IllegalArgumentException("Property: '" + propSchema
						+ "' has a null TYPE on class: " + classType.name());
			TYPE type = propSchema.getType();
			if (type.isPrimitive()) {
				body.assign(field,
						param.invoke(type.getMethodName()).arg(propName));
				continue;
			}
			// Add an if
			
			JConditional hasCondition = body._if(param.invoke("isNull").arg(
					propName).not());
			JBlock thenBlock = hasCondition._then();
			// For strings and primitives we can just assign the value right
			// from the adapter.
			if (TYPE.STRING == type) {
				// The format determines how JSON strings are read.
				JExpression rhs = null;
				if(propSchema.getEnum() != null){
					// Assign an enum
					JTryBlock tryBlock = thenBlock._try();
					rhs = assignJSONStringToEnumProperty(param, propName, field);
					tryBlock.body().assign(field, rhs);
					JCatchBlock catchBlock = tryBlock._catch(classType.owner().ref(IllegalArgumentException.class));
					// Create the throw string
					StringBuilder builder = new StringBuilder();
					builder.append("'").append(propName).append("' must be one of the following: ");
					for(int i=0; i<propSchema.getEnum().length; i++){
						if(i!=0){
							builder.append(", ");
						}
						builder.append("'").append(propSchema.getEnum()[i]).append("'");
					}
					builder.append(".");
					catchBlock.body()._throw(JExpr._new(classType.owner().ref(IllegalArgumentException.class)).arg(builder.toString()));
				}else{
					// This is just a string.
					rhs = assignJSONStringToProperty(classType.owner(),
							param, propName, propSchema);
					thenBlock.assign(field, rhs);
				}

			}else if (TYPE.BOOLEAN == type || TYPE.NUMBER == type || TYPE.INTEGER == type) {
				JClass typeClass = (JClass) field.type();
				// Basic assign
				thenBlock.assign(field, JExpr._new(typeClass).arg(param.invoke(type.getMethodName()).arg(propName)));
			} else if (TYPE.ARRAY == type) {
				// Determine the type of the field
				JClass typeClass = (JClass) field.type();
				if (typeClass.getTypeParameters().size() != 1)
					throw new IllegalArgumentException(
							"Cannot determine the type of an array: "
									+ typeClass.fullName());
				JClass arrayTypeClass = typeClass.getTypeParameters().get(0);
				ObjectSchema arrayTypeSchema = propSchema.getItems();
				if (arrayTypeSchema == null)
					throw new IllegalArgumentException(
							"A property type is ARRAY but the getItems() returned null");
				TYPE arrayType = arrayTypeSchema.getType();
				if (arrayType == null)
					throw new IllegalArgumentException(
							"TYPE cannot be null for an ObjectSchema");
				// Type arrayType =
				if (!propSchema.getUniqueItems()) {
					// Create a list
					thenBlock.assign(
							field,
							JExpr._new(classType.owner().ref(ArrayList.class)
									.narrow(arrayTypeClass)));
				} else {
					// Create a set
					thenBlock.assign(
							field,
							JExpr._new(classType.owner().ref(HashSet.class)
									.narrow(arrayTypeClass)));
				}
				// Create a local array
				JVar jsonArray = thenBlock
						.decl(classType.owner().ref(JSONArrayAdapter.class),
								"jsonArray",
								param.invoke("getJSONArray").arg(propName));
				JForLoop loop = thenBlock._for();
				JVar i = loop.init(classType.owner().INT, "i", JExpr.lit(0));
				loop.test(i.lt(jsonArray.invoke("length")));
				loop.update(i.incr());
				JBlock loopBody = loop.body();
				loopBody.add(field.invoke("add").arg(
						createExpresssionToGetFromArray(param, jsonArray, arrayTypeSchema,
								arrayTypeClass, i, createRegister)));
			} else {
				// First extract the type
				// If we have a register then we need to use it
				JClass typeClass = (JClass) field.type();
				if(typeClass.isInterface() || typeClass.isAbstract()){
					if(createRegister == null) throw new IllegalArgumentException("A register is need to inizilaize interfaces or abstract classes.");
					// Use the register to create the class
					thenBlock.assign(field, JExpr.cast(field.type(), createRegister.staticInvoke("singleton").invoke("newInstance").arg(param.invoke("getString").arg("entityType"))));
					thenBlock.add(field.invoke("initializeFromJSONObject").arg(param.invoke("getJSONObject").arg(propName)));

				}else{
					// We can just create a new type for this object.
					thenBlock.assign(
							field,
							JExpr._new(typeClass).arg(
									param.invoke("getJSONObject").arg(propName)));
				}

			}
			// throw an exception it this is a required fields
			if (propSchema.isRequired() && propSchema.getDefault() == null) {
				hasCondition._else()
						._throw(createIllegalArgumentException(classType,
								"Property: '" + propName
										+ "' is required and cannot be null"));
			} else {
				//if propSchema has a default defined the property must
				//be assigned to that default when  the adapter doesn't
				//have a corresponding property
				if (propSchema.getDefault() == null){
					// For non-require properties set the property to null
					hasCondition._else().assign(field, JExpr._null());
				}
				else {
					JExpression propShouldBe = assignDefaultProperty(propSchema);
					hasCondition._else().assign(field, propShouldBe);
				}
				
			}
		}
        // Always return the param
        body._return(param);
		return method;
	}



	/**
	 * Assign a JSON String to a property.  The format determines how it will be treated.
	 * @param param
	 * @param propName
	 * @param field
	 * @param propertySchema
	 * @param block
	 */
	protected JExpression assignJSONStringToProperty(JCodeModel model, JVar adapter, String propName, ObjectSchema propertySchema) {
		// The format determines how to treat a string.
		FORMAT format = propertySchema.getFormat();
		JExpression stringFromAdapter = adapter.invoke(TYPE.STRING.getMethodName()).arg(propName);
		return convertStringAsNeeded(model, adapter, format, stringFromAdapter);
	}

	/**
	 * If the string needst to be converted then do so.
	 * @param model
	 * @param adapter
	 * @param format
	 * @param stringFromAdapter
	 * @return
	 */
	public JExpression convertStringAsNeeded(JCodeModel model, JVar adapter, FORMAT format, JExpression stringFromAdapter) {
		if(format == null || format == FORMAT.URI){
			// Null format is treated as a simple string.
			return stringFromAdapter;
		}else{
			// Each format is handled separately.
			if(format == FORMAT.DATE_TIME || format == FORMAT.DATE || format == FORMAT.TIME){
				// These are all date formats
				// Use the adapter to adapter to convert from a string to a date
				return adapter.invoke("convertStringToDate").arg(model.ref(FORMAT.class).staticInvoke("valueOf").arg(format.name())).arg(stringFromAdapter);
			}else {
				throw new IllegalArgumentException("Unsupporetd format: "+format);
			}
		}
	}
	
	public JExpression convertLongAsNeeded(JCodeModel model, JVar adapter, FORMAT format, JExpression value) {
		if(format == null ){
			// Null format is treated as a simple long.
			return value;
		}else{
			// Each format is handled separately.
			if(format == FORMAT.UTC_MILLISEC){
				// Create a new date from the UTC string
				return JExpr._new(model.ref(Date.class)).arg(value);
			}else {
				throw new IllegalArgumentException("Unsupporetd format: "+format);
			}
		}
	}
	
	/**
	 * Assign a JSON String to a property.  The format determines how it will be treated.
	 * @param param
	 * @param propName
	 * @param field
	 * @param propertySchema
	 * @param block
	 */
	protected JExpression assignJSONStringToEnumProperty(JVar adapter, String propName, JFieldVar field) {
		// The format determines how to treat a string.
		JExpression stringFromAdapter = adapter.invoke(TYPE.STRING.getMethodName()).arg(propName);
		JClass enumClass = (JClass) field.type();
		return enumClass.staticInvoke("valueOf").arg(stringFromAdapter);
	}
	
	/**
	 * Assign a property to a  JSON String.  The format determines how it will be treated.
	 * @param param
	 * @param propName
	 * @param field
	 * @param propertySchema
	 * @param block
	 */
	protected JExpression assignPropertyToJSONString(JCodeModel model, JVar adapter, ObjectSchema propertySchema, JExpression field) {
		// The format determines how to treat a string.
		FORMAT format = propertySchema.getFormat();
//		JExpression stringFromAdapter = adapter.invoke(TYPE.STRING.getMethodName()).arg(propName);
		if(format == null || format == FORMAT.URI){
			// Null format is treated as a simple string.
			return field;
		}else{
			// Each format is handled separately.
			if(format == FORMAT.DATE_TIME || format == FORMAT.DATE || format == FORMAT.TIME){
				// These are all date formats
				// Use the adapter to adapter to convert from a string to a date
				return adapter.invoke("convertDateToString").arg(model.ref(FORMAT.class).staticInvoke("valueOf").arg(format.name())).arg(field);
			}else{
				throw new IllegalArgumentException("Unsupporetd format: "+format);
			}
		}
	}
	
	protected JExpression assignPropertyToJSONLong(JCodeModel model, ObjectSchema propertySchema, JExpression field) {
		// The format determines how to treat a string.
		FORMAT format = propertySchema.getFormat();
		if(format == null ){
			// Null format is treated as a simple string.
			return field;
		}else{
			// Each format is handled separately.
			if(format == FORMAT.UTC_MILLISEC){
				// Use the long to create a date
				return field.invoke("getTime");
			}else{
				throw new IllegalArgumentException("Unsupporetd format: "+format);
			}
		}
	}
	
	protected JExpression createExpresssionToGetFromArray(JVar adapter, JVar jsonArray, ObjectSchema arrayTypeSchema, JClass arrayTypeClass ,JVar index, JDefinedClass createRegister){
		TYPE arrayType = arrayTypeSchema.getType();
		FORMAT arrayFormat = arrayTypeSchema.getFormat();
		//check if our array type is an enum
		if (!arrayTypeClass.isPrimitive() && !arrayTypeClass.fullName().equals("java.lang.String") && arrayTypeClass instanceof JDefinedClass){
			JDefinedClass getTheClass = (JDefinedClass)arrayTypeClass;
			ClassType shouldHaveEnum = getTheClass.getClassType();
			if (ClassType.ENUM == shouldHaveEnum){
				//here we know we are dealing with an enum
				JExpression stringFromAdapter = jsonArray.invoke(arrayType.getMethodName()).arg(index);
				return arrayTypeClass.staticInvoke("valueOf").arg(stringFromAdapter);
			}
		}
		
		if(arrayType.isPrimitive() || TYPE.NUMBER == arrayType || TYPE.BOOLEAN == arrayType){
			return jsonArray.invoke(arrayType.getMethodName()).arg(index);
		}else if(TYPE.INTEGER == arrayType){
			JExpression longExper = jsonArray.invoke(arrayType.getMethodName()).arg(index);
			return convertLongAsNeeded(arrayTypeClass.owner(), adapter, arrayFormat, longExper);
		}else if(TYPE.STRING == arrayType){
			JExpression stringExper = jsonArray.invoke(arrayType.getMethodName()).arg(index);
			return convertStringAsNeeded(arrayTypeClass.owner(), adapter, arrayFormat, stringExper);
		}else if(TYPE.ARRAY == arrayType){
			throw new IllegalArgumentException("Arrays of Arrays are currently not supported");
		}else{
			// For an interface or abstract class we need to use the register to create it from the entity type.
			if(arrayTypeClass.isInterface() || arrayTypeClass.isAbstract()){
				if(createRegister == null) throw new IllegalArgumentException("A register is need to inizilaize interfaces or abstract classes.");
				// Use the register to create the class
				return JExpr.cast(arrayTypeClass, createRegister.staticInvoke("singleton").invoke("newInstance").arg(jsonArray.invoke("getJSONObject").arg(index).invoke("getString").arg("entityType")).invoke("initializeFromJSONObject").arg(jsonArray.invoke("getJSONObject").arg(index)));
			}else{
				// Now we need to create an object of the the type
				return JExpr._new(arrayTypeClass).arg(jsonArray.invoke("getJSONObject").arg(index));
			}

		}
	}
	
	
	/**
	 * Create the base method that is common to both initializeFromJSONObject() and writeToJSONObject()
	 * @param classSchema
	 * @param classType
	 * @param methodName
	 * @return
	 */
	protected JMethod createBaseMethod(ObjectSchema classSchema, JDefinedClass classType, String methodName){
		JMethod method  = classType.method(JMod.PUBLIC, JSONObjectAdapter.class, methodName);
		method._throws(JSONObjectAdapterException.class);
		method.annotate(Override.class);
		// add the parameter
		JVar param = method.param(classType.owner()._ref(JSONObjectAdapter.class), "adapter");
		JDocComment docs = method.javadoc();
		docs.add("@see JSONEntity#initializeFromJSONObject(JSONObjectAdapter)");
		docs.add("\n");
		docs.add("@see JSONEntity#writeToJSONObject(JSONObjectAdapter)");
		JCommentPart part = docs.addParam(param);
		docs.addThrows(JSONObjectAdapterException.class);
		// Create the constructor body
        JBlock body = method.body();
		// First add a super call
        if(classSchema.getExtends() != null){
        	JInvocation invocation = JExpr._super().invoke(methodName).arg(param);
        	body.add(invocation);
        }
        // Make sure the parameter is not null
        body._if(param.eq(JExpr._null()))
        	._then()._throw(createIllegalArgumentException(classType, JSONObjectAdapter.class.getName()+" cannot be null"));
        
        return method;
	}
	
	/**
	 * Create the write method, that pushes data to the JSONObject.
	 * @param classSchema
	 * @param classType
	 * @return
	 */
	protected JMethod createWriteToJSONObject(ObjectSchema classSchema, JDefinedClass classType) {
		JMethod method = createBaseMethod(classSchema, classType, "writeToJSONObject");
		JVar param = method.params().get(0);
		JBlock body = method.body();
		// Add the object type.
		this.getClass().getName();
		body.add(param.invoke("put").arg("entityType").arg(JExpr._this().invoke("getClass").invoke("getName")));
		// Now process each property
		Map<String, ObjectSchema> fieldMap = classSchema.getObjectFieldMap();
		Iterator<String> keyIt = fieldMap.keySet().iterator();
		while (keyIt.hasNext()) {
			String propName = keyIt.next();
			ObjectSchema propSchema = fieldMap.get(propName);
			// Look up the field for this property
			JFieldVar field = classType.fields().get(propName);
			if (field == null)
				throw new IllegalArgumentException(
						"Failed to find the JFieldVar for property: '"
								+ propName + "' on class: " + classType.name());
			// Now process this field
			if (propSchema.getType() == null)
				throw new IllegalArgumentException("Property: '" + propSchema
						+ "' has a null TYPE on class: " + classType.name());
			TYPE type = propSchema.getType();
			FORMAT format = propSchema.getFormat();

			// Primitives are easy, just assign them
			if (field.type().isPrimitive() && format == null) {
				body.add(param.invoke("put").arg(field.name()).arg(field));
				continue;
			}
			// Add an if
			JConditional hasCondition = body._if(field.ne(JExpr._null()));
			JBlock thenBlock = hasCondition._then();
			// For strings and primitives we can just assign the value right
			// from the adapter.
			if (TYPE.STRING == type) {
				// call the set method using the field
				JExpression valueToPut = null;
				if(propSchema.getEnum() != null){
					// Write the enum as a JSON string
					valueToPut = field.invoke("name");;
				}else{
					// This is just a string
					valueToPut = assignPropertyToJSONString(
							classType.owner(), param, propSchema, field);
				}
				thenBlock.add(param.invoke("put").arg(field.name())
						.arg(valueToPut));
			}else if (TYPE.INTEGER == type) {
				// Integers can be dates or longs
				JExpression expr = assignPropertyToJSONLong(classType.owner(), propSchema, field);
				// Basic assign
				thenBlock.add(param.invoke("put").arg(field.name()).arg(expr));
			} else if (TYPE.BOOLEAN == type || TYPE.NUMBER == type) {
				JClass typeClass = (JClass) field.type();
				// Basic assign
				thenBlock.add(param.invoke("put").arg(field.name()).arg(field));
			} else if (TYPE.ARRAY == type) {
				// Determine the type of the field
				JClass typeClass = (JClass) field.type();
				if (typeClass.getTypeParameters().size() != 1)
					throw new IllegalArgumentException(
							"Cannot determine the type of an array: "
									+ typeClass.fullName());
				JClass arrayTypeClass = typeClass.getTypeParameters().get(0);
				ObjectSchema arrayTypeSchema = propSchema.getItems();
				if (arrayTypeSchema == null)
					throw new IllegalArgumentException(
							"A property type is ARRAY but the getItems() returned null");
				TYPE arrayType = arrayTypeSchema.getType();
				if (arrayType == null)
					throw new IllegalArgumentException(
							"TYPE cannot be null for an ObjectSchema");
				// Create the new JSONArray
				JVar array = thenBlock.decl(JMod.NONE,
						classType.owner().ref(JSONArrayAdapter.class), "array",
						param.invoke("createNewArray"));
				JVar it = thenBlock.decl(
						JMod.NONE,
						classType.owner().ref(Iterator.class)
								.narrow(arrayTypeClass), "it",
						field.invoke("iterator"));
				JVar index = thenBlock.decl(JMod.NONE, classType.owner().INT,
						"index", JExpr.lit(0));
				// Create a local array
				JWhileLoop loop = thenBlock._while(it.invoke("hasNext"));
				JBlock loopBody = loop.body();
				loopBody.add(array
						.invoke("put")
						.arg(index)
						.arg(createExpresssionToSetFromArray(arrayTypeSchema,
								arrayTypeClass, it, param)));
				loopBody.directStatement("index++;");
				// Now set the new array
				thenBlock.add(param.invoke("put").arg(field.name()).arg(array));
			} else {
				// All others are treated as objects.
				thenBlock.add(param
						.invoke("put")
						.arg(field.name())
						.arg(field.invoke("writeToJSONObject").arg(
								param.invoke("createNew"))));
			}
			// throw an exception it this is a required fields
			if (propSchema.isRequired()) {
				hasCondition._else()
						._throw(createIllegalArgumentException(classType,
								"Property: '" + propName
										+ "' is required and cannot be null"));
			}
		}
        // Always return the param
        body._return(param);
        return method;
		
	}
	
	protected JExpression createExpresssionToSetFromArray(ObjectSchema arrayTypeSchema, JClass arrayTypeClass, JVar iterator, JVar param){
		TYPE arrayType = arrayTypeSchema.getType();
		FORMAT arrayFormat = arrayTypeSchema.getFormat();
		//need to determine if we are dealing with an array of enumerations
		if (!arrayTypeClass.isPrimitive() && !arrayTypeClass.fullName().equals("java.lang.String") && arrayTypeClass instanceof JDefinedClass){
			JDefinedClass getTheClass = (JDefinedClass)arrayTypeClass;
			ClassType shouldHaveEnum = getTheClass.getClassType();
			if (ClassType.ENUM == shouldHaveEnum){
				return iterator.invoke("next").invoke("name");
			}
		}
		
		if(arrayType.isPrimitive() || TYPE.NUMBER == arrayType || TYPE.BOOLEAN == arrayType){
			return iterator.invoke("next");
		}if(TYPE.STRING == arrayType){
			JExpression stringValue = iterator.invoke("next");
			return assignPropertyToJSONString(arrayTypeClass.owner(), param, arrayTypeSchema, stringValue); 
		}else if(TYPE.INTEGER == arrayType){
			JExpression value = iterator.invoke("next");
			return assignPropertyToJSONLong(arrayTypeClass.owner(), arrayTypeSchema, value);
		}else if(TYPE.ARRAY == arrayType){
			throw new IllegalArgumentException("Arrays of Arrays are currently not supported");
		}else{
			// Now we need to create an object of the the type
			return iterator.invoke("next").invoke("writeToJSONObject").arg(param.invoke("createNew"));
		}
	}

	/**
	 * Helper to create a new IllegalArgumentException 
	 * @param classType
	 * @param message
	 * @return
	 */
	private JInvocation createIllegalArgumentException(JDefinedClass classType, String message){
		return JExpr._new(classType.owner().ref(IllegalArgumentException.class)).arg(message);
	}

	/**
	 * Helper to assign a property a default value in the initializeFromJSONObject
	 * method that will be generated.  Handles situation where a property has a default
	 * value.
	 */
	private JExpression assignDefaultProperty(ObjectSchema propSchema){
		//determine what type the propSchema is
		TYPE type = propSchema.getType();
		JExpression propShouldBe = null;
		if (type == null){
			throw new IllegalArgumentException("property " + propSchema + 
					" has an null type and so a default can not " +
					"be assigned for this property");
		}
		if (TYPE.STRING == type){
			String defaultAsString = (String)propSchema.getDefault();
			propShouldBe = JExpr.lit(defaultAsString);
		}
		else if (TYPE.NUMBER == type){
			double defaultLong = (Double)propSchema.getDefault();
			propShouldBe = JExpr.lit(defaultLong);
		}
		else if (TYPE.INTEGER == type){
			long defaultLong = (Long)propSchema.getDefault();
			propShouldBe = JExpr.lit(defaultLong);
		}
		else if (TYPE.BOOLEAN == type){
			boolean defaultBoolean = (Boolean)propSchema.getDefault();
			propShouldBe = JExpr.lit(defaultBoolean);
		}
		else {
			throw new RuntimeException("can't assign default value " 
					+ propSchema.getDefault() + " as it is not a supported type");
		}
		return propShouldBe;
	}

	public JMethod createMethodInitializeFromJSONObject(ObjectSchema schema, JDefinedClass sampleClass) {
		return createMethodInitializeFromJSONObject(schema, sampleClass, null);
	}
}
