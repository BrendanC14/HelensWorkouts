package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

public interface IWorkoutFirestoreListener {
    void templateSaved(WorkoutTemplate template);
    void failedToSaveTemplate(WorkoutTemplate template, Exception e);
    void templateUpdated(WorkoutTemplate template);
    void failedToUpdateTemplate(WorkoutTemplate template, Exception e);
    void templateDeleted(WorkoutTemplate template);
    void templateRetrieved(WorkoutTemplate template);
    void failedToRetrieveTemplate(String workoutName, Exception e);
    void templatesRetrieved(MyList<WorkoutTemplate> templates);
    void failedToRetrieveTemplates(String collectionName, Exception e);
    
    void workoutSaved(Workout workout);
    void failedToSaveWorkout(Workout workout, Exception e);
    void workoutUpdated(Workout workout);
    void failedToUpdateWorkout(Workout workout, Exception e);
    void workoutRetrieved(Workout workout);
    void failedToRetrieveWorkout(String workoutName, Exception e);
    void workoutsRetrieved(MyList<Workout> workouts);
    void failedToRetrieveWorkouts(String collectionName, Exception e);

    void templateStepSaved(TemplateWorkoutStep step);
    void failedToSaveTemplateStep(TemplateWorkoutStep step, Exception e);
    void templateStepUpdated(TemplateWorkoutStep template);
    void failedToUpdateTemplateStep(TemplateWorkoutStep template, Exception e);
    void templateStepsRetrieved(WorkoutTemplate template, MyList<TemplateWorkoutStep> steps);
    void failedToRetrieveTemplateSteps(String collectionName, Exception e);
    
    void workoutStepSaved(TemplateWorkoutStep step);
    void failedToSaveWorkoutStep(TemplateWorkoutStep step, Exception e);
    void workoutUpdated(TemplateWorkoutStep step);
    void failedToUpdateWorkout(TemplateWorkoutStep step, Exception e);
    void workoutStepsRetrieved(Workout workout, MyList<TemplateWorkoutStep> steps);
    void failedToRetrieveWorkoutSteps(String collectionName, Exception e);
}
