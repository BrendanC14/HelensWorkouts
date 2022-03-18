package com.cutlerdevelopment.helensworkouts.model.saveables;

import java.util.Date;

public class SaveableDate extends AbstractSaveableField {
    private Date fieldValue;
    public Date getFieldValue() {
        return fieldValue;
    }
    public void setFieldValue(Date fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public Object getValueAsObject() {
        return fieldValue;
    }

    public SaveableDate(String fieldName, Date fieldValue) {
        super(fieldName);
        this.fieldValue = fieldValue;
    }
}
