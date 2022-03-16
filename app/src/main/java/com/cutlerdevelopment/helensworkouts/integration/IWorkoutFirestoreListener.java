package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.model.Workout;
import com.cutlerdevelopment.model.WorkoutStep;
import com.cutlerdevelopment.utils.MyList;

public interface IWorkoutFirestoreListener {
    void workoutSaved(Workout workout);
    void failedToSaveWorkout(Workout workout, Exception e);
    void workoutRetrieved(Workout workout);
    void failedToRetrieveWorkout(String workoutName, Exception e);
    void workoutsRetrieved(MyList<Workout> workouts);
    void failedToRetrieveWorkouts(String collectionName, Exception e);


    void workoutStepSaved(WorkoutStep step);
    void failedToSaveWorkoutStep(WorkoutStep step, Exception e);
    void workoutStepsRetrieved(Workout workout, MyList<WorkoutStep> steps);
    void failedToRetrieveWorkoutSteps(String collectionName, Exception e);
}
