package org.yard.validator.adapter.model;


import org.treblereel.gwt.yaml.api.annotation.YamlSubtype;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeInfo;

@YamlTypeInfo({
        @YamlSubtype(alias = "decisionTable", type = DecisionTable.class),
        @YamlSubtype(alias = "literalExpression", type = LiteralExpression.class)
})
public abstract class DecisionLogic {
}
