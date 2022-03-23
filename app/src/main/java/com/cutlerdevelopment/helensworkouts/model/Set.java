package com.cutlerdevelopment.helensworkouts.model;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableString;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.HashMap;

public class Set extends AbstractSaveableItem {

    public static final String WORKOUT_ID_FIRESTORE_KEY = "Workout ID";
    private final MyList<TemplateWorkoutStep> steps = new MyList<>();
    private WorkoutTemplate template;

    public WorkoutTemplate getTemplate() {
        return template;
    }

    public void setTemplate(WorkoutTemplate template) {
        this.template = template;
    }

    private Workout workout;
    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    protected Set(String name, WorkoutTemplate template) {
        super(name);
        this.template =template;
    }

    protected Set(String id, String name, WorkoutTemplate template) {
        super(id, name);
        this.template = template;
    }

    protected Set(String name, Workout workout) {
        super(name);
        this.workout =workout;
    }

    protected Set(String id, String name, Workout workout) {
        super(id, name);
        this.workout = workout;
    }

    public MyList<TemplateWorkoutStep> getTemplateSteps() {
        return steps;
    }
    public void addWorkoutStep(TemplateWorkoutStep step) {
        steps.addIfNew(step);
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> map = getSaveableFields();
        map.add(new SaveableString(WORKOUT_ID_FIRESTORE_KEY, getTemplate().getId()));
        return map;
    }
}
