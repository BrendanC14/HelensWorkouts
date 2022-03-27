package com.cutlerdevelopment.helensworkouts.model.workout_steps;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableDecimal;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableInt;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.math.BigDecimal;

public class WeightWorkoutStep extends RepsWorkoutStep {

    public static final String ACTUAL_WEIGHT_FIRESTORE_KEY = "Actual Weight";

    private final SaveableDecimal actualWeight;
    public BigDecimal getActualWeight() {
        return actualWeight.getFieldValue();
    }
    public void setActualWeight(BigDecimal weight) {
        actualWeight.setFieldValue(weight);
    }
    public SaveableDecimal getActualWeightField() {return actualWeight;}

    public WeightWorkoutStep(RepsTemplateWorkoutStep templateWorkoutStep) {
        super(templateWorkoutStep);
        actualWeight = new SaveableDecimal(ACTUAL_WEIGHT_FIRESTORE_KEY, BigDecimal.ZERO);
    }

    public WeightWorkoutStep(String id, int setNumber, int positionInWorkout, Exercise exercise, WorkoutTemplate workout, int minReps, int maxReps, int actualReps, BigDecimal actualWeight) {
        super(id, setNumber, positionInWorkout, exercise, workout, minReps, maxReps, actualReps);
        this.actualWeight = new SaveableDecimal(ACTUAL_WEIGHT_FIRESTORE_KEY, actualWeight);
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.add(actualWeight);
        return fields;
    }
}
