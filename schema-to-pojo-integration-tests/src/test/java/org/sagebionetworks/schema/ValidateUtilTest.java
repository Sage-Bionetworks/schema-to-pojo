package org.sagebionetworks.schema;

import org.junit.Test;
import org.sagebionetworks.AllTypes;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.JSONObjectAdapterImpl;

public class ValidateUtilTest {
	
	@Test (expected=JSONObjectAdapterException.class)
	public void testNonDeclaredValue() throws JSONObjectAdapterException{
		AllTypes at = new AllTypes();
		at.setBooleanProp(true);
		at.setLongProp(123L);
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		at.writeToJSONObject(adapter);
		// Now add an undeclared property to the object
		adapter.put("NOT DECLARED", "I should not be here");
		// This should fail validation
		ValidateUtil.validateEntity(at.getJSONSchema(), adapter, AllTypes.class);
	}
	
	@Test (expected=JSONObjectAdapterException.class)
	public void testNonDeclaredValueOnInit() throws JSONObjectAdapterException{
		AllTypes at = new AllTypes();
		at.setBooleanProp(true);
		at.setLongProp(123L);
		JSONObjectAdapterImpl adapter = new JSONObjectAdapterImpl();
		at.writeToJSONObject(adapter);
		// Now add an undeclared property to the object
		adapter.put("NOT DECLARED", "I should not be here");
		// This should fail validation
		AllTypes clone = new AllTypes();
		// The init should fail.
		clone.initializeFromJSONObject(adapter);
	}

}
