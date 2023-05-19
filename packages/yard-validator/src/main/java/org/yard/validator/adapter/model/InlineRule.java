package org.yard.validator.adapter.model;

import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeDeserializer;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeSerializer;

import java.util.List;

@YAMLMapper
public class InlineRule implements Rule {

    @YamlTypeSerializer(InlineRuleDefListSerializer.class)
    @YamlTypeDeserializer(InlineRuleDefListSerializer.class)
    public List<Object> def;

    public InlineRule() {

    }

    public InlineRule(List<Object> data) {
        this.def = data;
    }

    public List<Object> getDef() {
        return def;
    }

    public void setDef(List<Object> def) {
        this.def = def;
    }
}
