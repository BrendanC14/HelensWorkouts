package com.cutlerdevelopment.helensworkouts.model;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableInt;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableString;
import com.cutlerdevelopment.helensworkouts.utils.MyList;


public class TemplateWorkoutStep extends AbstractSaveableItem {

    public static final String EXERCISE_NAME_FIRESTORE_KEY = "Exercise";
    public static final String WORKOUT_NAME_FIRESTORE_KEY = "Workout";
    public static final String POS_IN_WORKOUT_FIRESTORE_KEY = "Step Number";
    private Exercise exercise;
    public Exercise getExercise() {
        return exercise;
    }
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    private WorkoutTemplate workout;
    public WorkoutTemplate getWorkout() {
        return workout;
    }
    public void setWorkout(WorkoutTemplate workout) {
        this.workout = workout;
    }

    private final SaveableInt positionInWorkout;
    public int getPositionInWorkout() {
        return positionInWorkout.getFieldValue();
    }
    public void setPositionInWorkout(int positionInWorkout) {
        this.positionInWorkout.setFieldValue(positionInWorkout);
    }

    public TemplateWorkoutStep(int positionInWorkout, Exercise exercise, WorkoutTemplate workout) {
        super(exercise.getName());
        this.exercise = exercise;
        this.positionInWorkout = new SaveableInt(POS_IN_WORKOUT_FIRESTORE_KEY, positionInWorkout);
        this.workout = workout;
    }

    @Override
    public String getNameForSaving() {
        return positionInWorkout.getFieldValue() + ". " + super.getNameForSaving();
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.add(new SaveableString(EXERCISE_NAME_FIRESTORE_KEY, exercise.getName()));
        fields.add(positionInWorkout);
        fields.add(new SaveableString(WORKOUT_NAME_FIRESTORE_KEY, workout.getName()));
        return fields;
    }
}
