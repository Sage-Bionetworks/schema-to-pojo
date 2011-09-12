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
		
		// Compile the code
		String path = result.getAbsolutePath().replaceAll("\\\\","/");
//		CompilationResult compResults = compile(new String[]{"-source","1.6","-target","1.6","-verbose", path}, outputDir);
//		CompilationResult compResults = compile(new String[]{"com/example/Product.java"}, outputDir);
//		assertNotNull(compResults);
//		assertNotNull(compResults.getErrors());
//		StringBuilder errors = new StringBuilder();
//		for(CompilationProblem problem: compResults.getErrors()){
//			errors.append(problem.toString());
//			errors.append("\n");
//		}
//		assertTrue(errors.toString(), compResults.getErrors().length == 0);

	}

//	public static CompilationResult compile(String[] toCompile, File outputDir) {
//		JavaCompiler compiler = new JavaCompilerFactory()
//				.createCompiler("eclipse");
//
//		JavaCompilerSettings compilerSettings = compiler
//				.createDefaultSettings();
//		compilerSettings.setSourceVersion("1.6");
//		compilerSettings.setTargetVersion("1.6");
//
//		return compiler.compile(toCompile, new FileResourceReader(outputDir),
//				new FileResourceStore(outputDir), Thread.currentThread()
//						.getContextClassLoader(), compilerSettings);
//	}

}
