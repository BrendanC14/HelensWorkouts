package com.cutlerdevelopment.helensworkouts.model.workout_steps;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.Workout;

public class WorkoutStep extends TemplateWorkoutStep{

    private Workout workout;
    @Override
    public Workout getWorkout() {
        return workout;
    }
    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public WorkoutStep(int positionInWorkout, Exercise exercise, Workout workout) {
        super(positionInWorkout, exercise, workout);
    }

    public WorkoutStep(String id, int positionInWorkout, Exercise exercise, Workout workout) {
        super(id, positionInWorkout, exercise, workout);
    }

    public WorkoutStep(TemplateWorkoutStep template, Workout workout) {
        super(template.getPositionInWorkout(), template.getExercise(), workout);
        this.workout = workout;
    }
    public WorkoutStep(String id, TemplateWorkoutStep template, Workout workout) {
        super(id, template.getPositionInWorkout(), template.getExercise(), workout);
        this.workout = workout;
    }
}