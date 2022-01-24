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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import jsinterop.base.JsPropertyMap;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Transition;

@ApplicationScoped
public class SWPatchBuilder {

    @Inject
    private SWClientDiagramMarshaller marshaller;

    public String build(Command<AbstractCanvasHandler, CanvasViolation> command) {

        String patch = "[";

        if (command instanceof AddNodeCommand) {
            AddNodeCommand addNodeCommand = (AddNodeCommand) command;
            Node candidate = addNodeCommand.getCandidate();
            JsPropertyMap<Object> map = marshaller.marshall(candidate);
            String nodeRaw = SWClientDiagramMarshaller.stringify(map);
            String addRaw = addJsonObject("/states/" + marshaller.getStatesCount(), nodeRaw);
            marshaller.addStateIndex(candidate.getUUID());
            patch += addRaw;
        } else if (command instanceof UpdateElementPropertyCommand) {
            UpdateElementPropertyCommand updateCommand = (UpdateElementPropertyCommand) command;
            Element element = updateCommand.getElement();
            if (element instanceof Node) {
                if (((View<?>) element.getContent()).getDefinition() instanceof State) {
                    String field = updateCommand.getField();
                    if ("name".equals(field)) {
                        int stateIndex = marshaller.getStateIndex(element.getUUID());
                        String value = (String) updateCommand.getValue();
                        String replaceRaw = replace("/states/" + stateIndex + "/name", value);
                        patch += replaceRaw;
                        List<Edge> inEdges = (List<Edge>) ((Node<?, ?>) element).getInEdges();
                        for (Edge inEdge : inEdges) {
                            Object inEdgeBean = ((View<?>) inEdge.getContent()).getDefinition();
                            if (inEdgeBean instanceof StartTransition) {
                                String startReplaceRaw = replace("/start", value);
                                patch += ", " + startReplaceRaw;
                            }
                        }
                    }
                }
            }
        } else if (command instanceof SetConnectionTargetNodeCommand) {
            SetConnectionTargetNodeCommand setTargetCommand = (SetConnectionTargetNodeCommand) command;
            Edge<? extends View<?>, Node> edge = setTargetCommand.getEdge();
            Node<? extends View<?>, Edge> node = setTargetCommand.getNode();
            Object edgeBean = ((View<?>) edge.getContent()).getDefinition();
            if (edgeBean instanceof StartTransition) {
                Object nodeBean = node.getContent().getDefinition();
                if (nodeBean instanceof State) {
                    String startReplaceRaw = replace("/start", ((State) nodeBean).getName());
                    patch += startReplaceRaw;
                }
            } else if (edgeBean instanceof Transition) {
                String source = edge.getSourceNode().getUUID();
                String target = node.getUUID();
                int stateIndex = marshaller.getStateIndex(source);
                String r = addJsonValue("/states/" + stateIndex + "/transition", target);
                patch += r;
            }
        }

        patch += "]";
        return patch;
    }

    private String addJsonValue(String path, String value) {
        return "{ \"op\": \"add\", \"path\": \"" + path + "\", \"value\": \"" + value + "\" }";
    }

    private String addJsonObject(String path, String value) {
        return "{ \"op\": \"add\", \"path\": \"" + path + "\", \"value\": " + value + " }";
    }

    private static String replace(String path, String value) {
        return "{ \"op\": \"replace\", \"path\": \"" + path + "\", \"value\": \"" + value + "\" }";
    }
}
