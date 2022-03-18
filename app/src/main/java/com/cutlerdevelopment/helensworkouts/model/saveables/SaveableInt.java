package com.cutlerdevelopment.helensworkouts.model.saveables;

public class SaveableInt extends AbstractSaveableField{

    private int fieldValue;
    public int getFieldValue() {
        return fieldValue;
    }
    public void setFieldValue(int fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public Object getValueAsObject() {
        return fieldValue;
    }

    public SaveableInt(String fieldName, int fieldValue) {
        super(fieldName);
        this.fieldValue = fieldValue;
    }
}
