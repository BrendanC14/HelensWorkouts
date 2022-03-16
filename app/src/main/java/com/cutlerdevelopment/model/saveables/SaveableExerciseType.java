package com.cutlerdevelopment.model.saveables;

import com.cutlerdevelopment.model.ExerciseType;

public class SaveableExerciseType extends AbstractSaveableField {

    private ExerciseType fieldValue;
    public ExerciseType getFieldValue() {
        return fieldValue;
    }
    public void setFieldValue(ExerciseType fieldValue) {
        this.fieldValue = fieldValue;
    }

    public SaveableExerciseType(String fieldName, ExerciseType type) {
        super(fieldName);
        fieldValue = type;
    }

    @Override
    public Object getValueAsObject() {
        return fieldValue.name();
    }
}
