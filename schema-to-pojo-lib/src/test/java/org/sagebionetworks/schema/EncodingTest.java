package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import org.junit.Test;

public class EncodingTest {
	
	@Test
	public void testGetEncodingForJSONValue(){
		// Make sure we can find the encoding for each jason value.
		for(ENCODING encoding: ENCODING.values()){
			String jsonValue = encoding.getJsonValue();
			assertNotNull(jsonValue);
			ENCODING fetched = ENCODING.getEncodingForJSONValue(jsonValue);
			assertEquals(encoding, fetched);
		}
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGetEncodingForJSONValueUnknown(){
		ENCODING.getEncodingForJSONValue("badValue");
	}

}
