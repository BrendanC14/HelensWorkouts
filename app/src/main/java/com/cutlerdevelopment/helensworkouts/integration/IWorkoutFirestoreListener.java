package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

public interface IWorkoutFirestoreListener {
    void templateSaved(WorkoutTemplate template);
    void failedToSaveTemplate(WorkoutTemplate template, Exception e);
    void templateRetrieved(WorkoutTemplate template);
    void failedToRetrieveTemplate(String workoutName, Exception e);
    void templatesRetrieved(MyList<WorkoutTemplate> templates);
    void failedToRetrieveTemplates(String collectionName, Exception e);
    
    void workoutSaved(Workout workout);
    void failedToSaveWorkout(Workout workout, Exception e);
    void workoutRetrieved(Workout workout);
    void failedToRetrieveWorkout(String workoutName, Exception e);
    void workoutsRetrieved(MyList<Workout> workouts);
    void failedToRetrieveWorkouts(String collectionName, Exception e);

    void templateStepSaved(TemplateWorkoutStep step);
    void failedToSaveTemplateStep(TemplateWorkoutStep step, Exception e);
    void templateStepsRetrieved(WorkoutTemplate template, MyList<TemplateWorkoutStep> steps);
    void failedToRetrieveTemplateSteps(String collectionName, Exception e);
    
    void workoutStepSaved(WorkoutStep step);
    void failedToSaveWorkoutStep(WorkoutStep step, Exception e);
    void workoutStepsRetrieved(Workout workout, MyList<WorkoutStep> steps);
    void failedToRetrieveWorkoutSteps(String collectionName, Exception e);
}
