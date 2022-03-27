package com.cutlerdevelopment.helensworkouts.model;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableDate;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.RepsTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.RepsWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TimedTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TimedWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.WeightWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.Date;

public class Workout extends WorkoutTemplate{

    public static final String DATE_FIRESTORE_KEY = "Date";
    private final SaveableDate date;

    public Date getDate() {
        return date.getFieldValue();
    }
    public void setDate(Date date) {
        this.date.setFieldValue(date);
    }

    public Workout(String name, Date date) {
        super(name);
        this.date = new SaveableDate(DATE_FIRESTORE_KEY, date);
    }
    public Workout(String id, String name, Date date) {
        super(id, name);
        this.date = new SaveableDate(DATE_FIRESTORE_KEY, date);
    }
    public Workout(WorkoutTemplate template, Date date) {
        super(template.getName());
        this.date = new SaveableDate(DATE_FIRESTORE_KEY, date );
        for (TemplateWorkoutStep templateStep : template.getTemplateSteps()) {
            ExerciseType type = templateStep.getExercise().getType();
            if (type == ExerciseType.REST || type == ExerciseType.TIMED) {
                TimedWorkoutStep step = new TimedWorkoutStep((TimedTemplateWorkoutStep) templateStep);
                addWorkoutStep(step);
                step.setWorkout(this);
            } else if (type == ExerciseType.WEIGHT) {
                WeightWorkoutStep step = new WeightWorkoutStep((RepsTemplateWorkoutStep) templateStep);
                addWorkoutStep(step);
                step.setWorkout(this);
            } else {
                RepsWorkoutStep step = new RepsWorkoutStep((RepsTemplateWorkoutStep) templateStep);
                addWorkoutStep(step);
                step.setWorkout(this);
            }
        }
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.addIfNew(date);
        return fields;
    }
}
