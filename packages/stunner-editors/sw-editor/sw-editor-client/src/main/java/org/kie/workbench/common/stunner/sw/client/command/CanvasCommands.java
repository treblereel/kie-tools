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

package org.kie.workbench.common.stunner.sw.client.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.sw.command.Commands;

public class CanvasCommands implements Commands<CanvasCommand<AbstractCanvasHandler>> {

    private final String shapeSetId;

    public CanvasCommands(String shapeSetId) {
        this.shapeSetId = shapeSetId;
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addNode(Node candidate) {
        return new AddNodeCommand(candidate, shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> updateElementPosition(Node<? extends View<?>, Edge> element, Point2D location) {
        return new UpdateElementPositionCommand((Node<View<?>, Edge>) element, location);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> addConnector(Node source, Edge candidate, Connection connection) {
        return new AddConnectorCommand(source,
                                       candidate,
                                       connection,
                                       shapeSetId);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> setSourceNode(Node<? extends View<?>, Edge> sourceNode, Edge<? extends View<?>, Node> edge, Connection connection) {
        return new SetConnectionSourceNodeCommand(sourceNode,
                                                  (Edge<? extends ViewConnector<?>, Node>) edge,
                                                  connection);
    }

    @Override
    public CanvasCommand<AbstractCanvasHandler> setTargetNode(Node<? extends View<?>, Edge> targetNode, Edge<? extends View<?>, Node> edge, Connection connection) {
        return new SetConnectionTargetNodeCommand(targetNode,
                                                  (Edge<? extends ViewConnector<?>, Node>) edge,
                                                  connection);
    }
}
