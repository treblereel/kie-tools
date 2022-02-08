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
    public StartEvent unmarshal(FallbackStartEvent v) {
        if (v.getEventDefinition() instanceof SignalEventDefinition) {
            StartSignalEvent result = new StartSignalEvent();
            result.setSignalEventDefinition((SignalEventDefinition) v.getEventDefinition());
            return result;
        }
        else if (v.getEventDefinition() instanceof CompensateEventDefinition) {
            StartCompensationEvent result = new StartCompensationEvent();
            result.setCompensateEventDefinition((CompensateEventDefinition) v.getEventDefinition());
            return result;
        }
        else if (v.getEventDefinition() instanceof org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2.MessageEventDefinition) {
            StartMessageEvent result = new StartMessageEvent();
            result.setMessageEventDefinition((MessageEventDefinition) v.getEventDefinition());
            return result;
        }
        else if (v.getEventDefinition() instanceof SignalEventDefinition) {
            StartSignalEvent result = new StartSignalEvent();
            result.setDataOutputs(v.getDataOutputs());
            result.setSignalEventDefinition((SignalEventDefinition) v.getEventDefinition());
            return result;
        }

        else if (v.getEventDefinition() instanceof ErrorEventDefinition) {
            StartErrorEvent result = new StartErrorEvent();
            result.setErrorEventDefinition((ErrorEventDefinition) v.getEventDefinition());
            return result;
        }

        else if (v.getEventDefinition() instanceof EscalationEventDefinition) {
            StartEscalationEvent result = new StartEscalationEvent();
            result.setEscalationEventDefinition((EscalationEventDefinition) v.getEventDefinition());
            return result;
        }

        else if (v.getEventDefinition() instanceof ConditionalEventDefinition) {
            StartConditionalEvent result = new StartConditionalEvent();
            result.setConditionalEventDefinition((ConditionalEventDefinition) v.getEventDefinition());
            return result;
        }
        else if (v.getEventDefinition() instanceof TimerEventDefinition) {
            StartTimerEvent result = new StartTimerEvent();
            return result;
        }
        else {
            return new StartNoneEvent();
        }
    }

    @Override
    public FallbackStartEvent marshal(StartEvent v) throws Exception {
        FallbackStartEvent fallbackStartEvent = new FallbackStartEvent();
        if(v instanceof StartCompensationEvent) {
            fallbackStartEvent.setEventDefinition(new CompensateEventDefinition());
        }
        // and so on ....
        return fallbackStartEvent;
    }
}
