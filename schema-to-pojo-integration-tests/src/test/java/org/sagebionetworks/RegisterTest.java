package org.sagebionetworks;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

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
			Class clazz = reg.forName(fullClassName);
			assertNotNull(clazz);
			assertEquals(clazz.getName(), fullClassName);
		}
	}
}
