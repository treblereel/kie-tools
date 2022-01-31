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

@MorphBase(defaultType = IntermediateTimerEvent.class)
public abstract class BaseCatchingIntermediateEvent extends BaseIntermediateEvent {

    @Category
    public static final transient String category = BPMNCategories.INTERMEDIATE_EVENTS;

    @XmlElement(name = "dataOutput")
    @XmlUnwrappedCollection
    private List<DataOutput> dataOutputs;

    @XmlElement(name = "dataOutputAssociation")
    @XmlUnwrappedCollection
    private List<DataOutputAssociation> dataOutputAssociation;

    /**
     * for marshallers purposes, not stored the value
     **/
    List<OutputSet> outputSet;

    public BaseCatchingIntermediateEvent() {
        super();
    }

    public BaseCatchingIntermediateEvent(final String name,
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
        labels.add("IntermediateEventOnSubprocessBoundary");
        labels.add("IntermediateEventOnActivityBoundary");
        labels.add("EventOnChoreographyActivityBoundary");
        labels.add("IntermediateEventsMorph");
        labels.add("cm_nop");
        labels.add("IntermediateEventCatching");
    }

    @Override
    public boolean hasOutputVars() {
        return true;
    }

    @Override
    public boolean isSingleOutputVar() {
        return true;
    }

    public String getCategory() {
        return category;
    }

    public List<DataOutput> getDataOutputs() {
        return AssignmentParser.parseDataOutputs(getId(), dataIOSet.getAssignmentsinfo().getValue());
    }

    public void setDataOutputs(List<DataOutput> dataOutputs) {
        this.dataOutputs = dataOutputs;
    }

    public List<DataOutputAssociation> getDataOutputAssociation() {
        return AssignmentParser.parseDataOutputAssociation(getId(), dataIOSet.getAssignmentsinfo().getValue());
    }

    public void setDataOutputAssociation(List<DataOutputAssociation> dataOutputAssociation) {
        this.dataOutputAssociation = dataOutputAssociation;
    }

    public List<OutputSet> getOutputSet() {
        return AssignmentParser.getOutputSet(getId(), dataIOSet.getAssignmentsinfo().getValue());
    }

    public void setOutputSet(List<OutputSet> outputSets) {
        this.outputSet = outputSets;
    }

    public List<ItemDefinition> getItemDefinition() {
        return AssignmentParser.getOutputItemDefinitions(getId(), dataIOSet.getAssignmentsinfo().getValue());
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(getClass()),
                                         Objects.hashCode(getDataOutputAssociation()),
                                         Objects.hashCode(getDataOutputs()),
                                         Objects.hashCode(labels));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseCatchingIntermediateEvent) {
            BaseCatchingIntermediateEvent other = (BaseCatchingIntermediateEvent) o;
            return super.equals(other)
                    && Objects.equals(dataOutputs, other.dataOutputs)
                    && Objects.equals(dataOutputAssociation, other.dataOutputAssociation)
                    && Objects.equals(labels, other.labels);
        }
        return false;
    }
}
