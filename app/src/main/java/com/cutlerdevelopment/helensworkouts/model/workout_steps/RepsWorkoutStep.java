package com.cutlerdevelopment.helensworkouts.model.workout_steps;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableInt;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

public class RepsWorkoutStep extends RepsTemplateWorkoutStep {

    private Workout workout;
    @Override
    public Workout getWorkout() {
        return workout;
    }
    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public static final String ACTUAL_REPS_FIRESTORE_KEY = "Actual Reps";

    private final SaveableInt actualReps;
    public int getActualReps() {
        return actualReps.getFieldValue();
    }
    public void setActualReps(int reps) {
        actualReps.setFieldValue(reps);
    }
    public SaveableInt getActualRepsField() {return actualReps;}

    public RepsWorkoutStep(RepsTemplateWorkoutStep templateWorkoutStep) {
        super(
                templateWorkoutStep.getSetNumber(),
                templateWorkoutStep.getPositionInWorkout(),
                templateWorkoutStep.getExercise(),
                templateWorkoutStep.getTemplate(),
                templateWorkoutStep.getMinReps(),
                templateWorkoutStep.getMaxReps());
        actualReps = new SaveableInt(ACTUAL_REPS_FIRESTORE_KEY, 0);
    }

    public RepsWorkoutStep(String id, int setNumber, int positionInWorkout, Exercise exercise, WorkoutTemplate workout, int minReps, int maxReps, int actualReps) {
        super(id, setNumber, positionInWorkout, exercise, workout, minReps, maxReps);
        this.actualReps = new SaveableInt(ACTUAL_REPS_FIRESTORE_KEY, actualReps);
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.add(actualReps);
        return fields;
    }
}
