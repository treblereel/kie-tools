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

package org.kie.workbench.common.stunner.sw.marshall;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.sw.autolayout.AutoLayout;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.CallbackState;
import org.kie.workbench.common.stunner.sw.definition.CompensationTransition;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.EventConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.EventState;
import org.kie.workbench.common.stunner.sw.definition.ForEachState;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.OnEvent;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.ParallelState;
import org.kie.workbench.common.stunner.sw.definition.SleepState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.uberfire.client.promise.Promises;

import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.ACTIONS_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.ANY_NODE_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.CALLBACK_STATE_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.EVENT_STATE_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.FOREACH_STATE_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.ONEVENTS_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.OPERATION_STATE_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.STATE_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.STATE_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.StateMarshalling.SWITCH_STATE_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.ANY_EDGE_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.COMPENSATION_TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.COMPENSATION_TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.DATA_CONDITION_TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.DATA_CONDITION_TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.DEFAULT_CONDITION_TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.DEFAULT_CONDITION_TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.ERROR_TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.ERROR_TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.EVENT_CONDITION_TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.EVENT_CONDITION_TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.START_TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.START_TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.TRANSITION_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.TransitionMarshalling.TRANSITION_UNMARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.WorkflowMarshalling.START_NODE_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.WorkflowMarshalling.WORKFLOW_MARSHALLER;
import static org.kie.workbench.common.stunner.sw.marshall.WorkflowMarshalling.WORKFLOW_UNMARSHALLER;

@ApplicationScoped
public class Marshaller {

    public static final int SPACING_LEVEL = 2;
    public static boolean LOAD_DETAILS = false;
    public static final String STATE_START = "startState";
    public static final String STATE_END = "endState";
    public static final String EDGE_START = "startEdge";

    private final DefinitionManager definitionManager;
    private final FactoryManager factoryManager;
    private final Parser parser;
    private final Promises promises;

    private Context context;

    private org.kie.workbench.common.stunner.sw.definition.Workflow_JsonMapperImpl mapper = org.kie.workbench.common.stunner.sw.definition.Workflow_JsonMapperImpl.INSTANCE;


