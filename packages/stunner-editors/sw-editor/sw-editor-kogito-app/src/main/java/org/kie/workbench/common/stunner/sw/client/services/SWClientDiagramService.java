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

package org.kie.workbench.common.stunner.sw.client.services;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.sw.SWDefinitions;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.StateNode;
import org.kie.workbench.common.stunner.sw.factory.SWDiagramFactory;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class SWClientDiagramService {

    private final DefinitionManager definitionManager;
    private final FactoryManager factoryManager;
    private final SWDiagramFactory diagramFactory;
    private final ShapeManager shapeManager;
    private final Promises promises;
    private final DefinitionUtils definitionUtils;

    //CDI proxy
    protected SWClientDiagramService() {
        this(null, null, null, null, null, null);
    }

    @Inject
    public SWClientDiagramService(final DefinitionManager definitionManager,
                                  final FactoryManager factoryManager,
                                  final SWDiagramFactory diagramFactory,
                                  final ShapeManager shapeManager,
                                  final Promises promises,
                                  final DefinitionUtils definitionUtils) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.diagramFactory = diagramFactory;
        this.shapeManager = shapeManager;
        this.promises = promises;
        this.definitionUtils = definitionUtils;
    }

    public void transform(final String xml,
                          final ServiceCallback<Diagram> callback) {
        doTransform("default", xml, callback);
    }

    public void transform(final String fileName,
                          final String xml,
                          final ServiceCallback<Diagram> callback) {
        doTransform(fileName, xml, callback);
    }

    private void doTransform(final String fileName,
                             final String xml,
                             final ServiceCallback<Diagram> callback) {
        Diagram diagram = doTransform(fileName, xml);
        callback.onSuccess(diagram);
    }

    private Diagram doTransform(final String fileName,
                                final String xml) {

        if (Objects.isNull(xml) || xml.isEmpty()) {
            return createNewDiagram(fileName);
        }
        return parse(fileName, xml);
    }

    public Promise<String> transform(final Diagram diagram) {
        return promises.resolve("{}");
    }

    private Diagram createNewDiagram(String fileName) {
        final String title = "default";
        final String defSetId = getDefinitionSetId();
        final Metadata metadata = createMetadata();
        metadata.setTitle(title);
        final Diagram diagram = factoryManager.newDiagram(title,
                                                          defSetId,
                                                          metadata);
        updateClientMetadata(diagram);
        return diagram;
    }

    private static String getDefinitionSetId() {
        return BindableAdapterUtils.getDefinitionSetId(SWDefinitions.class);
    }

    @SuppressWarnings("all")
    private Diagram parse(final String fileName, final String raw) {
        final Metadata metadata = createMetadata();
        final Graph<DefinitionSet, ?> graph = unmarshall(metadata, raw);
        final String title = "SW Test Diagram";
        metadata.setTitle(title);
        final Diagram diagram = diagramFactory.build(title,
                                                     metadata,
                                                     graph);
        updateClientMetadata(diagram);
        return diagram;
    }

    private static void testNodeApi() {
        StateNode state1 = new StateNode("state1");
        State definition = state1.getContent().getDefinition();
    }

    @SuppressWarnings("all")
    private Graph unmarshall(final Metadata metadata,
                             final String raw) {

        final State state1 = new State();
        state1.setName("State1");
        // final NodeImpl stage1Node = new NodeImpl<>("state1");
        final StateNode stage1Node = new StateNode("state1");
        final Bounds bounds = definitionUtils.buildBounds(state1,
                                                          50d,
                                                          50d);
        final View<State> content = new ViewImpl<>(state1,
                                                   bounds);
        stage1Node.setContent(content);
        appendLabels(stage1Node.getLabels(),
                     state1);

        final GraphImpl<Object> graph = GraphImpl.build("graph");
        graph.addNode(stage1Node);

        return graph;
    }

    @SuppressWarnings("all")
    protected void appendLabels(final Set<String> target,
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
    public static <T> String[] computeLabels(final DefinitionAdapter<T> adapter,
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

    private Metadata createMetadata() {
        return new MetadataImpl.MetadataImplBuilder(getDefinitionSetId(),
                                                    definitionManager)
                .build();
    }

    private void updateClientMetadata(final Diagram diagram) {
        if (null != diagram) {
            final Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata) && isEmpty(metadata.getShapeSetId())) {
                final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
                metadata.setShapeSetId(sId);
            }
        }
    }

    public static boolean isEmpty(CharSequence str) {
        return null == str || str.length() == 0;
    }
}
