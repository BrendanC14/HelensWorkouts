package com.cutlerdevelopment.helensworkouts.model.saveables;

public class SaveableString extends AbstractSaveableField{
    private String fieldValue;
    public String getFieldValue() {
        return fieldValue;
    }
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public Object getValueAsObject() {
        return fieldValue;
    }
    public SaveableString(String fieldName, String fieldValue) {
        super(fieldName);
        this.fieldValue = fieldValue;
    }
}
