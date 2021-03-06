package org.sagebionetworks.schema.generator;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.ObjectSchemaImpl;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.schema.generator.handler.schema03.HandlerFactoryImpl03;

public class PojoGeneratorDriverTest {
	
	PojoGeneratorDriver driver = null;
	ObjectSchema schema;
	Stack<ObjectSchema> recursiveAnchors;
	
	@BeforeEach
	public void before(){
		driver = new PojoGeneratorDriver(new HandlerFactoryImpl03());
		schema = new ObjectSchemaImpl();
		schema.setName("SampleClass");
		schema.setId("org.sample."+schema.getName());
		recursiveAnchors = new Stack<ObjectSchema>();
	}
	
	@Test
	public void testRegisterAllIdentifiedObjectSchemasDuplicate() {
		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		// Create a duplicate
		list.add(ObjectSchemaImpl.createNewWithId("one"));
		list.add(ObjectSchemaImpl.createNewWithId("one"));

		assertThrows(IllegalArgumentException.class, ()->
			// This should fail due to the duplicate
			PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list)
		);
	}
	
	@Test
	public void testRegisterAllIdentifiedObjectSchemasNestedDuplicate() {
		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		// Create a duplicate
		ObjectSchema root1 = ObjectSchemaImpl.createNewWithId("rootOne");
		list.add(root1);
		ObjectSchema root2 = ObjectSchemaImpl.createNewWithId("rootTwo");
		list.add(root2);
		// Add a child to each with a duplicate name
		root1.setItems(ObjectSchemaImpl.createNewWithId("child"));
		// Add a child to each with a duplicate name
		root2.setItems(ObjectSchemaImpl.createNewWithId("child"));
		assertThrows(IllegalArgumentException.class, ()->
			// This should fail due to the duplicate
			PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list)
		);
	}
	
	@Test 
	public void testRegisterAllIdentifiedObjectSchemasNested() {
		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		// Create a duplicate
		ObjectSchema root1 = ObjectSchemaImpl.createNewWithId("rootOne");
		list.add(root1);
		ObjectSchema root2 = ObjectSchemaImpl.createNewWithId("rootTwo");
		list.add(root2);
		// Add a child to each with a unique name
		root1.setItems(ObjectSchemaImpl.createNewWithId("child1"));
		// Add a child to each with a unique name
		root2.setItems(ObjectSchemaImpl.createNewWithId("child2"));
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
		ObjectSchema root1 = ObjectSchemaImpl.createNewWithId("rootOne");
		ObjectSchema replaced = PojoGeneratorDriver.replaceReference(new HashMap<String, ObjectSchema>(), root1, null);
		assertEquals(root1, replaced);
	}
	
	@Test
	public void testReplaceRefrenceSelf() {
		// This is not a reference so the replace should just return it.
		ObjectSchema self = ObjectSchemaImpl.createNewWithId("rootOne");
		self.setRef(ObjectSchemaImpl.SELF_REFERENCE);
		
		ObjectSchema replaced = PojoGeneratorDriver.replaceReference(new HashMap<String, ObjectSchema>(), self, recursiveAnchors);
		// Should be replaced with self
		assertEquals(self, replaced);
	}
	
	@Test
	public void testReplaceRefrenceRegistry() {
		// This is not a reference so the replace should just return it.
		String referenceId = "rootOne";
		ObjectSchema referenced = ObjectSchemaImpl.createNewWithId(referenceId);
		HashMap<String, ObjectSchema> registry = new HashMap<String, ObjectSchema>();
		// Add the referenced schema to the register.
		registry.put(referenceId, referenced);
		// Create a self self reference
		ObjectSchema referenceToOther = new ObjectSchemaImpl();
		referenceToOther.setRef(referenceId);
		
		// Create a third self
		ObjectSchema self = ObjectSchemaImpl.createNewWithId("self");
		
		ObjectSchema replaced = PojoGeneratorDriver.replaceReference(registry, referenceToOther, recursiveAnchors);
		// Should be replaced with referenced
		assertEquals(referenced, replaced);
	}
	
	@Test
	public void testReplaceRefrenceMissRegistry() {
		// This is not a reference so the replace should just return it.
		String referenceId = new String("rootOne");
		// This time the referenced is not in the register
		HashMap<String, ObjectSchema> registry = new HashMap<String, ObjectSchema>();
		// Create a self self reference
		ObjectSchema referenceToOther = new ObjectSchemaImpl();
		referenceToOther.setRef(referenceId);
		
		// Create a third self
		ObjectSchema self = ObjectSchemaImpl.createNewWithId(new String("self"));
		assertThrows(IllegalArgumentException.class, ()->
			// This should fail since the referenced is not in the register
			PojoGeneratorDriver.replaceReference(registry, referenceToOther, recursiveAnchors)
		);
	}
	
	@Test
	public void testFindAndReplaceAllReferencesSchemas() {
		// Build up a map with one reference and one not
		// This is not a reference so the replace should just return it.
		String referenceId = new String("rootOne");
		ObjectSchema referenced = ObjectSchemaImpl.createNewWithId(referenceId);
		HashMap<String, ObjectSchema> registry = new HashMap<String, ObjectSchema>();
		// Add the referenced schema to the register.
		registry.put(referenceId, referenced);
		// Create a self self reference
		ObjectSchema referenceToOther = new ObjectSchemaImpl();
		referenceToOther.setRef(referenceId);
		
		// Create a third self
		ObjectSchema self = ObjectSchemaImpl.createNewWithId(new String("self"));
		self.setRef(new String(ObjectSchemaImpl.SELF_REFERENCE));
		// Now add all three to the a map
		HashMap<String, ObjectSchema> map = new HashMap<String, ObjectSchema>();
		map.put("one", referenced);
		map.put("two", referenceToOther);
		map.put("three", self);
		
		Map<String, ObjectSchema> results = PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(registry,map, recursiveAnchors);
		assertNotNull(results);
		assertEquals(3, results.size());
		assertEquals(referenced, results.get("one"));
		assertEquals(referenced, results.get("two"));
		assertEquals(self, results.get("three"));
	}
	
	@Test
	public void testFindAndReplaceAllReferencesSchemasFull(){
		String referenceId = new String("rootOne");
		ObjectSchema referenced = ObjectSchemaImpl.createNewWithId(referenceId);
		HashMap<String, ObjectSchema> registry = new HashMap<String, ObjectSchema>();
		// Add the referenced schema to the register.
		registry.put(referenceId, referenced);
		// Create a third self
		ObjectSchema self = ObjectSchemaImpl.createNewWithId(new String("self"));
		self.setRef(new String(ObjectSchemaImpl.SELF_REFERENCE));
		
		ObjectSchema referenceToOther = new ObjectSchemaImpl();
		referenceToOther.setRef(referenceId);
		
		// Add references in all places
		self.putProperty("one", referenceToOther);
		self.putAdditionalProperty("two", referenceToOther);
		self.setItems(referenceToOther);
		self.setAdditionalItems(referenceToOther);
		self.setImplements(new ObjectSchema[]{referenceToOther});
		
		List<ObjectSchema> listToCheck = new ArrayList<ObjectSchema>();
		listToCheck.add(self);
		
		// find and replace
		PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(registry, listToCheck);
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
		ObjectSchema root = new ObjectSchemaImpl();
		root.setName("Root");
		root.setId(new String("root"));
		// Create a child class
		ObjectSchema child = new ObjectSchemaImpl();
		child.setName("Child");
		child.setType(TYPE.OBJECT);
		root.putProperty("childInstance1", child);
		// Create a grand child
		ObjectSchema grand = new ObjectSchemaImpl();
		grand.setName("Grand");
		grand.setType(TYPE.OBJECT);
		String grandId = new String("grand");
		grand.setId(grandId);
		child.putProperty("grandChildInstance1", grand);
		ObjectSchema grandRef = new ObjectSchemaImpl();
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
		ObjectSchema root = new ObjectSchemaImpl();
		root.setName("Root");
		root.setId(new String("root"));
		// Create a child class
		ObjectSchema child = new ObjectSchemaImpl();
		String childId = new String("child");
		child.setName("Child");
		child.setId(childId);
		child.setType(TYPE.OBJECT);
		root.putProperty("childInstance1", child);
		// Add a self reference child
		ObjectSchema childSelf = new ObjectSchemaImpl();
		childSelf.setRef(new String(ObjectSchemaImpl.SELF_REFERENCE));
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
		ObjectSchema inter = new ObjectSchemaImpl();
		inter.setName("SomeInterface");
		inter.setType(TYPE.INTERFACE);
		inter.setId("example.org.SomeInterface");
		
		ObjectSchema interRef = new ObjectSchemaImpl();
		interRef.setRef(inter.getId());
		
		ObjectSchema impl = new ObjectSchemaImpl();
		impl.setName("SomeInterfaceImpl");
		impl.setType(TYPE.OBJECT);
		impl.setId("example.org.SomeInterfaceImpl");
		impl.setImplements(new ObjectSchema[]{interRef});
		
		ObjectSchema root = new ObjectSchemaImpl();
		root.setName("Root");
		root.setId(new String("root"));
		root.setType(TYPE.OBJECT);

		// Create a child class
		ObjectSchema child = new ObjectSchemaImpl();
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
		ObjectSchema root = new ObjectSchemaImpl();
		root.setName("Root");
		String rootId = new String("root");
		root.setId(rootId);
		// Create a child class
		ObjectSchema child = new ObjectSchemaImpl();
		String childId = new String("child");
		child.setName("Child");
		child.setId(childId);
		child.setType(TYPE.OBJECT);
		root.putProperty("childInstance1", child);
		// Add a self reference child
		ObjectSchema rootRef = new ObjectSchemaImpl();
		rootRef.setRef(rootId);
		child.putProperty("rootRef", rootRef);
		// Create a grand child

		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		list.add(root);
		Map<String, ObjectSchema> register = PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list);
		PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(register, list);
	}
	
	@Test
	public void testFindAndReplaceAllReferencesSchemas_MultipleLevelsOfRecursion() throws JSONObjectAdapterException {
		// Create an object with nesting
		ObjectSchema root = new ObjectSchemaImpl();
		root.setId("root");
		root.set$recursiveAnchor(true);

		// First property is a list of type 'root'.
		ObjectSchema arrayOfRoots = new ObjectSchemaImpl();
		arrayOfRoots.setType(TYPE.ARRAY);
		ObjectSchema arrayOfRootsItems = new ObjectSchemaImpl();
		arrayOfRootsItems.set$recursiveRef("#");
		arrayOfRoots.setItems(arrayOfRootsItems);
		root.putProperty("listOfRoots", arrayOfRoots);
		
		ObjectSchema child = new ObjectSchemaImpl();
		child.setId("child");
		child.set$recursiveAnchor(true);
		// sibling is of the same type as child.
		ObjectSchema sibling = new ObjectSchemaImpl();
		sibling.set$recursiveRef("#");
		child.putProperty("sibling", sibling);
		
		root.putProperty("childWithSiblings", child);
		
		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		list.add(root);

		Map<String, ObjectSchema> register = PojoGeneratorDriver.registerAllIdentifiedObjectSchemas(list);
		// call under test
		list = PojoGeneratorDriver.findAndReplaceAllReferencesSchemas(register, list);
		assertNotNull(list);
		assertEquals(1, list.size());
		ObjectSchema newRoot = list.get(0);
		assertEquals(newRoot.getId(), "root");
		assertFalse(newRoot.is$RecursiveRefInstance());
		ObjectSchema listOfRoots = newRoot.getProperties().get("listOfRoots");
		assertNotNull(listOfRoots);
		assertNotNull(listOfRoots.getItems());
		// recursive reference should be replaced with root.
		assertEquals("root", listOfRoots.getItems().getId());
		assertTrue(listOfRoots.getItems().is$RecursiveRefInstance());
		
		ObjectSchema childWithSiblings = newRoot.getProperties().get("childWithSiblings");
		assertNotNull(childWithSiblings);
		sibling = childWithSiblings.getProperties().get("sibling");
		assertNotNull(sibling);
		// recursive reference should be replaced with child (not root).
		assertEquals("child", sibling.getId());
		assertTrue(sibling.is$RecursiveRefInstance());
	}

	@Test
	public void testRecursivlyCreateAllTypesNumber() throws ClassNotFoundException{
		ObjectSchema schema = new ObjectSchemaImpl();
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
	
	@Test
	public void testRecursivlyCreateAllTypesArrayNoType() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		JCodeModel codeModel = new JCodeModel();
		JPackage _package = codeModel._package("org.sample");
		assertThrows(IllegalArgumentException.class, ()->
			// should fail since the array type is not set
			driver.createOrGetType(codeModel, schema)
		);
	}
	
	@Test
	public void testRecursivlyCreateAllTypesArrayString() throws ClassNotFoundException{
		schema.setType(TYPE.ARRAY);
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema arrayType = new ObjectSchemaImpl();
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
		ObjectSchema parent = new ObjectSchemaImpl();
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
		ObjectSchema parent = new ObjectSchemaImpl();
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
			String fileString = FileHelper.loadFileAsStringFromClasspath(PojoGeneratorDriverTest.class.getClassLoader(), name);
			ObjectSchema schema = new ObjectSchemaImpl(new JSONObjectAdapterImpl(fileString));
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
		System.out.println(classString);
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
		assertEquals(3, map.size(),"Should have implemented two interfaces");
		// Now get the fields from the object an confirm they are all there
		Map<String, JFieldVar> fields = impl.fields();
		assertNotNull(fields);
		assertEquals(11, fields.size());
		assertNotNull(fields.get("fromInterfaceA"));
		assertNotNull(fields.get("alsoFromInterfaceB"));
		assertNotNull(fields.get("fromMe"));
	}
	
	@Test
	public void testLoadedInterfaceNoMembers() throws Exception{
		String[] namesToLoad = new String[]{
				"InterfaceA.json",
				"AImpl.json",
		};
		List<ObjectSchema> schemaList = new ArrayList<ObjectSchema>();
		for(String name: namesToLoad){
			String fileString = FileHelper.loadFileAsStringFromClasspath(PojoGeneratorDriverTest.class.getClassLoader(), name);
			ObjectSchema schema = new ObjectSchemaImpl(new JSONObjectAdapterImpl(fileString));
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
			String fileString = FileHelper.loadFileAsStringFromClasspath(PojoGeneratorDriverTest.class.getClassLoader(), name);
			ObjectSchema schema = new ObjectSchemaImpl(new JSONObjectAdapterImpl(fileString));
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
	
	@Test
	public void testRecursive() throws Exception{
		ObjectSchema root = new ObjectSchemaImpl();
		root.setType(TYPE.OBJECT);
		root.setId(new String("root"));
		root.set$recursiveAnchor(true);
		
		ObjectSchema refToRoot = new ObjectSchemaImpl();
		refToRoot.set$recursiveRef("#");
		
		ObjectSchema array = new ObjectSchemaImpl(TYPE.ARRAY);
		array.setItems(refToRoot);
		
		root.putProperty("listOfRecursive", array);

		List<ObjectSchema> list = new ArrayList<ObjectSchema>();
		list.add(root);
		JCodeModel codeModel = new JCodeModel();
		
		driver.createAllClasses(codeModel, list);
		// Get the class
		JPackage _package = codeModel._package("");
		JDefinedClass impl =  null;
		try{
			impl = _package._class("Recursive");
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
	
	@Test
	public void testCreateRecurisveInstanceCopy() {
		ObjectSchema toCopy = new ObjectSchemaImpl();
		toCopy.setId("recursive");
		assertFalse(toCopy.is$RecursiveRefInstance());
		// call under test
		ObjectSchema clone = PojoGeneratorDriver.createRecurisveInstanceCopy(toCopy);
		assertNotNull(clone);
		assertEquals(toCopy.getId(), clone.getId());
		assertTrue(clone.is$RecursiveRefInstance());
	}
	
	@Test
	public void testcreateAllClassesWithDefaultConcreteType() throws ClassNotFoundException {
		
		List<ObjectSchema> schemas = new ArrayList<>();
		
		ObjectSchema interfaceWithDefault = new ObjectSchemaImpl();
		interfaceWithDefault.setType(TYPE.INTERFACE);
		interfaceWithDefault.setName("InterfaceWithDefaultConcreteType");
		interfaceWithDefault.setId("org.example." + interfaceWithDefault.getName());
		interfaceWithDefault.setDefaultConcreteType("org.example.DefaultImplementation");
		
		ObjectSchema interfaceRef = new ObjectSchemaImpl();
		interfaceRef.setRef(interfaceWithDefault.getId());
		
		ObjectSchema defaultImplementation = new ObjectSchemaImpl();
		defaultImplementation.setType(TYPE.OBJECT);
		defaultImplementation.setName("DefaultImplementation");
		defaultImplementation.setId("org.example." + defaultImplementation.getName());
		defaultImplementation.setImplements(new ObjectSchema[] {interfaceRef});
		
		schemas.add(interfaceWithDefault);
		schemas.add(defaultImplementation);
		
		JCodeModel codeModel = new JCodeModel();
		
		// Call under test
		driver.createAllClasses(codeModel, schemas);
	}
	
	@Test
	public void testcreateAllClassesWithDefaultConcreteTypeNonExistingImpl() throws ClassNotFoundException {
		
		List<ObjectSchema> schemas = new ArrayList<>();
		
		ObjectSchema interfaceWithDefault = new ObjectSchemaImpl();
		interfaceWithDefault.setType(TYPE.INTERFACE);
		interfaceWithDefault.setName("InterfaceWithDefaultConcreteType");
		interfaceWithDefault.setId("org.example." + interfaceWithDefault.getName());
		interfaceWithDefault.setDefaultConcreteType("org.example.DefaultImplementation");
		
		schemas.add(interfaceWithDefault);
		
		JCodeModel codeModel = new JCodeModel();
		
		String errorMessage = assertThrows(IllegalStateException.class, () -> {			
			// Call under test
			driver.createAllClasses(codeModel, schemas);
		}).getMessage();
		
		assertEquals("The schema of the defaultConcreteType org.example.DefaultImplementation defined on the InterfaceWithDefaultConcreteType interface is not defined", errorMessage);
	}
	
	@Test
	public void testcreateAllClassesWithDefaultConcreteTypeNonImplementingInterface() throws ClassNotFoundException {
		
		List<ObjectSchema> schemas = new ArrayList<>();
		
		ObjectSchema interfaceWithDefault = new ObjectSchemaImpl();
		interfaceWithDefault.setType(TYPE.INTERFACE);
		interfaceWithDefault.setName("InterfaceWithDefaultConcreteType");
		interfaceWithDefault.setId("org.example." + interfaceWithDefault.getName());
		interfaceWithDefault.setDefaultConcreteType("org.example.DefaultImplementation");
		
		// Define an existing implementation, but it does not implement the given interface
		ObjectSchema defaultImplementation = new ObjectSchemaImpl();
		defaultImplementation.setType(TYPE.OBJECT);
		defaultImplementation.setName("DefaultImplementation");
		defaultImplementation.setId("org.example." + defaultImplementation.getName());
		
		schemas.add(interfaceWithDefault);
		schemas.add(defaultImplementation);
		
		JCodeModel codeModel = new JCodeModel();
		
		String errorMessage = assertThrows(IllegalStateException.class, () -> {			
			// Call under test
			driver.createAllClasses(codeModel, schemas);
		}).getMessage();
		
		assertEquals("The defaultConcreteType org.example.DefaultImplementation does not implement the interface org.example.InterfaceWithDefaultConcreteType", errorMessage);
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
