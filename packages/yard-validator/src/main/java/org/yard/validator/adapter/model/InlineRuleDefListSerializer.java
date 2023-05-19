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

import java.util.List;
import java.util.Locale;

public class InlineRuleDefListSerializer
        implements YAMLSerializer<List<Object>>, YAMLDeserializer<List<Object>> {


    @Override
    public List<Object> deserialize(YamlMapping yamlMapping, String s, YAMLDeserializationContext yamlDeserializationContext) throws YAMLDeserializationException {
        return null;
    }

    @Override
    public List<Object> deserialize(YamlNode yamlNode, YAMLDeserializationContext yamlDeserializationContext) {
        return null;
    }

    @Override
    public void serialize(YAMLWriter yamlWriter, String s, List<Object> objects, YAMLSerializationContext yamlSerializationContext) {

    }

    @Override
    public void serialize(YAMLSequenceWriter yamlSequenceWriter, List<Object> objects, YAMLSerializationContext yamlSerializationContext) {

    }
}
