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
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignment.AssignmentParser;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.treblereel.gwt.xml.mapper.api.annotation.XmlUnwrappedCollection;

@MorphBase(defaultType = IntermediateSignalEventThrowing.class)
public abstract class BaseThrowingIntermediateEvent extends BaseIntermediateEvent {

    @Category
    public static final transient String category = BPMNCategories.INTERMEDIATE_EVENTS;

    @XmlElement(name = "dataInput")
    @XmlUnwrappedCollection
    private List<DataInput> dataInputs;

    @XmlElement(name = "dataInputAssociation")
    @XmlUnwrappedCollection
    private List<DataInputAssociation> dataInputAssociation;

    /**
     * for marshallers purposes, not stored the value
     **/
    private List<InputSet> inputSet;

    public BaseThrowingIntermediateEvent() {
        super();
    }

    public BaseThrowingIntermediateEvent(final String name,
                                         final String documentation,
                                         final BackgroundSet backgroundSet,
                                         final FontSet fontSet,
                                         final CircleDimensionSet dimensionsSet,
                                         final DataIOSet dataIOSet,
                                         final AdvancedData advancedData) {
        super(name,
              documentation,
              backgroundSet,
              fontSet,
              dimensionsSet,
              dataIOSet,
              advancedData);
    }

    @Override
    protected void initLabels() {
        labels.add("all");
        labels.add("lane_child");
        labels.add("sequence_start");
        labels.add("sequence_end");
        labels.add("to_task_event");
        labels.add("from_task_event");
        labels.add("fromtoall");
        labels.add("IntermediateEventsMorph");
        labels.add("cmnop");
        labels.add("IntermediateEventThrowing");
    }

    @Override
    public boolean hasInputVars() {
        return true;
    }

    @Override
    public boolean isSingleInputVar() {
        return true;
    }

    public String getCategory() {
        return category;
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

    public List<ItemDefinition> getItemDefinition() {
        return AssignmentParser.getInputItemDefinitions(getId(), dataIOSet.getAssignmentsinfo().getValue());
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(getClass()),
                                         Objects.hashCode(getDataInputAssociation()),
                                         Objects.hashCode(getDataInputs()),
                                         Objects.hashCode(labels));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseThrowingIntermediateEvent) {
            BaseThrowingIntermediateEvent other = (BaseThrowingIntermediateEvent) o;
            return super.equals(other)
                    && Objects.equals(dataInputs, other.dataInputs)
                    && Objects.equals(dataInputAssociation, other.dataInputAssociation)
                    && Objects.equals(labels, other.labels);
        }
        return false;
    }
}
