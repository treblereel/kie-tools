/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.GatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@Morph(base = BaseGateway.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
@XmlType(propOrder = {"documentation", "extensionElements", "incoming", "outgoing"})
@XmlRootElement(name = "inclusiveGateway", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class InclusiveGateway extends BaseGateway {

    /* used for new marshalling only, all information
       stored in executionSet.
    */
    @XmlAttribute(namespace = "http://www.jboss.org/drools")
    private String dg;

    /* used for new marshalling only, all information
       stored in executionSet.
     */
    @XmlAttribute(name = "default")
    private String defaultNode;

    @Property
    @FormField(afterElement = "general")
    @Valid
    private GatewayExecutionSet executionSet;

    public InclusiveGateway() {
        this("",
             "",
             new BackgroundSet(),
             new FontSet(),
             new CircleDimensionSet(),
             new AdvancedData(),
             new GatewayExecutionSet());
    }

    public InclusiveGateway(final @MapsTo("name") String name,
                            final @MapsTo("documentation") String documentation,
                            final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                            final @MapsTo("fontSet") FontSet fontSet,
                            final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                            final @MapsTo("advancedData") AdvancedData advancedData,
                            final @MapsTo("executionSet") GatewayExecutionSet executionSet) {
        super(name,
              documentation,
              backgroundSet,
              fontSet,
              dimensionsSet,
              advancedData);
        this.executionSet = executionSet;
    }

    public GatewayExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final GatewayExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public String getDg() {
        return executionSet.getDefaultRoute().getValue();
    }

    public void setDg(String dg) {
        executionSet.getDefaultRoute().setValue(dg);
    }

    public String getDefaultNode() {
        return executionSet.getDefaultRoute().getValue();
    }

    public void setDefaultNode(String defaultNode) {
        executionSet.getDefaultRoute().setValue(defaultNode);
    }

    /*
        Inclusive Gateway doesn't support this attribute
     */
    @Override
    public String getGatewayDirection() {
        return null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(executionSet));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof InclusiveGateway) {
            InclusiveGateway other = (InclusiveGateway) o;
            return super.equals(other) &&
                    Objects.equals(executionSet,
                                   other.executionSet);
        }
        return false;
    }
}