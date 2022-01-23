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

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

// TODO: Move this stuff to Stunner core?
public interface Commands<C extends Command> {

    C addNode(Node candidate);

    C updateElementPosition(Node<? extends View<?>, Edge> element,
                            Point2D location);

    C addConnector(Node source,
                   Edge edge,
                   Connection connection);

    C setSourceNode(Node<? extends View<?>, Edge> sourceNode,
                    Edge<? extends View<?>, Node> edge,
                    Connection connection);

    C setTargetNode(Node<? extends View<?>, Edge> targetNode,
                    Edge<? extends View<?>, Node> edge,
                    Connection connection);
}
