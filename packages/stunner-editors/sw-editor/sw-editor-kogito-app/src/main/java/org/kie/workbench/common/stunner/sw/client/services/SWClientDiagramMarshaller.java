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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.sw.command.GraphCommands;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;

@ApplicationScoped
public class SWClientDiagramMarshaller {

    private static final String STATE_START = "start";
    private static final String STATE_END = "end";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String START = "start";
    private static final String STATES = "states";
    private static final String TYPE = "type";
    private static final String TYPE_INJECT = "inject";
    private static final String TYPE_SWITCH = "switch";
    private static final String TRANSITION = "transition";
    private static final String END = "end";

    private final DefinitionUtils definitionUtils;
    private final FactoryManager factoryManager;
    private final GraphIndexBuilder indexBuilder;
    private final GraphCommands commands;
    private final List<String> jsonStateIndexes = new ArrayList<>();

    @Inject
    public SWClientDiagramMarshaller(DefinitionUtils definitionUtils,
                                     FactoryManager factoryManager,
                                     GraphIndexBuilder indexBuilder) {
        this.definitionUtils = definitionUtils;
        this.factoryManager = factoryManager;
        this.indexBuilder = indexBuilder;
        this.commands = new GraphCommands();
    }

    // *************************** MARSHALLING ***************************************************

    public int getStateIndex(String uuid) {
        return jsonStateIndexes.indexOf(uuid);
    }

    public int getStatesCount() {
        return jsonStateIndexes.size();
    }

    public void addStateIndex(String uuid) {
        jsonStateIndexes.add(uuid);
    }

    public String marshall(Graph graph) {
        String result = "";
        Iterable<Node> nodes = graph.nodes();
        for (Node node : nodes) {
            Object bean = ((View<?>) node.getContent()).getDefinition();
            if (bean instanceof State) {
                JsPropertyMap<Object> nodeMap = marshall(node);
                String raw = stringify(nodeMap);
                result += raw;
            }
        }
        return result;
    }

    public JsPropertyMap<Object> marshall(Node stateNode) {
        State state = (State) ((View<?>) stateNode.getContent()).getDefinition();
        JsPropertyMap<Object> jsonStateMap = newJsonObject();
        jsonStateMap.set(ID, stateNode.getUUID());
        String name = state.getName();
        jsonStateMap.set(NAME, null == name || name.isEmpty() ? "" : name);
        String type = null;
        if (state instanceof InjectState) {
            type = TYPE_INJECT;
        } else if (state instanceof SwitchState) {
            type = TYPE_SWITCH;
        }
        if (null != type) {
            jsonStateMap.set(TYPE, type);
        }

        List<Edge> outEdges = stateNode.getOutEdges();
        for (Edge edge : outEdges) {
            Object edgeBean = ((View<?>) edge.getContent()).getDefinition();
            if (edgeBean instanceof Transition) {
                Node targetNode = edge.getTargetNode();
                if (null != targetNode) {
                    if (targetNode.getUUID().equals(STATE_END)) {
                        jsonStateMap.set(END, true);
                    } else {
                        // TODO: Obtain bean's name via DefinitionAdapter
                        Object targetBean = ((View<?>) targetNode.getContent()).getDefinition();
                        if (targetBean instanceof State) {
                            String targetName = ((State) targetBean).getName();
                            jsonStateMap.set(TRANSITION, targetName);
                        }
                    }
                }
            }
        }

        return jsonStateMap;
    }

    private static JsPropertyMap<Object> newJsonObject() {
        return Js.uncheckedCast(Global.JSON.parse("{}"));
    }

    public static String stringify(Object jsonObj) {
        return Global.JSON.stringify(jsonObj);
    }

    // *************************** UNMARSHALLING ***************************************************

    public Graph unmarshall(final Metadata metadata,
                            final String raw) {
        jsonStateIndexes.clear();
        return unmarshall(raw);
    }

