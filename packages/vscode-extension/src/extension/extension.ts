/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as vscode from "vscode";
import { KogitoEditorsExtension } from "./KogitoEditorsExtension";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditorFactory } from "./KogitoEditorFactory";
import { LocalRouter } from "./LocalRouter";

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const router = new LocalRouter(context);

  const editorStore = new KogitoEditorStore();
  const editorFactory = new KogitoEditorFactory(context, router, editorStore);
  const extension = new KogitoEditorsExtension(context, editorStore, editorFactory);

  extension.startReplacingTextEditorsByKogitoEditorsAsTheyOpenIfLanguageIsSupported();
  extension.registerCustomSaveCommand();
  extension.registerCustomSaveAllCommand();

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  //FIXME: For some reason, this method is not being called :(
  console.info("Extension is deactivating");
}
