/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.treblereel.gwt.xml.mapper.api.annotation.XMLMapper;

@XMLMapper
public class FallbackStartEvent extends StartEvent {

    @XmlElementRefs({
            @XmlElementRef(name = "compensateEventDefinition", type = CompensateEventDefinition.class),
            @XmlElementRef(name = "messageEventDefinition", type = MessageEventDefinition.class),
            @XmlElementRef(name = "signalEventDefinition", type = SignalEventDefinition.class),
            @XmlElementRef(name = "errorEventDefinition", type = ErrorEventDefinition.class),
            @XmlElementRef(name = "escalationEventDefinition", type = EscalationEventDefinition.class),
            @XmlElementRef(name = "conditionalEventDefinition", type = ConditionalEventDefinition.class),
            @XmlElementRef(name = "timerEventDefinition", type = TimerEventDefinition.class),
    })
    private AbstractEventDefinition eventDefinition;

    @XmlElement(name = "dataOutput")
    private List<DataOutput> dataOutputs;

    @XmlElement(name = "dataOutputAssociation")
    private List<DataOutputAssociation> dataOutputAssociation;

    @XmlElement(name = "outputSet")
    private List<OutputSet> outputSet;

    private FallbackStartEventExecutionSet executionSet;

    private DataIOSet dataIOSet;

    public AbstractEventDefinition getEventDefinition() {
        return eventDefinition;
    }

    public void setEventDefinition(AbstractEventDefinition eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    public List<DataOutput> getDataOutputs() {
        return dataOutputs;
    }

    public void setDataOutputs(List<DataOutput> dataOutputs) {
        this.dataOutputs = dataOutputs;
    }

    public List<DataOutputAssociation> getDataOutputAssociation() {
        return dataOutputAssociation;
    }

    public void setDataOutputAssociation(List<DataOutputAssociation> dataOutputAssociation) {
        this.dataOutputAssociation = dataOutputAssociation;
    }

    public List<OutputSet> getOutputSet() {
        return outputSet;
    }

    public void setOutputSet(List<OutputSet> outputSet) {
        this.outputSet = outputSet;
    }

    public FallbackStartEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(FallbackStartEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    public static class FallbackStartEventExecutionSet {

        private Boolean isInterrupting;

        private String metaData;

        private TimerSettings timerSettings;

        private ErrorRefHolder errorRef;

        private EscalationRefHolder escalationRef;

        private ConditionExpression conditionExpression;

        public TimerSettings getTimerSettings() {
            return timerSettings;
        }

        public void setTimerSettings(TimerSettings timerSettings) {
            this.timerSettings = timerSettings;
        }

        public Boolean isIsInterrupting() {
            return isInterrupting;
        }

        public void setIsInterrupting(Boolean interrupting) {
            isInterrupting = interrupting;
        }

        public String getMetaData() {
            return metaData;
        }

        public void setMetaData(String metaData) {
            this.metaData = metaData;
        }

        public ErrorRefHolder getErrorRef() {
            return errorRef;
        }

        public void setErrorRef(ErrorRefHolder errorRef) {
            this.errorRef = errorRef;
        }

        public ConditionExpression getConditionExpression() {
            return conditionExpression;
        }

        public void setConditionExpression(ConditionExpression conditionExpression) {
            this.conditionExpression = conditionExpression;
        }

        public EscalationRefHolder getEscalationRef() {
            return escalationRef;
        }

        public void setEscalationRef(EscalationRefHolder escalationRef) {
            this.escalationRef = escalationRef;
        }
    }

    public static class ErrorRefHolder {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class EscalationRefHolder {

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
