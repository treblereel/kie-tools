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

import javax.validation.Valid;
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
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Portable
@Bindable
@Definition
@Morph(base = BaseCatchingIntermediateEvent.class)
@FormDefinition(
        startElement = "name",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
@XmlType(propOrder = {
        "documentation",
        "extensionElements",
        "incoming",
        "outgoing",
        "dataOutputs",
        "dataOutputAssociation",
        "outputSet",
        "signalEventDefinition"
})
@XmlRootElement(name = "intermediateCatchEvent", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class IntermediateSignalEventCatching extends BaseCatchingIntermediateEvent {

    @Property
    @FormField(afterElement = "documentation")
    @Valid
    @XmlTransient
    protected CancellingSignalEventExecutionSet executionSet;

    @XmlTransient
    private String signalId;

    /**
     * used by marshaller, generated on the fly, no need to store the value
     **/
    private SignalEventDefinition signalEventDefinition;

    public IntermediateSignalEventCatching() {
        this("",
             "",
             new BackgroundSet(),
             new FontSet(),
             new CircleDimensionSet(),
             new DataIOSet(),
             new AdvancedData(),
             new CancellingSignalEventExecutionSet());
    }

    public IntermediateSignalEventCatching(final @MapsTo("name") String name,
                                           final @MapsTo("documentation") String documentation,
                                           final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                           final @MapsTo("fontSet") FontSet fontSet,
                                           final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                                           final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                                           final @MapsTo("advancedData") AdvancedData advancedData,
                                           final @MapsTo("executionSet") CancellingSignalEventExecutionSet executionSet) {
        super(name,
              documentation,
              backgroundSet,
              fontSet,
              dimensionsSet,
              dataIOSet,
              advancedData);
        this.executionSet = executionSet;
    }

    @Override
    protected void initLabels() {
        super.initLabels();
        labels.add("FromEventbasedGateway");
    }

    public CancellingSignalEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(CancellingSignalEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    @Override
    public ExtensionElements getExtensionElements() {
        ExtensionElements elements = super.getExtensionElements();

        if (executionSet.getSlaDueDate().getValue() != null && !executionSet.getSlaDueDate().getValue().isEmpty()) {
            MetaData customAutoStart = new MetaData("customSLADueDate",
                                                    executionSet.getSlaDueDate().getValue());
            elements.getMetaData().add(customAutoStart);
        }

        return elements.getMetaData().isEmpty() ? null : elements;
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

    public SignalEventDefinition getSignalEventDefinition() {
        return new SignalEventDefinition(signalId);
    }

    public void setSignalEventDefinition(SignalEventDefinition signalEventDefinition) {
        this.signalEventDefinition = signalEventDefinition;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IntermediateSignalEventCatching) {
            IntermediateSignalEventCatching other = (IntermediateSignalEventCatching) o;
            return super.equals(other) &&
                    executionSet.equals(other.executionSet);
        }
        return false;
    }
}