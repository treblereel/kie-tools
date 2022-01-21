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

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientService;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.FlowNode;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.FlowNodeRef;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.StartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmndi.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.definition.models.bpmndi.BpmnShape;
import org.kie.workbench.common.stunner.bpmn.definition.models.di.Waypoint;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNDiagramFactory;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.kogito.client.service.AbstractKogitoClientDiagramService;
import org.uberfire.client.promise.Promises;

import static org.kie.workbench.common.stunner.bpmn.util.XmlUtils.createValidId;

@ApplicationScoped
public class BPMNClientDiagramService extends AbstractKogitoClientDiagramService {

    static final String DEFAULT_PACKAGE = "com.example";
    static final String NO_DIAGRAM_MESSAGE = "No BPMN Diagram can be found.";

    private final DefinitionManager definitionManager;
    private final BPMNClientMarshalling marshalling;
    private final FactoryManager factoryManager;
    private final BPMNDiagramFactory diagramFactory;
    private final ShapeManager shapeManager;
    private final Promises promises;
    private final WorkItemDefinitionClientService widService;
    private final NodeFactoryImpl nodeFactory;
    private final EdgeFactoryImpl edgeFactory;
    private final DefinitionUtils definitionUtils;

    //CDI proxy
    protected BPMNClientDiagramService() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Inject
    public BPMNClientDiagramService(final DefinitionManager definitionManager,
                                    final BPMNClientMarshalling marshalling,
                                    final FactoryManager factoryManager,
                                    final BPMNDiagramFactory diagramFactory,
                                    final ShapeManager shapeManager,
                                    final Promises promises,
                                    final WorkItemDefinitionClientService widService,
                                    final NodeFactoryImpl nodeFactory,
                                    final EdgeFactoryImpl edgeFactory,
                                    final DefinitionUtils definitionUtils) {
        this.definitionManager = definitionManager;
        this.marshalling = marshalling;
        this.factoryManager = factoryManager;
        this.diagramFactory = diagramFactory;
        this.shapeManager = shapeManager;
        this.promises = promises;
        this.widService = widService;
        this.nodeFactory = nodeFactory;
        this.edgeFactory = edgeFactory;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public void transform(final String xml,
                          final ServiceCallback<Diagram> callback) {
        doTransform(DEFAULT_DIAGRAM_ID, xml, callback);
    }

    @Override
    public void transform(final String fileName,
                          final String xml,
                          final ServiceCallback<Diagram> callback) {
        doTransform(createDiagramTitleFromFilePath(fileName), xml, callback);
    }

    private void doTransform(final String fileName,
                             final String xml,
                             final ServiceCallback<Diagram> callback) {
        final Metadata metadata = createMetadata();
        widService
                .call(metadata)
                .then(wid -> {
                    Diagram diagram = doTransform(fileName, xml);
                    callback.onSuccess(diagram);
                    return promises.resolve();
                })
                .catch_((Promise.CatchOnRejectedCallbackFn<Collection<WorkItemDefinition>>) error -> {
                    callback.onError(new ClientRuntimeError(new DiagramParsingException(metadata, xml)));
                    return promises.resolve();
                });
    }

    private Diagram doTransform(final String fileName,
                                final String xml) {

        if (Objects.isNull(xml) || xml.isEmpty()) {
            return createNewDiagram(fileName);
        }
        return parse(fileName, xml);
    }

    public Promise<String> transform(final Diagram diagram) {
        String raw = marshalling.marshall(convert(diagram));
        return promises.resolve(raw);
    }

    private void updateDiagramSet(Node<Definition<BPMNDiagram>, ?> diagramNode, String name) {
        final BPMNDiagram diagramSet = diagramNode.getContent().getDefinition();

        if (diagramSet.getPackageName() == null ||
                diagramSet.getName().isEmpty()) {
            diagramSet.setName(name);
        }

        if (diagramSet.getPackageName() == null ||
                diagramSet.getId().isEmpty()) {
            diagramSet.setId(createValidId(name));
        }

        if (diagramSet.getPackageName() == null ||
                diagramSet.getPackageName().isEmpty()) {
            diagramSet.setPackageName(DEFAULT_PACKAGE);
        }
    }

    private Diagram createNewDiagram(String fileName) {
        final String title = createDiagramTitleFromFilePath(fileName);
        final String defSetId = BPMNClientMarshalling.getDefinitionSetId();
        final Metadata metadata = createMetadata();
        metadata.setTitle(title);
        final Diagram diagram = factoryManager.newDiagram(title,
                                                          defSetId,
                                                          metadata);

        final Node<Definition<BPMNDiagram>, ?> diagramNode = GraphUtils.getFirstNode((Graph<?, Node>) diagram.getGraph(), Process.class);

        updateDiagramSet(diagramNode, fileName);
        updateClientMetadata(diagram);
        return diagram;
    }

    @SuppressWarnings("unchecked")
    private Diagram parse(final String fileName, final String raw) {
        final Metadata metadata = createMetadata();
        final String title = createDiagramTitleFromFilePath(fileName);
        final String defSetId = BPMNClientMarshalling.getDefinitionSetId();
        metadata.setTitle(title);

        final Definitions definitions = marshalling.unmarshall(raw);
        final Diagram diagram = factoryManager.newDiagram(title,
                                                          defSetId,
                                                          metadata);

        final Node<Definition<BPMNDiagram>, ?> diagramNode = GraphUtils.getFirstNode((Graph<?, Node>) diagram.getGraph(), Process.class);

        diagramNode.getContent().setDefinition(definitions.getProcess());

        buildStartEvents(definitions, diagram);
        buildEndEvents(definitions, diagram);
        buildTasks(definitions, diagram);
        buildLanes(definitions, diagram);
        buildGateways(definitions, diagram);
        buildSequenceFlows(definitions, diagram);

        updateDiagramSet(diagramNode, fileName);
        updateClientMetadata(diagram);

        return diagram;
    }

    private void buildSequenceFlows(Definitions definitions, Diagram diagram) {
        for (SequenceFlow event : definitions.getProcess().getSequenceFlows()) {
            for (BpmnEdge shape : definitions.getBpmnDiagram().getBpmnPlane().getBpmnEdges()) {
                if (shape.getBpmnElement().equals(event.getId())) {

                    final String sourceRef = event.getSourceRef();
                    final String targetRef = event.getTargetRef();

                    View<Object> contentSource = (View<Object>) diagram.getGraph().getNode(sourceRef).getContent();
                    View<Object> contentTarget = (View<Object>) diagram.getGraph().getNode(targetRef).getContent();

                    double sourceX = contentSource.getBounds().getUpperLeft().getX();
                    double sourceY = contentSource.getBounds().getUpperLeft().getY();

                    double targetX = contentTarget.getBounds().getUpperLeft().getX();
                    double targetY = contentTarget.getBounds().getUpperLeft().getY();

                    double sourceWaypointX = shape.getWaypoint().get(0).getX();
                    double sourceWaypointY = shape.getWaypoint().get(0).getY();

                    double targetWaypointX = shape.getWaypoint().get(shape.getWaypoint().size()-1).getX();
                    double targetWaypointY = shape.getWaypoint().get(shape.getWaypoint().size()-1).getY();

                    DomGlobal.console.debug("Source X: " + sourceX);
                    DomGlobal.console.debug("Source Y: " + sourceY);

                    DomGlobal.console.debug("Waypoint Source X: " + sourceWaypointX);
                    DomGlobal.console.debug("Waypoint Source Y: " + sourceWaypointY);

                    DomGlobal.console.debug("Target X: " + targetX);
                    DomGlobal.console.debug("target Y: " + targetY);

                    DomGlobal.console.debug("Waypoint Target X: " + targetWaypointX);
                    DomGlobal.console.debug("Waypoint target Y: " + targetWaypointY);

                    final Edge<Definition<Object>, Node> build = edgeFactory.build(event.getId(), event);

                    build.setSourceNode(diagram.getGraph().getNode(sourceRef));
                    build.setTargetNode(diagram.getGraph().getNode(targetRef));

                    MagnetConnection sourceConnection = MagnetConnection.Builder.at(sourceWaypointX, sourceWaypointY)
                            .setAuto(event.isAutoConnectionSource());
                    MagnetConnection targetConnection = MagnetConnection.Builder.at(targetWaypointX, targetWaypointY)
                            .setAuto(event.isAutoConnectionTarget());

                    ViewConnector<Object> connector = (ViewConnector<Object>) build.getContent();

                    if (shape.getWaypoint().size() > 2) {
                        final List<Waypoint> waypoints = shape.getWaypoint().subList(1, shape.getWaypoint().size() - 1);
                        ControlPoint[] controlPoints = new ControlPoint[waypoints.size()];

                        for (int i = 0; i < waypoints.size(); i++) {
                            controlPoints[i] = ControlPoint.build(waypoints.get(i).getX(), waypoints.get(i).getY());
                        }

                        connector.setControlPoints(controlPoints);
                    }

                    connector.setSourceConnection(sourceConnection);
                    connector.setTargetConnection(targetConnection);

                    diagram.getGraph().getNode(sourceRef).getOutEdges().add(build);
                    diagram.getGraph().getNode(targetRef).getInEdges().add(build);
                }
            }
        }
    }

    private void buildTasks(Definitions definitions, Diagram diagram) {
        for (BaseTask task : definitions.getProcess().getTasks()) {
            for (BpmnShape shape : definitions.getBpmnDiagram().getBpmnPlane().getBpmnShapes()) {
                if (shape.getBpmnElement().equals(task.getId())) {
                    final Node<Definition<Object>, Edge> build = nodeFactory.build(task.getId(), task);
                    View<Object> content = (View) build.getContent();
                    Bounds bounds = definitionUtils.buildBounds(task, shape.getBounds().getX(), shape.getBounds().getY());
                    content.setBounds(bounds);
                    content.getBounds().getLowerRight().setX(content.getBounds().getUpperLeft().getX() + shape.getBounds().getWidth());
                    content.getBounds().getLowerRight().setY(content.getBounds().getUpperLeft().getY() + shape.getBounds().getHeight());
                    diagram.getGraph().addNode(build);
                }
            }
        }
    }

    private void buildLanes(Definitions definitions, Diagram diagram) {
        DomGlobal.console.debug("Number of Lanes: " + definitions.getProcess().getLanes().size());
        for (Lane lane : definitions.getProcess().getLanes()) {

            DomGlobal.console.debug("Children:::: " + lane.getFlowNodeRefs().size());

            for (FlowNodeRef ref : lane.getFlowNodeRefs()) {
                DomGlobal.console.debug("Flow Node: " + ref.getValue());
            }


            for (BpmnShape shape : definitions.getBpmnDiagram().getBpmnPlane().getBpmnShapes()) {
                if (shape.getBpmnElement().equals(lane.getId())) {
                    final Node<Definition<Object>, Edge> build = nodeFactory.build(lane.getId(), lane);
                    View<Object> content = (View) build.getContent();
                    Bounds bounds = definitionUtils.buildBounds(lane, shape.getBounds().getX(), shape.getBounds().getY());
                    content.setBounds(bounds);
                    //content.getBounds().getLowerRight().setX(content.getBounds().getUpperLeft().getX() + shape.getBounds().getWidth());
                    //content.getBounds().getLowerRight().setY(content.getBounds().getUpperLeft().getY() + shape.getBounds().getHeight());
                    diagram.getGraph().addNode(build);
                }
            }
        }
    }

    private void buildGateways(Definitions definitions, Diagram diagram) {
        for (ParallelGateway gateway : definitions.getProcess().getParallelGateways()) {
            buildGateway(definitions, diagram, gateway);
        }

        for (ExclusiveGateway gateway : definitions.getProcess().getExclusiveGateways()) {
            buildGateway(definitions, diagram, gateway);
        }

        for (InclusiveGateway gateway : definitions.getProcess().getInclusiveGateways()) {
            buildGateway(definitions, diagram, gateway);
        }

        for (EventGateway gateway : definitions.getProcess().getEventBasedGateways()) {
            buildGateway(definitions, diagram, gateway);
        }

    }

    private void buildGateway(Definitions definitions, Diagram diagram, BaseGateway event) {
        for (BpmnShape shape : definitions.getBpmnDiagram().getBpmnPlane().getBpmnShapes()) {
            if (shape.getBpmnElement().equals(event.getId())) {
                final Node<Definition<Object>, Edge> build = nodeFactory.build(event.getId(), event);
                View<Object> content = (View) build.getContent();
                Bounds bounds = definitionUtils.buildBounds(event, shape.getBounds().getX(), shape.getBounds().getY());
                content.setBounds(bounds);
                diagram.getGraph().addNode(build);
            }
        }
    }

    private void buildEndEvents(Definitions definitions, Diagram diagram) {
        for (EndEvent event : definitions.getProcess().getEndEvents()) {
            buildEvent(definitions, diagram, event);
        }
    }

    private void buildStartEvents(Definitions definitions, Diagram diagram) {
        for (StartEvent event : definitions.getProcess().getStartEvents()) {
            buildEvent(definitions, diagram, event);
        }
    }

    private void buildEvent(Definitions definitions, Diagram diagram, FlowNode event) {
        for (BpmnShape shape : definitions.getBpmnDiagram().getBpmnPlane().getBpmnShapes()) {
            if (shape.getBpmnElement().equals(event.getId())) {
                final Node<Definition<Object>, Edge> build = nodeFactory.build(event.getId(), event);
                View<Object> content = (View) build.getContent();
                Bounds bounds = definitionUtils.buildBounds(event, shape.getBounds().getX(), shape.getBounds().getY());
                content.setBounds(bounds);
                diagram.getGraph().addNode(build);
            }
        }
    }

    private Metadata createMetadata() {
        return new MetadataImpl.MetadataImplBuilder(BPMNClientMarshalling.getDefinitionSetId(),
                                                    definitionManager)
                .build();
    }

    private void updateClientMetadata(final Diagram diagram) {
        if (null != diagram) {
            final Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata)) {
                final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
                metadata.setShapeSetId(sId);
            }
        }
    }

    private DiagramImpl convert(final Diagram diagram) {
        return new DiagramImpl(diagram.getName(),
                               diagram.getGraph(),
                               diagram.getMetadata());
    }
}
