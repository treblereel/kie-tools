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
package org.kie.workbench.common.stunner.bpmn.marshaller.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.marshaller.Marshaller;
import org.kie.workbench.common.stunner.bpmn.marshaller.Unmarshalling;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SimpleProcessMarshallingTest {

    private final Marshaller marshaller = new Marshaller();

    private final String PATH = "org/kie/workbench/common/stunner/bpmn/marshaller/model/";

    private final String FILE_NAME_OLD = PATH + "SimpleProcess(OLD).bpmn";
    private final String FILE_NAME_NEW = PATH + "SimpleProcess(NEW).bpmn";

    private Diagram<Graph, Metadata> diagramOld;
    private Diagram<Graph, Metadata> diagramNew;

    private Process processOld;
    private Process processNew;

    @Before
    public void init() throws Exception {
        diagramOld = Unmarshalling.unmarshall(marshaller, FILE_NAME_OLD);
        processOld = getProcess(diagramOld, "process1");

        diagramNew = Unmarshalling.unmarshall(marshaller, FILE_NAME_NEW);
        processNew = getProcess(diagramNew, "process1");
    }

    @Test
    public void testOldStartEvent() {
        List<StartEvent> startEvents = processOld.getStartEvents();
        assertEquals(8, startEvents.size());
        assertTrue(startEvents.get(0) instanceof StartCompensationEvent);
        assertTrue(startEvents.get(1) instanceof StartEscalationEvent);
        assertTrue(startEvents.get(2) instanceof StartConditionalEvent);
        assertTrue(startEvents.get(3) instanceof StartErrorEvent);
        assertTrue(startEvents.get(4) instanceof StartTimerEvent);
        assertTrue(startEvents.get(5) instanceof StartSignalEvent);
        assertTrue(startEvents.get(6) instanceof StartMessageEvent);
        assertTrue(startEvents.get(7) instanceof StartNoneEvent);
    }

    @Test
    public void testNewStartEvent() {
        List<StartEvent> startEvents = processNew.getStartEvents();
        assertEquals(1, startEvents.size());
    }

    @SuppressWarnings("unchecked")
    private List<Node<Process, ?>> getNodes(Diagram<Graph, Metadata> diagram) {
        Graph<?, Node<Process, ?>> graph = diagram.getGraph();
        assertNotNull(graph);
        Iterator<Node<Process, ?>> nodesIterable = graph.nodes().iterator();
        List<Node<Process, ?>> nodes = new ArrayList<>();
        nodesIterable.forEachRemaining(nodes::add);
        return nodes;
    }

    @SuppressWarnings("unchecked")
    private Process getProcess(Diagram<Graph, Metadata> diagram, String id) {
        Node<? extends Process, ?> node = getNodeById(diagram, id);
        return node.getContent();
    }

    @SuppressWarnings("unchecked")
    private Node<? extends Process, ?> getNodeById(Diagram<Graph, Metadata> diagram, String id) {
        Node<? extends Process, ?> node = diagram.getGraph().getNode(id);
        assertThat(node).isNotNull();
        return node;
    }

}

