package org.sagebionetworks.schema.adapter.org.json;

import org.sagebionetworks.schema.adapter.JSONEntity;

/**
 * Used to test abstract class initialization.
 * 
 * @author John
 *
 */
public interface SimpleInterface extends JSONEntity {
	
	public String getConcreteType();
	
	public void setConcreteType(String type);

}
