/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.core.util.HashUtil;

@XmlRootElement(name = "escalationEventDefinition", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class EscalationEventDefinition {

    @XmlAttribute(namespace = "http://www.jboss.org/drools")
    private String esccode;

    @XmlAttribute
    private String escalationRef;

    public EscalationEventDefinition() {

    }

    public EscalationEventDefinition(String droolsesccode, String escalationRef) {
        this.esccode = droolsesccode;
        this.escalationRef = escalationRef;
    }

    public String getEsccode() {
        return esccode;
    }

    public void setEsccode(String esccode) {
        this.esccode = esccode;
    }

    public String getEscalationRef() {
        return escalationRef;
    }

    public void setEscalationRef(String escalationRef) {
        this.escalationRef = escalationRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ErrorEventDefinition)) {
            return false;
        }
        ErrorEventDefinition that = (ErrorEventDefinition) o;
        return Objects.equals(getEsccode(), that.getErefname())
                && Objects.equals(getEscalationRef(), that.getErefname());
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(esccode),
                                         Objects.hashCode(escalationRef));
    }
}