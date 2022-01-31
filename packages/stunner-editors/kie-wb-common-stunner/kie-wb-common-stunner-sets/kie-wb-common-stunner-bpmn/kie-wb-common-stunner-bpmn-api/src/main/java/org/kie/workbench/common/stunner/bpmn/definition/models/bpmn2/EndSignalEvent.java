/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.Objects;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.models.drools.MetaData;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignment.AssignmentParser;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.treblereel.gwt.xml.mapper.api.annotation.XmlUnwrappedCollection;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;
import static org.kie.workbench.common.stunner.core.util.StringUtils.nonEmpty;

@Portable
@Bindable
@Definition
@Morph(base = EndEvent.class)
@FormDefinition(
        startElement = "name",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
@XmlType(propOrder = {
        "documentation",
        "extensionElements",
        "incoming",
        "dataInputs",
        "dataInputAssociation",
        "inputSet",
        "signalEventDefinition"
})
@XmlRootElement(name = "endEvent", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class EndSignalEvent extends EndEvent {

    @Property
    @FormField(afterElement = "documentation")
    @Valid
    @XmlTransient
    protected ScopedSignalEventExecutionSet executionSet;

    @Property
    @FormField(afterElement = "executionSet")
    @XmlTransient
    protected DataIOSet dataIOSet;

    @XmlTransient
    private String signalId;

    /**
     * for marshallers purposes, not stored the value
     **/
    private List<InputSet> inputSet;

    @XmlElement(name = "dataInput")
    @XmlUnwrappedCollection
    private List<DataInput> dataInputs;

    @XmlElement(name = "dataInputAssociation")
    @XmlUnwrappedCollection
    private List<DataInputAssociation> dataInputAssociation;

    private SignalEventDefinition signalEventDefinition;

    public EndSignalEvent() {
        this("",
             "",
             new ScopedSignalEventExecutionSet(),
             new AdvancedData(),
             new DataIOSet());
    }

    public EndSignalEvent(final @MapsTo("name") String name,
                          final @MapsTo("documentation") String documentation,
                          final @MapsTo("executionSet") ScopedSignalEventExecutionSet executionSet,
                          final @MapsTo("advancedData") AdvancedData advancedData,
                          final @MapsTo("dataIOSet") DataIOSet dataIOSet) {
        super(name,
              documentation,
              advancedData);
        this.executionSet = executionSet;
        this.dataIOSet = dataIOSet;
    }

    @Override
    public boolean hasInputVars() {
        return true;
    }

    @Override
    public boolean isSingleInputVar() {
        return true;
    }

    public ScopedSignalEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(ScopedSignalEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    @Override
    public ExtensionElements getExtensionElements() {
        ExtensionElements elements = super.getExtensionElements();

        if (nonEmpty(executionSet.getSignalScope().getValue())) {
            MetaData scope = new MetaData("customScope", executionSet.getSignalScope().getValue());
            elements.getMetaData().add(scope);
        }

        return elements.getMetaData().isEmpty() ? null : elements;
    }

    public List<DataInput> getDataInputs() {
        return AssignmentParser.parseDataInputs(getId(), dataIOSet.getAssignmentsinfo().getValue());
    }

    public void setDataInputs(List<DataInput> dataInputs) {
        this.dataInputs = dataInputs;
    }

    public List<DataInputAssociation> getDataInputAssociation() {
        return AssignmentParser.parseDataInputAssociation(getId(), dataIOSet.getAssignmentsinfo().getValue());
    }

    public void setDataInputAssociation(List<DataInputAssociation> dataInputAssociation) {
        this.dataInputAssociation = dataInputAssociation;
    }

    public List<InputSet> getInputSet() {
        return AssignmentParser.getInputSet(getId(), dataIOSet.getAssignmentsinfo().getValue());
    }

    public void setInputSet(List<InputSet> inputSets) {
        this.inputSet = inputSets;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public Signal getSignal() {
        if (isEmpty(executionSet.getSignalRef().getValue())) {
            return null;
        }

        return new Signal(getSignalId(),
                          executionSet.getSignalRef().getValue());
    }

    public List<ItemDefinition> getItemDefinition() {
        return AssignmentParser.getInputItemDefinitions(getId(), dataIOSet.getAssignmentsinfo().getValue());
    }

    public SignalEventDefinition getSignalEventDefinition() {
        return new SignalEventDefinition(signalId);
    }

    public void setSignalEventDefinition(SignalEventDefinition signalEventDefinition) {
        this.signalEventDefinition = signalEventDefinition;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode(),
                                         dataIOSet.hashCode(),
                                         labels.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EndSignalEvent) {
            EndSignalEvent other = (EndSignalEvent) o;
            return super.equals(other) &&
                    Objects.equals(executionSet, other.executionSet) &&
                    Objects.equals(dataIOSet, other.dataIOSet) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }
}
