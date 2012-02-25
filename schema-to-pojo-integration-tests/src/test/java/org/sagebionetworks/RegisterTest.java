package org.sagebionetworks;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;
import org.sagebionetworks.schema.adapter.JSONEntity;

/**
 * Test that the register is auto-generated.
 * @author John
 *
 */
public class RegisterTest {

	@Test
	public void testRegister(){
		// Make sure we can find each register
		Register reg = new Register();
		Iterator<String> keyIt = reg.getKeySetIterator();
		while(keyIt.hasNext()){
			String fullClassName = keyIt.next();
			JSONEntity newInstance = reg.newInstance(fullClassName);
			assertNotNull(newInstance);
			assertEquals(newInstance.getClass().getName(), fullClassName);
		}
	}
}
