package org.yard.validator.adapter.model;


import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.yard.validator.adapter.key.KeyFraction;

@YAMLMapper
public class AttributeKeyFraction implements KeyFraction {

    private String attribute;

    public AttributeKeyFraction(){

    }
    public AttributeKeyFraction(final String attribute) {
        this.attribute = attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
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
