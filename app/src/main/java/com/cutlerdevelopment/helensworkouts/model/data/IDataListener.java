package com.cutlerdevelopment.helensworkouts.model.data;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.Date;

public interface IDataListener {
    void exerciseAdded(Exercise exercise);
    void exerciseChanged(Exercise exercise);
    void allTemplatesRetrieved(MyList<WorkoutTemplate> templates);
    void templateAdded(WorkoutTemplate workout);
    void templateChanged(WorkoutTemplate template);
    void workoutAdded(Workout workout);
    void workoutChanged(Workout workout);

    void templateSavedToDate(Date date, WorkoutTemplate template);
    void templateRetrievedForDate(Date date, WorkoutTemplate template);
}
