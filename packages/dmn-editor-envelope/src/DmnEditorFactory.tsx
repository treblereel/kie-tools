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
import {
  Editor,
  EditorFactory,
  EditorInitArgs,
  KogitoEditorEnvelopeContextType,
  KogitoEditorChannelApi,
  EditorTheme,
  DEFAULT_WORKING_DIR_BASE_PATH,
} from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { DmnEditorRoot } from "./DmnEditorRoot";
import { ResourceContent, ResourcesList, WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";

export class DmnEditorFactory implements EditorFactory<Editor, KogitoEditorChannelApi> {
  public createEditor(
    envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorChannelApi>,
    initArgs: EditorInitArgs
  ): Promise<Editor> {
    return Promise.resolve(new DmnEditorInterface(envelopeContext, initArgs));
  }
}

export class DmnEditorInterface implements Editor {
  private self: DmnEditorRoot;
  public af_isReact = true;
  public af_componentId: "dmn-editor";
  public af_componentTitle: "DMN Editor";

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorChannelApi>,
    private readonly initArgs: EditorInitArgs
  ) {}

  // Not in-editor

  public getPreview(): Promise<string | undefined> {
    return Promise.resolve(`
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg
  xmlns="http://www.w3.org/2000/svg"
  xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1"
  width="1080" height="1080" viewBox="0 0 1080 1080" xml:space="preserve">
  <g transform="matrix(1 0 0 1 540 540)" id="ee1530d3-d469-49de-b8ad-62ffb6e5db7a"></g>
  <g transform="matrix(1 0 0 1 540 540)" id="b6eca5e2-94e1-4e3f-a04e-16bc0ada9ea4">
    <rect style="stroke: none; stroke-width: 1; stroke-dasharray: none; stroke-linecap: butt; stroke-dashoffset: 0; stroke-linejoin: miter; stroke-miterlimit: 4; fill: rgb(255,255,255); fill-rule: nonzero; opacity: 1; visibility: hidden;" vector-effect="non-scaling-stroke"  x="-540" y="-540" rx="0" ry="0" width="1080" height="1080" />
  </g>
  <g transform="matrix(0.68 0 0 0.68 540 292.07)" style="" id="12bd02ec-b291-4d62-acdc-3fdd30cc84d7"  >
    <text xml:space="preserve" font-family="Raleway" font-size="105" font-style="normal" font-weight="900" style="stroke: none; stroke-width: 1; stroke-dasharray: none; stroke-linecap: butt; stroke-dashoffset: 0; stroke-linejoin: miter; stroke-miterlimit: 4; fill: rgb(0,0,0); fill-rule: nonzero; opacity: 1; white-space: pre;" >
      <tspan x="-187.11" y="-26.34" >Not yet</tspan>
      <tspan x="-351.65" y="92.31" >implemented</tspan>
    </text>
  </g>
  <g transform="matrix(1 0 0 1 523.42 454.97)" style="" id="b2ea8c5b-9fc6-43c0-9e3f-5837adab8b51"  >
    <text xml:space="preserve" font-family="Alegreya" font-size="44" font-style="normal" font-weight="700" style="stroke: none; stroke-width: 1; stroke-dasharray: none; stroke-linecap: butt; stroke-dashoffset: 0; stroke-linejoin: miter; stroke-miterlimit: 4; fill: rgb(0,0,0); fill-rule: nonzero; opacity: 1; white-space: pre;" >
      <tspan x="-270.07" y="-11.04" style="white-space: pre; ">Use the legacy DMN Editor to </tspan>
      <tspan x="-251.26" y="38.68" >generate SVGs temporarily.</tspan>
    </text>
  </g>
</svg>`);
  }

  public async validate(): Promise<Notification[]> {
    return Promise.resolve([]);
  }

  // Forwarding to the editor

  public async setTheme(theme: EditorTheme): Promise<void> {
    return Promise.resolve(); // No-op for now. The DMN Editor only has the LIGHT theme.
  }

  public async undo(): Promise<void> {
    return this.self.undo();
  }

  public async redo(): Promise<void> {
    return this.self.redo();
  }

  public getContent(): Promise<string> {
    return this.self.getContent();
  }

  public setContent(path: string, content: string): Promise<void> {
    return this.self.setContent(path, content);
  }

  // This is the argument to ReactDOM.render. These props can be understood like "static globals".
  public af_componentRoot() {
    return (
      <DmnEditorRootWrapper
        exposing={(dmnEditorRoot) => (this.self = dmnEditorRoot)}
        envelopeContext={this.envelopeContext}
        workingDirBasePath={this.initArgs.workingDirBasePath ?? DEFAULT_WORKING_DIR_BASE_PATH}
      />
    );
  }
}

// This component is a wrapper. It memoizes the DmnEditorRoot props beforing rendering it.
function DmnEditorRootWrapper({
  envelopeContext,
  exposing,
  workingDirBasePath,
}: {
  envelopeContext?: KogitoEditorEnvelopeContextType<KogitoEditorChannelApi>;
  exposing: (s: DmnEditorRoot) => void;
  workingDirBasePath: string;
}) {
  const onNewEdit = React.useCallback(
    (workspaceEdit: WorkspaceEdit) => {
      envelopeContext?.channelApi.notifications.kogitoWorkspace_newEdit.send(workspaceEdit);
    },
    [envelopeContext]
  );

  const onRequestFileList = React.useCallback(
    async (resource: ResourcesList) => {
      return (
        envelopeContext?.channelApi.requests.kogitoWorkspace_resourceListRequest(resource) ?? new ResourcesList("", [])
      );
    },
    [envelopeContext]
  );

  const onRequestFileContent = React.useCallback(
    async (resource: ResourceContent) => {
      return envelopeContext?.channelApi.requests.kogitoWorkspace_resourceContentRequest(resource);
    },
    [envelopeContext]
  );

  const onOpenFile = React.useCallback(
    (path: string) => {
      envelopeContext?.channelApi.notifications.kogitoWorkspace_openFile.send(path);
    },
    [envelopeContext]
  );

  return (
    <DmnEditorRoot
      exposing={exposing}
      onNewEdit={onNewEdit}
      onRequestFileList={onRequestFileList}
      onRequestFileContent={onRequestFileContent}
      onOpenFile={onOpenFile}
      workingDirBasePath={workingDirBasePath}
    />
  );
}
