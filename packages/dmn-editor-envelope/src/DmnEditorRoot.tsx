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

import * as __path from "path";
import * as React from "react";
import * as DmnEditor from "@kie-tools/dmn-editor/dist/DmnEditor";
import { getMarshaller } from "@kie-tools/dmn-marshaller";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  ContentType,
  ResourceContent,
  SearchType,
  WorkspaceChannelApi,
  WorkspaceEdit,
} from "@kie-tools-core/workspace/dist/api";
import { DMN15_SPEC } from "@kie-tools/dmn-editor/dist/Dmn15Spec";
import { DMN_LATEST_VERSION, DmnLatestModel, DmnMarshaller } from "@kie-tools/dmn-marshaller";
import { domParser } from "@kie-tools/xml-parser-ts";
import { ns as dmn15ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { useEffect, useMemo, useState } from "react";
import { XML2PMML } from "@kie-tools/pmml-editor-marshaller";
import { getPmmlNamespace } from "@kie-tools/dmn-editor/dist/pmml/pmml";
import { getNamespaceOfDmnImport } from "@kie-tools/dmn-editor/dist/includedModels/importNamespaces";
import {
  imperativePromiseHandle,
  PromiseImperativeHandle,
} from "@kie-tools-core/react-hooks/dist/useImperativePromiseHandler";

export const EXTERNAL_MODELS_SEARCH_GLOB_PATTERN = "**/*.{dmn,pmml}";

export const EMPTY_DMN = () => `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="${dmn15ns.get("")}"
  expressionLanguage="${DMN15_SPEC.expressionLanguage.default}"
  namespace="https://kie.org/dmn/${generateUuid()}"
  id="${generateUuid()}"
  name="DMN${generateUuid()}">
</definitions>`;

export type DmnEditorRootProps = {
  exposing: (s: DmnEditorRoot) => void;
  onNewEdit: (edit: WorkspaceEdit) => void;
  onRequestFileList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
  onRequestFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onOpenFile: WorkspaceChannelApi["kogitoWorkspace_openFile"];
  workingDirBasePath: string;
};

export type DmnEditorRootState = {
  marshaller: DmnMarshaller<typeof DMN_LATEST_VERSION> | undefined;
  stack: DmnLatestModel[];
  pointer: number;
  absolutePath: string | undefined;
  externalModelsByNamespace: DmnEditor.ExternalModelsIndex;
  readonly: boolean;
  externalModelsManagerDoneBootstraping: boolean;
};

export class DmnEditorRoot extends React.Component<DmnEditorRootProps, DmnEditorRootState> {
  private readonly externalModelsManagerDoneBootstraping = imperativePromiseHandle<void>();

  constructor(props: DmnEditorRootProps) {
    super(props);
    props.exposing(this);
    this.state = {
      externalModelsByNamespace: {},
      marshaller: undefined,
      stack: [],
      pointer: -1,
      absolutePath: undefined,
      readonly: true,
      externalModelsManagerDoneBootstraping: false,
    };
  }

  // Exposed API

  public async undo(): Promise<void> {
    this.setState((prev) => ({ ...prev, pointer: Math.max(0, prev.pointer - 1) }));
  }

  public async redo(): Promise<void> {
    this.setState((prev) => ({ ...prev, pointer: Math.min(prev.stack.length - 1, prev.pointer + 1) }));
  }

  public async getContent(): Promise<string> {
    if (!this.state.marshaller || !this.model) {
      throw new Error(
        `DMN EDITOR ROOT: Content has not been set yet. Throwing an error to prevent returning a "default" content.`
      );
    }

    return this.state.marshaller.builder.build(this.model);
  }

  public async setContent(absolutePath: string, content: string): Promise<void> {
    const marshaller = getMarshaller(content || EMPTY_DMN(), { upgradeTo: "latest" });

    // Save stack
    let savedStackPointer: DmnLatestModel[] = [];

    // Set the model and path for external models manager.
    this.setState((prev) => {
      savedStackPointer = [...prev.stack];
      return { stack: [marshaller.parser.parse()], absolutePath, pointer: 0 };
    });

    // Wait the external manager models to load.
    await this.externalModelsManagerDoneBootstraping.promise;

    // Set the valeus to render the DMN Editor.
    this.setState((prev) => {
      // External change to the same file.
      if (prev.absolutePath === absolutePath) {
        const newStack = savedStackPointer.slice(0, prev.pointer + 1);
        return {
          marshaller,
          stack: [...newStack, marshaller.parser.parse()],
          readonly: false,
          pointer: newStack.length,
          externalModelsManagerDoneBootstraping: true,
        };
      }

      // Different file opened. Need to reset everything.
      else {
        return {
          marshaller,
          stack: [marshaller.parser.parse()],
          readonly: false,
          pointer: 0,
          externalModelsManagerDoneBootstraping: true,
        };
      }
    });
  }

  // Internal methods

  public get model(): DmnLatestModel | undefined {
    return this.state.stack[this.state.pointer];
  }

  private setExternalModelsByNamespace = (externalModelsByNamespace: DmnEditor.ExternalModelsIndex) => {
    this.setState((prev) => ({ ...prev, externalModelsByNamespace }));
  };

  private onModelChange: DmnEditor.OnDmnModelChange = (model) => {
    this.setState(
      (prev) => {
        const newStack = prev.stack.slice(0, prev.pointer + 1);
        return {
          ...prev,
          stack: [...newStack, model],
          pointer: newStack.length,
        };
      },
      () => this.props.onNewEdit({ id: `${this.state.absolutePath}__${generateUuid()}` })
    );
  };

  private onRequestExternalModelsAvailableToInclude: DmnEditor.OnRequestExternalModelsAvailableToInclude = async () => {
    if (!this.state.absolutePath) {
      return [];
    }

    const list = await this.props.onRequestFileList({
      pattern: EXTERNAL_MODELS_SEARCH_GLOB_PATTERN,
      opts: { type: SearchType.TRAVERSAL },
    });

    return list.paths.flatMap((pathRelativeToWorkspaceRoot) =>
      // Ignore thisDmn's file absolutePath
      this.joinPathWithWorkingDirBasePath(pathRelativeToWorkspaceRoot) === this.state.absolutePath
        ? []
        : pathRelativeToWorkspaceRoot
    );
  };

  private joinPathWithWorkingDirBasePath = (relativePath: string) => {
    return __path.join(this.props.workingDirBasePath, relativePath);
  };

  private onRequestToResolvePath = (relativePath: string) => {
    return __path.relative(
      this.props.workingDirBasePath,
      __path.resolve(__path.dirname(this.state.absolutePath!), relativePath)
    );
  };

  private onRequestExternalModelByPath: DmnEditor.OnRequestExternalModelByPath = async (
    pathRelativeToWorkspaceRoot
  ) => {
    const absolutePathOfRequestFile = this.joinPathWithWorkingDirBasePath(pathRelativeToWorkspaceRoot);
    const resource = await this.props.onRequestFileContent({
      path: absolutePathOfRequestFile,
      opts: { type: ContentType.TEXT },
    });
    const pathRelativeToOpenFile = __path.relative(__path.dirname(this.state.absolutePath!), absolutePathOfRequestFile);

    const ext = __path.extname(pathRelativeToWorkspaceRoot);
    if (ext === ".dmn") {
      return {
        relativePath: pathRelativeToOpenFile,
        type: "dmn",
        model: getMarshaller(resource?.content ?? "", { upgradeTo: "latest" }).parser.parse(),
        svg: "",
      };
    } else if (ext === ".pmml") {
      return {
        relativePath: pathRelativeToOpenFile,
        type: "pmml",
        model: XML2PMML(resource?.content ?? ""),
      };
    } else {
      throw new Error(`Unknown extension '${ext}'.`);
    }
  };

  private onOpenFileFromRelativePath = (relativePath: string) => {
    if (!this.state.absolutePath) {
      return;
    }

    this.props.onOpenFile(this.joinPathWithWorkingDirBasePath(relativePath));
  };

  public render() {
    return (
      <>
        {this.model && (
          <>
            {this.state.marshaller && this.state.externalModelsManagerDoneBootstraping && (
              <DmnEditor.DmnEditor
                originalVersion={this.state.marshaller.originalVersion}
                model={this.model}
                externalModelsByNamespace={this.state.externalModelsByNamespace}
                evaluationResults={[]}
                validationMessages={[]}
                externalContextName={""}
                externalContextDescription={""}
                issueTrackerHref={""}
                onModelChange={this.onModelChange}
                onRequestExternalModelByPath={this.onRequestExternalModelByPath}
                onRequestExternalModelsAvailableToInclude={this.onRequestExternalModelsAvailableToInclude}
                onRequestToJumpToPath={this.onOpenFileFromRelativePath}
                onRequestToResolvePath={this.onRequestToResolvePath}
              />
            )}
            <ExternalModelsManager
              thisDmnsAbsolutePath={this.state.absolutePath}
              model={this.model}
              onChange={this.setExternalModelsByNamespace}
              onRequestFileList={this.props.onRequestFileList}
              onRequestFileContent={this.props.onRequestFileContent}
              externalModelsManagerDoneBootstraping={this.externalModelsManagerDoneBootstraping}
            />
          </>
        )}
      </>
    );
  }
}

const NAMESPACES_EFFECT_SEPARATOR = " , ";

function ExternalModelsManager({
  thisDmnsAbsolutePath,
  model,
  onChange,
  onRequestFileContent,
  onRequestFileList,
  externalModelsManagerDoneBootstraping,
}: {
  thisDmnsAbsolutePath: string | undefined;
  model: DmnLatestModel;
  onChange: (externalModelsByNamespace: DmnEditor.ExternalModelsIndex) => void;
  onRequestFileContent: WorkspaceChannelApi["kogitoWorkspace_resourceContentRequest"];
  onRequestFileList: WorkspaceChannelApi["kogitoWorkspace_resourceListRequest"];
  externalModelsManagerDoneBootstraping: PromiseImperativeHandle<void>;
}) {
  const namespaces = useMemo(
    () =>
      (model.definitions.import ?? [])
        .map((i) => getNamespaceOfDmnImport({ dmnImport: i }))
        .join(NAMESPACES_EFFECT_SEPARATOR),
    [model.definitions.import]
  );

  const [externalUpdatesCount, setExternalUpdatesCount] = useState(0);

  // This is a hack. Every time a file is updates in KIE Sandbox, the Shared Worker emits an event to this BroadcastChannel.
  // By listening to it, we can reload the `externalModelsByNamespace` object. This makes the DMN Editor react to external changes,
  // Which is very important for multi-file editing.
  //
  // Now, this mechanism is not ideal. We would ideally only be notified on changes to relevant files, but this sub-system does not exist yet.
  // The consequence of this "hack" is some extra reloads.
  useEffect(() => {
    const bc = new BroadcastChannel("workspaces_files");
    bc.onmessage = ({ data }) => {
      // Changes to `thisDmn` shouldn't update its references to external models.
      if (data?.relativePath === thisDmnsAbsolutePath) {
        return;
      }

      setExternalUpdatesCount((prev) => prev + 1);
    };
    return () => {
      bc.close();
    };
  }, [thisDmnsAbsolutePath]);

  // This effect actually populates `externalModelsByNamespace` through the `onChange` call.
  useEffect(() => {
    let canceled = false;

    if (!thisDmnsAbsolutePath) {
      return;
    }

    onRequestFileList({ pattern: EXTERNAL_MODELS_SEARCH_GLOB_PATTERN, opts: { type: SearchType.TRAVERSAL } })
      .then((list) => {
        const resources: Array<Promise<ResourceContent | undefined>> = [];
        for (let i = 0; i < list.paths.length; i++) {
          const absolutePath = list.paths[i];
          if (absolutePath === thisDmnsAbsolutePath) {
            continue;
          }

          resources.push(onRequestFileContent({ path: absolutePath, opts: { type: ContentType.TEXT } }));
        }
        return Promise.all(resources);
      })
      .then((resources) => {
        const externalModelMap: DmnEditor.ExternalModelsIndex = {};

        const namespacesSet = new Set(namespaces.split(NAMESPACES_EFFECT_SEPARATOR));

        for (let i = 0; i < resources.length; i++) {
          const r = resources[i];
          const content = r?.content ?? "";
          const pathRelativeToWorkspaceRoot = r?.path ?? "";

          const ext = __path.extname(pathRelativeToWorkspaceRoot);
          if (ext === ".dmn") {
            const namespace = domParser.getDomDocument(content).documentElement.getAttribute("namespace");
            if (namespace && namespacesSet.has(namespace)) {
              // Check for multiplicity of namespaces on DMN models
              if (externalModelMap[namespace]) {
                console.warn(
                  `DMN EDITOR ROOT: Multiple DMN models encountered with the same namespace '${namespace}': '${pathRelativeToWorkspaceRoot}' and '${
                    externalModelMap[namespace]!.relativePath
                  }'. The latter will be considered.`
                );
              }

              externalModelMap[namespace] = {
                relativePath: pathRelativeToWorkspaceRoot,
                model: getMarshaller(content, { upgradeTo: "latest" }).parser.parse(),
                type: "dmn",
                svg: "",
              };
            }
          } else if (ext === ".pmml") {
            const namespace = getPmmlNamespace({ fileRelativePath: pathRelativeToWorkspaceRoot });
            if (namespace && namespacesSet.has(namespace)) {
              // No need to check for namespaces being equal becuase there can't be two files with the same relativePath.
              externalModelMap[namespace] = {
                relativePath: pathRelativeToWorkspaceRoot,
                model: XML2PMML(content),
                type: "pmml",
              };
            }
          } else {
            throw new Error(`Unknown extension '${ext}'.`);
          }
        }

        if (!canceled) {
          onChange(externalModelMap);
        }
        externalModelsManagerDoneBootstraping.resolve();
      });

    return () => {
      canceled = true;
    };
  }, [
    namespaces,
    onChange,
    onRequestFileContent,
    onRequestFileList,
    thisDmnsAbsolutePath,
    externalUpdatesCount,
    externalModelsManagerDoneBootstraping,
  ]);

  return <></>;
}
