/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.FlowElement;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Association;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.BaseIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.DataObjectReference;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Definitions_XMLMapperImpl;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.ExtensionElements;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.FlowNodeRef;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Incoming;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.NonDirectionalAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Outgoing;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Relationship;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Signal;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmndi.BpmnDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmndi.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmndi.BpmnPlane;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmndi.BpmnShape;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpsim.BPSimData;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpsim.ElementParameters;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpsim.Scenario;
import org.kie.workbench.common.stunner.bpmn.definition.models.dc.Bounds;
import org.kie.workbench.common.stunner.bpmn.definition.models.di.Waypoint;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

import static java.util.stream.StreamSupport.stream;

@ApplicationScoped
public class BPMNClientMarshalling {

    private static Definitions_XMLMapperImpl mapper = Definitions_XMLMapperImpl.INSTANCE;

    @Inject
    public BPMNClientMarshalling() {
    }

    @PostConstruct
    public void init() {
    }

    @SuppressWarnings("unchecked")
    public String marshall(final Diagram diagram) {
        Definitions definitions = createDefinitions(diagram.getGraph());
        try {
            return mapper.write(definitions);
        } catch (XMLStreamException e) {
            return "";
        }
    }

    Definitions createDefinitions(Graph graph) {
        Iterable<NodeImpl<ViewImpl<BPMNViewDefinition>>> nodes = graph.nodes();
        IdGenerator.reset();
        Definitions definitions = new Definitions();
        BpmnDiagram bpmnDiagram = new BpmnDiagram();
        BpmnPlane plane = new BpmnPlane();
        List<ElementParameters> simulationElements = new ArrayList<>();

        bpmnDiagram.setBpmnPlane(plane);
        definitions.setBpmnDiagram(bpmnDiagram);

        Process process = (Process) stream(nodes.spliterator(), false)
                .map(node -> node.getContent().getDefinition())
                .filter(node -> node instanceof Process)
                .findFirst().orElse(new Process());
        setProcessVariablesToDefinitions(definitions, process.getProperties());
        definitions.setProcess(process);
        plane.setBpmnElement(process.getName());

        List<SequenceFlow> sequenceFlows = new ArrayList<>();
        for (final NodeImpl<ViewImpl<BPMNViewDefinition>> node : nodes) {
            BPMNViewDefinition definition = node.getContent().getDefinition();
            if (definition instanceof Process) {
                continue;
            }

            if (definition instanceof FlowElement) {
                // All except Lanes
                ((FlowElement) definition).setId(IdGenerator.getNextIdFor(definition, node.getUUID()));
            }

            if (definition instanceof StartEvent) {
                StartEvent startEvent = (StartEvent) definition;
                process.getStartEvents().add(startEvent);

                // Sequence Flows
                List<Outgoing> outgoing = checkOutgoingFlows(node.getOutEdges(), startEvent.getId(), sequenceFlows, plane);
                startEvent.setOutgoing(outgoing);

                // Type ID
                if (startEvent instanceof StartMessageEvent) {
                    StartMessageEvent startMessage = (StartMessageEvent) startEvent;
                    startMessage.setMessageId(IdGenerator.getTypeId(startMessage));
                    definitions.getMessages().add(startMessage.getMessage());
                }

                // Type ID
                if (startEvent instanceof StartSignalEvent) {
                    StartSignalEvent startSignal = (StartSignalEvent) startEvent;
                    startSignal.setSignalId(IdGenerator.getTypeId(startSignal));
                    Signal signal = startSignal.getSignal();
                    if (signal != null) {
                        definitions.getSignals().add(signal);
                    }
                    definitions.getItemDefinitions().addAll(startSignal.getItemDefinition());
                }

                // Adding simulation properties
                simulationElements.add(startEvent.getElementParameters());
            }

            if (definition instanceof EndEvent) {
                EndEvent endEvent = (EndEvent) definition;
                process.getEndEvents().add(endEvent);

                // Sequence Flows
                List<Incoming> incoming = checkIncomingFlows(node.getInEdges(), endEvent.getId(), sequenceFlows, plane);
                endEvent.setIncoming(incoming);

                // Type ID
                if (endEvent instanceof EndSignalEvent) {
                    EndSignalEvent endSignal = (EndSignalEvent) endEvent;
                    endSignal.setSignalId(IdGenerator.getTypeId(endEvent));
                    Signal signal = endSignal.getSignal();
                    if (signal != null) {
                        definitions.getSignals().add(signal);
                    }
                    definitions.getItemDefinitions().addAll(endSignal.getItemDefinition());
                }

                // Adding simulation properties
                simulationElements.add(endEvent.getElementParameters());
            }

            if (definition instanceof BaseIntermediateEvent) {
                BaseIntermediateEvent intermediateEvent = (BaseIntermediateEvent) definition;

                // Sequence Flows
                List<Incoming> incoming = checkIncomingFlows(node.getInEdges(), intermediateEvent.getId(), sequenceFlows, plane);
                intermediateEvent.setIncoming(incoming);
                List<Outgoing> outgoing = checkOutgoingFlows(node.getOutEdges(), intermediateEvent.getId(), sequenceFlows, plane);
                intermediateEvent.setOutgoing(outgoing);

                // Type ID
                if (intermediateEvent instanceof IntermediateSignalEventThrowing) {
                    IntermediateSignalEventThrowing throwingSignal = (IntermediateSignalEventThrowing) intermediateEvent;
                    throwingSignal.setSignalId(IdGenerator.getTypeId(intermediateEvent));
                    Signal signal = throwingSignal.getSignal();
                    if (signal != null) {
                        definitions.getSignals().add(signal);
                    }
                    definitions.getItemDefinitions().addAll(throwingSignal.getItemDefinition());
                    process.getIntermediateThrowEvent().add(throwingSignal);
                }

                if (intermediateEvent instanceof IntermediateSignalEventCatching) {
                    IntermediateSignalEventCatching catchingSignal = (IntermediateSignalEventCatching) intermediateEvent;
                    catchingSignal.setSignalId(IdGenerator.getTypeId(intermediateEvent));
                    Signal signal = catchingSignal.getSignal();
                    if (signal != null) {
                        definitions.getSignals().add(signal);
                    }
                    definitions.getItemDefinitions().addAll(catchingSignal.getItemDefinition());
                    process.getIntermediateCatchEvent().add(catchingSignal);
                }

                // Adding simulation properties
                simulationElements.add(intermediateEvent.getElementParameters());
            }

            if (definition instanceof TextAnnotation) {
                TextAnnotation annotation = (TextAnnotation) definition;
                process.getTextAnnotations().add(annotation);

                node.getInEdges().forEach(edge -> {
                    if (edge.getContent() instanceof ViewConnectorImpl) {
                        ViewConnectorImpl connector = (ViewConnectorImpl) edge.getContent();
                        Association association = (NonDirectionalAssociation) connector.getDefinition();
                        association.setSourceRef(edge.getSourceNode().getUUID());
                        association.setTargetRef(edge.getTargetNode().getUUID());
                        association.setId(IdGenerator.getNextIdFor(association, edge.getUUID()));

                        BpmnEdge edgeNode = new BpmnEdge();
                        edgeNode.setBpmnElement(association.getId());
                        edgeNode.getWaypoint().addAll(createWaypoints(connector));
                        plane.getBpmnEdges().add(edgeNode);
                        process.getAssociations().add(association);
                    }
                });
            }

            if (definition instanceof BaseTask) {
                BaseTask task = (BaseTask) definition;
                if (definition instanceof ScriptTask) {
                    process.getScriptTasks().add(task);
                } else if (definition instanceof UserTask) {
                    process.getUserTasks().add(task);

                    UserTask uTask = (UserTask) task;
                    definitions.getItemDefinitions().addAll(uTask.getItemDefinitions());
                } else {
                    process.getTasks().add(task);
                }

                // Sequence Flows
                List<Outgoing> outgoing = checkOutgoingFlows(node.getOutEdges(), task.getId(), sequenceFlows, plane);
                List<Incoming> incoming = checkIncomingFlows(node.getInEdges(), task.getId(), sequenceFlows, plane);
                task.setIncoming(incoming);
                task.setOutgoing(outgoing);
            }

            if (definition instanceof BaseGateway) {
                BaseGateway gateway = (BaseGateway) definition;

                if (gateway instanceof ExclusiveGateway) {
                    process.getExclusiveGateways().add((ExclusiveGateway) gateway);
                }

                if (gateway instanceof ParallelGateway) {
                    process.getParallelGateways().add((ParallelGateway) gateway);
                }

                if (gateway instanceof EventGateway) {
                    process.getEventBasedGateways().add((EventGateway) gateway);
                }

                if (gateway instanceof InclusiveGateway) {
                    process.getInclusiveGateways().add((InclusiveGateway) gateway);
                }

                // Sequence Flows
                List<Outgoing> outgoing = checkOutgoingFlows(node.getOutEdges(), gateway.getId(), sequenceFlows, plane);
                List<Incoming> incoming = checkIncomingFlows(node.getInEdges(), gateway.getId(), sequenceFlows, plane);
                gateway.setIncoming(incoming);
                gateway.setOutgoing(outgoing);
            }

            if (definition instanceof Lane) {
                Lane lane = (Lane) definition;
                lane.setId(IdGenerator.getNextIdFor(definition, node.getUUID()));
                process.getLanes().add(lane);
            }

            if (definition instanceof DataObjectReference) {
                DataObjectReference dataObject = (DataObjectReference) definition;
                definitions.getItemDefinitions().add(dataObject.getItemDefinition());
                process.getDataObjects().add(dataObject.getDataObject());
                process.getDataObjectsReference().add(dataObject);
            }

            // Adding Shape to Diagram
            plane.getBpmnShapes().add(
                    createShapeForBounds(node.getContent().getBounds(), definition.getId())
            );
        }

        // When all IDs ready push children to lanes
        for (final NodeImpl<ViewImpl<BPMNViewDefinition>> node : nodes) {
            BPMNViewDefinition definition = node.getContent().getDefinition();
            if (definition instanceof Lane) {
                addChildrenToLane(graph, node);
            }
        }

        // Update associations IDs when all IDs are ready.
        for (Association association : process.getAssociations()) {
            association.setTargetRef(IdGenerator.getIdBUuid(association.getTargetRef()));
            association.setSourceRef(IdGenerator.getIdBUuid(association.getSourceRef()));
            for (BpmnEdge edge : plane.getBpmnEdges()) {
                if (Objects.equals(edge.getBpmnElement(), association.getId())) {
                    edge.setId("edge_shape_" + association.getSourceRef() + "_to_shape_" + association.getTargetRef());
                    break;
                }
            }
        }

        // Set BpmnEdges ids now when all sources and targets are ready
        for (SequenceFlow flow : sequenceFlows) {
            for (BpmnEdge edge : plane.getBpmnEdges()) {
                if (Objects.equals(edge.getBpmnElement(), flow.getId())) {
                    edge.setId("edge_shape_" + flow.getSourceRef() + "_to_shape_" + flow.getTargetRef());
                    break;
                }
            }
        }
        process.getSequenceFlows().addAll(sequenceFlows);

        Scenario scenario = new Scenario();
        scenario.setElementParameters(simulationElements);

        BPSimData simData = new BPSimData();
        simData.setScenario(scenario);

        ExtensionElements extensionElements = new ExtensionElements();
        extensionElements.setBpSimData(simData);

        Relationship relationship = new Relationship();
        relationship.setTarget("BPSimData");
        relationship.setExtensionElements(extensionElements);
        relationship.setTarget(definitions.getId());
        relationship.setSource(definitions.getId());
        definitions.setRelationship(relationship);

        return definitions;
    }

