package org.sagebionetworks.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sagebionetworks.Recursive;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;
import org.sagebionetworks.schema.util.FileUtil;

public class RecursiveTest {

	@Test
	public void testRecursiveJsonRoundTrip() throws JSONObjectAdapterException {

		Recursive parent = new Recursive();
		parent.setName("parent");
		Recursive child = new Recursive();
		child.setName("child");
		List<Recursive> list = new ArrayList<Recursive>();
		list.add(child);
		parent.setListOfRecursive(list);
		
		String json = EntityFactory.createJSONStringForEntity(parent);
		Recursive clone = EntityFactory.createEntityFromJSONString(json, Recursive.class);
		assertEquals(parent, clone);
	}
	
	@Test
	public void testRecursiveSchema() throws Exception {
		String fileName = "org/sagebionetworks/Recursive-effective.json";
		String schemaJson = FileUtil.loadStringFromClasspathFile(Recursive.class.getClassLoader(), fileName);
		
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl(schemaJson);
		ObjectSchemaImpl schema = new ObjectSchemaImpl(adapter);
		assertNotNull(schema.get$recursiveAnchor());
		assertTrue(schema.get$recursiveAnchor());
		ObjectSchema listSchema = schema.getProperties().get("listOfRecursive");
		assertNotNull(listSchema);
		ObjectSchema items = listSchema.getItems();
		assertNotNull(items);
		assertEquals(items.get$recursiveRef(), "#");
		ObjectSchema refToSelf = schema.getProperties().get("refToSelf");
		assertNotNull(refToSelf);
		assertEquals(refToSelf.get$recursiveRef(), "#");
		assertNull(refToSelf.getProperties());
	}
}
