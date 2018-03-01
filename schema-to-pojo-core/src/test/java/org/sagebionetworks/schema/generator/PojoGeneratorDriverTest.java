package org.sagebionetworks.schema.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.schema.generator.handler.schema03.HandlerFactoryImpl03;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDeclaration;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

public class PojoGeneratorDriverTest {
	
	PojoGeneratorDriver driver = null;
	ObjectSchema schema;
	
	@Before
	public void before(){
		driver = new PojoGeneratorDriver(new HandlerFactoryImpl03());
		schema = new ObjectSchema();
		schema.setName("SampleClass");
		schema.setId("org.sample."+schema.getName());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testRegisterAllIdentifiedObjectSchemasDuplicate() {
		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		// Create a duplicate
		list.add(ObjectSchema.createNewWithId("one"));
		list.add(ObjectSchema.createNewWithId("one"));
		// This should fail due to the duplicate
		PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testRegisterAllIdentifiedObjectSchemasNestedDuplicate() {
		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		// Create a duplicate
		ObjectSchema root1 = ObjectSchema.createNewWithId("rootOne");
		list.add(root1);
		ObjectSchema root2 = ObjectSchema.createNewWithId("rootTwo");
		list.add(root2);
		// Add a child to each with a duplicate name
		root1.setItems(ObjectSchema.createNewWithId("child"));
		// Add a child to each with a duplicate name
		root2.setItems(ObjectSchema.createNewWithId("child"));
		// This should fail due to the duplicate
		PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list);
	}
	
	@Test 
	public void testRegisterAllIdentifiedObjectSchemasNested() {
		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		// Create a duplicate
		ObjectSchema root1 = ObjectSchema.createNewWithId("rootOne");
		list.add(root1);
		ObjectSchema root2 = ObjectSchema.createNewWithId("rootTwo");
		list.add(root2);
		// Add a child to each with a unique name
		root1.setItems(ObjectSchema.createNewWithId("child1"));
		// Add a child to each with a unique name
		root2.setItems(ObjectSchema.createNewWithId("child2"));
		// This should not fail this time.
		Map<String, ObjectSchema> map = PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list);
		assertNotNull(map);
		assertEquals(4, map.size());
		assertEquals(root1, map.get(new String("rootOne")));
		assertEquals(root2, map.get(new String("rootTwo")));
		assertNotNull(map.get(new String("child1")));
		assertNotNull(map.get(new String("child2")));
	}
	
	@Test
	public void testReplaceRefrence() {
		// This is not a reference so the replace should just return it.
		ObjectSchema root1 = ObjectSchema.createNewWithId("rootOne");
		ObjectSchema replaced = PojoGeneratorDriver.replaceRefrence(new HashMap<String, ObjectSchema>(), root1, null);
		assertEquals(root1, replaced);
	}
	
	@Test
	public void testReplaceRefrenceSelf() {
		// This is not a reference so the replace should just return it.
		ObjectSchema self = ObjectSchema.createNewWithId("rootOne");
		// Create a self self reference
		ObjectSchema refrenceToSelf = new ObjectSchema();
		refrenceToSelf.setRef(ObjectSchema.SELF_REFERENCE);
		
		ObjectSchema replaced = PojoGeneratorDriver.replaceRefrence(new HashMap<String, ObjectSchema>(), refrenceToSelf, self);
		// Should be replaced with self
		assertEquals(self, replaced);
	}
	
