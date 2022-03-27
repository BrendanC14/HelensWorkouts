package com.cutlerdevelopment.helensworkouts.model.workout_steps;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableInt;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

public class TimedWorkoutStep extends TimedTemplateWorkoutStep {

    private Workout workout;
    @Override
    public Workout getWorkout() {
        return workout;
    }
    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public static final String ACTUAL_MINS_FIRESTORE_KEY = "Actual Mins";
    public static final String ACTUAL_SECS_FIRESTORE_KEY = "Actual Secs";

    private final SaveableInt actualMins;
    public int getActualMins() {
        return actualMins.getFieldValue();
    }
    public void setActualMins(int mins) {
        actualMins.setFieldValue(mins);
    }
    public SaveableInt getActualRepsField() {return actualMins;}

    private final SaveableInt actualSecs;
    public int getActualSecs() {
        return actualSecs.getFieldValue();
    }
    public void setActualSecs(int secs) {
        actualSecs.setFieldValue(secs);
    }
    public SaveableInt getActualSecsField() {return actualSecs;}

    public TimedWorkoutStep(TimedTemplateWorkoutStep templateWorkoutStep) {
        super(
                templateWorkoutStep.getSetNumber(),
                templateWorkoutStep.getPositionInWorkout(),
                templateWorkoutStep.getExercise(),
                templateWorkoutStep.getTemplate(),
                templateWorkoutStep.getMinutes(),
                templateWorkoutStep.getSeconds());
        actualMins = new SaveableInt(ACTUAL_MINS_FIRESTORE_KEY, 0);
        actualSecs = new SaveableInt(ACTUAL_SECS_FIRESTORE_KEY, 0);
    }

    public TimedWorkoutStep(String id, int setNumber, int positionInWorkout, Exercise exercise, WorkoutTemplate workout, int minReps, int maxReps, int actualMins, int actualSecs) {
        super(id, setNumber, positionInWorkout, exercise, workout, minReps, maxReps);
        this.actualMins = new SaveableInt(ACTUAL_MINS_FIRESTORE_KEY, actualMins);
        this.actualSecs = new SaveableInt(ACTUAL_SECS_FIRESTORE_KEY, actualSecs);
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.add(actualMins);
        fields.add(actualSecs);
        return fields;
    }
}
