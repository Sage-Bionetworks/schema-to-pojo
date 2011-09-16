package org.sagebionetworks.schema.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
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
		SchemaToPojo.generatePojos(sampleFile, outputDir, "com.example",
				factory);
		// Make sure the file exists
		File result = new File(outputDir, "com/example/Product.java");
		System.out.println(result.getAbsolutePath());
		assertTrue(result.exists());
		// Load the file string
		String resultString = FileUtil.readToString(result);
		System.out.println(resultString);
	}


}
