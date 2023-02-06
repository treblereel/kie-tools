package org.kie.workbench.common.stunner.sw.definition;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.json.GwtIncompatible;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.sw.definition.custom.json.MetadataJsonSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.yaml.MetadataYamlSerializer;

@JsType
@JsonbTypeSerializer(MetadataJsonSerializer.class)
@JsonbTypeDeserializer(MetadataJsonSerializer.class)
@YamlTypeSerializer(MetadataYamlSerializer.class)
@YamlTypeDeserializer(MetadataYamlSerializer.class)
public class Metadata extends GWTMetadata {

    @JsIgnore
    public static final String LABEL_METADATA = "metadata";

    @Category
    @JsIgnore
    public static final transient String category = Categories.STATES;

    @Labels
    @JsIgnore
    private static final Set<String> labels = Stream.of(LABEL_METADATA).collect(Collectors.toSet());

    /**
     * Unique data condition name.
     */
    @Property
    public String name;

    public String type;

    public String icon;

    public Metadata() {
    }

    public void setName(String name) {
        this.name = name;
    }

    @GwtIncompatible
    public String getName() {
        return name;
    }

    @GwtIncompatible
    public String getType() {
        return type;
    }

    @GwtIncompatible
    public void setType(String type) {
        this.type = type;
    }

    @GwtIncompatible
    public String getIcon() {
        return icon;
    }

    @GwtIncompatible
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
    }
}
