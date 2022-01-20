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

package io.serverlessworkflow.tests;

import java.util.List;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.start.Start;
import io.serverlessworkflow.api.states.DefaultState;
import io.serverlessworkflow.api.states.InjectState;
import io.serverlessworkflow.api.states.OperationState;
import io.serverlessworkflow.api.workflow.Events;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CNCFSWTests {

    public static String YAML = "id: greeting\n" +
            "version: '1.0'\n" +
            "name: Greeting Workflow\n" +
            "start: Greet\n" +
            "description: Greet Someone\n" +
            "functions:\n" +
            "  - name: greetingFunction\n" +
            "    operation: file://myapis/greetingapis.json#greeting\n" +
            "states:\n" +
            "- name: Greet\n" +
            "  type: operation\n" +
            "  actions:\n" +
            "  - functionRef:\n" +
            "      refName: greetingFunction\n" +
            "      arguments:\n" +
            "        name: \"${ .greet.name }\"\n" +
            "    actionDataFilter:\n" +
            "      results: \"${ .payload.greeting }\"\n" +
            "  stateDataFilter:\n" +
            "    output: \"${ .greeting }\"\n" +
            "  end: true";

    @Test
    public void testApi() {

        Workflow workflow = Workflow.fromSource(YAML);

        assertNotNull(workflow);
        assertEquals("greeting", workflow.getId());
        assertEquals("Greeting Workflow", workflow.getName());

        assertNotNull(workflow.getFunctions());
        assertEquals(1, workflow.getFunctions().getFunctionDefs().size());
        assertEquals("greetingFunction", workflow.getFunctions().getFunctionDefs().get(0).getName());

        assertNotNull(workflow.getStates());
        assertEquals(1, workflow.getStates().size());

        OperationState operationState = (OperationState) workflow.getStates().get(0);
        assertEquals("Greet", operationState.getName());
        assertEquals(DefaultState.Type.OPERATION, operationState.getType());

        Start start = workflow.getStart();
        List<State> states = workflow.getStates();
        Events events = workflow.getEvents();

        InjectState injectState = null;

        injectState.getEnd();
        System.out.println(workflow.getId());
        System.out.println(workflow.getName());
        System.out.println(workflow.getDescription());
        System.out.println(workflow.getStart().getStateName());
    }
}
