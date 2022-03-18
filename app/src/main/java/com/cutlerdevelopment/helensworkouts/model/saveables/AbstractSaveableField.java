package com.cutlerdevelopment.helensworkouts.model.saveables;

public abstract class AbstractSaveableField {
    private String fieldName;
    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public abstract Object getValueAsObject();
    protected AbstractSaveableField(String fieldName) {
        this.fieldName = fieldName;
    }
}