	@Test
	public void testReplaceRefrenceRegistry() {
		// This is not a reference so the replace should just return it.
		String referenceId = "rootOne";
		ObjectSchema referenced = ObjectSchema.createNewWithId(referenceId);
		HashMap<String, ObjectSchema> registry = new HashMap<String, ObjectSchema>();
		// Add the referenced schema to the register.
		registry.put(referenceId, referenced);
		// Create a self self reference
		ObjectSchema referenceToOther = new ObjectSchema();
		referenceToOther.setRef(referenceId);
		
		// Create a third self
		ObjectSchema self = ObjectSchema.createNewWithId("self");
		
		ObjectSchema replaced = PojoGeneratorDriver.replaceRefrence(registry, referenceToOther, self);
		// Should be replaced with referenced
		assertEquals(referenced, replaced);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testReplaceRefrenceMissRegistry() {
		// This is not a reference so the replace should just return it.
		String referenceId = new String("rootOne");
		// This time the referenced is not in the register
		HashMap<String, ObjectSchema> registry = new HashMap<String, ObjectSchema>();
		// Create a self self reference
		ObjectSchema referenceToOther = new ObjectSchema();
		referenceToOther.setRef(referenceId);
		
		// Create a third self
		ObjectSchema self = ObjectSchema.createNewWithId(new String("self"));
		// This should fail since the referenced is not in the register
		ObjectSchema replaced = PojoGeneratorDriver.replaceRefrence(registry, referenceToOther, self);
	}
	
	@Test
	public void testFindAndReplaceAllReferencesSchemas() {
		// Build up a map with one reference and one not
		// This is not a reference so the replace should just return it.
		String referenceId = new String("rootOne");
		ObjectSchema referenced = ObjectSchema.createNewWithId(referenceId);
		HashMap<String, ObjectSchema> registry = new HashMap<String, ObjectSchema>();
		// Add the referenced schema to the register.
		registry.put(referenceId, referenced);
		// Create a self self reference
		ObjectSchema referenceToOther = new ObjectSchema();
		referenceToOther.setRef(referenceId);
		
		// Create a third self
		ObjectSchema self = ObjectSchema.createNewWithId(new String("self"));
		ObjectSchema refToSelf = new ObjectSchema();
		refToSelf.setRef(new String(ObjectSchema.SELF_REFERENCE));
		// Now add all three to the a map
		HashMap<String, ObjectSchema> map = new HashMap<String, ObjectSchema>();
		map.put("one", referenced);
		map.put("two", referenceToOther);
		map.put("three", refToSelf);
		
		Map<String, ObjectSchema> results = PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(registry,map, self);
		assertNotNull(results);
		assertEquals(3, results.size());
		assertEquals(referenced, results.get("one"));
		assertEquals(referenced, results.get("two"));
		assertEquals(self, results.get("three"));
	}
	
	@Test
	public void testFindAndReplaceAllReferencesSchemasFull(){
		String referenceId = new String("rootOne");
		ObjectSchema referenced = ObjectSchema.createNewWithId(referenceId);
		HashMap<String, ObjectSchema> registry = new HashMap<String, ObjectSchema>();
		// Add the referenced schema to the register.
		registry.put(referenceId, referenced);
		// Create a third self
		ObjectSchema self = ObjectSchema.createNewWithId(new String("self"));
		ObjectSchema refToSelf = new ObjectSchema();
		refToSelf.setRef(new String(ObjectSchema.SELF_REFERENCE));
		
		ObjectSchema referenceToOther = new ObjectSchema();
		referenceToOther.setRef(referenceId);
		
		// Add references in all places
		self.putProperty("one", referenceToOther);
		self.putAdditionalProperty("two", referenceToOther);
		self.setItems(refToSelf);
		self.setAdditionalItems(refToSelf);
		self.setImplements(new ObjectSchema[]{referenceToOther});
		
		// find and replace
		PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(registry, self);
		// Make sure there are no references
		Iterator<ObjectSchema> it = self.getSubSchemaIterator();
		while(it.hasNext()){
			ObjectSchema toTest = it.next();
			assertTrue(toTest.getRef() == null);
		}
	}
	

	@Test
	public void testNestedObjects() throws JSONObjectAdapterException{
		// Create an object with nesting
		ObjectSchema root = new ObjectSchema();
		root.setName("Root");
		root.setId(new String("root"));
		// Create a child class
		ObjectSchema child = new ObjectSchema();
		child.setName("Child");
		child.setType(TYPE.OBJECT);
		root.putProperty("childInstance1", child);
		// Create a grand child
		ObjectSchema grand = new ObjectSchema();
		grand.setName("Grand");
		grand.setType(TYPE.OBJECT);
		String grandId = new String("grand");
		grand.setId(grandId);
		child.putProperty("grandChildInstance1", grand);
		ObjectSchema grandRef = new ObjectSchema();
		grandRef.setRef(grandId);
		child.putProperty("grandChildInstance2", grandRef);
		System.out.println(root.toJSONString(new JSONObjectAdapterImpl()));
		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		list.add(root);
		
		// Now before the are replaces this should be a references
		ObjectSchema test = child.getProperties().get("grandChildInstance2");
		assertNotNull(test);
		assertEquals(grandId, test.getRef());
		
		Map<String, ObjectSchema> register = PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list);
		PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(register, list);
		// Validate that the nest grand child reference is replaced
		test = child.getProperties().get("grandChildInstance2");
		assertNotNull(test);
		assertEquals(null, test.getRef());
		assertEquals(grand, test);	
	}
	
