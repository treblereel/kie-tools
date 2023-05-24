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
    public void serialize(YAMLWriter yamlWriter, String s, Object objects, YAMLSerializationContext yamlSerializationContext) {

    }

    @Override
    public void serialize(YAMLSequenceWriter yamlSequenceWriter, Object objects, YAMLSerializationContext yamlSerializationContext) {

    }
}
