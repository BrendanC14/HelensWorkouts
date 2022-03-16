package com.cutlerdevelopment.model.data;

import com.cutlerdevelopment.helensworkouts.integration.ExerciseFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.integration.IExerciseFirestoreListener;
import com.cutlerdevelopment.helensworkouts.integration.IFirestoreListener;
import com.cutlerdevelopment.helensworkouts.integration.IWorkoutFirestoreListener;
import com.cutlerdevelopment.helensworkouts.integration.WorkoutFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.integration.WorkoutStepFirestoreHandler;
import com.cutlerdevelopment.model.Exercise;
import com.cutlerdevelopment.model.ExerciseType;
import com.cutlerdevelopment.model.Notifications;
import com.cutlerdevelopment.model.Workout;
import com.cutlerdevelopment.model.WorkoutStep;
import com.cutlerdevelopment.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.utils.MyList;

import java.util.HashMap;

public class DataHolder implements IExerciseFirestoreListener, IWorkoutFirestoreListener {

    private static DataHolder instance;
    public static DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }
    public static DataHolder getInstance(IDataListener listener) {
        if (instance == null) {
            instance = new DataHolder();
        }
        instance.notifications.subscribe(listener);
        return instance;
    }

    public Notifications<IDataListener> notifications = new Notifications<>();

    private final ExerciseFirestoreHandler exerciseFirestoreHandler = new ExerciseFirestoreHandler(this);
    private final HashMap<String, Exercise> exerciseByNameMap = new HashMap<>();
    private final HashMap<ExerciseType, MyList<Exercise>> exercisesByTypeMap = new HashMap<>();

    private DataHolder() {
        LoadAllExercises();
        LoadAllWorkouts();
    }

    public void saveNewExercise(Exercise exercise) {
        exerciseFirestoreHandler.saveExercise(exercise);
    }
    public void LoadAllExercises() {
        exerciseFirestoreHandler.getAllExercises();
    }
    public Exercise getExerciseByName(String name) {
        return exerciseByNameMap.getOrDefault(name, null);
    }
    public MyList<Exercise> getExercisesByType(ExerciseType type) {
        return exercisesByTypeMap.getOrDefault(type, new MyList<>());
    }

    @Override
    public void exerciseRetrieved(Exercise exercise) {
        exerciseByNameMap.put(exercise.getName(), exercise);
        ExerciseType type = exercise.getType();
        if (!exercisesByTypeMap.containsKey(type)) exercisesByTypeMap.put(type, new MyList<>());
        exercisesByTypeMap.get(type).addIfNew(exercise);
        notifications.trigger((IDataListener listener) -> listener.exerciseAdded(exercise));
    }

    @Override
    public void exerciseSaved(Exercise exercise) {
        exerciseRetrieved(exercise);
    }

    @Override
    public void failedToSaveExercise(Exercise exercise, Exception e) {

    }


    @Override
    public void failedToRetrieveExercise(String exerciseName, Exception e) {

    }

    @Override
    public void exercisesRetrieved(MyList<Exercise> exercises) {
        for (Exercise exercise : exercises) {
            exerciseRetrieved(exercise);
        }
    }

    @Override
    public void failedToRetrieveExercises(String collectionName, Exception e) {

    }

    private final WorkoutFirestoreHandler workoutFirestoreHandler = new WorkoutFirestoreHandler(this);
    private final WorkoutStepFirestoreHandler workoutStepFirestoreHandler = new WorkoutStepFirestoreHandler(this);
    private final HashMap<String, Workout> workoutByNameMap = new HashMap<>();

    public void saveNewWorkout(Workout workout) {
        workoutFirestoreHandler.saveWorkout(workout);
    }
    public void LoadAllWorkouts() {
        workoutFirestoreHandler.getAllWorkouts();
    }
    public Workout getWorkoutByName(String name) {
        return workoutByNameMap.getOrDefault(name, null);
    }

    @Override
    public void workoutSaved(Workout workout) {
        workoutStepFirestoreHandler.saveWorkoutSteps(workout.getSteps());
        workoutByNameMap.put(workout.getName(), workout);
        notifications.trigger((IDataListener listener) -> listener.workoutAdded(workout));
    }

    @Override
    public void failedToSaveWorkout(Workout workout, Exception e) {

    }

    @Override
    public void workoutRetrieved(Workout workout) {
        workoutByNameMap.put(workout.getName(), workout);
        workoutStepFirestoreHandler.getStepsFromWorkout(workout);
    }

    @Override
    public void failedToRetrieveWorkout(String workoutName, Exception e) {

    }

    @Override
    public void workoutsRetrieved(MyList<Workout> workouts) {
        for (Workout workout : workouts) {
            workoutRetrieved(workout);
        }
    }

    @Override
    public void failedToRetrieveWorkouts(String collectionName, Exception e) {

    }

    @Override
    public void workoutStepSaved(WorkoutStep step) {
        step.getWorkout().addWorkoutStep(step);
    }

    @Override
    public void failedToSaveWorkoutStep(WorkoutStep step, Exception e) {

    }

    @Override
    public void workoutStepsRetrieved(Workout workout, MyList<WorkoutStep> steps) {
        for (WorkoutStep step : steps) {
            workout.addWorkoutStep(step);
        }
        notifications.trigger((IDataListener listener) -> listener.workoutAdded(workout));
    }

    @Override
    public void failedToRetrieveWorkoutSteps(String collectionName, Exception e) {

    }
}
