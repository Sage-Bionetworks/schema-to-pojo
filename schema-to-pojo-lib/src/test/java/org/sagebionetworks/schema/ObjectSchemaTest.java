package org.sagebionetworks.schema;

import org.junit.Test;

public class ObjectSchemaTest {
	
	@Test
	public void testEntityTypeAsString(){
		// The entity type property must be of type string.
		ObjectSchema schema  = new ObjectSchema(TYPE.OBJECT);
		ObjectSchema propSchema = new ObjectSchema(TYPE.STRING);
		// The entityType property can (and must be a string).
		schema.putProperty(ObjectSchema.CONCRETE_TYPE, propSchema);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testEntityTypeNotString(){
		// The entity type property must be of type string.
		ObjectSchema schema  = new ObjectSchema(TYPE.OBJECT);
		ObjectSchema propSchema = new ObjectSchema(TYPE.ARRAY);
		// The entityType property can (and must be a string).
		schema.putProperty(ObjectSchema.CONCRETE_TYPE, propSchema);
	}
}
