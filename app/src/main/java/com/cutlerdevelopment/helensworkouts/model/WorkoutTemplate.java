package com.cutlerdevelopment.helensworkouts.model;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

public class WorkoutTemplate extends AbstractSaveableItem {

    private final MyList<TemplateWorkoutStep> steps = new MyList<>();
    public MyList<TemplateWorkoutStep> getTemplateSteps() {
        return steps;
    }
    public void addWorkoutStep(TemplateWorkoutStep step) {
        steps.addIfNew(step);
    }
    public void addWorkoutStep(Exercise exercise) {
        steps.addIfNew(new TemplateWorkoutStep(steps.size(), exercise, this));
    }

    public WorkoutTemplate(String name) {
        super(name);
    }
    public WorkoutTemplate(String id, String name) {
        super(id, name);
    }

}
