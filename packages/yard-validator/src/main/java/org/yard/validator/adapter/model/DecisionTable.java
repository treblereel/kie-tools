package org.yard.validator.adapter.model;


import java.util.List;

public class DecisionTable extends DecisionLogic {
    private List<String> inputs;
    private String hitPolicy = "ANY";
    @Deprecated
    private List<String> outputComponents;
    private List<Rule> rules;

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

    public static interface Rule {

    }

    public static class InlineRule implements Rule {
        public List<Object> def;
        
        public InlineRule(List<Object> data) {
            this.def = data;
        }

        public List<Object> getDef() {
            return def;
        }
    }

    public static class WhenThenRule implements Rule {
        private List<Object> when;
        private Object then;

        public List<Object> getWhen() {
            return when;
        }

        public Object getThen() {
            return then;
        }
    }
}