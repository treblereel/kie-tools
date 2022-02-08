/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "compensateEventDefinition", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class ConditionalEventDefinition extends AbstractEventDefinition {

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
