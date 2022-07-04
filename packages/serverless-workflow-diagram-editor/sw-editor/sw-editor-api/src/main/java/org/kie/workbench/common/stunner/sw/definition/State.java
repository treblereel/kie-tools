/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.definition;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.crysknife.ui.databinding.client.api.Bindable;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.rule.annotation.CanDock;
import org.kie.workbench.common.stunner.sw.definition.custom.DataConditionTransitionEndJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.DataConditionTransitionTransitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.ObjectJsonbTypeDeserializer;

/**
 * This class defines workflow states define building blocks of the workflow execution instructions.
 * They define the control flow logic instructions on what the workflow is supposed to do.
 *
 * Type of the state is specified by its category, which is set in constructor for all its descendants.
 */
@Bindable
@Definition
@CanDock(roles = {Timeout.LABEL_TIMEOUT})
@MorphBase(defaultType = InjectState.class)
public class State {

    public static final String LABEL_STATE = "state";

    @Category
    public static final transient String category = Categories.STATES;

    @Labels
    public static final Set<String> labels = Stream.of(Workflow.LABEL_ROOT_NODE,
                                                       LABEL_STATE).collect(Collectors.toSet());

    /**
     * Unique state name, can't be null.
     */
    @Property(meta = PropertyMetaTypes.NAME)
    public String name;

    /**
     * Type of the state, can't be null.
     */
    public String type;

    /**
     * Next transition of the workflow.
     */
    // TODO: Not all states supports this (eg: switch state)
    @JsonbTypeSerializer(DataConditionTransitionTransitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(ObjectJsonbTypeDeserializer.class)
    public Object transition;

    /**
     * Whether this State is a last state in the workflow.
     */
    // TODO: Not all states supports this (eg: switch state)
    @JsonbTypeSerializer(DataConditionTransitionEndJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(ObjectJsonbTypeDeserializer.class)
    public Object end;

    /**
     * Definitions of states error handling.
     */
    public ErrorTransition[] onErrors;

    /**
     * State specific timeouts.
     */
    public String eventTimeout;

    /**
     * Unique name of a workflow state which is responsible for compensation of this state.
     */
    public String compensatedBy;

    public State() {
        this.name = "State";
    }

    public State setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public State setType(String type) {
        this.type = type;
        return this;
    }

    public Object getTransition() {
        return transition;
    }

    public State setTransition(Object transition) {
        this.transition = transition;
        return this;
    }

    public Object getEnd() {
        return end;
    }

    public State setEnd(Object end) {
        this.end = end;
        return this;
    }

    public ErrorTransition[] getOnErrors() {
        return onErrors;
    }

    public State setOnErrors(ErrorTransition[] onErrors) {
        this.onErrors = onErrors;
        return this;
    }

    public String getEventTimeout() {
        return eventTimeout;
    }

    public State setEventTimeout(String eventTimeout) {
        this.eventTimeout = eventTimeout;
        return this;
    }

    public String getCompensatedBy() {
        return compensatedBy;
    }

    public State setCompensatedBy(String compensatedBy) {
        this.compensatedBy = compensatedBy;
        return this;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
    }
}