    private void addChildrenToLane(Graph graph, NodeImpl<ViewImpl<BPMNViewDefinition>> parent) {
        ChildrenTraverseProcessorImpl processor = new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl());
        processor
                .setRootUUID(parent.getUUID())
                .traverse(graph,
                          new ChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>>() {

                              @Override
                              public void startGraphTraversal(Graph<DefinitionSet, Node<View, Edge>> graph) {

                              }

                              @Override
                              public void startEdgeTraversal(Edge<Child, Node> edge) {

                              }

                              @Override
                              public void endEdgeTraversal(Edge<Child, Node> edge) {

                              }

                              @Override
                              public void startNodeTraversal(Node<View, Edge> node) {

                              }

                              @Override
                              public void endNodeTraversal(Node<View, Edge> node) {

                              }

                              @Override
                              public void endGraphTraversal() {

                              }

                              @Override
                              public boolean startNodeTraversal(List<Node<View, Edge>> parents,
                                                                Node<View, Edge> node) {
                                  Lane lane = (Lane) parent.getContent().getDefinition();
                                  if (node.getContent().getDefinition() instanceof FlowElement) {
                                      FlowElement element = (FlowElement) node.getContent().getDefinition();

                                      FlowNodeRef ref = new FlowNodeRef();
                                      ref.setValue(element.getId());
                                      lane.getFlowNodeRefs().add(ref);
                                  }
                                  return false;
                              }
                          });
    }

    private void setProcessVariablesToDefinitions(Definitions definitions, List<Property> processVariables) {
        processVariables.forEach(processVariable -> {
            ItemDefinition itemDefinition = new ItemDefinition(processVariable.getItemSubjectRef(), processVariable.getVariableType());
            definitions.getItemDefinitions().add(itemDefinition);
        });
    }

    private List<Outgoing> checkOutgoingFlows(List<Edge> edges, String nodeId, List<SequenceFlow> sequenceFlows, BpmnPlane plane) {
        List<Outgoing> outgoing = new ArrayList<>();
        edges.forEach(edge -> {
            if (edge.getContent() instanceof ViewConnectorImpl) {
                ViewConnector connector = (ViewConnector) edge.getContent();
                if (connector.getDefinition() instanceof SequenceFlow) {
                    DiscreteConnection sourceConnection = (DiscreteConnection) connector.getSourceConnection().get();
                    SequenceFlow flow = (SequenceFlow) connector.getDefinition();
                    flow.setAutoConnectionSource(sourceConnection.isAuto());
                    flow.setId(edge.getUUID());
                    addOutgoingFlow(flow, nodeId, sequenceFlows, createWaypoints(connector), plane);

                    outgoing.add(new Outgoing(flow.getId()));
                }
            }
        });
        return outgoing;
    }

    private List<Incoming> checkIncomingFlows(List<Edge> edges, String nodeId, List<SequenceFlow> sequenceFlows, BpmnPlane plane) {
        List<Incoming> incoming = new ArrayList<>();
        edges.forEach(edge -> {
            if (edge.getContent() instanceof ViewConnectorImpl) {
                ViewConnector connector = (ViewConnector) edge.getContent();
                if (connector.getDefinition() instanceof SequenceFlow) {
                    DiscreteConnection targetConnection = (DiscreteConnection) connector.getTargetConnection().get();
                    SequenceFlow flow = (SequenceFlow) connector.getDefinition();
                    flow.setAutoConnectionTarget(targetConnection.isAuto());
                    flow.setId(edge.getUUID());

                    addIncomingFlow(flow, nodeId, sequenceFlows, createWaypoints(connector), plane);

                    incoming.add(new Incoming(flow.getId()));
                }
            }
        });
        return incoming;
    }

    private void addOutgoingFlow(SequenceFlow flow, String id, List<SequenceFlow> sequenceFlows, List<Waypoint> waypoints, BpmnPlane plane) {
        for (SequenceFlow f : sequenceFlows) {
            if (Objects.equals(flow.getId(), f.getId())) {
                f.setSourceRef(id);
                return;
            }
        }

        flow.setSourceRef(id);
        sequenceFlows.add(flow);
        BpmnEdge edge = createBpmnEdge(flow, waypoints);
        plane.getBpmnEdges().add(edge);
    }

    private void addIncomingFlow(SequenceFlow flow, String id, List<SequenceFlow> sequenceFlows, List<Waypoint> waypoints, BpmnPlane plane) {
        for (SequenceFlow f : sequenceFlows) {
            if (Objects.equals(flow.getId(), f.getId())) {
                f.setTargetRef(id);
                return;
            }
        }

        flow.setTargetRef(id);
        sequenceFlows.add(flow);
        BpmnEdge edge = createBpmnEdge(flow, waypoints);
        plane.getBpmnEdges().add(edge);
    }

    private List<Waypoint> createWaypoints(ViewConnector connector) {
        List<Waypoint> waypoints = new ArrayList<>();

        // Source point
        Point2D sourcePoint = ((Connection) connector.getSourceConnection().get()).getLocation();
        Waypoint source = new Waypoint(sourcePoint.getX(), sourcePoint.getY());
        waypoints.add(source);

        // Waypoints
        for (ControlPoint point : connector.getControlPoints()) {
            waypoints.add(new Waypoint(
                                  point.getLocation().getX(),
                                  point.getLocation().getY()
                          )
            );
        }

        // Target point
        Point2D targetPoint = ((Connection) connector.getTargetConnection().get()).getLocation();
        Waypoint target = new Waypoint(targetPoint.getX(), targetPoint.getY());
        waypoints.add(target);

        return waypoints;
    }

    private BpmnEdge createBpmnEdge(SequenceFlow flow, List<Waypoint> waypoints) {
        BpmnEdge edge = new BpmnEdge();
        // ID can't be set yet, since target is not set yet.
        edge.setBpmnElement(flow.getId());

        edge.setWaypoint(waypoints);
        return edge;
    }

    private BpmnShape createShapeForBounds(final org.kie.workbench.common.stunner.core.graph.content.Bounds bounds, final String id) {
        BpmnShape shape = new BpmnShape("shape_" + id, id);
        Bounds b = new Bounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        shape.setBounds(b);

        return shape;
    }

    public Definitions unmarshall(final String raw) {
        try {
            return mapper.read(raw);
        } catch (XMLStreamException e) {
            return new org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Definitions();
        }
    }

    public static String getDefinitionSetId() {
        return BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);
    }
}