    @Inject
    public Marshaller(DefinitionManager definitionManager,
                      FactoryManager factoryManager,
                      Parser parser,
                      Promises promises) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.parser = parser;
        this.promises = promises;
    }

    @SuppressWarnings("all")
    public Promise<Graph> unmarshallGraph(String raw) {
        final Workflow workflow;
        try {
            workflow = parser.parse(mapper.fromJSON(raw));
        } catch (Exception e) {
            return promises.create(new Promise.PromiseExecutorCallbackFn<Graph>() {
                @Override
                public void onInvoke(ResolveCallbackFn<Graph> resolveCallbackFn,
                                     RejectCallbackFn rejectCallbackFn) {
                    rejectCallbackFn.onInvoke(new ClientRuntimeError("Error parsing JSON file.", e));
                }
            });
        }

        final GraphImpl<Object> graph;
        try {
            // TODO: Use dedicated factory instead.
            graph = GraphImpl.build(workflow.id);
            final Index index = new MapIndexBuilder().build(graph);
            context = new Context(index);
        } catch (Exception ex) {
            return promises.create(new Promise.PromiseExecutorCallbackFn<Graph>() {
                @Override
                public void onInvoke(ResolveCallbackFn<Graph> resolveCallbackFn,
                                     RejectCallbackFn rejectCallbackFn) {
                    rejectCallbackFn.onInvoke(new ClientRuntimeError("Error building graph.", ex));
                }
            });
        }

        final BuilderContext builderContext;
        try {
            builderContext = new BuilderContext(context, definitionManager, factoryManager);
            unmarshallNode(builderContext, workflow);
        } catch (Exception ex) {
            return promises.create(new Promise.PromiseExecutorCallbackFn<Graph>() {
                @Override
                public void onInvoke(ResolveCallbackFn<Graph> resolveCallbackFn,
                                     RejectCallbackFn rejectCallbackFn) {
                    rejectCallbackFn.onInvoke(new ClientRuntimeError("Error unmarshalling nodes.", ex));
                }
            });
        }

        try {
            builderContext.execute();
        } catch (Exception ex) {
            return promises.create(new Promise.PromiseExecutorCallbackFn<Graph>() {
                @Override
                public void onInvoke(ResolveCallbackFn<Graph> resolveCallbackFn,
                                     RejectCallbackFn rejectCallbackFn) {
                    rejectCallbackFn.onInvoke(new ClientRuntimeError("Error executing builder context.", ex));
                }
            });
        }

        try {
            final Promise<Node> layout = AutoLayout.applyLayout(graph, context.getWorkflowRootNode(), promises, builderContext.buildExecutionContext(), false);
            return promises.create(new Promise.PromiseExecutorCallbackFn<Graph>() {
                @Override
                public void onInvoke(ResolveCallbackFn<Graph> success, RejectCallbackFn reject) {
                    layout.then(new IThenable.ThenOnFulfilledCallbackFn<Node, Object>() {
                        @Override
                        public IThenable<Object> onInvoke(Node node) {
                            success.onInvoke(graph);
                            return null;
                        }
                    });
                }
            });
        } catch (Exception ex) {
            return promises.create(new Promise.PromiseExecutorCallbackFn<Graph>() {
                @Override
                public void onInvoke(ResolveCallbackFn<Graph> resolveCallbackFn,
                                     RejectCallbackFn rejectCallbackFn) {
                    rejectCallbackFn.onInvoke(new ClientRuntimeError("Error applying auto-layout.", ex));
                }
            });
        }
    }

    @SuppressWarnings("all")
    public Promise<String> marshallGraph(Graph graph) {
        // TODO: Obtain the root node from the graph argument.
        return marshallNode(context.getWorkflowRootNode());
    }

    public Context getContext() {
        return context;
    }

    public static Object parse(String raw) {
        return Global.JSON.parse(raw);
    }

    public static String stringify(Object jsonObj) {
        return Global.JSON.stringify(jsonObj, (key, value) -> {
            if (null == value) {
                return Global.undefined;
            }
            if (key.contains("hashCode") ||
                    key.contains("host") ||
                    key.contains("labels") ||
                    key.startsWith("$")) {
                return Global.undefined;
            }
            return value;
        }, SPACING_LEVEL);
    }

    /* +++++++++++++++++ UN-MARSHALLING +++++++++++++++++ */

    @SuppressWarnings("all")
    public Promise<Node> unmarshallNode(Object bean) {
        final BuilderContext builderContext = new BuilderContext(context, definitionManager, factoryManager);
        Node node = unmarshallNode(builderContext, bean);
        // TODO: Handle errors? if any (no rules execution context)?
        builderContext.execute();
        return AutoLayout.applyLayout(context.getGraph(), node, promises, builderContext.buildExecutionContext(), true);
    }

    @FunctionalInterface
    public interface NodeUnmarshaller<T> {

        Node<? extends Definition<?>, Edge> unmarshall(BuilderContext context, T bean);
    }

    @FunctionalInterface
    public interface EdgeUnmarshaller<T> {

        Edge<ViewConnector<T>, Node> unmarshall(BuilderContext context, T domainBean);
    }

    public static Node unmarshallNode(BuilderContext context, Object bean) {
        return getNodeUnmarshaller(bean).unmarshall(context, bean);
    }

    public static Edge<ViewConnector<Object>, Node> unmarshallEdge(BuilderContext context, Object bean) {
        return getEdgeUnmarshaller(bean).unmarshall(context, bean);
    }

    @SuppressWarnings("unchecked")
    public static <T> NodeUnmarshaller<T> getNodeUnmarshaller(Object bean) {
        final Class<?> type = bean.getClass();
        if (Workflow.class.equals(type)) {
            return (NodeUnmarshaller<T>) WORKFLOW_UNMARSHALLER;
        } else if (EventState.class.equals(type)) {
            return (NodeUnmarshaller<T>) EVENT_STATE_UNMARSHALLER;
        } else if (OperationState.class.equals(type)) {
            return (NodeUnmarshaller<T>) OPERATION_STATE_UNMARSHALLER;
        } else if (InjectState.class.equals(type)) {
            return (NodeUnmarshaller<T>) STATE_UNMARSHALLER;
        } else if (SwitchState.class.equals(type)) {
            return (NodeUnmarshaller<T>) SWITCH_STATE_UNMARSHALLER;
        } else if (SleepState.class.equals(type)) {
            return (NodeUnmarshaller<T>) STATE_UNMARSHALLER;
        } else if (ParallelState.class.equals(type)) {
            return (NodeUnmarshaller<T>) STATE_UNMARSHALLER;
        } else if (ForEachState.class.equals(type)) {
            return (NodeUnmarshaller<T>) FOREACH_STATE_UNMARSHALLER;
        } else if (CallbackState.class.equals(type)) {
            return (NodeUnmarshaller<T>) CALLBACK_STATE_UNMARSHALLER;
        } else if (State.class.equals(type)) {
            return (NodeUnmarshaller<T>) STATE_UNMARSHALLER;
        } else if (ActionNode[].class.equals(type)) {
            return (NodeUnmarshaller<T>) ACTIONS_UNMARSHALLER;
        } else if (OnEvent[].class.equals(type)) {
            return (NodeUnmarshaller<T>) ONEVENTS_UNMARSHALLER;
        }
        throw new UnsupportedOperationException("No NodeUnmarshaller found for " + type.getName());
    }

    @SuppressWarnings("unchecked")
    public static <T> EdgeUnmarshaller<T> getEdgeUnmarshaller(Object bean) {
        final Class<?> type = bean.getClass();
        if (StartTransition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) START_TRANSITION_UNMARSHALLER;
        }
        if (Transition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) TRANSITION_UNMARSHALLER;
        }
        if (ErrorTransition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) ERROR_TRANSITION_UNMARSHALLER;
        }
        if (CompensationTransition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) COMPENSATION_TRANSITION_UNMARSHALLER;
        }
        if (DefaultConditionTransition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) DEFAULT_CONDITION_TRANSITION_UNMARSHALLER;
        }
        if (EventConditionTransition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) EVENT_CONDITION_TRANSITION_UNMARSHALLER;
        }
        if (DataConditionTransition.class.equals(type)) {
            return (EdgeUnmarshaller<T>) DATA_CONDITION_TRANSITION_UNMARSHALLER;
        }
        throw new UnsupportedOperationException("No EdgeUnmarshaller found for " + type.getName());
    }


    /* +++++++++++++++++ MARSHALLING +++++++++++++++++ */

    @SuppressWarnings("all")
    public Promise<String> marshallNode(Node node) {
        Workflow bean = marshallNode(context, node);

        String raw = mapper.toJSON(bean);
        return promises.resolve(raw);
    }

    @FunctionalInterface
    public interface NodeMarshaller<T> {

        T marshall(Context context, Node<? extends Definition<T>, Edge> node);
    }

    @FunctionalInterface
    public interface EdgeMarshaller<T> {

        Edge<ViewConnector<T>, Node> marshall(Context context, Edge<ViewConnector<T>, Node> edge);
    }

    @SuppressWarnings("all")
    public static <T> T marshallNode(Context context, Node node) {
        return (T) getNodeMarshaller(node).marshall(context, node);
    }

    @SuppressWarnings("all")
    public static Edge<ViewConnector<Object>, Node> marshallEdge(Context context, Edge edge) {
        return getEdgeMarshaller(edge).marshall(context, edge);
    }

    @SuppressWarnings("all")
    public static boolean hasNodeMarshaller(Node node) {
        Object bean = ((Definition) node.getContent()).getDefinition();
        NodeMarshaller<Object> marshaller = getNodeMarshallerForBean(bean);
        return null != marshaller;
    }

    @SuppressWarnings("all")
    public static <T> NodeMarshaller<T> getNodeMarshaller(Node node) {
        Object bean = ((Definition) node.getContent()).getDefinition();
        NodeMarshaller<Object> marshaller = getNodeMarshallerForBean(bean);
        if (null == marshaller) {
            DomGlobal.console.warn("No NodeMarshaller found for " + bean.getClass().getName());
            marshaller = (NodeMarshaller<Object>) ANY_NODE_MARSHALLER;
        }
        return (NodeMarshaller<T>) marshaller;
    }

    @SuppressWarnings("all")
    public static <T> EdgeMarshaller<T> getEdgeMarshaller(Edge edge) {
        Object bean = ((Definition) edge.getContent()).getDefinition();
        EdgeMarshaller<Object> marshaller = getEdgeMarshallerForBean(bean);
        if (null == marshaller) {
            final Class<?> type = bean.getClass();
            DomGlobal.console.warn("No EdgeMarshaller found for " + bean.getClass().getName());
            marshaller = (EdgeMarshaller<Object>) ANY_EDGE_MARSHALLER;
        }
        return (EdgeMarshaller<T>) marshaller;
    }

    @SuppressWarnings("unchecked")
    public static <T> NodeMarshaller<T> getNodeMarshallerForBean(Object bean) {
        final Class<?> type = bean.getClass();
        if (Workflow.class.equals(type)) {
            return (NodeMarshaller<T>) WORKFLOW_MARSHALLER;
        } else if (Start.class.equals(type)) {
            return (NodeMarshaller<T>) START_NODE_MARSHALLER;
        } else if (EventState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        } else if (InjectState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        } else if (OperationState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        } else if (SwitchState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        } else if (SleepState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        } else if (ParallelState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        } else if (ForEachState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        } else if (CallbackState.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        } else if (State.class.equals(type)) {
            return (NodeMarshaller<T>) STATE_MARSHALLER;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> EdgeMarshaller<T> getEdgeMarshallerForBean(Object bean) {
        final Class<?> type = bean.getClass();
        if (StartTransition.class.equals(type)) {
            return (EdgeMarshaller<T>) START_TRANSITION_MARSHALLER;
        }
        if (Transition.class.equals(type)) {
            return (EdgeMarshaller<T>) TRANSITION_MARSHALLER;
        }
        if (ErrorTransition.class.equals(type)) {
            return (EdgeMarshaller<T>) ERROR_TRANSITION_MARSHALLER;
        }
        if (CompensationTransition.class.equals(type)) {
            return (EdgeMarshaller<T>) COMPENSATION_TRANSITION_MARSHALLER;
        }
        if (DefaultConditionTransition.class.equals(type)) {
            return (EdgeMarshaller<T>) DEFAULT_CONDITION_TRANSITION_MARSHALLER;
        }
        if (EventConditionTransition.class.equals(type)) {
            return (EdgeMarshaller<T>) EVENT_CONDITION_TRANSITION_MARSHALLER;
        }
        if (DataConditionTransition.class.equals(type)) {
            return (EdgeMarshaller<T>) DATA_CONDITION_TRANSITION_MARSHALLER;
        }
        return null;
    }

    public static boolean isStartState(Node node) {
        return ((View<?>) node.getContent()).getDefinition() instanceof Start;
    }

    public static boolean isEndState(Node node) {
        return ((View<?>) node.getContent()).getDefinition() instanceof End;
    }
}
