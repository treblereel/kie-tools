package org.yard.validator.adapter.model;


import java.util.List;

public class Element {

    private String name;
    private String type;
    private List<String> requirements;
    private DecisionLogic logic;

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