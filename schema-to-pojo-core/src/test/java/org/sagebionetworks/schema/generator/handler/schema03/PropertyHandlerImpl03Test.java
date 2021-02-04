package org.sagebionetworks.schema.generator.handler.schema03;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.ObjectSchemaImpl;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.generator.handler.PropertyHandler;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

public class PropertyHandlerImpl03Test {
	
	JCodeModel codeModel;
	JPackage _package;
	JDefinedClass sampleClass;
	JDefinedClass sampleInterfance;
	JType type;
	ObjectSchema schema;
	
	@Before
	public void before() throws JClassAlreadyExistsException{
		codeModel = new JCodeModel();
		_package = codeModel._package("org.sample");
		sampleClass = _package._class("Sample");
		sampleInterfance = _package._interface("SampleInterface");
		schema = new ObjectSchemaImpl();
		schema.setType(TYPE.STRING);
		// Create a string property
		type = codeModel.ref(String.class);
	}
	@Test
	public void testGetterName(){
		String name = "name";
		String getter = PropertyHandlerImpl03.getterName(name);
		System.out.println(getter);
		assertEquals("getName", getter);
	}
	
	@Test
	public void testGetterSingleChar(){
		String name = "a";
		String getter = PropertyHandlerImpl03.getterName(name);
		System.out.println(getter);
		assertEquals("getA", getter);
	}
	
	@Test
	public void testGetterSingleChar2(){
		String name = "A";
		String getter = PropertyHandlerImpl03.getterName(name);
		System.out.println(getter);
		assertEquals("getA", getter);
	}
	
	@Test
	public void testGetterCamelCase(){
		String name = "camelCase";
		String getter = PropertyHandlerImpl03.getterName(name);
		System.out.println(getter);
		assertEquals("getCamelCase", getter);
	}
	
	@Test
	public void testSetterNameCamelCase(){
		String name = "camelCase";
		String setter = PropertyHandlerImpl03.setterName(name);
		System.out.println(setter);
		assertEquals("setCamelCase", setter);
	}

	@Test
	public void testCreateProperty() throws JClassAlreadyExistsException{
		// Now handle the 
		PropertyHandler handler = new PropertyHandlerImpl03();
		JFieldVar field = handler.createProperty(schema, sampleClass, "name", type);
		assertNotNull(field);
		assertEquals(JMod.PRIVATE, field.mods().getValue());
		assertEquals("name", field.name());
		assertEquals(type, field.type());
	}
	
	@Test
	public void testCreatePropertyInterfance() throws JClassAlreadyExistsException{
		// Now handle the 
		PropertyHandler handler = new PropertyHandlerImpl03();
		JFieldVar field = handler.createProperty(schema, sampleInterfance, "name", type);
		// Interfaces have not fields
		assertNull(field);
	}
	
	@Test
	public void testCreatePropertyGetter() throws JClassAlreadyExistsException{
		// Now handle the 
		PropertyHandler handler = new PropertyHandlerImpl03();
		handler.createProperty(schema, sampleClass, "name", type);
		// Make sure there is a getter
		 Collection<JMethod> methods =  sampleClass.methods();
		 assertNotNull(methods);
		 assertTrue(methods.size() > 0);
		 // Find the getter
		 JMethod getName = null;
		 for(JMethod method: methods){
			 if("getName".equals(method.name())){
				 getName = method;
				 break;
			 }
		 }
		 assertNotNull("Failed to find the getter method", getName);
		 // It should be public
		 assertEquals(JMod.PUBLIC, getName.mods().getValue());
		 assertEquals(type, getName.type());
		 assertNotNull(getName.params());
		 assertEquals(0, getName.params().size());
		 
	}
	
	@Test
	public void testCreatePropertyGetterInterfance() throws JClassAlreadyExistsException{
		// Now handle the 
		PropertyHandler handler = new PropertyHandlerImpl03();
		handler.createProperty(schema, sampleInterfance, "name", type);
		// Make sure there is a getter
		 Collection<JMethod> methods =  sampleInterfance.methods();
		 assertNotNull(methods);
		 assertTrue(methods.size() > 0);
		 // Find the getter
		 JMethod getName = null;
		 for(JMethod method: methods){
			 if("getName".equals(method.name())){
				 getName = method;
				 break;
			 }
		 }
		 assertNotNull("Failed to find the getter method", getName);
		 // It should be public
		 assertEquals(JMod.PUBLIC, getName.mods().getValue());
		 assertEquals(type, getName.type());
		 assertNotNull(getName.params());
		 assertEquals(0, getName.params().size());
		 // Make sure the getter has no body.
		 StringWriter writer = new StringWriter();
		 JFormatter formatter = new JFormatter(writer);
		 getName.declare(formatter);
//		 System.out.println(writer.toString());
		 String methodString = writer.toString();
		 assertTrue(methodString.indexOf("public java.lang.String getName();") > 0);;
	}
	
