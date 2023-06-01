package org.yard.validator.adapter.model;

import org.treblereel.gwt.yaml.api.YAMLDeserializer;
import org.treblereel.gwt.yaml.api.YAMLSerializer;
import org.treblereel.gwt.yaml.api.exception.YAMLDeserializationException;
import org.treblereel.gwt.yaml.api.internal.deser.YAMLDeserializationContext;
import org.treblereel.gwt.yaml.api.internal.ser.YAMLSerializationContext;
import org.treblereel.gwt.yaml.api.node.YamlMapping;
import org.treblereel.gwt.yaml.api.node.YamlNode;
import org.treblereel.gwt.yaml.api.node.YamlSequence;

public class InlineRuleDefSerializer
        implements YAMLSerializer<Object>, YAMLDeserializer<Object> {


    @Override
    public Object deserialize(YamlMapping yamlMapping, String s, YAMLDeserializationContext yamlDeserializationContext) throws YAMLDeserializationException {
        return null;
    }

    @Override
    public Object deserialize(YamlNode yamlNode, YAMLDeserializationContext yamlDeserializationContext) {
        return null;
    }

    @Override
    public void serialize(YamlMapping yamlWriter, String s, Object objects, YAMLSerializationContext yamlSerializationContext) {

    }

    @Override
    public void serialize(YamlSequence yamlSequenceWriter, Object objects, YAMLSerializationContext yamlSerializationContext) {

    }
}
