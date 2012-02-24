package org.sagebionetworks.schema.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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

	@Before
	public void before() throws IOException {
		// Create a temp directory for output
		outputDir = FileUtil.createTempDirectory("output");
	}

	@After
	public void after() {
		// Delete the output directory
		FileUtil.recursivelyDeleteDirectory(outputDir);
		assertFalse(outputDir.exists());
	}

	@Test
	public void loadSingleFile() throws IOException,
			JSONObjectAdapterException, ClassNotFoundException {
		// Load form the sample file
		File sampleFile = new File("src/test/resources/ExampleSchema.json");
		assertTrue("Test file does not exist: " + sampleFile.getAbsolutePath(),
				sampleFile.exists());
		// Create the class
		HandlerFactory factory = new HandlerFactoryImpl03();
		// Generate the class
		SchemaToPojo.generatePojos(sampleFile, outputDir, "org.sample.Register", factory);
		// Make sure the file exists
		File result = new File(outputDir, "Product.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		// Make sure the register class exists
		result = new File(outputDir, "org/sample/Register.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		// Load the file string
		String resultString = FileUtil.readToString(result);
		System.out.println(resultString);
	}

	@Ignore
	@Test
	public void loadAllFiles() throws IOException,
			JSONObjectAdapterException, ClassNotFoundException {
		// Load form the sample file
		File sampleFile = new File("src/test/resources");
		assertTrue("Test file does not exist: " + sampleFile.getAbsolutePath(),
				sampleFile.exists());
		// Create the class
		HandlerFactory factory = new HandlerFactoryImpl03();
		// Generate the class
		SchemaToPojo.generatePojos(sampleFile, outputDir, "org.sample.Register", factory);
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
		
		// Make sure the register class exists
		result = new File(outputDir, "org/sample/Register.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		
		// Load the file string
		String resultString = FileUtil.readToString(result);
		System.out.println(resultString);
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
