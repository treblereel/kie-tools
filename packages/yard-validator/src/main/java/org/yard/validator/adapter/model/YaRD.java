package org.yard.validator.adapter.model;


import java.util.List;

public class YaRD {

    private String specVersion = "alpha";
    private String kind = "YaRD";
    private String name;
    private String expressionLang;
    private List<Input> inputs;
    private List<Element> elements;

    public String getName() {
        return name;
    }

    public String getExpressionLang() {
        return expressionLang;
    }

    public void setExpressionLang(String expressionLang) {
        this.expressionLang = expressionLang;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public List<Element> getElements() {
        return elements;
    }
}