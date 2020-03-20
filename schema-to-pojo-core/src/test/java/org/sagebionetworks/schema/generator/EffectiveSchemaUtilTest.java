package org.sagebionetworks.schema.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sagebionetworks.schema.EnumValue;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.ObjectSchemaImpl;
import org.sagebionetworks.schema.TYPE;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class EffectiveSchemaUtilTest {
	
	ObjectSchema baseClassSchema;
	ObjectSchema childClassSchema;
	ObjectSchema interfaceSchema;
	ObjectSchema childInterface;
	ObjectSchema compositeSchema;
	ObjectSchema enumSchema;
	
	File tempFolder;
	
	@Before
	public void before() throws IOException{
		// A base class.
		baseClassSchema = new ObjectSchemaImpl();
		baseClassSchema.setType(TYPE.OBJECT);
		baseClassSchema.setName("BaseClass");
		baseClassSchema.setId("org.sample.BaseClass");
		baseClassSchema.putProperty("fromBase", new ObjectSchemaImpl(TYPE.STRING));
		
		childClassSchema = new ObjectSchemaImpl();
		childClassSchema.setType(TYPE.OBJECT);
		childClassSchema.setName("ChildClass");
		childClassSchema.setId("org.sample.ChildClass");
		childClassSchema.putProperty("fromChild", new ObjectSchemaImpl(TYPE.NUMBER));
		childClassSchema.setExtends(baseClassSchema);
		
		// An interface
		interfaceSchema = new ObjectSchemaImpl();
		interfaceSchema.setType(TYPE.INTERFACE);
		interfaceSchema.setName("InterfaceSchema");
		interfaceSchema.setId("org.sample.InterfaceSchema");
		interfaceSchema.putProperty("fromInterface", new ObjectSchemaImpl(TYPE.BOOLEAN));
		
		childInterface = new ObjectSchemaImpl();
		childInterface.setType(TYPE.INTERFACE);
		childInterface.setName("ChildInterfaceSchema");
		childInterface.setId("org.sample.ChildInterfaceSchema");
		childInterface.putProperty("fromChildInterface", new ObjectSchemaImpl(TYPE.STRING));
		childInterface.setImplements(new ObjectSchema[]{interfaceSchema});
		
		// Create a schema that uses both
		compositeSchema = new ObjectSchemaImpl();
		compositeSchema.setType(TYPE.OBJECT);
		compositeSchema.setName("CompositeSchema");
		compositeSchema.setId("org.sample.CompositeSchema");
		compositeSchema.putProperty("fromMe", new ObjectSchemaImpl(TYPE.INTEGER));
		// It extends the child
		compositeSchema.setExtends(childClassSchema);
		// It implements the child interface
		compositeSchema.setImplements(new ObjectSchema[]{childInterface});
		
		enumSchema = new ObjectSchemaImpl();
		enumSchema.setName("EnEnum");
		enumSchema.setEnum(new EnumValue[] {
				new EnumValue("a", "a description"),
				new EnumValue("b", "b description")
		});
		enumSchema.setProperties(new LinkedHashMap<String, ObjectSchema>());
		
		tempFolder = FileUtils.createTempDirectory("testfolder");	
		tempFolder.deleteOnExit();
	}
	
	@After
	public void after() {
		if(tempFolder != null) {
			FileUtils.recursivelyDeleteDirectory(tempFolder);
		}
	}

	@Test
	public void testGenerateEffectiveSchema_classWithNoPropertiesImplementInterface() throws JSONObjectAdapterException {
		ObjectSchema noPropertiesImplementation = new ObjectSchemaImpl();
		noPropertiesImplementation.setImplements(new ObjectSchema[]{interfaceSchema});

		ObjectSchema effective = EffectiveSchemaUtil.generateEffectiveSchema(noPropertiesImplementation);
		assertTrue(effective.getProperties().containsKey("fromInterface"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGenerateEffectiveSchemaNull() throws JSONObjectAdapterException{
		// Create a schema
		EffectiveSchemaUtil.generateEffectiveSchema(null);
	}

	@Test
	public void testGenerateEffectiveSchema() throws JSONObjectAdapterException{
		// Create a schema
		ObjectSchema effective = EffectiveSchemaUtil.generateEffectiveSchema(compositeSchema);
		checkEffectiveCompositeSchemaIsFlattened(effective);
	}

	@Test
	public void testGenerateEffectiveSchema_InterfaceImplementerInsidePropertyField() throws JSONObjectAdapterException{

		ObjectSchema fieldCompositeSchema = new ObjectSchemaImpl(TYPE.OBJECT);
		fieldCompositeSchema.putProperty("field", compositeSchema);

		ObjectSchema effective = EffectiveSchemaUtil.generateEffectiveSchema(fieldCompositeSchema)
				.getProperties().get("field");
		checkEffectiveCompositeSchemaIsFlattened(effective);
	}

	@Test
	public void testGenerateEffectiveSchema_InterfaceImplementerInsideARRAYProperty() throws JSONObjectAdapterException{

		ObjectSchema arraySchema = new ObjectSchemaImpl(TYPE.ARRAY);
		arraySchema.setItems(compositeSchema);

		ObjectSchema arrayCompositeSchema = new ObjectSchemaImpl(TYPE.OBJECT);
		arrayCompositeSchema.putProperty("array", arraySchema);

		ObjectSchema effective = EffectiveSchemaUtil.generateEffectiveSchema(arrayCompositeSchema)
				.getProperties().get("array").getItems();
		checkEffectiveCompositeSchemaIsFlattened(effective);
	}

	@Test
	public void testGenerateEffectiveSchema_InterfaceImplementerInsideMAPProperty() throws JSONObjectAdapterException{
		ObjectSchema stringKeySchema = new ObjectSchemaImpl(TYPE.STRING);

		ObjectSchema mapSchema = new ObjectSchemaImpl(TYPE.TUPLE_ARRAY_MAP);
		mapSchema.setKey(stringKeySchema);
		mapSchema.setValue(compositeSchema);

		ObjectSchema mapCompositeSchema = new ObjectSchemaImpl(TYPE.OBJECT);
		mapCompositeSchema.putProperty("map", mapSchema);

		//check that the effective schema for compositeSchema got flattened even though it is nested
		ObjectSchema effective = EffectiveSchemaUtil.generateEffectiveSchema(mapCompositeSchema)
				.getProperties().get("map").getValue();
		checkEffectiveCompositeSchemaIsFlattened(effective);
	}

	@Test
	public void testGenerateEffectiveSchema_InterfaceImplementerInsideMAP() throws JSONObjectAdapterException{
		ObjectSchema stringKeySchema = new ObjectSchemaImpl(TYPE.STRING);

		ObjectSchema mapSchema = new ObjectSchemaImpl(TYPE.TUPLE_ARRAY_MAP);
		mapSchema.setKey(stringKeySchema);
		mapSchema.setValue(compositeSchema);

		//check that the effective schema for compositeSchema got flattened even though it is nested
		ObjectSchema effective = EffectiveSchemaUtil.generateEffectiveSchema(mapSchema)
				.getValue();
		checkEffectiveCompositeSchemaIsFlattened(effective);
	}

	@Test
	public void testGenerateEffectiveSchema_InterfaceImplementerInsideArray() throws JSONObjectAdapterException{
		ObjectSchema arraySchema = new ObjectSchemaImpl(TYPE.ARRAY);
		arraySchema.setItems(compositeSchema);

		//check that the effective schema for compositeSchema got flattened even though it is nested
		ObjectSchema effective = EffectiveSchemaUtil.generateEffectiveSchema(arraySchema)
				.getItems();
		checkEffectiveCompositeSchemaIsFlattened(effective);
	}


	private void checkEffectiveCompositeSchemaIsFlattened(ObjectSchema effectiveCompositeSchema) {
		assertNotNull(effectiveCompositeSchema);
		// It should make a copy of the schema
		assertNotSame(effectiveCompositeSchema, compositeSchema);
		// The effective should not have extends or implements
		assertNull(effectiveCompositeSchema.getExtends());
		assertNull(effectiveCompositeSchema.getImplements());
		// Get the properties
		Map<String, ObjectSchema> props = effectiveCompositeSchema.getProperties();
		assertNotNull(props);
		// All of the properties from the base class and interfaces should be in this object
		assertEquals(5, props.size());
		assertNotNull(props.get("fromBase"));
		assertNotNull(props.get("fromChild"));
		assertNotNull(props.get("fromInterface"));
		assertNotNull(props.get("fromChildInterface"));
		assertNotNull(props.get("fromMe"));
	}


	@Test (expected=IllegalArgumentException.class)
	public void testGenerateJSONofEffectiveSchemaNull() throws JSONObjectAdapterException{
		// Create a schema
		EffectiveSchemaUtil.generateJSONofEffectiveSchema(null);
	}
	
	@Test
	public void testGenerateJSONofEffectiveSchema() throws JSONObjectAdapterException{
		String json = EffectiveSchemaUtil.generateJSONofEffectiveSchema(compositeSchema);
		assertNotNull(json);
//		System.out.println(json);
		// Get the object
		ObjectSchema effective = EffectiveSchemaUtil.generateEffectiveSchema(compositeSchema);
		assertNotNull(effective);
		// Create a clone from the json
		ObjectSchema clone = new ObjectSchemaImpl(new JSONObjectAdapterImpl(json));
		assertNotNull(clone);
		assertEquals(effective, clone);
	}
	
	@Test
	public void testGenerateJSONofEffectiveSchemaWithEnum() throws JSONObjectAdapterException {
		String json = EffectiveSchemaUtil.generateJSONofEffectiveSchema(enumSchema);
		assertNotNull(json);
		System.out.println(json);
		
		// Create a clone from the json
		ObjectSchema clone = new ObjectSchemaImpl(new JSONObjectAdapterImpl(json));
		assertNotNull(clone);
		assertEquals(enumSchema, clone);
	}
	
	@Test
	public void testCreateFileNameForSchema() throws IOException {
		ObjectSchema schema = new ObjectSchemaImpl(TYPE.OBJECT);
		schema.setId("org.sample.FooBar");
		// call under test
		File result = EffectiveSchemaUtil.createFileForSchema(tempFolder, schema);
		assertEquals("FooBar-effective.json", result.getName());
		assertFalse(result.exists());
		// the full path must exist
		File orgFolder = new File(tempFolder, "org");
		assertTrue(orgFolder.exists());
		assertTrue(orgFolder.isDirectory());
		File sample = new File(orgFolder, "sample");
		assertTrue(sample.exists());
		assertTrue(sample.isDirectory());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateFileNameForSchemaNullSchema() throws IOException {
		ObjectSchema schema = null;
		// call under test
		EffectiveSchemaUtil.createFileForSchema(tempFolder, schema);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCreateFileNameForSchemaNullId() throws IOException {
		ObjectSchema schema = new ObjectSchemaImpl(TYPE.OBJECT);
		schema.setId(null);
		// call under test
		EffectiveSchemaUtil.createFileForSchema(tempFolder, schema);
	}
	
	@Test
	public void testGenerateEffectiveSchemaFile() throws Exception {
		// call under test
		File result = EffectiveSchemaUtil.generateEffectiveSchemaFile(tempFolder, compositeSchema);
		assertNotNull(result);
		assertTrue(result.exists());
		assertTrue(result.isFile());
		String jsonString = FileUtils.readToString(result);
		assertNotNull(jsonString);
		ObjectSchema expected = new ObjectSchemaImpl(new JSONObjectAdapterImpl(EffectiveSchemaUtil.generateJSONofEffectiveSchema(compositeSchema)));
		ObjectSchema clone = new ObjectSchemaImpl(new JSONObjectAdapterImpl(jsonString));
		assertEquals(expected, clone);
	}
	
}
