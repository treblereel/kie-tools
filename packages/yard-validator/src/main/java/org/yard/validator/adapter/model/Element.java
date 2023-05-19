package org.yard.validator.adapter.model;


import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;

import java.util.List;

@YAMLMapper
public class Element {

    private String name;
    private String type;
    private List<String> requirements;
    private DecisionLogic logic;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public void setLogic(DecisionLogic logic) {
        this.logic = logic;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public DecisionLogic getLogic() {
        return logic;
    }
}