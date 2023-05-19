package org.yard.validator.adapter.model;

import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeDeserializer;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeSerializer;

import java.util.List;

@YAMLMapper
public class WhenThenRule implements Rule {

    @YamlTypeSerializer(WhenThenRuleObjectListSerializer.class)
    @YamlTypeDeserializer(WhenThenRuleObjectListSerializer.class)
    private List<Object> when;
    @YamlTypeSerializer(WhenThenRuleThenSerializer.class)
    @YamlTypeDeserializer(WhenThenRuleThenSerializer.class)
    private Object then;

    public List<Object> getWhen() {
        return when;
    }

    public Object getThen() {
        return then;
    }

    public void setWhen(List<Object> when) {
        this.when = when;
    }

    public void setThen(Object then) {
        this.then = then;
    }
}
