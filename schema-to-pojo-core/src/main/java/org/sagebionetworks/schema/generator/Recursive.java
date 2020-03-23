package org.sagebionetworks.schema.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sagebionetworks.schema.ExtraFields;
import org.sagebionetworks.schema.ObjectSchema;
import org.sagebionetworks.schema.adapter.AdapterCollectionUtils;
import org.sagebionetworks.schema.adapter.JSONArrayAdapter;
import org.sagebionetworks.schema.adapter.JSONEntity;
import org.sagebionetworks.schema.adapter.JSONObjectAdapter;
import org.sagebionetworks.schema.adapter.JSONObjectAdapterException;

/**
 * Note: This class was auto-generated, and should not be directly modified.
 * 
 */
public class Recursive implements Serializable, JSONEntity
{

    private final static String _KEY_LISTOFRECURSIVE = "listOfRecursive";
    private final static String[] _ALL_KEYS = new String[] {_KEY_LISTOFRECURSIVE };
    private List<Recursive> listOfRecursive;
    private Map<String, Object> extraFieldsFromNewerVersion = null;

    public Recursive() {
    }

    /**
     * Marshal a new Recursive from JSON using the provided implementation of org.sagebionetworks.schema.adapter.JSONObjectAdapter
     * 
     * @param adapter
     *     Data will be read from this adapter to populate this object.
     * @throws JSONObjectAdapterException
     */
    public Recursive(JSONObjectAdapter adapter)
        throws JSONObjectAdapterException
    {
        super();
        if (adapter == null) {
            throw new IllegalArgumentException(ObjectSchema.OBJECT_ADAPTER_CANNOT_BE_NULL);
        }
        initializeFromJSONObject(adapter);
    }

    /**
     * 
     * @return
     *     listOfRecursive
     */
    public List<Recursive> getListOfRecursive() {
        return listOfRecursive;
    }

    /**
     * 
     * @param listOfRecursive
     */
    public void setListOfRecursive(List<Recursive> listOfRecursive) {
        this.listOfRecursive = listOfRecursive;
    }

    /**
     * @see JSONEntity#initializeFromJSONObject(JSONObjectAdapter)
     * @see JSONEntity#writeToJSONObject(JSONObjectAdapter)
     * 
     * @param adapter
     * @throws JSONObjectAdapterException
     */
    @Override
    public JSONObjectAdapter initializeFromJSONObject(JSONObjectAdapter adapter)
        throws JSONObjectAdapterException
    {
        if (adapter == null) {
            throw new IllegalArgumentException(ObjectSchema.OBJECT_ADAPTER_CANNOT_BE_NULL);
        }
        extraFieldsFromNewerVersion = ExtraFields.createExtraFieldsMap(adapter, _ALL_KEYS);
        if (!adapter.isNull(_KEY_LISTOFRECURSIVE)) {
            listOfRecursive = new ArrayList<Recursive>();
            JSONArrayAdapter __jsonArray = adapter.getJSONArray(_KEY_LISTOFRECURSIVE);
            for (int __i = 0; (__i<__jsonArray.length()); __i ++) {
                listOfRecursive.add((__jsonArray.isNull(__i)?null:new Recursive(__jsonArray.getJSONObject(__i))));
            }
        } else {
            listOfRecursive = null;
        }
        return adapter;
    }

    /**
     * @see JSONEntity#initializeFromJSONObject(JSONObjectAdapter)
     * @see JSONEntity#writeToJSONObject(JSONObjectAdapter)
     * 
     * @param adapter
     * @throws JSONObjectAdapterException
     */
    @Override
    public JSONObjectAdapter writeToJSONObject(JSONObjectAdapter adapter)
        throws JSONObjectAdapterException
    {
        if (adapter == null) {
            throw new IllegalArgumentException(ObjectSchema.OBJECT_ADAPTER_CANNOT_BE_NULL);
        }
        if (extraFieldsFromNewerVersion!= null) {
            AdapterCollectionUtils.writeToObject(adapter, extraFieldsFromNewerVersion);
        }
        if (listOfRecursive!= null) {
            JSONArrayAdapter __array = adapter.createNewArray();
            Iterator<Recursive> __it = listOfRecursive.iterator();
            int __index = 0;
            while (__it.hasNext()) {
                Recursive __value = __it.next();
                __array.put(__index, ((__value == null)?null:__value.writeToJSONObject(adapter.createNew())));
                __index++;
            }
            adapter.put(_KEY_LISTOFRECURSIVE, __array);
        }
        return adapter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((listOfRecursive == null)? 0 :listOfRecursive.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass()!= obj.getClass()) {
            return false;
        }
        Recursive other = ((Recursive) obj);
        if (listOfRecursive == null) {
            if (other.listOfRecursive!= null) {
                return false;
            }
        } else {
            if (!listOfRecursive.equals(other.listOfRecursive)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds toString method to pojo.
     * returns a string
     * 
     * @return
     */
    @Override
    public String toString() {
        StringBuilder result;
        result = new StringBuilder();
        result.append("");
        result.append("Recursive");
        result.append(" [");
        result.append("listOfRecursive=");
        result.append(listOfRecursive);
        result.append(" ");
        result.append("]");
        return result.toString();
    }

}
