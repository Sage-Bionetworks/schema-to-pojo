package org.sagebionetworks.schema;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sagebionetworks.schema.util.CompareUtils;

public class CompareUtilsTest {
	
	@Test
	public void doubleEquals(){
		double a = 0.3d;
		System.out.println(Double.toString(a));
		double b = 0.1d+0.1d+0.1d;
		System.out.println(Double.toString(b));
		// due to the limits of representing float point number as binary integers these two doubles
		// do not actually equal each other even though they appear to. The equals method gives a close approximation
		// of equals using the epsilon - absolute error.
		assertTrue(CompareUtils.doubleEquals(a, b));
	}

}
