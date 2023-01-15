package com.cutlerdevelopment.helensworkouts.model;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableDecimal;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableExerciseType;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.math.BigDecimal;

public class Exercise extends AbstractSaveableItem {

    public static final String TYPE_FIRESTORE_KEY = "Type";
    public static final String RECORD_WEIGHT_FIRESTORE_KEY = "Record Weight";

    private final SaveableExerciseType type;
    public SaveableExerciseType getTypeField() { return type; }
    public ExerciseType getType() {
        return type.getFieldValue();
    }
    public void setType(ExerciseType type) {
        this.type.setFieldValue(type);
    }

    private final SaveableDecimal recordWeight;
    public SaveableDecimal getRecordWeightField() { return recordWeight;}
    public BigDecimal getRecordWeight() { return recordWeight.getFieldValue();}
    public void setRecordWeight(BigDecimal decimal) { recordWeight.setFieldValue(decimal);}

    public Exercise(String name, ExerciseType type) {
        super(name);
        this.type = new SaveableExerciseType(TYPE_FIRESTORE_KEY, type);
        this.recordWeight = new SaveableDecimal(RECORD_WEIGHT_FIRESTORE_KEY, BigDecimal.ZERO);
    }

    public Exercise(String id, String name, ExerciseType type, BigDecimal recordWeight) {
        super(id, name);
        this.type = new SaveableExerciseType(TYPE_FIRESTORE_KEY, type);
        this.recordWeight = new SaveableDecimal(RECORD_WEIGHT_FIRESTORE_KEY, recordWeight);
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.add(type);
        fields.add(recordWeight);
        return fields;
    }

    public boolean lowerIsBetter() {
        return this.getName().equals("Assisted Pull Ups");
    }

    public static Exercise getRestExercise() {
        return new Exercise("Rest", ExerciseType.REST);
    }
}
