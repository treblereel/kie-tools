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
        //String.format is not a part of jre emulation
        //return String.format("%s %s",parent, value);
        return parent + " " + value;
    }
}
