package org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "compensateEventDefinition", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class ConditionalEventDefinition {

    @XmlElement(name = "condition")
    private List<ConditionExpression> conditions;

    public ConditionalEventDefinition() {
        this.conditions = new ArrayList<>();
    }

    public ConditionalEventDefinition(List<ConditionExpression> conditions) {
        this.conditions = conditions;
    }

    public List<ConditionExpression> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionExpression> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConditionalEventDefinition that = (ConditionalEventDefinition) o;
        return Objects.equals(conditions, that.conditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditions);
    }
}
