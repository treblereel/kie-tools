package org.yard.validator.adapter.model;

import org.treblereel.gwt.yaml.api.YAMLDeserializer;
import org.treblereel.gwt.yaml.api.YAMLSerializer;
import org.treblereel.gwt.yaml.api.exception.YAMLDeserializationException;
import org.treblereel.gwt.yaml.api.internal.deser.YAMLDeserializationContext;
import org.treblereel.gwt.yaml.api.internal.ser.YAMLSerializationContext;
import org.treblereel.gwt.yaml.api.node.YamlMapping;
import org.treblereel.gwt.yaml.api.node.YamlNode;
import org.treblereel.gwt.yaml.api.node.YamlSequence;

import java.util.Locale;

public class WhenThenRuleThenSerializer
        implements YAMLSerializer<Object>, YAMLDeserializer<Object> {
    @Override
    public Object deserialize(YamlMapping yamlMapping, String key, YAMLDeserializationContext yamlDeserializationContext) throws YAMLDeserializationException {
        return deserialize(yamlMapping.getNode(key), yamlDeserializationContext);
    }

    @Override
    public Object deserialize(YamlNode yamlNode, YAMLDeserializationContext yamlDeserializationContext) {
        if (yamlNode == null || yamlNode.isEmpty()) {
            return null;
        }
        return yamlNode.<String>asScalar().value().toLowerCase(Locale.ROOT);
    }

    @Override
    public void serialize(YamlMapping yamlWriter, String s, Object o, YAMLSerializationContext yamlSerializationContext) {
        yamlWriter.addScalarNode(s, o.toString());
    }

    @Override
    public void serialize(YamlSequence yamlSequenceWriter, Object o, YAMLSerializationContext yamlSerializationContext) {
        yamlSequenceWriter.addScalarNode(o.toString());
    }
}
