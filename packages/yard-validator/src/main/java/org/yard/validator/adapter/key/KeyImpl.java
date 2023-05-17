package org.yard.validator.adapter.key;

public class KeyImpl implements Key {

    protected final Location location;
    protected final KeyParent parent;

    public KeyImpl(final Location location,
                   final KeyParent parent) {
        this.location = location;
        this.parent = parent;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public KeyParent getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return parent.toString();
    }
}
