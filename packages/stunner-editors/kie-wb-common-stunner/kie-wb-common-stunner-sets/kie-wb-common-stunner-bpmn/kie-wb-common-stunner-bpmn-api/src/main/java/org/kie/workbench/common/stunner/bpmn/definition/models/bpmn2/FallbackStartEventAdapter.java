/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class FallbackStartEventAdapter extends XmlAdapter<FallbackStartEvent, StartEvent> {

    @Override
    public StartEvent unmarshal(FallbackStartEvent v) throws Exception {
        if (v.getCompensateEventDefinition() != null) {
            return new StartCompensationEvent();
        }
        else if (v.getMessageEventDefinition() != null) {
            return new StartMessageEvent();
        }
        else if (v.getSignalEventDefinition() != null) {
            return new StartSignalEvent();
        }
        else if (v.getErrorEventDefinition() != null) {
            return new StartErrorEvent();
        }
        else if (v.getEscalationEventDefinition() != null) {
            return new StartEscalationEvent();
        }
        else if (v.getConditionalEventDefinition() != null) {
            return new StartConditionalEvent();
        }
        else if (v.getTimerEventDefinition() != null) {
            return new StartTimerEvent();
        }
        else {
            return new StartNoneEvent();
        }
    }

    @Override
    public FallbackStartEvent marshal(StartEvent v) throws Exception {
        return null;
    }
}
