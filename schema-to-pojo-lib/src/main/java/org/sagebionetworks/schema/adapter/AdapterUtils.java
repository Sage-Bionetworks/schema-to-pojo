package org.sagebionetworks.schema.adapter;

import org.sagebionetworks.schema.ObjectSchema;

public class AdapterUtils {
	
	public static <T> String extractConcreteType(JSONObjectAdapter adapter, Class<T> clazz) throws JSONObjectAdapterException {
		if (adapter.has(ObjectSchema.CONCRETE_TYPE)) {
			return adapter.getString(ObjectSchema.CONCRETE_TYPE);
		}
		
		JSONDefaultConcreteType defaultConcreteType = clazz.getDeclaredAnnotation(JSONDefaultConcreteType.class);
		
		if (defaultConcreteType == null) {
			throw new JSONObjectAdapterException("Missing " + ObjectSchema.CONCRETE_TYPE + " property, cannot discriminate the polymorphic type.");
		}
		
		return defaultConcreteType.value();
	}

}
