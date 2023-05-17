package org.yard.validator.adapter.key;

public class HardConstraintKey extends KeyImpl {

    private Comparable value;

    public HardConstraintKey(final Location location,
                             final KeyParent parent,
                             final Comparable value) {
        super(location,
              parent);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s %s",parent, value);
    }
}
