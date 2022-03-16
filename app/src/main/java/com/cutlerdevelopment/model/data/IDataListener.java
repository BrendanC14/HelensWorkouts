package com.cutlerdevelopment.model.data;

import com.cutlerdevelopment.model.Exercise;
import com.cutlerdevelopment.model.Workout;

public interface IDataListener {
    void exerciseAdded(Exercise exercise);
    void workoutAdded(Workout workout);
}
