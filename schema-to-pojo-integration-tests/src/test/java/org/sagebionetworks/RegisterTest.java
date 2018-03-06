package org.sagebionetworks;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;
import org.junit.Test;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.schema.generator.EffectiveSchemaUtil;

/**
 * Test that the register is auto-generated.
 * 
 * @author John
 * 
 */
public class RegisterTest {
	@Test
	public void testRegister() throws IOException, JSONObjectAdapterException {
		// Make sure we can find each register
		Register reg = new Register();
		Iterator<String> keyIt = reg.getKeySetIterator();
		while (keyIt.hasNext()) {
			String fullClassName = keyIt.next();
			JSONEntity newInstance = reg.newInstance(fullClassName);
			assertNotNull(newInstance);
			assertEquals(newInstance.getClass().getName(), fullClassName);
			// Should be able to load the schema for each registered file
			String json = EffectiveSchemaUtil.loadEffectiveSchemaFromClasspath(newInstance);
			ObjectSchema schema = new ObjectSchema(new JSONObjectAdapterImpl(json));
			assertEquals(newInstance.getClass().getName(), schema.getId());
		}
	}
	
}
