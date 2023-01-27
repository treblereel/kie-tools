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

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jsinterop.annotations.JsType;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanConnect;
import org.kie.workbench.common.stunner.core.rule.annotation.EdgeOccurrences;
import org.kie.workbench.common.stunner.sw.definition.custom.json.StateEndDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.json.StateTransitionDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.yaml.StateEndDefinitionYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.yaml.StateTransitionDefinitionYamlTypeSerializer;

/**
 * Switch state event conditions specify events, which the switch state must wait for.
 * Each condition can reference one workflow-defined event.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Switch-State-Event-Conditions"> State event conditions</a>
 */
@Bindable
@Definition(graphFactory = EdgeFactory.class)
@CanConnect(startRole = State.LABEL_STATE, endRole = State.LABEL_STATE)
@CanConnect(startRole = State.LABEL_STATE, endRole = End.LABEL_END)
@EdgeOccurrences(role = State.LABEL_STATE, type = EdgeOccurrences.EdgeType.INCOMING, max = -1)
@EdgeOccurrences(role = State.LABEL_STATE, type = EdgeOccurrences.EdgeType.OUTGOING, max = 1)
@EdgeOccurrences(role = Start.LABEL_START, type = EdgeOccurrences.EdgeType.INCOMING, max = 0)
@EdgeOccurrences(role = Start.LABEL_START, type = EdgeOccurrences.EdgeType.OUTGOING, max = 0)
@EdgeOccurrences(role = End.LABEL_END, type = EdgeOccurrences.EdgeType.OUTGOING, max = 0)
@JsType
public class EventConditionTransition {

    public static final String LABEL_TRANSITION_EVENT_CONDITION = "transition_event_condition";

    @Category
    public static final transient String category = Categories.TRANSITIONS;

    @Labels
    private static final Set<String> labels = Stream.of(LABEL_TRANSITION_EVENT_CONDITION).collect(Collectors.toSet());

    /**
     * Event condition name.
     */
    @Property
    private String name;

    /**
     * Reference to an unique event name in the defined workflow events.
     */
    private String eventRef;

    /**
     * Defines what to do if condition is true.
     * Transitions to another state if set.
     */
    @JsonbTypeSerializer(StateTransitionDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateTransitionDefinitionJsonbTypeSerializer.class)
    @YamlTypeSerializer(StateTransitionDefinitionYamlTypeSerializer.class)
    @YamlTypeDeserializer(StateTransitionDefinitionYamlTypeSerializer.class)
    private Object transition;

    /**
     * Defines what to do if condition is true.
     * End the workflow if set to true.
     */
    @JsonbTypeSerializer(StateEndDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateEndDefinitionJsonbTypeSerializer.class)
    @YamlTypeSerializer(StateEndDefinitionYamlTypeSerializer.class)
    @YamlTypeDeserializer(StateEndDefinitionYamlTypeSerializer.class)
    private Object end;

    public EventConditionTransition() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventRef() {
        return eventRef;
    }

    public void setEventRef(String eventRef) {
        this.eventRef = eventRef;
    }

    public Object getTransition() {
        return transition;
    }

    public void setTransition(Object transition) {
        this.transition = transition;
    }

    public Object getEnd() {
        return end;
    }

    public void setEnd(Object end) {
        this.end = end;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
    }
}
