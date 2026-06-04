package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sagebionetworks.InlineNamedRecursive;
import org.sagebionetworks.InlineNamedRecursiveCompound;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

/**
 * Verifies that an inline named OBJECT (a property declaring its own name/id and properties rather
 * than using a $ref to a root schema) is generated as a complete, marshalable class, and that its
 * $recursiveRef slots resolve to the anchor type &mdash; both the array-of-$recursiveRef form
 * ({@code children}) and the direct-$recursiveRef form ({@code single}).
 */
public class InlineNamedRecursiveTest {

	@Test
	public void testInlineNamedObjectGeneratesCompleteClassAndRoundTrips()
			throws JSONObjectAdapterException {
		InlineNamedRecursive grandchild = new InlineNamedRecursive();
		grandchild.setName("grandchild");

		InlineNamedRecursiveCompound compound = new InlineNamedRecursiveCompound();
		compound.setLabel("compound");
		// Array-of-$recursiveRef slot resolves to List<InlineNamedRecursive>.
		List<InlineNamedRecursive> children = new ArrayList<InlineNamedRecursive>();
		children.add(grandchild);
		compound.setChildren(children);
		// Direct-$recursiveRef slot resolves to a single InlineNamedRecursive.
		InlineNamedRecursive single = new InlineNamedRecursive();
		single.setName("single");
		compound.setSingle(single);

		InlineNamedRecursive parent = new InlineNamedRecursive();
		parent.setName("parent");
		parent.setCompound(compound);

		// call under test: a full JSON round trip through the generated marshaling.
		String json = EntityFactory.createJSONStringForEntity(parent);
		InlineNamedRecursive clone = EntityFactory.createEntityFromJSONString(json,
				InlineNamedRecursive.class);

		assertEquals(parent, clone);
	}
}
