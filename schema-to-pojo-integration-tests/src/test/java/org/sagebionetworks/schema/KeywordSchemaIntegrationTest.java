package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;
import org.sagebionetworks.ABImpl;
import org.sagebionetworks.ABImpl2;
import org.sagebionetworks.KeywordAsProperties;
import org.sagebionetworks.StringKeyMap;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class KeywordSchemaIntegrationTest {

	@Test
	public void testRoundTrip() throws JSONObjectAdapterException {
		KeywordAsProperties keywordAsProperties = new KeywordAsProperties();

		keywordAsProperties.set_boolean("not a boolean");
		keywordAsProperties.set_const(Arrays.asList(1L,2L,3L));
		keywordAsProperties.set_enum(3.2);
		keywordAsProperties.set_null(true);
		keywordAsProperties.setNonKeyword("normal key");

		// Now make the round trip
		String jsonString = EntityFactory.createJSONStringForEntity(keywordAsProperties);
		assertNotNull(jsonString);
		String expectedJson = "{" +
				"\"null\":true," +
				"\"boolean\":\"not a boolean\"," +
				"\"enum\":3.2," +
				"\"const\":[1,2,3]," +
				"\"nonKeyword\":\"normal key\"" +
				"}";
		assertEquals(expectedJson, jsonString);

		// Clone it
		KeywordAsProperties clone = EntityFactory.createEntityFromJSONString(jsonString, KeywordAsProperties.class);
		assertNotNull(clone);
		//clone should have same expected JSON
		assertEquals(expectedJson, EntityFactory.createJSONStringForEntity(clone));

		assertEquals(keywordAsProperties, clone);
	}
}


