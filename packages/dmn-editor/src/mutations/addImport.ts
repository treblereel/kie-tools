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

import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15__tDefinitions, DMN15__tImport } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getXmlNamespaceDeclarationName } from "../xml/xmlNamespaceDeclarations";

export function addImport({
  definitions,
  includedModel,
}: {
  definitions: DMN15__tDefinitions;
  includedModel: {
    name: string;
    namespace: string;
    xmlns: string;
    locationURI: string;
  };
}) {
  const newImport: DMN15__tImport = {
    "@_id": generateUuid(),
    "@_name": includedModel.name.trim(),
    "@_importType": includedModel.xmlns,
    "@_namespace": includedModel.namespace,
    "@_locationURI": includedModel.locationURI,
  };

  definitions.import ??= [];
  definitions.import.push(newImport);

  // Find the first unused index. This will prevent declaring two namespaces with the same name.
  let index = 0;
  while (definitions[`@_xmlns:included${index}`]) {
    index++;
  }

  definitions[`@_xmlns:included${index}`] = includedModel.namespace;

  return newImport;
}
