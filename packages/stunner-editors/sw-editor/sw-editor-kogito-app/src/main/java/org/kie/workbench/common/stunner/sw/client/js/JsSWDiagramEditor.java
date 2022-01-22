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

package org.kie.workbench.common.stunner.sw.client.js;

import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Node;

@JsType
public class JsSWDiagramEditor {

    @JsIgnore
    public ClientSession session;

    public JsSWCommands commands;

    public void logNodes() {
        Iterable<Node> nodes = session.getCanvasHandler().getDiagram().getGraph().nodes();
        for (Node node : nodes) {
            DomGlobal.console.log(node.getUUID());
        }
    }

    public Node getNode(String uuid) {
        Iterable<Node> nodes = session.getCanvasHandler().getDiagram().getGraph().nodes();
        for (Node node : nodes) {
            if (node.getUUID().equals(uuid)) {
                return node;
            }
        }
        return null;
    }
}
