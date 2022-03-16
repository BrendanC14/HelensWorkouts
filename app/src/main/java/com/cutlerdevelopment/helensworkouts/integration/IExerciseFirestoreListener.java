package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.model.Exercise;
import com.cutlerdevelopment.utils.MyList;

public interface IExerciseFirestoreListener {
    void exerciseSaved(Exercise exercise);
    void failedToSaveExercise(Exercise exercise, Exception e);
    void exerciseRetrieved(Exercise exercise);
    void failedToRetrieveExercise(String exerciseName, Exception e);
    void exercisesRetrieved(MyList<Exercise> exercise);
    void failedToRetrieveExercises(String collectionName, Exception e);
}
