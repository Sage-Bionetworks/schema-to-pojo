package org.sagebionetworks;

import static org.junit.Assert.*;

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
		for(Register reg: Register.values()){
			assertNotNull(reg.getRegisteredClass());
			String name = reg.getRegisteredClass().getName();
			System.out.println(name);
			// Make sure we can find this register with the name
			Register lookup = Register.typeForName(name);
			assertNotNull(lookup);
			assertEquals(reg, lookup);
		}
	}
}
