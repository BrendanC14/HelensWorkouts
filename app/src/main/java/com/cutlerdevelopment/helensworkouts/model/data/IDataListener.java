package com.cutlerdevelopment.helensworkouts.model.data;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;

public interface IDataListener {
    void exerciseAdded(Exercise exercise);
    void exerciseChanged(Exercise exercise);
    void templateAdded(WorkoutTemplate workout);
    void templateChanged(WorkoutTemplate template);
    void workoutAdded(Workout workout);
    void workoutChanged(Workout workout);
}
