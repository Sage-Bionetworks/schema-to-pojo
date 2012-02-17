package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.schema.LinkDescription.LinkRel;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class LinkDescriptionTest {

	@Test
	public void testLinkRel(){
		// Make sure we can fetch each rel from its json
		for(LinkRel rel: LinkRel.values()){
			String json = rel.getJsonName();
			assertNotNull(json);
			LinkRel fetched = LinkRel.getRelForJson(json);
			assertEquals(rel, fetched);
		}
	}
	@Test
	public void testLinkDescriptionRoundTrip() throws JSONObjectAdapterException{
		String href = "https://mail.google.com/";
		LinkDescription start = new LinkDescription(LinkRel.DESCRIBED_BY, href);
		assertEquals(LinkRel.DESCRIBED_BY, start.getRel());
		assertEquals(href, start.getHref());
		// test the equals
		assertEquals(start, start);
		// Now write it to json
		String json = EntityFactory.createJSONStringForEntity(start);
		assertNotNull(json);
		System.out.println(json);
		LinkDescription back = EntityFactory.createEntityFromJSONString(json, LinkDescription.class);
		assertEquals(start, back);
		
	}
}
