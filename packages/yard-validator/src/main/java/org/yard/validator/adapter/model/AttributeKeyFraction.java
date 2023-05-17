package org.yard.validator.adapter.model;


import org.yard.validator.adapter.key.KeyFraction;

public class AttributeKeyFraction implements KeyFraction {

    private String attribute;

    public AttributeKeyFraction(final String attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return attribute;
    }

    @Override
    public KeyFraction newInstance() {
        return new AttributeKeyFraction(attribute);
    }
}
