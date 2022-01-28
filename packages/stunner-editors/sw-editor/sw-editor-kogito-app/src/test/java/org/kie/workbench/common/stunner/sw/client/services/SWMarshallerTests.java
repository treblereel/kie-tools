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

package org.kie.workbench.common.stunner.sw.client.services;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class SWMarshallerTests {

    private static final String EXAMPLE =
            "{\n" +
                    "  \"id\": \"helloworld\",\n" +
                    "  \"version\": \"1.0\",\n" +
                    "  \"specVersion\": \"0.8\",\n" +
                    "  \"name\": \"Hello World Workflow\",\n" +
                    "  \"description\": \"Inject Hello World\",\n" +
                    "  \"start\": \"Hello State\",\n" +
                    "  \"states\": [\n" +
                    "    {\n" +
                    "      \"name\": \"Hello State\",\n" +
                    "      \"type\": \"inject\",\n" +
                    "      \"data\": {\n" +
                    "        \"result\": \"Hello World!\"\n" +
                    "      },\n" +
                    "      \"end\": true\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    private static final String PATCH =
            "mydoc = {\n" +
                    "  \"baz\": \"qux\",\n" +
                    "  \"foo\": \"bar\"\n" +
                    "};\n" +
                    "thepatch = [\n" +
                    "  { \"op\": \"replace\", \"path\": \"/baz\", \"value\": \"boo\" }\n" +
                    "]\n" +
                    "patcheddoc = jsonpatch.apply_patch(mydoc, thepatch);";

    public static void test() {

        Object parsed = Global.JSON.parse(EXAMPLE);
        JsPropertyMap<Object> map = Js.uncheckedCast(parsed);
        String id = (String) map.get("id");
        DomGlobal.console.log(id);
        String name = (String) map.get("name");
        DomGlobal.console.log(name);
        String start = (String) map.get("start");
        DomGlobal.console.log(start);
        Object states = map.get("states");
        JsArray statesArray = Js.uncheckedCast(states);
        Object state = statesArray.getAt(0);
        parseState(state);
    }

    private static void parseState(Object state) {
        JsPropertyMap<Object> map = Js.uncheckedCast(state);
        String name = (String) map.get("name");
        DomGlobal.console.log(name);
        String type = (String) map.get("type");
        DomGlobal.console.log(type);
    }
}
