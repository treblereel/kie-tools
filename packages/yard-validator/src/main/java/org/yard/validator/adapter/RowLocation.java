package org.yard.validator.adapter;


import org.yard.validator.adapter.key.Location;

public class RowLocation implements Location {

    private int rowNumber;

    public RowLocation(final int rowNumber) {
        this.rowNumber = rowNumber;
    }

    @Override
    public String toString() {
        return "Row " + rowNumber;
    }
}
