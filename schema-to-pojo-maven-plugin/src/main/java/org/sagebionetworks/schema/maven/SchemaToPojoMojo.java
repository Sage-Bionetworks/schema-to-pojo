package org.sagebionetworks.schema.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.sagebionetworks.schema.generator.SchemaToPojo;
import org.sagebionetworks.schema.generator.handler.schema03.HandlerFactoryImpl03;

/**
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 * @see <a
 *      href="http://maven.apache.org/developers/mojo-api-specification.html">Mojo
 *      API Specification</a>
 * @author jmhill
 * 
 */
public class SchemaToPojoMojo extends AbstractMojo {
	
    /**
     * Target directory for generated Java source files.
     * 
     * @parameter expression="${schema-to-pojo.outputDirectory}"
     *            default-value="${project.build.directory}/generated-sources"
     * @since 0.1.0
     */
    private File outputDirectory;
    
    /** 
     * @parameter expression="${schema-to-pojo.sourceDirectory}"
     * @required
     * @since 0.1.0
     */
    private File sourceDirectory;

    /**
     * Package name used for generated Java classes (for types where a fully
     * qualified name has not been supplied in the schema using the 'javaType'
     * property).
     * 
     * @parameter expression="${schema-to-pojo.packageName}"
     * @since 0.1.0
     */
    private String packageName = "";
    
     /**
    * This is a an optional parameter. When set, a register class will be generated using the specific fully qualified class name.
    * @parameter expression="${schema-to-pojo.createRegister}"
    * @since 0.1.13
    */
    private String createRegister;
    
    
    /**
     * The project being built.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			// Add all of the auto-generated classes to the project
			 project.addCompileSourceRoot(outputDirectory.getPath());
			 // Hard code the factory for now
			 HandlerFactoryImpl03 factory = new HandlerFactoryImpl03();
			 // Generate the classes from their schemas.
			StringBuilder tmplog = new StringBuilder();
			SchemaToPojo.generatePojos(sourceDirectory, outputDirectory,createRegister, factory, tmplog);
			if (tmplog.length() > 0) {
				getLog().info(tmplog.toString());
			}
		} catch (Exception e) {
			throw new MojoFailureException("Failed to execute mojo: "+e.getMessage(), e);
		} 
	}

}
