package org.yard.validator.adapter.key;

public class NamedKeyFraction implements KeyFraction {

    private String name;

    public NamedKeyFraction(final String name) {
        this.name = name;
    }

    @Override
    public KeyFraction newInstance() {
        return new NamedKeyFraction(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
