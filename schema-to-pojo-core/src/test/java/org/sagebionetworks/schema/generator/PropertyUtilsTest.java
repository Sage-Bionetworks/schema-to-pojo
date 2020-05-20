package org.sagebionetworks.schema.generator;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.schema.JavaKeyword;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.ObjectSchemaImpl;
import org.sagebionetworks.schema.TYPE;

class PropertyUtilsTest {

	JDefinedClass jDefinedClass;

	@BeforeEach
	void setup() throws JClassAlreadyExistsException {
		JCodeModel codeModel = new JCodeModel();
		JPackage testPackage = codeModel._package("org.sagebionetworks.test.class");
		jDefinedClass = testPackage._class("myTestClass");
	}

	@Test
	public void testGetPropertyReference_NoProperty(){
		String error = assertThrows(IllegalArgumentException.class, () ->
			// method under test
			PropertyUtils.getPropertyReference(jDefinedClass, "noExists")
		).getMessage();
		assertEquals("Failed to find the JFieldVar for property: 'noExists' on class: myTestClass", error);
	}
	@Test
	public void testGetPropertyReference_PropertyIsKeyword(){
		String keywordProperty = "final";
		JFieldVar field = jDefinedClass.field(JMod.PRIVATE, String.class,
				"_final");
		// method under test
		JFieldVar result = PropertyUtils.getPropertyReference(jDefinedClass, keywordProperty);
		assertEquals(field, result);
	}
	@Test
	public void testGetPropertyReference_PropertyNotKeyword(){
		String normalProperty = "yeeeeeeeeet";
		JFieldVar field = jDefinedClass.field(JMod.PRIVATE, String.class, normalProperty);
		// method under test
		JFieldVar result = PropertyUtils.getPropertyReference(jDefinedClass, normalProperty);
		assertEquals(field, result);
	}

	@Test
	public void testValidateNonNullType_nullType(){
		ObjectSchema objectSchema = new ObjectSchemaImpl();
		String error = assertThrows(IllegalArgumentException.class, () ->
			// method under test
			PropertyUtils.validateNonNullType(jDefinedClass, objectSchema)
		).getMessage();
		assertEquals("Property: 'ObjectSchemaImpl [name=null, type=null, properties=null, " +
				"additionalProperties=null, items=null, additionalItems=null, key=null, value=null, " +
				"required=null, dependencies=null, minimum=null, maximum=null, exclusiveMinimum=null, " +
				"exclusiveMaximum=null, minItems=null, maxItems=null, uniqueItems=null, _transient=null, " +
				"pattern=null, minLength=null, maxLength=null, _enum=null, _default=null, title=null, " +
				"description=null, format=null, divisibleBy=null, disallow=null, _extends=null, _implements=null, " +
				"id=null, ref=null, schema=null, contentEncoding=null, links=null, $recursiveAnchor=null," +
				" $recursiveRef=null, is$RecursiveRefInstance=false]' has a null TYPE on class: myTestClass", error);
	}

	@Test
	public void testValidateNonNullType_nonNullType(){
		ObjectSchema objectSchema = new ObjectSchemaImpl(TYPE.STRING);
		TYPE type = PropertyUtils.validateNonNullType(jDefinedClass, objectSchema);
		assertEquals(TYPE.STRING, type);
	}

}