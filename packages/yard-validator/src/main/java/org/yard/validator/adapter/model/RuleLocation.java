package org.yard.validator.adapter.model;


import org.yard.validator.adapter.key.Location;

public class RuleLocation implements Location {

    private String ruleName;

    public RuleLocation(final String ruleName) {

        this.ruleName = ruleName;
    }

    @Override
    public String toString() {
        return "Rule " + ruleName;
    }
}
