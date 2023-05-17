package org.yard.validator.adapter;


import org.yard.validator.adapter.key.KeyParent;

public class ColumnKey implements KeyParent {

    private final String column;

    public ColumnKey(final String column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return column;
    }
}
