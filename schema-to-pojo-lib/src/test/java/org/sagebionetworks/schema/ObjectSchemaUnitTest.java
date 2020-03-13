package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Unit tests for ObjectSchema.
 * @author jmhill
 *
 */
public class ObjectSchemaUnitTest {
	
	@Test
	public void testSubSchemaIterator() {
		Map<String, ObjectSchema> expectedMap = new HashMap<String, ObjectSchema>();
		// The root schema to put objects into
		ObjectSchema root = new ObjectSchema();
		// Now add some sub-schema
		
		// Add a property sub-schema
		String string = new String("one");
		ObjectSchema sub = new ObjectSchema();
		sub.setId(string);
		expectedMap.put(string, sub);
		root.putProperty("someKey", sub);
		
		// Add an additionalProperty sub-schema
		string = new String("two");
		sub = new ObjectSchema();
		sub.setId(string);
		expectedMap.put(string, sub);
		root.putAdditionalProperty("addKey", sub);
		
		// Add an item sub
		string = new String("three");
		sub = new ObjectSchema();
		sub.setId(string);
		expectedMap.put(string, sub);
		root.setItems(sub);
		
		// Add an additional item
		string = new String("four");
		sub = new ObjectSchema();
		sub.setId(string);
		expectedMap.put(string, sub);
		root.setAdditionalItems(sub);
		
		// Extends
		string = new String("five");
		sub = new ObjectSchema();
		sub.setId(string);
		expectedMap.put(string, sub);
		root.setExtends(sub);
		
		// Implements
		string = new String("six");
		sub = new ObjectSchema();
		sub.setId(string);
		expectedMap.put(string, sub);
		root.setImplements(new ObjectSchema[]{sub});
		
		
		//Now make sure we find all item with the iterator
		Iterator<ObjectSchema> it = root.getSubSchemaIterator();
		assertNotNull(it);
		// Now check each value
		while(it.hasNext()){
			ObjectSchema subFromIt = it.next();
			assertNotNull(subFromIt);
			assertNotNull(subFromIt.getId());
			ObjectSchema fromMap = expectedMap.remove(subFromIt.getId());
			assertNotNull("The iterator had an unexpected sub-schema", fromMap);
			assertEquals(fromMap, subFromIt);
		}
		// If all items were found the map should be empty
		assertEquals("The iterator missed: "+expectedMap.size()+" sub-scheams", 0, expectedMap.size());
	}
	
	@Test
	public void testGetObjectFieldMap(){
		// First create some Interface hierarchy
		ObjectSchema baseInterface = new ObjectSchema();
		baseInterface.setType(TYPE.INTERFACE);
		baseInterface.setName("BaseInterface");
		baseInterface.putProperty("stringFromBase", new ObjectSchema(TYPE.STRING));
		
		// Now add one more implementation
		ObjectSchema implementsBase = new ObjectSchema();
		implementsBase.setType(TYPE.INTERFACE);
		implementsBase.setName("BaseImpl");
		implementsBase.putProperty("longFromBaseImpl", new ObjectSchema(TYPE.INTEGER));
		implementsBase.setImplements(new ObjectSchema[]{baseInterface});
		
		// Now add a class that implements both
		ObjectSchema implImpl = new ObjectSchema();
		implImpl.setType(TYPE.OBJECT);
		implImpl.setName("ImplImpl");
		implImpl.putProperty("doubleFromImplImpl", new ObjectSchema(TYPE.NUMBER));
		implImpl.setImplements(new ObjectSchema[]{implementsBase});
		
		// Get the field map
		Map<String, ObjectSchema> fieldMap = implImpl.getObjectFieldMap();
		assertNotNull(fieldMap);
		assertEquals(3, fieldMap.size());
		assertTrue(fieldMap.containsKey("stringFromBase"));
		assertTrue(fieldMap.containsKey("longFromBaseImpl"));
		assertTrue(fieldMap.containsKey("doubleFromImplImpl"));
		
	}
	
	@Test
	public void testGetObjectFieldMapDuplicate(){
		// First create some Interface hierarchy
		ObjectSchema baseInterface = new ObjectSchema();
		baseInterface.setType(TYPE.INTERFACE);
		baseInterface.setName("BaseInterface");
		baseInterface.putProperty("definedInBase", new ObjectSchema(TYPE.STRING));
		
		// Now add one more implementation
		ObjectSchema implementsBase = new ObjectSchema();
		implementsBase.setType(TYPE.INTERFACE);
		implementsBase.setName("BaseImpl");
		implementsBase.putProperty("longFromBaseImpl", new ObjectSchema(TYPE.INTEGER));
		implementsBase.setImplements(new ObjectSchema[]{baseInterface});
		
		// Now add a class that implements both
		ObjectSchema implImpl = new ObjectSchema();
		implImpl.setType(TYPE.OBJECT);
		implImpl.setName("ImplImpl");
		implImpl.putProperty("definedInBase", new ObjectSchema(TYPE.NUMBER));
		implImpl.setImplements(new ObjectSchema[]{implementsBase});
		
		// Get the field map
		Map<String, ObjectSchema> fieldMap = implImpl.getObjectFieldMap();
		assertNotNull(fieldMap);
		assertEquals(2, fieldMap.size());
		assertTrue(fieldMap.containsKey("definedInBase"));
		assertTrue(fieldMap.containsKey("longFromBaseImpl"));
		
		// For this case we want to use the wrong type from the ImplImpl
		// because it will lead to a compile time error when the interface
		// contract is not met.
		assertEquals(TYPE.NUMBER,fieldMap.get("definedInBase").getType());
		
	}

	@Test
	public void testGetPackageName(){
		ObjectSchema schema = new ObjectSchema();
		schema.setName("SampleClass");
		schema.setId("org.sample."+schema.getName());
		String packageName = schema.getPackageName();
		assertNotNull(packageName);
		assertEquals("org.sample", packageName);
	}
	
	@Test
	public void testGetPackageNameDefault(){
		ObjectSchema schema = new ObjectSchema();
		schema.setName("SampleClass");
		schema.setId(""+schema.getName());
		String packageName = schema.getPackageName();
		assertNotNull(packageName);
		assertEquals("", packageName);
	}
	
	@Test
	public void testIterationOrder(){
		ObjectSchema schema = new ObjectSchema();
		schema.setName("SampleClass");
		schema.putProperty("0", new ObjectSchema(TYPE.STRING));
		schema.putProperty("1", new ObjectSchema(TYPE.STRING));
		schema.putProperty("2", new ObjectSchema(TYPE.STRING));
		// Iterate over the properties
		Iterator<String> keyIt =schema.getProperties().keySet().iterator();
		int index = 0;
		while(keyIt.hasNext()){
			int key = Integer.parseInt(keyIt.next());
			assertEquals("The iteration order of the schema must be consistent with the order they are added.",index, key);
			index++;
		}
	}
	

	
}
