package org.kie.workbench.common.stunner.bpmn.definition.models.bpmn2;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "timerEventDefinition", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class TimerEventDefinition {

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
