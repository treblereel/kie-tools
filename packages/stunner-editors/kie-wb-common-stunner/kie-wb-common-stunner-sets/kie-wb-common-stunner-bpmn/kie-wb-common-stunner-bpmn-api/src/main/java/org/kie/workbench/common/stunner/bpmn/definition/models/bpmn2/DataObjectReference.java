/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.Set;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@FormDefinition(
        policy = FieldPolicy.ONLY_MARKED,
        startElement = "name",
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
@XmlRootElement(name = "dataObjectReference", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class DataObjectReference extends BaseArtifacts {

    @Labels
    @XmlTransient
    private static final Set<String> labels = new Sets.Builder<String>()
            .add("all")
            .add("lane_child")
            .build();

    @XmlAttribute
    protected String name;

    @XmlAttribute
    private String id;

    @XmlTransient
    protected String documentation;

    @XmlTransient
    private String itemSubjectRef;

    @XmlAttribute
    private String dataObjectRef;

    @Property
    @Valid
    @FormField
    @XmlTransient
    private Name dataObjectName;

    @Property
    @FormField
    @XmlTransient
    private DataObjectType type;

    public DataObjectReference() {
        this(new Name("DataObject"),
             new DataObjectType(),
             "",
             "",
             new BackgroundSet(),
             new FontSet(),
             new RectangleDimensionsSet(),
             new AdvancedData());
    }

    public DataObjectReference(final @MapsTo("dataObjectName") Name dataObjectName,
                               final @MapsTo("type") DataObjectType type,
                               final @MapsTo("name") String name,
                               final @MapsTo("documentation") String documentation,
                               final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                               final @MapsTo("fontSet") FontSet fontSet,
                               final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                               final @MapsTo("advancedData") AdvancedData advancedData) {

        super(name, documentation, backgroundSet, fontSet, dimensionsSet, advancedData);
        this.dataObjectName = dataObjectName;
        this.dataObjectName.setValue(this.dataObjectName.getValue());
        this.type = type;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public Name getDataObjectName() {
        return dataObjectName;
    }

    public void setDataObjectName(Name dataObjectName) {
        this.dataObjectName = dataObjectName;
    }

    @Override
    public String getName() {
        return null;
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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public DataObjectType getType() {
        return type;
    }

    public void setType(DataObjectType type) {
        this.type = type;
    }

    public String getDataObjectRef() {
        return dataObjectName.getValue();
    }

    public void setDataObjectRef(String dataObjectRef) {
        this.dataObjectName.setValue(dataObjectRef);
    }

    public String getItemSubjectRef() {
        return itemSubjectRef;
    }

    public void setItemSubjectRef(String itemSubjectRef) {
        this.itemSubjectRef = itemSubjectRef;
    }

    public ItemDefinition getItemDefinition() {
        return new ItemDefinition(getDataObjectName().getValue() + "_ItemRef", type.getValue().getType());
    }

    public DataObject getDataObject() {
        DataObject result = new DataObject();
        result.setId(getDataObjectName().getValue());
        result.setName(getDataObjectName().getValue());
        result.setItemSubjectRef(generateItemRefId());

        return result;
    }

    private String generateItemRefId() {
        return getDataObjectName().getValue() + "_ItemRef";
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(dataObjectName),
                                         Objects.hashCode(type),
                                         Objects.hashCode(id),
                                         Objects.hashCode(dataObjectRef));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataObjectReference) {
            DataObjectReference other = (DataObjectReference) o;
            return super.equals(other)
                    && Objects.equals(dataObjectName, other.dataObjectName)
                    && Objects.equals(type, other.type)
                    && Objects.equals(id, other.id)
                    && Objects.equals(dataObjectRef, other.dataObjectRef)
                    && Objects.equals(documentation, other.documentation);
        }
        return false;
    }
}
