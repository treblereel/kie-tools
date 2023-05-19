package org.yard.validator.adapter.model;


import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.yard.validator.adapter.key.Location;

@YAMLMapper
public class RuleLocation implements Location {

    private String ruleName;

    public RuleLocation(){

    }
    public RuleLocation(final String ruleName) {

        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public String toString() {
        return "Rule " + ruleName;
    }
}
