package org.yard.validator.adapter.model;


import org.yard.validator.adapter.key.KeyImpl;
import org.yard.validator.adapter.key.KeyParent;
import org.yard.validator.adapter.key.Location;

public class ObjectKey extends KeyImpl {

    private Object value;

    public ObjectKey(final Location location,
                     final KeyParent parent,
                     final Object value) {
        super(location,
             parent );
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s %s", parent, value);
    }

}
