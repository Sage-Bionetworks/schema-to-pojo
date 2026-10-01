package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sagebionetworks.HyphenatedProperties;
import org.sagebionetworks.KeywordAsProperties;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class HyphenatedPropertiesIntegrationTest {

	@Test
	public void testRoundTrip() throws JSONObjectAdapterException {
		KeywordAsProperties nested = new KeywordAsProperties();
		nested.set_null(true);
		HyphenatedProperties hyphenated = new HyphenatedProperties();
		hyphenated.setNormalizationProcessor("min_max");
		hyphenated.setNestedKeywords(nested);

		String jsonString = EntityFactory.createJSONStringForEntity(hyphenated);
		String expectedJson = "{" +
				"\"normalization-processor\":\"min_max\"," +
				"\"nested-keywords\":{\"null\":true}" +
				"}";
		assertEquals(expectedJson, jsonString);

		HyphenatedProperties clone = EntityFactory.createEntityFromJSONString(jsonString, HyphenatedProperties.class);
		assertEquals("min_max", clone.getNormalizationProcessor());
		assertEquals(nested, clone.getNestedKeywords());
		assertEquals(hyphenated, clone);
	}
}
