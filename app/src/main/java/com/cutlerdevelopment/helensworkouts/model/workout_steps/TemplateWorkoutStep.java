package com.cutlerdevelopment.helensworkouts.model.workout_steps;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableInt;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableString;
import com.cutlerdevelopment.helensworkouts.utils.MyList;


public class TemplateWorkoutStep extends AbstractSaveableItem {

    public static final String EXERCISE_ID_FIRESTORE_KEY = "Exercise ID";
    public static final String WORKOUT_ID_FIRESTORE_KEY = "Workout ID";
    public static final String POS_IN_WORKOUT_FIRESTORE_KEY = "Step Number";
    public static final String SET_NUMBER_FIRESTORE_KEY = "Set Number";
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

    private final SaveableInt setNumber;
    public int getSetNumber() {
        return setNumber.getFieldValue();
    }
    public void setSetNumber(int setNumber) {
        this.setNumber.setFieldValue(setNumber);
    }
    public SaveableInt getSetNumberField() { return setNumber;}

    public TemplateWorkoutStep(int setNumber, int positionInWorkout, Exercise exercise, WorkoutTemplate workout) {
        super(exercise.getName());
        this.exercise = exercise;
        this.positionInWorkout = new SaveableInt(POS_IN_WORKOUT_FIRESTORE_KEY, positionInWorkout);
        this.setNumber = new SaveableInt(SET_NUMBER_FIRESTORE_KEY, setNumber);
        this.workout = workout;
    }
    public TemplateWorkoutStep(String id, int setNumber,  int positionInWorkout, Exercise exercise, WorkoutTemplate workout) {
        super(id, exercise.getName());
        this.exercise = exercise;
        this.positionInWorkout = new SaveableInt(POS_IN_WORKOUT_FIRESTORE_KEY, positionInWorkout);
        this.setNumber = new SaveableInt(SET_NUMBER_FIRESTORE_KEY, setNumber);
        this.workout = workout;
    }

    @Override
    public String getNameForSaving() {
        return positionInWorkout.getFieldValue() + ". " + super.getNameForSaving();
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.add(new SaveableString(EXERCISE_ID_FIRESTORE_KEY, exercise.getId()));
        fields.add(setNumber);
        fields.add(positionInWorkout);
        fields.add(new SaveableString(WORKOUT_ID_FIRESTORE_KEY, workout.getId()));
        return fields;
    }
}