	@Test
	public void testNestedSelfObjects() throws JSONObjectAdapterException {
		// Create an object with nesting
		ObjectSchema root = new ObjectSchema();
		root.setName("Root");
		root.setId(new String("root"));
		// Create a child class
		ObjectSchema child = new ObjectSchema();
		String childId = new String("child");
		child.setName("Child");
		child.setId(childId);
		child.setType(TYPE.OBJECT);
		root.putProperty("childInstance1", child);
		// Add a self reference child
		ObjectSchema childSelf = new ObjectSchema();
		childSelf.setRef(new String(ObjectSchema.SELF_REFERENCE));
		child.putProperty("selfRefrence", childSelf);
		// Create a grand child

		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		list.add(root);
		Map<String, ObjectSchema> register = PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list);
		PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(register, list);
	}
	
	@Test
	public void testInterfaceField() throws JSONObjectAdapterException, ClassNotFoundException {
		// Create an object with nesting
		ObjectSchema inter = new ObjectSchema();
		inter.setName("SomeInterface");
		inter.setType(TYPE.INTERFACE);
		inter.setId("example.org.SomeInterface");
		
		ObjectSchema interRef = new ObjectSchema();
		interRef.setRef(inter.getId());
		
		ObjectSchema impl = new ObjectSchema();
		impl.setName("SomeInterfaceImpl");
		impl.setType(TYPE.OBJECT);
		impl.setId("example.org.SomeInterfaceImpl");
		impl.setImplements(new ObjectSchema[]{interRef});
		
		ObjectSchema root = new ObjectSchema();
		root.setName("Root");
		root.setId(new String("root"));
		root.setType(TYPE.OBJECT);

		// Create a child class
		ObjectSchema child = new ObjectSchema();
		String childId = new String("child");
		child.setName("Child");
		child.setId(childId);
		child.setType(TYPE.OBJECT);
		child.setRef(inter.getId());
		root.putProperty("interfaceField", child);

		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		list.add(inter);
		list.add(impl);
		list.add(root);
//		Map<String, ObjectSchema> register = PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list);
//		List<ObjectSchema> schemaList = PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(register, list);
		JCodeModel codeModel = new JCodeModel();
		driver.createAllClasses(codeModel, list);
	}
	
	@Test
	public void testCycle() throws JSONObjectAdapterException {
		// Create an object with nesting
		ObjectSchema root = new ObjectSchema();
		root.setName("Root");
		String rootId = new String("root");
		root.setId(rootId);
		// Create a child class
		ObjectSchema child = new ObjectSchema();
		String childId = new String("child");
		child.setName("Child");
		child.setId(childId);
		child.setType(TYPE.OBJECT);
		root.putProperty("childInstance1", child);
		// Add a self reference child
		ObjectSchema rootRef = new ObjectSchema();
		rootRef.setRef(rootId);
		child.putProperty("rootRef", rootRef);
		// Create a grand child

		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		list.add(root);
		Map<String, ObjectSchema> register = PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list);
		PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(register, list);
	}

	@Test
	public void testRecursivlyCreateAllTypesNumber() throws ClassNotFoundException{
		ObjectSchema schema = new ObjectSchema();
		schema.setType(TYPE.NUMBER);
		JCodeModel codeModel = new JCodeModel();
		schema.setId("org.sample.SampleClass");
		JPackage _package = codeModel._package("org.sample");
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Double.class.getName(), type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesInteger() throws ClassNotFoundException{
		schema.setType(TYPE.INTEGER);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Long.class.getName(), type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesBoolean() throws ClassNotFoundException{
		schema.setType(TYPE.BOOLEAN);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Boolean.class.getName(), type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesString() throws ClassNotFoundException{
		schema.setType(TYPE.STRING);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(String.class.getName(), type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesAny() throws ClassNotFoundException{
		schema.setType(TYPE.ANY);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Object.class.getName(), type.fullName());
	}
	
	@Test 
	public void testRecursivlyCreateAllTypesNull() throws ClassNotFoundException{
		schema.setType(TYPE.NULL);
		JCodeModel codeModel = new JCodeModel();
		// Null is not supported
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Object.class.getName(), type.fullName());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testRecursivlyCreateAllTypesArrayNoType() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		JCodeModel codeModel = new JCodeModel();
		JPackage _package = codeModel._package("org.sample");
		// should fail since the array type is not set
		JType type = driver.createOrGetType(codeModel, schema);
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayString() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.STRING);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(List.class.getName()+"<"+String.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayStringSet() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		// set it to be unique to get a set
		schema.setUniqueItems(true);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.STRING);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Set.class.getName()+"<"+String.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayInteger() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.INTEGER);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(List.class.getName()+"<"+Long.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayIntegerSet() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		// set it to be unique to get a set
		schema.setUniqueItems(true);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.INTEGER);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Set.class.getName()+"<"+Long.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayBoolean() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.BOOLEAN);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(List.class.getName()+"<"+Boolean.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayBooleanSet() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		// set it to be unique to get a set
		schema.setUniqueItems(true);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.BOOLEAN);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Set.class.getName()+"<"+Boolean.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayNumber() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.NUMBER);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(List.class.getName()+"<"+Double.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayNumberSet() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		// set it to be unique to get a set
		schema.setUniqueItems(true);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.NUMBER);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Set.class.getName()+"<"+Double.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayAny() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.ANY);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(List.class.getName()+"<"+Object.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayAnySet() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		// set it to be unique to get a set
		schema.setUniqueItems(true);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.ANY);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Set.class.getName()+"<"+Object.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayNull() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.NULL);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(List.class.getName()+"<"+Object.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayNullSet() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		// set it to be unique to get a set
		schema.setUniqueItems(true);
		ObjectSchema arrayType = new ObjectSchema();
		arrayType.setType(TYPE.NULL);
		schema.setItems(arrayType);
		JCodeModel codeModel = new JCodeModel();
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		assertEquals(Set.class.getName()+"<"+Object.class.getName()+">", type.fullName());
	}
	
	@Test
	public void testCreateOrGetTypeExtends() throws ClassNotFoundException{
		JCodeModel codeModel = new JCodeModel();
		ObjectSchema parent = new ObjectSchema();
		parent.setType(TYPE.OBJECT);
		parent.setName("ParentClass");
		parent.setId("org.sample."+parent.getName());
		schema.setExtends(parent);
		schema.setType(TYPE.OBJECT);
		schema.setName("ChildClass");
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		JDefinedClass def = (JDefinedClass) type;
		String classDeffString = declareToString(def);
		assertTrue(classDeffString.indexOf("extends org.sample.ParentClass") > 0);
	}
	
	@Test
	public void testCreateOrGetTypeImplements() throws ClassNotFoundException{
		JCodeModel codeModel = new JCodeModel();
		ObjectSchema parent = new ObjectSchema();
		parent.setType(TYPE.INTERFACE);
		parent.setName("ParentInterface");
		parent.setId("org.sample."+parent.getName());
		schema.setImplements(new ObjectSchema[]{parent});
		schema.setType(TYPE.OBJECT);
		schema.setName("ChildClass");
		JType type = driver.createOrGetType(codeModel, schema);
		assertNotNull(type);
		JDefinedClass def = (JDefinedClass) type;
		String classDeffString = declareToString(def);
//		System.out.println(classDeffString);
		assertTrue(classDeffString.indexOf("implements java.io.Serializable, org.sagebionetworks.schema.adapter.JSONEntity, org.sample.ParentInterface") > 0);
	}
	
	@Test
	public void testLoadedInterfaces() throws Exception{
		String[] namesToLoad = new String[]{
				"InterfaceA.json",
				"InterfaceB.json",
				"ABImpl.json",
		};
		List<ObjectSchema> schemaList = new ArrayList<ObjectSchema>();
		for(String name: namesToLoad){
			String fileString = FileUtils.loadFileAsStringFromClasspath(PojoGeneratorDriverTest.class.getClassLoader(), name);
			ObjectSchema schema = new ObjectSchema(new JSONObjectAdapterImpl(fileString));
//			schema.setName(name);
			schema.setId(schema.getName());
			schemaList.add(schema);
		}
		JCodeModel codeModel = new JCodeModel();
		driver.createAllClasses(codeModel, schemaList);
		// Get the class
		JPackage _package = codeModel._package("");
		JDefinedClass impl =  null;
		try{
			impl = _package._class("ABImpl");
		}catch (JClassAlreadyExistsException e) {
			impl = e.getExistingClass();
		} 
		String classString = declareToString(impl);
//		System.out.println(classString);
		Iterator<JClass> it = impl._implements();
		assertNotNull(it);
		String intA = "InterfaceA";
		String intB = "InterfaceB";
		String jsonEntity = "JSONEntity";
		Map<String, JClass> map = new HashMap<String, JClass>();
		while(it.hasNext()){
			JClass impClass = it.next();
			if(intA.equals(impClass.name())){
				map.put(intA, impClass);
			}else if(intB.equals(impClass.name())){
				map.put(intB, impClass);
			}else if(jsonEntity.equals(impClass.name())){
				map.put(jsonEntity, impClass);
			}
		}
		assertEquals("Should have implemented two interfaces",3, map.size());
		// Now get the fields from the object an confirm they are all there
		Map<String, JFieldVar> fields = impl.fields();
		assertNotNull(fields);
		assertEquals(6, fields.size());
		assertNotNull(fields.get("fromInterfaceA"));
		assertNotNull(fields.get("alsoFromInterfaceB"));
		assertNotNull(fields.get("fromMe"));
		assertNotNull(fields.get(ObjectSchema.EXTRA_FIELDS));
	}
	
	@Test
	public void testLoadedInterfaceNoMembers() throws Exception{
		String[] namesToLoad = new String[]{
				"InterfaceA.json",
				"AImpl.json",
		};
		List<ObjectSchema> schemaList = new ArrayList<ObjectSchema>();
		for(String name: namesToLoad){
			String fileString = FileUtils.loadFileAsStringFromClasspath(PojoGeneratorDriverTest.class.getClassLoader(), name);
			ObjectSchema schema = new ObjectSchema(new JSONObjectAdapterImpl(fileString));
			schema.setId(schema.getName());
			schemaList.add(schema);
		}
		JCodeModel codeModel = new JCodeModel();
		driver.createAllClasses(codeModel, schemaList);
		// Get the class
		JPackage _package = codeModel._package("");
		JDefinedClass impl =  null;
		try{
			impl = _package._class("AImpl");
		}catch (JClassAlreadyExistsException e) {
			impl = e.getExistingClass();
		} 
		String classString = declareToString(impl);
//		System.out.println(classString);
		Map<String, JFieldVar> fields = impl.fields();
		assertNotNull(fields);
		assertNotNull(fields.get("fromInterfaceA"));
		assertNotNull(fields.get("alsoFromInterfaceA"));
	}
	
	@Test
	public void testCreateAllClassesEnum() throws Exception{
		String[] namesToLoad = new String[]{
				"PetEnum.json",
		};
		List<ObjectSchema> schemaList = new ArrayList<ObjectSchema>();
		for(String name: namesToLoad){
			String fileString = FileUtils.loadFileAsStringFromClasspath(PojoGeneratorDriverTest.class.getClassLoader(), name);
			ObjectSchema schema = new ObjectSchema(new JSONObjectAdapterImpl(fileString));
			schema.setId(schema.getName());
			schemaList.add(schema);
		}
		JCodeModel codeModel = new JCodeModel();
		driver.createAllClasses(codeModel, schemaList);
		// Get the class
		JPackage _package = codeModel._package("");
		JDefinedClass impl =  null;
		try{
			impl = _package._class("PetEnum");
		}catch (JClassAlreadyExistsException e) {
			impl = e.getExistingClass();
		} 
		String classString = declareToString(impl);
		System.out.println(classString);
		
		Map<String, JFieldVar> fields = impl.fields();
		assertNotNull(fields);
		// Enums should have no fields
		assertEquals(0, fields.size());
		Collection<JMethod> methods = impl.methods();
		assertNotNull(methods);
		// enums should have no methods
		assertEquals(0, methods.size());
		// Enums should have no constructors
		assertFalse(impl.constructors().hasNext());
	}
	/**
	 * Helper to declare a model object to string.
	 * @param toDeclare
	 * @return
	 */
	public String declareToString(JDeclaration toDeclare){
		StringWriter writer = new StringWriter();
		JFormatter formatter = new JFormatter(writer);
		toDeclare.declare(formatter);
		return writer.toString();
	}
	
}
