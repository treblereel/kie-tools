package org.yard.validator.adapter.model;


import org.treblereel.gwt.yaml.api.annotation.YamlSubtype;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeInfo;

@YamlTypeInfo({
        @YamlSubtype(alias = "InlineRule", type = InlineRule.class),
        @YamlSubtype(alias = "WhenThenRule", type = WhenThenRule.class)
})
public interface Rule {
}
