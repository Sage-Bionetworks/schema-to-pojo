package org.sagebionetworks.schema.generator;

import java.util.List;

import org.json.JSONString;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.adapter.JSONEntity;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;

/**
 * Auto-generates a register enumeration class.
 * 
 * @author John
 *
 */
public class RegisterGenerator {
	
	public static final String AUTO_GENERATED_MESSAGE = "Note: This class was auto-generated, and should not be directly modified.";
	
	/**
	 * Create a register for each including each class in the schema list.
	 * @param codeModel
	 * @param list
	 * @param registerClass
	 * @throws JClassAlreadyExistsException 
	 */
	public JDefinedClass createRegister(JCodeModel codeModel, List<ObjectSchema> list, String registerClass) {
		// Create the enumeration
		String packageName = getPackageName(registerClass);
		String className = getClassName(registerClass);
		JPackage _package = codeModel._package(packageName);
		// Create the enum
		JDefinedClass enumClass = null;
		try {
			enumClass = _package._enum(className);
		} catch (JClassAlreadyExistsException e) {
			enumClass = e.getExistingClass();
		}
		// Create an enum constant for each class.
		createConstants(codeModel, list, enumClass);
		
		// Add the class name field
		createFieldsConstructorGetter(codeModel, enumClass);
		
		// Create a static lookup method to lookup the type by classname
		createStaticLookup(codeModel, enumClass);
		
		// Add the auto-generated title
		// Add the comments to the class
		JDocComment docs = enumClass.javadoc();
		docs.add(AUTO_GENERATED_MESSAGE);
		docs.add("\n\n");
		docs.add("This is an enumeration of all of the classes that were auto-generated.  It can be used as a substitute for reflection in GWT client-side code.");
		return enumClass;
	}

	/**
	 * 
	 * @param codeModel
	 * @param enumClass
	 */
	public static JMethod createStaticLookup(JCodeModel codeModel, JDefinedClass enumClass) {
		JMethod getRegMethod = enumClass.method(JMod.PUBLIC | JMod.STATIC, enumClass, "typeForName");
		JVar param = getRegMethod.param(String.class, "fullClassName");
		getRegMethod.javadoc().add("Lookup a Class using its full package name");
		JBlock body = getRegMethod.body();
		JForEach loop = body.forEach(enumClass, "reg", enumClass.staticInvoke("values"));
		JBlock loopBody = loop.body();
		// If the parameter matches the 
		JConditional con = loopBody._if(loop.var().ref("clazz").invoke("getName").invoke("equals").arg(param));
		con._then()._return(loop.var());
		// If the loop did not find the type then fail
		body._throw(JExpr._new(codeModel._ref(IllegalArgumentException.class)).arg(JExpr.lit("No class registered with the name: ").plus(param)));
		return getRegMethod;
	}

	/**
	 * Create all fields, the constructor and getters.
	 * @param codeModel
	 * @param enumClass
	 */
	public static void createFieldsConstructorGetter(JCodeModel codeModel,
			JDefinedClass enumClass) {
		JClass classType = codeModel.ref(Class.class).narrow(codeModel.wildcard());
		JFieldVar fieldClass = enumClass.field(JMod.PRIVATE, classType, "clazz");
		
		// Add the constructor that takes the class
		JMethod constructor = enumClass.constructor(JMod.PRIVATE);
		JVar constructorArg = constructor.param(classType, "clazz");
		constructor.body().assign(JExpr._this().ref(fieldClass), constructorArg);
		
		// Add a method to get the class
		JMethod method = enumClass.method(JMod.PUBLIC, classType, "getRegisteredClass");
		method.javadoc().add("Get the Class registerd to this type");
		method.body()._return(JExpr._this().ref(fieldClass));
	}

	/**
	 * For each ObjectSchema create an enum constant.
	 * @param codeModel
	 * @param list
	 * @param enumClass
	 */
	public static void createConstants(JCodeModel codeModel, List<ObjectSchema> list,
			JDefinedClass enumClass) {
		// Add all of the constants
		for(ObjectSchema schema: list){
			// Add some constants
			if(schema.getId() == null) throw new IllegalArgumentException("Id cannot be null for an auto-generated Register");
			// This will be the name of this enum value
			String enumValue = schema.getId().toUpperCase().replaceAll("\\.", "_");
			// Create the constant
			JEnumConstant enumCon = enumClass.enumConstant(enumValue);
			// The first argument is the full class name
			JDefinedClass classToRegister = null;
			try {
				classToRegister = codeModel._class(schema.getId());
			} catch (JClassAlreadyExistsException e) {
				classToRegister = e.getExistingClass();
			}
			enumCon.arg(JExpr.dotclass(classToRegister));
		}
	}
	
	/**
	 * Extract the class name;
	 * @param registerClass
	 * @return
	 */
	protected static String getClassName(String fullClassName) {
		return fullClassName.substring(fullClassName.lastIndexOf('.')+1, fullClassName.length());
	}

	/**
	 * Get the package name from the fully qualified name.
	 * @param name
	 * @return
	 */
	protected static String getPackageName(String fullClassName){
		return fullClassName.substring(0, fullClassName.lastIndexOf('.'));
	}

}
