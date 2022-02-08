/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "timerEventDefinition", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class TimerEventDefinition extends AbstractEventDefinition {

    @XmlElement(name = "timeDate")
    protected String timeDate;

    @XmlElement(name = "timeDuration")
    protected String timeDuration;

    @XmlElement(name = "timeCycle")
    protected String timeCycle;

    @XmlElement(name = "timeCycleLanguage")
    protected String timeCycleLanguage;

    public TimerEventDefinition() {
    }

    public TimerEventDefinition(String timeDate, String timeDuration, String timeCycle, String timeCycleLanguage) {
        this.timeDate = timeDate;
        this.timeDuration = timeDuration;
        this.timeCycle = timeCycle;
        this.timeCycleLanguage = timeCycleLanguage;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }

    public String getTimeCycle() {
        return timeCycle;
    }

    public void setTimeCycle(String timeCycle) {
        this.timeCycle = timeCycle;
    }

    public String getTimeCycleLanguage() {
        return timeCycleLanguage;
    }

    public void setTimeCycleLanguage(String timeCycleLanguage) {
        this.timeCycleLanguage = timeCycleLanguage;
    }
}
