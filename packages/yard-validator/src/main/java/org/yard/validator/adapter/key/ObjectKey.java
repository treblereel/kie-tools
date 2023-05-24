package org.yard.validator.adapter.key;


import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeDeserializer;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeSerializer;
import org.yard.validator.adapter.key.KeyImpl;
import org.yard.validator.adapter.key.KeyParent;
import org.yard.validator.adapter.key.Location;

public class ObjectKey extends KeyImpl {

    private Object value;

    public ObjectKey(final Location location,
                     final KeyParent parent,
                     final Object value) {
        super(location,
             parent );
        this.value = value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        //String.format is not a part of jre emulation
        //return String.format("%s %s",parent, value);
        return parent + " " + value;
    }

}
