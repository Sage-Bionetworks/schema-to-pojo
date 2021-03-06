package org.sagebionetworks.schema.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.generator.handler.HandlerFactory;
import org.sagebionetworks.schema.generator.handler.schema03.HandlerFactoryImpl03;

public class SchemaToPojoTest {

	File outputDir;

	@Test
	public void testExtractSchemaNameFromFileName() {
		File file = new File("someFileName.JSON");
		String result = SchemaToPojo.extractSchemaNameFromFileName(file);
		assertNotNull(result);
		assertEquals("SomeFileName", result);
	}

	@BeforeEach
	public void before() throws IOException {
		// Create a temp directory for output
		outputDir = FileUtils.createTempDirectory("output");
	}

	@AfterEach
	public void after() {
		// Delete the output directory
		FileUtils.recursivelyDeleteDirectory(outputDir);
		assertFalse(outputDir.exists());
	}

	@Test
	public void loadSingleFile() throws IOException,
			JSONObjectAdapterException, ClassNotFoundException {
		// Load form the sample file
		File sampleFile = new File("src/test/resources/ExampleSchema.json");
		assertTrue(sampleFile.exists(), "Test file does not exist: " + sampleFile.getAbsolutePath());
		// Create the class
		HandlerFactory factory = new HandlerFactoryImpl03();
		// Generate the class
		SchemaToPojo.generatePojos(sampleFile, outputDir,"org.sample.Register", factory, new StringBuilder());
		// Make sure the file exists
		File result = new File(outputDir, "Product.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		// Make sure the register class exists
		result = new File(outputDir, "org/sample/Register.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		// Load the file string
		String resultString = FileUtils.readToString(result);
		System.out.println(resultString);
	}

	@Test
	public void loadAllFiles() throws IOException,
			JSONObjectAdapterException, ClassNotFoundException {
		// Load form the sample file
		File sampleFile = new File("src/test/resources");
		assertTrue(sampleFile.exists(), "Test file does not exist: " + sampleFile.getAbsolutePath());
		// Create the class
		HandlerFactory factory = new HandlerFactoryImpl03();
		// Generate the class
		SchemaToPojo.generatePojos(sampleFile, outputDir,"org.sample.Register", factory, new StringBuilder());
		// Make sure the file exists
		File result = new File(outputDir, "org/sample/PackageSample.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		result = new File(outputDir, "org/sample/ReferToPackageSample.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		result = new File(outputDir, "org/sample/Nested.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		result = new File(outputDir, "org/sample/ValidPets.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		result = new File(outputDir, "org/sample/Register.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		// Make sure the register class exists
		result = new File(outputDir, "InterfaceAInstanceFactory.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		result = new File(outputDir, "InterfaceBInstanceFactory.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		result = new File(outputDir, "Recursive.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		result = new File(outputDir, "InterfaceWithDefaultConcreteType.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		System.out.println(FileUtils.readToString(result));
		
		result = new File(outputDir, "DefaultConcreteTypeImpl.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
	}

	@Test
	public void testGetPackageName() throws IOException{
		File root = File.createTempFile("root file test", "");
		// Delete the file
		root.delete();
		// Convert to directory
		root.mkdirs();
		File jsonFile = new File(root, "org/sagebionetworks/test.json");
		String packageName = SchemaToPojo.getPackageNameFromFiles(root, jsonFile);
		assertNotNull(packageName);
		System.out.println(packageName);
		assertEquals("org.sagebionetworks.", packageName);
		// Now test a file at the root
		jsonFile = new File(root, "test.json");
		packageName = SchemaToPojo.getPackageNameFromFiles(root, jsonFile);
		assertNotNull(packageName);
		System.out.println(packageName);
		assertEquals("", packageName);
		root.delete();
	}

}
