package com.cutlerdevelopment.helensworkouts.model.saveables;

import java.math.BigDecimal;

public class SaveableDecimal extends AbstractSaveableField {

    private BigDecimal fieldValue;
    public BigDecimal getFieldValue() {
        return fieldValue;
    }
    public void setFieldValue(BigDecimal fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public Object getValueAsObject() {
        return fieldValue;
    }

    public SaveableDecimal(String fieldName, BigDecimal fieldValue) {
        super(fieldName);
        this.fieldValue = fieldValue;
    }
}
