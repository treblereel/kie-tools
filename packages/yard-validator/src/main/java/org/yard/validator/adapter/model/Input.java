package org.yard.validator.adapter.model;


import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;

@YAMLMapper
public class Input {
    private String name;
    private String type;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}