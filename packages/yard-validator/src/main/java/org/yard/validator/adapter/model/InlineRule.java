package org.yard.validator.adapter.model;

import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeDeserializer;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeSerializer;

import java.util.List;

@YAMLMapper
public class InlineRule implements Rule {

    @YamlTypeSerializer(InlineRuleDefSerializer.class)
    @YamlTypeDeserializer(InlineRuleDefSerializer.class)
    public List def;

    public InlineRule() {

    }

    public InlineRule(List data) {
        this.def = data;
    }

    public List getDef() {
        return def;
    }

    public void setDef(List def) {
        this.def = def;
    }
}
