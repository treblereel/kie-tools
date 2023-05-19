package org.yard.validator.adapter.model;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.treblereel.gwt.yaml.api.YAMLDeserializer;
import org.treblereel.gwt.yaml.api.YAMLSerializer;
import org.treblereel.gwt.yaml.api.exception.YAMLDeserializationException;
import org.treblereel.gwt.yaml.api.internal.deser.YAMLDeserializationContext;
import org.treblereel.gwt.yaml.api.internal.ser.YAMLSerializationContext;
import org.treblereel.gwt.yaml.api.stream.YAMLSequenceWriter;
import org.treblereel.gwt.yaml.api.stream.YAMLWriter;

import java.util.Locale;

public class WhenThenRuleThenSerializer
        implements YAMLSerializer, YAMLDeserializer {
    @Override
    public Object deserialize(YamlMapping yamlMapping, String key, YAMLDeserializationContext yamlDeserializationContext) throws YAMLDeserializationException {
        return deserialize(yamlMapping.value(key), yamlDeserializationContext);
    }

    @Override
    public Object deserialize(YamlNode yamlNode, YAMLDeserializationContext yamlDeserializationContext) {
        if (yamlNode == null || yamlNode.isEmpty()) {
            return null;
        }
        return yamlNode.asScalar().value().toLowerCase(Locale.ROOT);
    }

    @Override
    public void serialize(YAMLWriter yamlWriter, String s, Object o, YAMLSerializationContext yamlSerializationContext) {
        yamlWriter.value(s, o.toString());
    }

    @Override
    public void serialize(YAMLSequenceWriter yamlSequenceWriter, Object o, YAMLSerializationContext yamlSerializationContext) {
        yamlSequenceWriter.value(o.toString());
    }
}
