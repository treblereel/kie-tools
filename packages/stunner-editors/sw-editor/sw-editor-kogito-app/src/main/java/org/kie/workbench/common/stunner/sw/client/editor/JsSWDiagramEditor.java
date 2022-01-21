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

package org.kie.workbench.common.stunner.sw.client.editor;

import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.sw.client.services.SWGraphExamples;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.State;

@JsType
public class JsSWDiagramEditor {

    @JsIgnore
    ClientSession session;
    @JsIgnore
    SWGraphExamples graphExamples;

    public void logNodes() {
        Iterable<Node> nodes = session.getCanvasHandler().getDiagram().getGraph().nodes();
        for (Node node : nodes) {
            DomGlobal.console.log(node.getUUID());
        }
    }

    public Node getState1Node() {
        Iterable<Node> nodes = session.getCanvasHandler().getDiagram().getGraph().nodes();
        for (Node node : nodes) {
            if (node.getUUID().equals("state1")) {
                return node;
            }
        }
        return null;
    }

    // sweditor.addState('SomeState1', 'someState1', 50, 300);
    public Node addState(String uuid, String name, double x, double y) {
        AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) session.getCanvasHandler();
        State someState = new InjectState();
        someState.setName(name);
        Node someStateNode = graphExamples.createStateNode(uuid, someState, 0, 0);
        String shapeSetId = canvasHandler.getDiagram().getMetadata().getShapeSetId();
        final CompositeCommand.Builder commandBuilder = new CompositeCommand.Builder();
        commandBuilder.addCommand(new AddNodeCommand(someStateNode, shapeSetId));
        commandBuilder.addCommand(new UpdateElementPositionCommand((Node) someStateNode, new Point2D(x, y)));
        CompositeCommand command = commandBuilder.build();
        CommandResult result = ((EditorSession) session).getCommandManager().execute(canvasHandler, command);
        if (CommandUtils.isError(result)) {
            DomGlobal.console.log("ERROR ADDING SOWE STATE: " + result);
        } else {
            DomGlobal.console.log("SUCCESSFULLY ADDED SOME STATE: ");
        }
        return someStateNode;
    }
}
