package org.sagebionetworks.schema;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sagebionetworks.HasEnumProperty;
import org.sagebionetworks.ValidPets;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;
import org.sagebionetworks.schema.adapter.org.json.EntityFactory;

public class HasEnumPropertyTest {
	
	@Test
	public void testEquals(){
		HasEnumProperty source = new HasEnumProperty();
		source.setSomeEnum(ValidPets.FISH);
		HasEnumProperty clone = new HasEnumProperty();
		clone.setSomeEnum(ValidPets.DOG);
		assertFalse(source.equals(clone));
		// flip it
		assertFalse(clone.equals(source));
		// Make the equal
		clone.setSomeEnum(ValidPets.FISH);
		assertTrue(source.equals(clone));
		// flip it
		assertTrue(clone.equals(source));
	}
	
	public void testHashCode(){
		HasEnumProperty source = new HasEnumProperty();
		source.setSomeEnum(ValidPets.FISH);
		HasEnumProperty clone = new HasEnumProperty();
		clone.setSomeEnum(ValidPets.DOG);
		assertTrue(source.hashCode() != clone.hashCode());
		// Make the equal
		clone.setSomeEnum(ValidPets.FISH);
		assertTrue(source.hashCode() == clone.hashCode());
	}
	
	@Test
	public void testRoundTrip() throws JSONObjectAdapterException{
		HasEnumProperty source = new HasEnumProperty();
		source.setSomeEnum(ValidPets.CAT);
		String jsonString = EntityFactory.createJSONStringForEntity(source);
		System.out.println(jsonString);
		HasEnumProperty clone = EntityFactory.createEntityFromJSONString(jsonString, HasEnumProperty.class);
		assertNotNull(clone);
		assertEquals(source,clone);
	}

}
