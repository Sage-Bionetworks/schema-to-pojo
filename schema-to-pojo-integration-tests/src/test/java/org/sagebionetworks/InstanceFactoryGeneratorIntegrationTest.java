package org.sagebionetworks;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.jstp20.OneImpl;
import org.sagebionetworks.jstp20.SomeInterfaceInstatanceFactory;
import org.sagebionetworks.jstp20.TwoImpl;

/**
 * Does the factory work as expected?
 * @author John
 *
 */
public class InstanceFactoryGeneratorIntegrationTest {

	@Test
	public void testInterfaceAInstatanceFactory(){
		ABImpl ab = (ABImpl) InterfaceAInstatanceFactory.singleton().newInstance(ABImpl.class.getName());
		assertNotNull(ab);
		ABImpl2 ab2 = (ABImpl2) InterfaceAInstatanceFactory.singleton().newInstance(ABImpl2.class.getName());
		assertNotNull(ab2);
	}
	
	@Test
	public void testSomeInterfaceInstatanceFactory(){
		OneImpl one = (OneImpl) SomeInterfaceInstatanceFactory.singleton().newInstance(OneImpl.class.getName());
		assertNotNull(one);
		TwoImpl two = (TwoImpl) SomeInterfaceInstatanceFactory.singleton().newInstance(TwoImpl.class.getName());
		assertNotNull(two);
	}
}