    private Graph unmarshall(String raw) {
        VerticalLayoutBuilder layoutBuilder = new VerticalLayoutBuilder();
        final CompositeCommand.Builder builder = new CompositeCommand.Builder();
        final Map<String, Node> stateNodes = new HashMap<>();

        Object root = Global.JSON.parse(raw);
        JsPropertyMap<Object> rootMap = Js.uncheckedCast(root);

        // Start State.
        String start = (String) rootMap.get(START);
        Node startNode = null;
        if (null != start) {
            Start startBean = new Start();
            // TODO: The id STATE_START could collide with an existing states from the json model declaration
            startBean.setId(STATE_START);
            Point2D startLocation = layoutBuilder.getNextLocation();
            startNode = createNodeAt(STATE_START, startBean, startLocation, builder);
            stateNodes.put(STATE_START, startNode);
        }

        Object states = rootMap.get(STATES);
        JsArray statesArray = Js.uncheckedCast(states);

        // End State.
        final End end = new End();
        // TODO: The id STATE_END could collide with an existing states from the json model declaration
        end.setId(STATE_END);
        Point2D endLocation = layoutBuilder.getEndLocation(statesArray.length);
        Node endNode = createNodeAt(STATE_END, end, endLocation, builder);
        stateNodes.put(STATE_END, endNode);

        // States.
        for (int i = 0; i < statesArray.length; i++) {
            Object stateRaw = statesArray.getAt(i);
            Point2D nodeLocation = layoutBuilder.getNextLocation();
            Node stateNode = parseState(stateRaw, nodeLocation, endNode, builder);
            stateNodes.put(stateNode.getUUID(), stateNode);
            jsonStateIndexes.add(stateNode.getUUID());
        }

        // Start Transition.
        if (null != startNode) {
            Node target = stateNodes.get(start);
            if (null != target) {
                final StartTransition tstart = new StartTransition();
                Edge tstartEdge = createEdge("tstart", tstart, startNode, builder);
                connect(tstartEdge, startNode, target, builder);
            } else {
                logError("No start state found for [" + start + "]");
            }
        }

        // State Transitions.
        for (int i = 0; i < statesArray.length; i++) {
            Object stateRaw = statesArray.getAt(i);
            parseTransition(stateRaw, stateNodes, builder);
        }

        String id = (String) rootMap.get(ID);
        final GraphImpl<Object> graph = GraphImpl.build(id);
        final CompositeCommand<GraphCommandExecutionContext, RuleViolation> all = builder.build();
        final Index index = indexBuilder.build(graph);
        final DirectGraphCommandExecutionContext context =
                new DirectGraphCommandExecutionContext(definitionUtils.getDefinitionManager(),
                                                       factoryManager,
                                                       index);
        // TODO: Check errors...
        all.execute(context);

        return graph;
    }

    private static void logError(String s) {
        DomGlobal.console.error(s);
    }

    private Edge parseTransition(Object raw,
                                 Map<String, Node> stateNodes,
                                 CompositeCommand.Builder builder) {
        JsPropertyMap<Object> map = Js.uncheckedCast(raw);
        String id = (String) map.get(NAME);
        String transition = (String) map.get(TRANSITION);
        if (null != transition && !transition.isEmpty()) {
            String tid = id + "_to_" + transition;
            Node source = stateNodes.get(id);
            if (null != source) {
                Node target = stateNodes.get(transition);
                if (null != target) {
                    final Transition t = new Transition();
                    t.setId(tid);
                    t.setName(tid);
                    Edge edge = createEdge(tid, t, source, builder);
                    connect(edge, source, target, builder);
                    return edge;
                } else {
                    logError("No state found for [" + transition + "]");
                }
            } else {
                logError("No state found for [" + id + "]");
            }
        }
        return null;
    }

    private Node parseState(Object raw,
                            Point2D location,
                            Node endNode,
                            CompositeCommand.Builder builder) {
        JsPropertyMap<Object> map = Js.uncheckedCast(raw);
        String id = (String) map.get(NAME);
        String name = (String) map.get(NAME);
        String type = (String) map.get(TYPE);
        State state = null;
        switch (type) {
            case TYPE_INJECT:
                state = new InjectState();
                break;
            case TYPE_SWITCH:
                state = new SwitchState();
                break;
        }
        state.setId(id);
        state.setName(name);
        final Node stateNode = createNodeAt(id, state, location, builder);

        if (map.has(END)) {
            boolean end = (boolean) map.get(END);
            if (end) {
                final Transition tend = new Transition();
                tend.setId("tend");
                tend.setName("End");
                Edge tendEdge = createEdge("tend", tend, stateNode, builder);
                connect(tendEdge, stateNode, endNode, builder);
            }
        }

        return stateNode;
    }