	@Test
	public void testCreatePropertySetter() throws JClassAlreadyExistsException{
		// Now handle the 
		PropertyHandler handler = new PropertyHandlerImpl03();
		handler.createProperty(schema, sampleClass, "name", type);
		// Make sure there is a getter
		 Collection<JMethod> methods =  sampleClass.methods();
		 assertNotNull(methods);
		 assertTrue(methods.size() > 0);
		 // Find the getter
		 JMethod setName = null;
		 for(JMethod method: methods){
			 if("setName".equals(method.name())){
				 setName = method;
				 break;
			 }
		 }
		 assertNotNull("Failed to find the setter method", setName);
		 // It should be public
		 assertEquals(JMod.PUBLIC, setName.mods().getValue());
		 assertEquals(sampleClass, setName.type());
		 assertNotNull(setName.params());
		 assertEquals(1, setName.params().size());
	}
	
	@Test
	public void testCreatePropertySetterInterfance() throws JClassAlreadyExistsException{
		// Now handle the 
		PropertyHandler handler = new PropertyHandlerImpl03();
		handler.createProperty(schema, sampleInterfance, "name", type);
		// Make sure there is a getter
		 Collection<JMethod> methods =  sampleInterfance.methods();
		 assertNotNull(methods);
		 assertTrue(methods.size() > 0);
		 // Find the getter
		 JMethod setName = null;
		 for(JMethod method: methods){
			 if("setName".equals(method.name())){
				 setName = method;
				 break;
			 }
		 }
		 assertNotNull("Failed to find the setter method", setName);
		 // It should be public
		 assertEquals(JMod.PUBLIC, setName.mods().getValue());
		 assertEquals(sampleInterfance, setName.type());
		 assertNotNull(setName.params());
		 assertEquals(1, setName.params().size());
		 StringWriter writer = new StringWriter();
		 JFormatter formatter = new JFormatter(writer);
		 setName.declare(formatter);
		 System.out.println(writer.toString());
		 String methodString = writer.toString();
		 assertTrue(methodString.indexOf("public org.sample.SampleInterface setName(java.lang.String name);") > 0);;
	}
	
	@Test
	public void testCreatePropertySetterRequired() throws JClassAlreadyExistsException{
		// Now handle the 
		PropertyHandler handler = new PropertyHandlerImpl03();
		// Make this property required
		schema.setRequired(true);
		handler.createProperty(schema, sampleClass, "name", type);
		// Make sure there is a getter
		 Collection<JMethod> methods =  sampleClass.methods();
		 assertNotNull(methods);
		 assertTrue(methods.size() > 0);
		 // Find the getter
		 JMethod setName = null;
		 for(JMethod method: methods){
			 if("setName".equals(method.name())){
				 setName = method;
				 break;
			 }
		 }
		 assertNotNull("Failed to find the setter method", setName);
		 // It should be public
		 assertEquals(JMod.PUBLIC, setName.mods().getValue());
		 // Get the body
		 StringWriter writer = new StringWriter();
		 JFormatter formatter = new JFormatter(writer);
		 setName.declare(formatter);
//		 System.out.println(writer.toString());
		 String bodyString = writer.toString();
		 assertTrue(bodyString.indexOf("this.name = name;") > 0);
		 // This body should throw an exception if the value is set to null;
		 assertTrue(bodyString.indexOf("if (name == null)") > 0);
		 assertTrue(bodyString.indexOf("throw new java.lang.IllegalArgumentException(") > 0);
	}
	
	@Test
	public void testCreatePropertyTitle() throws JClassAlreadyExistsException{
		// Now handle the 
		PropertyHandler handler = new PropertyHandlerImpl03();
		// Make this property required
		schema.setTitle("This is a short title string");
		schema.setDescription("This is a longer description that should come after the title");
		handler.createProperty(schema, sampleClass, "name", type);
		// Make sure there is a getter
		 Collection<JMethod> methods =  sampleClass.methods();
		 assertNotNull(methods);
		 assertTrue(methods.size() > 0);
		 // Find the getter
		 JMethod setName = null;
		 JMethod getName = null;
		 for(JMethod method: methods){
			 if("setName".equals(method.name())){
				 setName = method;
				 continue;
			 }
			 if("getName".equals(method.name())){
				 getName = method;
				 continue;
			 }
		 }
		 assertNotNull(setName);
		 assertNotNull(getName);
		 // check the method comments
		 StringWriter writer = new StringWriter();
		 JFormatter formatter = new JFormatter(writer);
		 setName.declare(formatter);
//		 System.out.println(writer.toString());
		 String methodString = writer.toString();
		 assertTrue(methodString.indexOf("This is a short title string") > 0);
		 assertTrue(methodString.indexOf("This is a longer description that should come after the title") > 0);
		 assertTrue(methodString.indexOf("@param name") > 0);
		 // Now check the getter
		 writer = new StringWriter();
		 formatter = new JFormatter(writer);
		 getName.declare(formatter);
//		 System.out.println(writer.toString());
		 methodString = writer.toString();
		 assertTrue(methodString.indexOf("This is a short title string") > 0);
		 assertTrue(methodString.indexOf("This is a longer description that should come after the title") > 0);
		 assertTrue(methodString.indexOf("@return") > 0);
	}
	
	@Test
	public void testConcreteType(){
		// Now handle the 
		PropertyHandler handler = new PropertyHandlerImpl03();
		// Make this property required
		JFieldVar field = handler.createProperty(schema, sampleClass, ObjectSchema.CONCRETE_TYPE, type);
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		sampleClass.declare(formatter);
		String classString = writer.toString();
		System.out.println(classString);
		 assertTrue(classString.indexOf("private java.lang.String concreteType = org.sample.Sample.class.getName();") > 0);
	}
	
}
