package org.yard.validator.adapter.key;

import java.util.ArrayList;
import java.util.List;

public class KeyParentImpl implements KeyParent {

    private final List<KeyFraction> fractions = new ArrayList<>();

    public KeyParentImpl append(final KeyFraction keyFraction) {

        fractions.add(keyFraction);

        return this;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (final KeyFraction fraction : fractions) {
            result.append(fraction.toString());
        }
        return result.toString();
    }

    public KeyParentImpl newInstance() {

        final KeyParentImpl result = new KeyParentImpl();
        for (KeyFraction fraction : fractions) {
            result.append(fraction.newInstance());
        }

        return result;
    }
}
