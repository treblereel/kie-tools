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

import org.treblereel.gwt.xml.mapper.api.annotation.XMLMapper;

@XMLMapper
public class FallbackStartEvent {

    protected CompensateEventDefinition compensateEventDefinition;

    protected MessageEventDefinition messageEventDefinition;

    protected SignalEventDefinition signalEventDefinition;

    protected ErrorEventDefinition errorEventDefinition;

    protected EscalationEventDefinition escalationEventDefinition;

    protected ConditionalEventDefinition conditionalEventDefinition;

    protected TimerEventDefinition timerEventDefinition;

    @XmlElement(name = "dataOutput")
    protected List<DataOutput> dataOutputs;

    @XmlElement(name = "dataOutputAssociation")
    protected List<DataOutputAssociation> dataOutputAssociation;

    @XmlElement(name = "outputSet")
    protected List<OutputSet> outputSet;

    public FallbackStartEvent() {
    }

    protected CompensateEventDefinition getCompensateEventDefinition() {
        return compensateEventDefinition;
    }

    protected void setCompensateEventDefinition(CompensateEventDefinition compensateEventDefinition) {
        this.compensateEventDefinition = compensateEventDefinition;
    }

    protected MessageEventDefinition getMessageEventDefinition() {
        return messageEventDefinition;
    }

    protected void setMessageEventDefinition(MessageEventDefinition messageEventDefinition) {
        this.messageEventDefinition = messageEventDefinition;
    }

    protected SignalEventDefinition getSignalEventDefinition() {
        return signalEventDefinition;
    }

    protected void setSignalEventDefinition(SignalEventDefinition signalEventDefinition) {
        this.signalEventDefinition = signalEventDefinition;
    }

    public ErrorEventDefinition getErrorEventDefinition() {
        return errorEventDefinition;
    }

    public void setErrorEventDefinition(ErrorEventDefinition errorEventDefinition) {
        this.errorEventDefinition = errorEventDefinition;
    }

    public EscalationEventDefinition getEscalationEventDefinition() {
        return escalationEventDefinition;
    }

    public void setEscalationEventDefinition(EscalationEventDefinition escalationEventDefinition) {
        this.escalationEventDefinition = escalationEventDefinition;
    }

    public ConditionalEventDefinition getConditionalEventDefinition() {
        return conditionalEventDefinition;
    }

    public void setConditionalEventDefinition(ConditionalEventDefinition conditionalEventDefinition) {
        this.conditionalEventDefinition = conditionalEventDefinition;
    }

    public TimerEventDefinition getTimerEventDefinition() {
        return timerEventDefinition;
    }

    public void setTimerEventDefinition(TimerEventDefinition timerEventDefinition) {
        this.timerEventDefinition = timerEventDefinition;
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
}
