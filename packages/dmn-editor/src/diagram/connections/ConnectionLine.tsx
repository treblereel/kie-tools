/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import * as RF from "reactflow";
import { snapPoint } from "../SnapGrid";
import { EDGE_TYPES } from "../edges/EdgeTypes";
import {
  AssociationPath,
  AuthorityRequirementPath,
  InformationRequirementPath,
  KnowledgeRequirementPath,
} from "../edges/Edges";
import { NODE_TYPES } from "../nodes/NodeTypes";
import { getPositionalHandlePosition } from "../maths/Maths";
import { DecisionNodeSvg, BkmNodeSvg, KnowledgeSourceNodeSvg, TextAnnotationNodeSvg } from "../nodes/NodeSvgs";
import { getNodeCenterPoint, pointsToPath } from "../maths/DmnMaths";
import { NodeType, getDefaultEdgeTypeBetween } from "./graphStructure";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { DEFAULT_NODE_SIZES } from "../nodes/DefaultSizes";
import { useDmnEditorStore } from "../../store/Store";
import { useKieEdgePath } from "../edges/useKieEdgePath";
import { PositionalNodeHandleId } from "./PositionalNodeHandles";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";

export function ConnectionLine({ toX, toY, fromNode, fromHandle }: RF.ConnectionLineComponentProps) {
  const diagram = useDmnEditorStore((s) => s.diagram);

  const edgeId = useDmnEditorStore((s) => s.diagram.edgeIdBeingUpdated);
  const { edgesById } = useDmnEditorDerivedStore();
  const edge = edgeId ? edgesById.get(edgeId) : undefined;
  const kieEdgePath = useKieEdgePath(edge?.source, edge?.target, edge?.data);

  // This works because nodes are configured with:
  // - Source handles with ids matching EDGE_TYPES or NODE_TYPES
  // - Target handles with ids matching TargetHandleId
  //
  // When editing an existing edge from its first waypoint (i.e., source handle) the edge is rendered
  // in reverse. So the connection line's "from" properties are actually "to" properties.
  const isUpdatingFromSourceHandle = Object.keys(PositionalNodeHandleId).some(
    (k) => (PositionalNodeHandleId as any)[k] === fromHandle?.id
  );

  const { "@_x": fromX, "@_y": fromY } = getNodeCenterPoint(fromNode);

  const connectionLinePath =
    edge && kieEdgePath.points
      ? isUpdatingFromSourceHandle
        ? pointsToPath([{ "@_x": toX, "@_y": toY }, ...kieEdgePath.points.slice(1)]) // First point is being dragged
        : pointsToPath([...kieEdgePath.points.slice(0, -1), { "@_x": toX, "@_y": toY }]) // Last point is being dragged
      : `M${fromX},${fromY} L${toX},${toY}`;

  const handleId = isUpdatingFromSourceHandle ? edge?.type : fromHandle?.id;

  // Edges
  if (handleId === EDGE_TYPES.informationRequirement) {
    return <InformationRequirementPath d={connectionLinePath} />;
  } else if (handleId === EDGE_TYPES.knowledgeRequirement) {
    return <KnowledgeRequirementPath d={connectionLinePath} />;
  } else if (handleId === EDGE_TYPES.authorityRequirement) {
    return <AuthorityRequirementPath d={connectionLinePath} centerToConnectionPoint={true} />;
  } else if (handleId === EDGE_TYPES.association) {
    return <AssociationPath d={connectionLinePath} />;
  }
  // Nodes
  else {
    const nodeType = handleId as NodeType;
    const { "@_x": toXsnapped, "@_y": toYsnapped } = snapPoint(diagram.snapGrid, { "@_x": toX, "@_y": toY });

    const defaultSize = DEFAULT_NODE_SIZES[nodeType](diagram.snapGrid);
    const [toXauto, toYauto] = getPositionalHandlePosition(
      { x: toXsnapped, y: toYsnapped, width: defaultSize["@_width"], height: defaultSize["@_height"] },
      { x: fromX, y: fromY, width: 1, height: 1 }
    );

    const edge = getDefaultEdgeTypeBetween(fromNode?.type as NodeType, handleId as NodeType);
    if (!edge) {
      throw new Error(`Invalid structure: ${fromNode?.type} --(any)--> ${handleId}`);
    }

    const path = `M${fromX},${fromY} L${toXauto},${toYauto}`;

    const edgeSvg = switchExpression(edge, {
      [EDGE_TYPES.informationRequirement]: <InformationRequirementPath d={path} />,
      [EDGE_TYPES.knowledgeRequirement]: <KnowledgeRequirementPath d={path} />,
      [EDGE_TYPES.authorityRequirement]: <AuthorityRequirementPath d={path} centerToConnectionPoint={false} />,
      [EDGE_TYPES.association]: <AssociationPath d={path} />,
    });

    if (nodeType === NODE_TYPES.decision) {
      return (
        <g>
          {edgeSvg}
          <DecisionNodeSvg
            x={toXsnapped}
            y={toYsnapped}
            width={defaultSize["@_width"]}
            height={defaultSize["@_height"]}
          />
        </g>
      );
    } else if (nodeType === NODE_TYPES.bkm) {
      return (
        <g className={"pulse"}>
          {edgeSvg}
          <BkmNodeSvg x={toXsnapped} y={toYsnapped} width={defaultSize["@_width"]} height={defaultSize["@_height"]} />
        </g>
      );
    } else if (nodeType === NODE_TYPES.knowledgeSource) {
      return (
        <g>
          {edgeSvg}
          <KnowledgeSourceNodeSvg
            x={toXsnapped}
            y={toYsnapped}
            width={defaultSize["@_width"]}
            height={defaultSize["@_height"]}
          />
        </g>
      );
    } else if (nodeType === NODE_TYPES.textAnnotation) {
      return (
        <g>
          {edgeSvg}
          <TextAnnotationNodeSvg
            x={toXsnapped}
            y={toYsnapped}
            width={defaultSize["@_width"]}
            height={defaultSize["@_height"]}
          />
        </g>
      );
    }
  }

  throw new Error(`Unknown source of ConnectionLine '${handleId}'.`);
}
