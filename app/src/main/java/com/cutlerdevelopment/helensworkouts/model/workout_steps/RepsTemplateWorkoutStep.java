package com.cutlerdevelopment.helensworkouts.model.workout_steps;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableInt;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

public class RepsTemplateWorkoutStep extends TemplateWorkoutStep {

    public static final String MIN_REPS_FIRESTORE_KEY = "Min Reps";
    public static final String MAX_REPS_FIRESTORE_KEY = "Max Reps";

    private final SaveableInt minReps;
    public SaveableInt getMinRepsField() { return minReps; }
    public int getMinReps() { return minReps.getFieldValue(); }
    public void setMinReps(int newReps) { minReps.setFieldValue(newReps);}

    private final SaveableInt maxReps;
    public SaveableInt getMaxRepsField() { return maxReps; }
    public int getMaxReps() { return maxReps.getFieldValue(); }
    public void setMaxReps(int newReps) { maxReps.setFieldValue(newReps);}

    public RepsTemplateWorkoutStep(int setNumber, int positionInWorkout, Exercise exercise, WorkoutTemplate workout, int minReps, int maxReps) {
        super(setNumber, positionInWorkout, exercise, workout);
        this.minReps = new SaveableInt(MIN_REPS_FIRESTORE_KEY, minReps);
        this.maxReps = new SaveableInt(MAX_REPS_FIRESTORE_KEY, maxReps);
    }

    public RepsTemplateWorkoutStep(String id, int setNumber, int positionInWorkout, Exercise exercise, WorkoutTemplate workout, int minReps, int maxReps) {
        super(id, setNumber, positionInWorkout, exercise, workout);
        this.minReps = new SaveableInt(MIN_REPS_FIRESTORE_KEY, minReps);
        this.maxReps = new SaveableInt(MAX_REPS_FIRESTORE_KEY, maxReps);
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.add(minReps);
        fields.add(maxReps);
        return fields;
    }
}
