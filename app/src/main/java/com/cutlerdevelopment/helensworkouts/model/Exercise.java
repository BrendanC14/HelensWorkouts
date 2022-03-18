package com.cutlerdevelopment.helensworkouts.model;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableExerciseType;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

public class Exercise extends AbstractSaveableItem {

    public static final String TYPE_FIRESTORE_KEY = "Type";

    private final SaveableExerciseType type;
    public SaveableExerciseType getTypeField() { return type; }
    public ExerciseType getType() {
        return type.getFieldValue();
    }
    public void setType(ExerciseType type) {
        this.type.setFieldValue(type);
    }

    public Exercise(String name, ExerciseType type) {
        super(name);
        this.type = new SaveableExerciseType(TYPE_FIRESTORE_KEY, type);
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.add(type);
        return fields;
    }
}