    private Graph basicInjectStateExample() {
        final CompositeCommand.Builder builder = new CompositeCommand.Builder();
        final Graph graph = createGraph();

        Start start = new Start();
        start.setId("start");
        Node startNode = createNodeAt("start", start, 137.5, 50, builder);

        final State state1 = new InjectState();
        state1.setId("state1");
        state1.setName("state1");
        final Node state1Node = createNodeAt("state1", state1, 100, 100, builder);

        final End end = new End();
        end.setId("end");
        Node endNode = createNodeAt("end", end, 137.5, 275, builder);

        final StartTransition tstart = new StartTransition();
        Edge tstartEdge = createEdge("tstart", tstart, startNode, builder);
        connect(tstartEdge, startNode, state1Node, builder);

        final Transition tend = new Transition();
        tend.setId("tend");
        tend.setName("End");
        Edge tendEdge = createEdge("tend", tend, state1Node, builder);
        connect(tendEdge, state1Node, endNode, builder);

        final CompositeCommand<GraphCommandExecutionContext, RuleViolation> all = builder.build();
        final Index index = indexBuilder.build(graph);
        final DirectGraphCommandExecutionContext context =
                new DirectGraphCommandExecutionContext(definitionUtils.getDefinitionManager(),
                                                       factoryManager,
                                                       index);
        // TODO: Check errors...
        all.execute(context);

        return graph;
    }

    public void connect(Edge edge, Node source, Node target, CompositeCommand.Builder builder) {
        Command command = commands.setTargetNode(target,
                                                 edge,
                                                 MagnetConnection.Builder.forTarget(source,
                                                                                    target));
        builder.addCommand(command);
    }

    @SuppressWarnings("all")
    public Edge createEdge(String uuid, Object bean, Node source, CompositeCommand.Builder builder) {
        final EdgeImpl edge = new EdgeImpl<>(uuid);
        ViewConnector<Object> content = new ViewConnectorImpl(bean, Bounds.create(0d, 0d, 30d, 30d));
        edge.setContent(content);
        appendLabels(edge.getLabels(),
                     bean);
        builder.addCommand(commands.addConnector(source, edge, MagnetConnection.Builder.atCenter(source)));
        return edge;
    }

    private Node createNodeAt(String uuid, Object bean, double x, double y, CompositeCommand.Builder builder) {
        return createNodeAt(uuid, bean, new Point2D(x, y), builder);
    }

    private Node createNodeAt(String uuid, Object bean, Point2D location, CompositeCommand.Builder builder) {
        Node node = createNode(uuid, bean, location.getX(), location.getY());
        builder.addCommand(commands.addNode(node));
        builder.addCommand(commands.updateElementPosition(node, location));
        return node;
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
    private void appendLabels(final Set<String> target,
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

    private static Graph createGraph() {
        return GraphImpl.build("swExampleGraph");
    }

    private static class VerticalLayoutBuilder {

        private static double X = 200;
        private static double YDELTA = 100;
        private double y = 0;

        private Point2D getNextLocation() {
            y = y + YDELTA;
            return new Point2D(X, y);
        }

        private Point2D getEndLocation(int statesCount) {
            return new Point2D(X, (statesCount + 2) * YDELTA);
        }
    }

    private static class HorizontalLayoutBuilder {

        private static double Y = 100;
        private static double XDELTA = 100;
        private double x = 0;

        private Point2D getNextLocation() {
            x = x + XDELTA;
            return new Point2D(x, Y);
        }

        private Point2D getEndLocation(int statesCount) {
            return new Point2D((statesCount + 3) * XDELTA, Y);
        }
    }
}
