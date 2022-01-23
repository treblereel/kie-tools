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

package org.kie.workbench.common.stunner.sw.client.services;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Transition;

@ApplicationScoped
public class SWGraphExamples {

    private final DefinitionUtils definitionUtils;

    @Inject
    public SWGraphExamples(DefinitionUtils definitionUtils) {
        this.definitionUtils = definitionUtils;
    }

    @SuppressWarnings("all")
    public Graph basicInjectStates() {
        final Graph graph = createGraph();

        // Start.
        final Start start = new Start();
        start.setId("start");
        Node startNode = createEventNode("start", start, 137.5, 50);
        graph.addNode(startNode);

        // State1.
        final State state1 = configureState("state1", new InjectState());
        final Node state1Node = createStateNode("state1", state1, 100, 100);
        graph.addNode(state1Node);

        // State2.
        final State state2 = configureState("state2", new InjectState());
        final Node state2Node = createStateNode("state2", state2, 100, 200);
        graph.addNode(state2Node);

        // End.
        final End end = new End();
        end.setId("end");
        Node endNode = createEventNode("end", end, 137.5, 275);
        graph.addNode(endNode);

        // Tstart.
        final StartTransition tstart = new StartTransition();
        Edge tstartEdge = createEdge("tstart", tstart);
        tstartEdge = connect(tstartEdge, startNode, state1Node);

        // T12
        final Transition t12 = createTransition("t12");
        Edge t12Edge = createEdge("t12", t12);
        t12Edge = connect(t12Edge, state1Node, state2Node);

        // Tend.
        final Transition tend = createTransition("tend");
        Edge tendEdge = createEdge("tend", tend);
        tendEdge = connect(tendEdge, state2Node, endNode);

        return graph;
    }

    @SuppressWarnings("all")
    public Graph justASingleStateThere() {
        final Graph graph = createGraph();
        final State state1 = configureState("state1", new InjectState());
        final Node state1Node = createStateNode("state1", state1, 50, 50);
        graph.addNode(state1Node);
        return graph;
    }

    private static Transition createTransition(String id) {
        final Transition transition = new Transition();
        transition.setId(id);
        transition.setName(id);
        return transition;
    }

    @SuppressWarnings("all")
    public Edge createEdge(String uuid, Object bean) {
        final EdgeImpl edge = new EdgeImpl<>(uuid);
        ViewConnector<Object> content = new ViewConnectorImpl(bean, Bounds.create(0d, 0d, 30d, 30d));
        edge.setContent(content);
        appendLabels(edge.getLabels(),
                     bean);
        return edge;
    }

    @SuppressWarnings("all")
    private static Edge connect(Edge edge, Node source, Node target) {
        edge.setSourceNode(source);
        edge.setTargetNode(target);
        source.getOutEdges().add(edge);
        target.getInEdges().add(edge);
        ViewConnector tendConnector = (ViewConnector) edge.getContent();
        tendConnector.setSourceConnection(MagnetConnection.Builder.atCenter(source));
        tendConnector.setTargetConnection(MagnetConnection.Builder.atCenter(target));
        return edge;
    }

    public Node createEventNode(String uuid, Object bean, double x, double y) {
        return createNode(uuid, bean, x, y);
    }

    public Node createStateNode(String uuid, Object bean, double x, double y) {
        return createNode(uuid, bean, x, y);
    }

    private static State configureState(String id, State s) {
        s.setId(id);
        s.setName(id);
        return s;
    }

    private static Graph createGraph() {
        return GraphImpl.build("swExampleGraph");
    }

    @SuppressWarnings("all")
    public Node createNode(String uuid, Object bean, double x, double y) {
        final Node node = new NodeImpl(uuid);
        final Bounds bounds = definitionUtils.buildBounds(bean, x, y);
        final View content = new ViewImpl<>(bean, bounds);
        node.setContent(content);
        appendLabels(node.getLabels(),
                     bean);
        return node;
    }

    @SuppressWarnings("all")
    public void appendLabels(final Set<String> target,
                             final Object definition) {
        final String[] labels = computeLabels(definitionUtils.getDefinitionManager()
                                                      .adapters()
                                                      .registry()
                                                      .getDefinitionAdapter(definition.getClass()),
                                              definition);
        for (String label : labels) {
            target.add(label);
        }
    }

    @SuppressWarnings("all")
    private static <T> String[] computeLabels(final DefinitionAdapter<T> adapter,
                                              final T definition) {
        final Set<String> target = new HashSet<>();
        final DefinitionId id = adapter.getId(definition);
        target.add(id.value());
        if (id.isDynamic()) {
            target.add(id.type());
        }
        String[] labels = adapter.getLabels(definition);
        if (null != labels) {
            for (String label : labels) {
                target.add(label);
            }
        }
        return target.toArray(new String[target.size()]);
    }
}
