package com.cutlerdevelopment.helensworkouts.model;

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

    public WorkoutStep(TemplateWorkoutStep template, Workout workout) {
        super(template.getPositionInWorkout(), template.getExercise(), workout);
        this.workout = workout;
    }
}
