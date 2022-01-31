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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpsim.ElementParameters;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpsim.NormalDistribution;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpsim.ProcessingTime;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpsim.TimeParameters;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.treblereel.gwt.xml.mapper.api.annotation.XmlUnwrappedCollection;

public abstract class BaseIntermediateEvent extends FlowNode
        implements BPMNViewDefinition,
                   DataIOModel {

    @Labels
    @XmlTransient
    protected final Set<String> labels = new HashSet<>();

    @Property
    @Valid
    @XmlTransient
    protected BackgroundSet backgroundSet;

    @Property
    @XmlTransient
    protected FontSet fontSet;

    @Property
    @XmlTransient
    protected CircleDimensionSet dimensionsSet;

    @Property
    @FormField(afterElement = "executionSet")
    @Valid
    @XmlTransient
    protected DataIOSet dataIOSet;

    @XmlUnwrappedCollection
    @XmlElement(name = "incoming")
    private List<Incoming> incoming = new ArrayList<>();

    @XmlUnwrappedCollection
    @XmlElement(name = "outgoing")
    private List<Outgoing> outgoing = new ArrayList<>();

    /*
    Simulation parameters for the start event. Used in Simulation section during marshalling/unmarshalling
    This parameter currently not changed by Stunner but can preserve users values.
    Shouldn't be handled in Equals/HashCode.
     */
    @XmlTransient
    private ElementParameters elementParameters = new ElementParameters();

    public BaseIntermediateEvent() {
        initLabels();
    }

    public BaseIntermediateEvent(final String name,
                                 final String documentation,
                                 final BackgroundSet backgroundSet,
                                 final FontSet fontSet,
                                 final CircleDimensionSet dimensionsSet,
                                 final DataIOSet dataIOSet,
                                 final AdvancedData advancedData) {
        this();
        this.name = name;
        this.documentation = documentation;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
        this.advancedData = advancedData;
        this.dataIOSet = dataIOSet;
    }

    protected abstract void initLabels();

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setFontSet(FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public CircleDimensionSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(CircleDimensionSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    public Set<String> getLabels() {
        return labels;
    }

    /*
    We should keep ID updated for simulation data since during marshalling
    simulation section will be moved to separate part of XML.
     */
    public void setId(String id) {
        super.setId(id);
        getElementParameters().setElementRef(id);
    }

    //TODO maybe we don't need simulation data for End Events at all, need to check BPMN Spec
    /*
    For compatibility reasons if simulation data is missing we need to generate default one.
     */
    public ElementParameters getElementParameters() {
        if (elementParameters.getTimeParameters() == null) {
            elementParameters.setTimeParameters(
                    new TimeParameters(
                            new ProcessingTime(
                                    new NormalDistribution()
                            )
                    )
            );
        }
        return elementParameters;
    }

    public void setElementParameters(ElementParameters elementParameters) {
        this.elementParameters = elementParameters;
    }

    public List<Incoming> getIncoming() {
        return incoming;
    }

    public void setIncoming(List<Incoming> incoming) {
        this.incoming = incoming;
    }

    public List<Outgoing> getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(List<Outgoing> outgoing) {
        this.outgoing = outgoing;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(getClass()),
                                         super.hashCode(),
                                         Objects.hashCode(backgroundSet),
                                         Objects.hashCode(fontSet),
                                         Objects.hashCode(dimensionsSet),
                                         Objects.hashCode(advancedData),
                                         Objects.hashCode(dataIOSet),
                                         Objects.hashCode(incoming),
                                         Objects.hashCode(outgoing),
                                         Objects.hashCode(elementParameters),
                                         Objects.hashCode(labels));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BaseIntermediateEvent) {
            BaseIntermediateEvent other = (BaseIntermediateEvent) o;
            return super.equals(other) &&
                    Objects.equals(backgroundSet, other.backgroundSet) &&
                    Objects.equals(fontSet, other.fontSet) &&
                    Objects.equals(dimensionsSet, other.dimensionsSet) &&
                    Objects.equals(advancedData, other.advancedData) &&
                    Objects.equals(dataIOSet, other.dataIOSet) &&
                    Objects.equals(incoming, other.incoming) &&
                    Objects.equals(outgoing, other.outgoing) &&
                    Objects.equals(elementParameters, other.elementParameters) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }
}
