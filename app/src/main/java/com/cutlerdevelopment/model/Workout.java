package com.cutlerdevelopment.model;

import com.cutlerdevelopment.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.utils.MyList;

public class Workout extends AbstractSaveableItem {

    private final MyList<WorkoutStep> steps = new MyList<>();
    public MyList<WorkoutStep> getSteps() {
        return steps;
    }
    public void addWorkoutStep(WorkoutStep step) {
        steps.addIfNew(step);
    }
    public void addWorkoutStep(Exercise exercise) {
        steps.addIfNew(new WorkoutStep(steps.size(), exercise, this));
    }

    public Workout(String name) {
        super(name);
    }

}
