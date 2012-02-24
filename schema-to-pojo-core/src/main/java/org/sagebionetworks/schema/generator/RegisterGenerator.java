package org.sagebionetworks.schema.generator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
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
		JDefinedClass regClass = null;
		try {
			regClass = _package._class(className);
		} catch (JClassAlreadyExistsException e) {
			regClass = e.getExistingClass();
		}
		// This field is the map
		JFieldRef mapRef = createMapFieldRef(codeModel, regClass);
		// Create the constructor.  This will build up the map.
		createConstructor(codeModel, list, regClass, mapRef);
		
		// Create the classsForName method
		createClassForName(codeModel, regClass, mapRef);
		
		createKeySetIterator(codeModel, regClass, mapRef);
		
		// Add the auto-generated title
		// Add the comments to the class
		JDocComment docs = regClass.javadoc();
		docs.add(AUTO_GENERATED_MESSAGE);
		docs.add("\n\n");
		docs.add("This is a map of all of the classes that were auto-generated.  It can be used as a substitute for reflection in GWT client-side code.");
		return regClass;
	}

	/**
	 * Create the keyset iterator.
	 * @param codeModel
	 * @param regClass
	 * @param mapRef
	 * @return
	 */
	protected static JMethod createKeySetIterator(JCodeModel codeModel, JDefinedClass regClass, JFieldRef mapRef) {
		JClass itType = codeModel.ref(Iterator.class).narrow(String.class);
		JMethod method = regClass.method(JMod.PUBLIC, itType, "getKeySetIterator");
		method.body()._return(mapRef.invoke("keySet").invoke("iterator"));
		JDocComment docs = method.javadoc();
		docs.add("Get the key set iterator.");
		return method;
	}

	/**
	 * Create the map field and return the references this.map
	 * @param codeModel
	 * @param regClass
	 * @return
	 */
	protected static JFieldRef createMapFieldRef(JCodeModel codeModel,JDefinedClass regClass) {
		JClass fieldType = codeModel.ref(Map.class).narrow(codeModel.ref(String.class), codeModel.ref(Class.class));
		JFieldVar mapField = regClass.field(JMod.PRIVATE, fieldType, "map");
		// get the reference to this.map
		JFieldRef mapRef = JExpr._this().ref(mapField);
		return mapRef;
	}

	/**
	 * Creates the constructor.  Will initialize the map and populate it with each schema class.
	 * @param codeModel
	 * @param list
	 * @param regClass
	 * @return
	 */
	protected static JMethod createConstructor(JCodeModel codeModel, List<ObjectSchema> list, JDefinedClass regClass, JFieldRef mapRef) {
		// Create the Constructor
		JMethod constructor = regClass.constructor(JMod.PUBLIC);
		JBlock conBody = constructor.body();
		// This is the hash map
		JClass hashMap = codeModel.ref(HashMap.class).narrow(codeModel.ref(String.class), codeModel.ref(Class.class));

		conBody.assign(mapRef, JExpr._new(hashMap));
		// Populate it with the values
		for(ObjectSchema schema: list){
			// Add each entry
			if(schema.getId() == null) throw new IllegalArgumentException("Id cannot be null for an auto-generated Register");
			JDefinedClass classToRegister = null;
			try {
				classToRegister = codeModel._class(schema.getId());
			} catch (JClassAlreadyExistsException e) {
				classToRegister = e.getExistingClass();
			}
			JFieldRef classDotClass = classToRegister.staticRef("class");
			
			JInvocation putInvoke = mapRef.invoke("put");
			// First arg
			putInvoke.arg(classDotClass.invoke("getName"));
			// second arg
			putInvoke.arg(classDotClass);
			conBody.add(putInvoke);
		}
		return constructor;
	}

	/**
	 * 
	 * @param codeModel
	 * @param regClass
	 */
	public static JMethod createClassForName(JCodeModel codeModel, JDefinedClass regClass, JFieldRef mapRef) {
		JMethod getRegMethod = regClass.method(JMod.PUBLIC, codeModel.ref(Class.class), "forName");
		JVar param = getRegMethod.param(String.class, "className");
		getRegMethod.javadoc().add("Lookup a class using its full package name.  This works like Class.forName(className), but is GWT compatible.");
		JBlock body = getRegMethod.body();
		JInvocation getInvoke = mapRef.invoke("get");
		getInvoke.arg(param);
		body._return(getInvoke);
		return getRegMethod;
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
