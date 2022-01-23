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

package org.kie.workbench.common.stunner.sw.command;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class GraphCommands implements Commands<GraphCommand> {

    @Override
    public GraphCommand addNode(Node candidate) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand(candidate);
    }

    @Override
    public GraphCommand updateElementPosition(Node<? extends View<?>, Edge> element, Point2D location) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand(element,
                                                                                                         location);
    }

    @Override
    public GraphCommand addConnector(Node source, Edge candidate, Connection connection) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.AddConnectorCommand(source,
                                                                                                candidate,
                                                                                                connection);
    }

    @Override
    public GraphCommand setSourceNode(Node<? extends View<?>, Edge> sourceNode, Edge<? extends View<?>, Node> edge, Connection connection) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand(sourceNode,
                                                                                                           edge,
                                                                                                           connection);
    }

    @Override
    public GraphCommand setTargetNode(Node<? extends View<?>, Edge> targetNode, Edge<? extends View<?>, Node> edge, Connection connection) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand(targetNode,
                                                                                                           edge,
                                                                                                           connection);
    }
}
