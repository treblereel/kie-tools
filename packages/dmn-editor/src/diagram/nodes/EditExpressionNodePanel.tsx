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
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";

export function EditExpressionNodePanel(props: { isVisible: boolean; id: string }) {
  const dispatch = useDmnEditorStore((s) => s.dispatch);
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  return (
    <>
      {props.isVisible && (
        <Label
          onClick={() =>
            dmnEditorStoreApi.setState((state) => {
              dispatch.boxedExpressionEditor.open(state, props.id);
            })
          }
          className={"kie-dmn-editor--edit-expression-node-panel"}
        >
          Edit
        </Label>
      )}
    </>
  );
}
