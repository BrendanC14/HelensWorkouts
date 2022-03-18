package com.cutlerdevelopment.helensworkouts.model.data;

import com.cutlerdevelopment.helensworkouts.integration.ExerciseFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.integration.IExerciseFirestoreListener;
import com.cutlerdevelopment.helensworkouts.integration.IWorkoutFirestoreListener;
import com.cutlerdevelopment.helensworkouts.integration.WorkoutFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.integration.WorkoutStepFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.Notifications;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.Date;
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
        LoadAllTemplates();
    }

    public void saveNewExercise(Exercise exercise) {
        exerciseFirestoreHandler.saveNewExercise(exercise);
    }
    public void LoadAllExercises() {
        exerciseFirestoreHandler.getAllExercises();
    }
    public MyList<Exercise> getAllExercises() {
        MyList<Exercise> exercises = new MyList<>();
        for(Exercise exercise : exerciseByNameMap.values()) {
            exercises.addIfNew(exercise);
        }
        return exercises;
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
    public void exerciseUpdated(Exercise exercise) {

    }

    @Override
    public void failedToUpdateExercise(Exercise exercise, Exception e) {

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

    private final WorkoutFirestoreHandler templateFirestoreHandler = new WorkoutFirestoreHandler(this, true);
    private final WorkoutStepFirestoreHandler templateStepFirestoreHandler = new WorkoutStepFirestoreHandler(this, true);
    private final HashMap<String, WorkoutTemplate> templateByNameMap = new HashMap<>();

    public void saveNewTemplate(WorkoutTemplate template) {
        templateFirestoreHandler.saveTemplate(template);
    }
    public void LoadAllTemplates() {
        templateFirestoreHandler.getAllTemplates();
    }

    public WorkoutTemplate getTemplateByName(String name) {
        return templateByNameMap.getOrDefault(name, null);
    }

    @Override
    public void templateSaved(WorkoutTemplate template) {
        templateStepFirestoreHandler.saveTemplateSteps(template.getTemplateSteps());
        templateByNameMap.put(template.getName(), template);
        notifications.trigger((IDataListener listener) -> listener.templateAdded(template));
    }

    @Override
    public void failedToSaveTemplate(WorkoutTemplate template, Exception e) {

    }

    @Override
    public void templateRetrieved(WorkoutTemplate template) {
        templateByNameMap.put(template.getName(), template);
        templateStepFirestoreHandler.getStepsFromTemplate(template);
    }

    @Override
    public void failedToRetrieveTemplate(String workoutName, Exception e) {

    }

    @Override
    public void templatesRetrieved(MyList<WorkoutTemplate> templates) {
        for (WorkoutTemplate template : templates) {
            templateRetrieved(template);
        }
    }

    @Override
    public void failedToRetrieveTemplates(String collectionName, Exception e) {

    }

    @Override
    public void templateStepSaved(TemplateWorkoutStep step) {
        step.getWorkout().addWorkoutStep(step);
    }

    @Override
    public void failedToSaveTemplateStep(TemplateWorkoutStep step, Exception e) {

    }

    @Override
    public void templateStepsRetrieved(WorkoutTemplate template, MyList<TemplateWorkoutStep> steps) {
        for (TemplateWorkoutStep step : steps) {
            template.addWorkoutStep(step);
        }
        notifications.trigger((IDataListener listener) -> listener.templateAdded(template));
    }

    @Override
    public void failedToRetrieveTemplateSteps(String collectionName, Exception e) {

    }


    private final WorkoutFirestoreHandler workoutFirestoreHandler = new WorkoutFirestoreHandler(this, false);
    private final WorkoutStepFirestoreHandler workoutStepFirestoreHandler = new WorkoutStepFirestoreHandler(this, false);
    private final HashMap<Date, Workout> workoutByDateMap = new HashMap<>();

    public void saveNewWorkout(Workout workout) {
        workoutFirestoreHandler.saveWorkout(workout);
    }

    @Override
    public void workoutSaved(Workout workout) {
        workoutStepFirestoreHandler.saveWorkoutSteps(workout.getSteps());
        workoutByDateMap.put(workout.getDate(), workout);
        notifications.trigger((IDataListener listener) -> listener.workoutAdded(workout));
    }

    @Override
    public void failedToSaveWorkout(Workout workout, Exception e) {

    }

    public WorkoutTemplate getWorkoutByDate(Date date) {
        Workout workout = workoutByDateMap.getOrDefault(date, null);
        if (workout == null) {
            workoutFirestoreHandler.getWorkoutWithDate(date);
        }
        return workout;
    }

    @Override
    public void workoutRetrieved(Workout workout) {
        if (workout != null) {
            workoutByDateMap.put(workout.getDate(), workout);
            workoutStepFirestoreHandler.getStepsFromWorkout(workout);
        }
    }

    @Override
    public void failedToRetrieveWorkout(String workoutName, Exception e) {

    }

    @Override
    public void workoutsRetrieved(MyList<Workout> workouts) {
        for (Workout workout : workouts) {
            workoutStepFirestoreHandler.getStepsFromWorkout(workout);
            workoutByDateMap.put(workout.getDate(), workout);
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
        for (TemplateWorkoutStep step : steps) {
            workout.addWorkoutStep(step);
        }
        notifications.trigger((IDataListener listener) -> listener.workoutAdded(workout));
    }

    @Override
    public void failedToRetrieveWorkoutSteps(String collectionName, Exception e) {

    }
}