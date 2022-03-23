package com.cutlerdevelopment.helensworkouts.model;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableDate;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.WorkoutStep;
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

    private MyList<WorkoutStep> steps = new MyList<>();

    public MyList<WorkoutStep> getSteps() {
        return steps;
    }

    public void addWorkoutStep(WorkoutStep step) {
        steps.addIfNew(step);
    }
    public void addWorkoutStep(Exercise exercise) {
        steps.addIfNew(new WorkoutStep(steps.size(), exercise, this));
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
            steps.add(new WorkoutStep(templateStep, this));
        }
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.addIfNew(date);
        return fields;
    }
}
