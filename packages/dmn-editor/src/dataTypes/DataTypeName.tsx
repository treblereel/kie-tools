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
import { useCallback } from "react";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { EditableNodeLabel, useEditableNodeLabel } from "../diagram/nodes/EditableNodeLabel";
import { TypeRefLabel } from "./TypeRefLabel";
import { useDmnEditorStoreApi } from "../store/Store";
import { renameItemDefinition } from "../mutations/renameItemDefinition";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { UniqueNameIndex } from "../Dmn15Spec";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { InlineFeelNameInput, OnInlineFeelNameRenamed } from "../feel/InlineFeelNameInput";

export function DataTypeName({
  isReadonly,
  itemDefinition,
  isActive,
  editMode,
  relativeToNamespace,
  shouldCommitOnBlur,
  allUniqueNames,
  enableAutoFocusing,
}: {
  isReadonly: boolean;
  editMode: "hover" | "double-click";
  itemDefinition: DMN15__tItemDefinition;
  isActive: boolean;
  relativeToNamespace: string;
  shouldCommitOnBlur?: boolean;
  allUniqueNames: UniqueNameIndex;
  enableAutoFocusing?: boolean;
}) {
  const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(
    enableAutoFocusing ?? true ? itemDefinition["@_id"] : undefined
  );

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { allDataTypesById, importsByNamespace } = useDmnEditorDerivedStore();

  const dataType = allDataTypesById.get(itemDefinition["@_id"]!);

  const feelQNameToDisplay = buildFeelQNameFromNamespace({
    namedElement: itemDefinition,
    importsByNamespace,
    namespace: dataType!.namespace,
    relativeToNamespace,
  });

  const onRenamed = useCallback<OnInlineFeelNameRenamed>(
    (newName) => {
      if (isReadonly) {
        return;
      }

      dmnEditorStoreApi.setState((state) => {
        renameItemDefinition({
          definitions: state.dmn.model.definitions,
          newName,
          itemDefinitionId: itemDefinition["@_id"]!,
          allDataTypesById,
        });
      });
    },
    [allDataTypesById, dmnEditorStoreApi, isReadonly, itemDefinition]
  );

  const _shouldCommitOnBlur = shouldCommitOnBlur ?? true; // Defaults to true

  return (
    <>
      {editMode === "hover" && (
        <InlineFeelNameInput
          isPlain={true}
          isReadonly={isReadonly}
          id={itemDefinition["@_id"]!}
          shouldCommitOnBlur={_shouldCommitOnBlur}
          name={feelQNameToDisplay.full}
          onRenamed={onRenamed}
          allUniqueNames={allUniqueNames}
        />
      )}
      {editMode === "double-click" && (
        <Flex
          tabIndex={-1}
          style={isEditingLabel ? { flexGrow: 1 } : {}}
          flexWrap={{ default: "nowrap" }}
          spaceItems={{ default: "spaceItemsNone" }}
          justifyContent={{ default: "justifyContentFlexStart" }}
          alignItems={{ default: "alignItemsCenter" }}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          {/* Using this component here is not ideal, as we're not dealing with Node names, but it works well enough */}
          <EditableNodeLabel
            truncate={true}
            grow={true}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            onChange={onRenamed}
            shouldCommitOnBlur={shouldCommitOnBlur}
            value={itemDefinition["@_name"]}
            key={itemDefinition["@_id"]}
            position={"top-left"}
            namedElement={itemDefinition}
            namedElementQName={{
              type: "xml-qname",
              localPart: itemDefinition["@_name"],
              prefix: feelQNameToDisplay.prefix,
            }}
            allUniqueNames={allUniqueNames}
          />
          {!isEditingLabel && (
            <TypeRefLabel
              typeRef={itemDefinition.typeRef?.__$$text}
              isCollection={itemDefinition["@_isCollection"]}
              relativeToNamespace={relativeToNamespace}
            />
          )}
        </Flex>
      )}
    </>
  );
}
