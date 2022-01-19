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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.treblereel.gwt.xml.mapper.api.annotation.XmlUnwrappedCollection;

@MorphBase(defaultType = ParallelGateway.class)
public abstract class BaseGateway extends FlowNode implements BPMNViewDefinition {

    @Category
    @XmlTransient
    public static final transient String category = BPMNCategories.GATEWAYS;

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

    @Labels
    @XmlTransient
    protected final Set<String> labels = new HashSet<>();

    @XmlUnwrappedCollection
    private List<Incoming> incoming = new ArrayList<>();

    @XmlUnwrappedCollection
    private List<Outgoing> outgoing = new ArrayList<>();

    @XmlAttribute
    /*
        Calculated on the fly don't store a value, needed for marshallers.
     */
    private String gatewayDirection;

    public BaseGateway() {
        initLabels();
    }

    public BaseGateway(String name,
                       String documentation,
                       BackgroundSet backgroundSet,
                       FontSet fontSet,
                       CircleDimensionSet dimensionsSet,
                       AdvancedData advancedData) {
        this();
        this.name = name;
        this.documentation = documentation;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
        this.advancedData = advancedData;
    }

    protected void initLabels() {
        labels.add("all");
        labels.add("lane_child");
        labels.add("sequence_start");
        labels.add("sequence_end");
        labels.add("choreography_sequence_start");
        labels.add("choreography_sequence_end");
        labels.add("fromtoall");
        labels.add("GatewaysMorph");
        labels.add("cm_nop");
    }

    public String getCategory() {
        return category;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

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

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public CircleDimensionSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final CircleDimensionSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    @Override
    public ExtensionElements getExtensionElements() {
        ExtensionElements elements = super.getExtensionElements();

        return elements;
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

    public String getGatewayDirection() {
        if (outgoing.size() <= 1 && incoming.size() <= 1) {
            return "Unspecified";
        }

        if (outgoing.size() >= 2 && incoming.size() >= 2) {
            return "Mixed";
        }

        if (outgoing.size() >= 2) {
            return "Diverging";
        }

        if (incoming.size() >= 2) {
            return "Converging";
        }

        return null;
    }

    public void setGatewayDirection(String gatewayDirection) {
        this.gatewayDirection = gatewayDirection;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(getClass()),
                                         super.hashCode(),
                                         Objects.hashCode(backgroundSet),
                                         Objects.hashCode(fontSet),
                                         Objects.hashCode(dimensionsSet),
                                         Objects.hashCode(advancedData),
                                         Objects.hashCode(labels),
                                         Objects.hashCode(incoming),
                                         Objects.hashCode(outgoing),
                                         Objects.hashCode(gatewayDirection));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BaseGateway) {
            BaseGateway other = (BaseGateway) o;
            return super.equals(other) &&
                    Objects.equals(backgroundSet, other.backgroundSet) &&
                    Objects.equals(fontSet, other.fontSet) &&
                    Objects.equals(dimensionsSet, other.dimensionsSet) &&
                    Objects.equals(advancedData, other.advancedData) &&
                    Objects.equals(labels, other.labels) &&
                    Objects.equals(incoming, other.incoming) &&
                    Objects.equals(outgoing, other.outgoing) &&
                    Objects.equals(gatewayDirection, other.gatewayDirection);
        }
        return false;
    }
}
