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

import "@patternfly/react-core/dist/styles/base.css";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { App } from "./App";

// Dynamically generate and set the nonce (example - in a real app, this comes from the server)
// For demonstration, a static base64 string is used, but this should be dynamic.
// declare const __webpack_nonce__: string | undefined;
// if (typeof __webpack_nonce__ === 'undefined') {
//   // In a production environment, this value would be provided by the server
//   // and injected into the HTML. For local development or client-side generation,
//   // you might have a placeholder or a simple static value.
//   // Example for a static nonce (not recommended for production CSP):
//    (window as any).__webpack_nonce__ = 'sha256-gEh1+8U9S1vkEuQSmmUMTZjyNSu5tIoECP4UXIEjMTk=';
// }

ReactDOM.render(<App />, document.getElementById("app")!);
