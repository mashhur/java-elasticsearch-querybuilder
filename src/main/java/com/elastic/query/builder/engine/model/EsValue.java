package com.elastic.query.builder.engine.model;

import java.util.List;

public class EsValue {

    private String field;
    private List<String> fields;

    private Object value;

    public EsValue(String field, Object value) {
        setField(field);
        setValue(value);
    }

    public EsValue(List<String> fields, Object value) {
        setFields(fields);
        setValue(value);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
