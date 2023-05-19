package org.yard.validator.adapter.model;


import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeDeserializer;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeSerializer;

import java.util.List;

@YAMLMapper
public class DecisionTable extends DecisionLogic {
    private List<String> inputs;
    private String hitPolicy = "ANY";
    @Deprecated
    private List<String> outputComponents;
    private List<Rule> rules;

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public void setOutputComponents(List<String> outputComponents) {
        this.outputComponents = outputComponents;
    }

    public List<String> getInputs() {
        return inputs;
    }

    @Deprecated
    public List<String> getOutputComponents() {
        return outputComponents;
    }

    public String getHitPolicy() {
        return hitPolicy;
    }

    public void setHitPolicy(String hitPolicy) {
        this.hitPolicy = hitPolicy;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

}