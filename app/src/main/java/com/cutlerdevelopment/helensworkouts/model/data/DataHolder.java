package com.cutlerdevelopment.helensworkouts.model.data;

import com.cutlerdevelopment.helensworkouts.integration.ExerciseFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.integration.IExerciseFirestoreListener;
import com.cutlerdevelopment.helensworkouts.integration.IScheduleListener;
import com.cutlerdevelopment.helensworkouts.integration.IWorkoutFirestoreListener;
import com.cutlerdevelopment.helensworkouts.integration.ScheduleFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.integration.WorkoutFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.integration.WorkoutStepFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.Notifications;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DataHolder implements IExerciseFirestoreListener, IWorkoutFirestoreListener, IScheduleListener {

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
    private final LinkedHashMap<String, Exercise> exerciseByIDMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, Exercise> exerciseByNameMap = new LinkedHashMap<>();
    private final LinkedHashMap<ExerciseType, MyList<Exercise>> exercisesByTypeMap = new LinkedHashMap<>();

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
        for(Exercise exercise : exerciseByIDMap.values()) {
            exercises.addIfNew(exercise);
        }
        return exercises;
    }
    public MyList<String> getAllExerciseNames() {
        MyList<String> names = new MyList<>();
        names.addAll(exerciseByNameMap.keySet());
        return names;
    }
    public Exercise getExerciseByID(String id) {
        return exerciseByIDMap.getOrDefault(id, null);
    }
    public Exercise getExerciseByName(String name) {
        return exerciseByNameMap.getOrDefault(name, null);
    }
    public MyList<Exercise> getExercisesByType(ExerciseType type) {
        return exercisesByTypeMap.getOrDefault(type, new MyList<>());
    }

    public void updateExercise(Exercise exercise, MyList<AbstractSaveableField> updatedFields) {
        exerciseFirestoreHandler.updateExistingExercise(exercise, updatedFields);
    }

    @Override
    public void exerciseRetrieved(Exercise exercise) {
        exerciseByIDMap.put(exercise.getId(), exercise);
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
        notifications.trigger((IDataListener listener) -> listener.exerciseChanged(exercise));
    }

    @Override
    public void failedToUpdateExercise(Exercise exercise, Exception e) {

    }

    @Override
    public void failedToRetrieveExercise(String exerciseName, Exception e) {

    }

    @Override
    public void exercisesRetrieved(MyList<Exercise> exercises) {
        exercises.sort(Comparator.comparing(AbstractSaveableItem::getName));
        for (Exercise exercise : exercises) {
            exerciseRetrieved(exercise);
        }

    }

    @Override
    public void failedToRetrieveExercises(String collectionName, Exception e) {

    }

    private final WorkoutFirestoreHandler templateFirestoreHandler = new WorkoutFirestoreHandler(this, true);
    private final WorkoutStepFirestoreHandler templateStepFirestoreHandler = new WorkoutStepFirestoreHandler(this, true);
    private final HashMap<String, WorkoutTemplate> templateByIDMap = new HashMap<>();

    public void saveNewTemplate(WorkoutTemplate template) {
        templateFirestoreHandler.saveTemplate(template);
    }
    public void LoadAllTemplates() {
        templateFirestoreHandler.getAllTemplates();
    }

    public WorkoutTemplate getTemplateByName(String id) {
        return templateByIDMap.getOrDefault(id, null);
    }

    public MyList<WorkoutTemplate> getAllTemplates() {
        MyList<WorkoutTemplate> templates = new MyList<>();
        for(WorkoutTemplate template : templateByIDMap.values()) {
            templates.addIfNew(template);
        }
        return templates;
    }

    @Override
    public void templateSaved(WorkoutTemplate template) {
        saveTemplateSteps(template.getTemplateSteps());
        templateByIDMap.put(template.getId(), template);
         notifications.trigger((IDataListener listener) -> listener.templateAdded(template));
    }

    @Override
    public void failedToSaveTemplate(WorkoutTemplate template, Exception e) {

    }

    public void updateTemplate(WorkoutTemplate template, MyList<AbstractSaveableField> updatedFields) {
        templateFirestoreHandler.updateExistingTemplate(template, updatedFields);
    }

    @Override
    public void templateUpdated(WorkoutTemplate template) {
        notifications.trigger((IDataListener listener) -> listener.templateChanged(template));
    }

    @Override
    public void failedToUpdateTemplate(WorkoutTemplate template, Exception e) {

    }

    @Override
    public void templateRetrieved(WorkoutTemplate template) {
        templateByIDMap.put(template.getId(), template);
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
        notifications.trigger((IDataListener listener) -> listener.allTemplatesRetrieved(templates));
    }

    @Override
    public void failedToRetrieveTemplates(String collectionName, Exception e) {

    }

    public void saveTemplateSteps(MyList<TemplateWorkoutStep> steps) {
        templateStepFirestoreHandler.saveTemplateSteps(steps);
    }
    @Override
    public void templateStepSaved(TemplateWorkoutStep step) {
        step.getTemplate().addWorkoutStep(step);
    }

    @Override
    public void failedToSaveTemplateStep(TemplateWorkoutStep step, Exception e) {

    }

    @Override
    public void templateStepsRetrieved(WorkoutTemplate template, MyList<TemplateWorkoutStep> steps) {
        steps.sort(Comparator.comparingInt(TemplateWorkoutStep::getPositionInWorkout));
        for (TemplateWorkoutStep step : steps) {
            template.addWorkoutStep(step);
        }
        notifications.trigger((IDataListener listener) -> listener.templateAdded(template));
    }

    @Override
    public void failedToRetrieveTemplateSteps(String collectionName, Exception e) {

    }

    public void updateTemplateStep(TemplateWorkoutStep step, MyList<AbstractSaveableField> updatedFields) {
        templateStepFirestoreHandler.updateExistingTemplateStep(step, updatedFields);
    }

    @Override
    public void templateStepUpdated(TemplateWorkoutStep step) {

    }

    @Override
    public void failedToUpdateTemplateStep(TemplateWorkoutStep template, Exception e) {

    }

    public void deleteTemplateStep(TemplateWorkoutStep step) {
        workoutStepFirestoreHandler.deleteStep(step);
        step.getTemplate().removeStep(step);
    }

    private final WorkoutFirestoreHandler workoutFirestoreHandler = new WorkoutFirestoreHandler(this, false);
    private final WorkoutStepFirestoreHandler workoutStepFirestoreHandler = new WorkoutStepFirestoreHandler(this, false);
    private final HashMap<Date, Workout> workoutByDateMap = new HashMap<>();

    public void saveNewWorkout(Workout workout) {
        workoutFirestoreHandler.saveWorkout(workout);
    }

    @Override
    public void workoutSaved(Workout workout) {
        workoutStepFirestoreHandler.saveWorkoutSteps(workout.getTemplateSteps());
        workoutByDateMap.put(workout.getDate(), workout);
        notifications.trigger((IDataListener listener) -> listener.workoutAdded(workout));
    }

    @Override
    public void failedToSaveWorkout(Workout workout, Exception e) {

    }

    public void updateWorkout(Workout workout, MyList<AbstractSaveableField> updatedFields) {
        workoutFirestoreHandler.updateExistingWorkout(workout, updatedFields);
    }

    @Override
    public void workoutUpdated(Workout workout) {
        notifications.trigger((IDataListener listener) -> listener.workoutChanged(workout));
    }

    @Override
    public void failedToUpdateWorkout(Workout workout, Exception e) {

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
    public void workoutStepSaved(TemplateWorkoutStep step) {
        step.getTemplate().addWorkoutStep(step);
    }

    @Override
    public void failedToSaveWorkoutStep(TemplateWorkoutStep step, Exception e) {

    }

    @Override
    public void workoutUpdated(TemplateWorkoutStep step) {

    }

    @Override
    public void failedToUpdateWorkout(TemplateWorkoutStep step, Exception e) {

    }

    @Override
    public void workoutStepsRetrieved(Workout workout, MyList<TemplateWorkoutStep> steps) {
        for (TemplateWorkoutStep step : steps) {
            workout.addWorkoutStep(step);
        }
        notifications.trigger((IDataListener listener) -> listener.workoutAdded(workout));
    }

    @Override
    public void failedToRetrieveWorkoutSteps(String collectionName, Exception e) {

    }

    private final ScheduleFirestoreHandler scheduleFirestoreHandler = new ScheduleFirestoreHandler(this);
    private final HashMap<Date, String> templateIDByDateMap = new HashMap<>();

    public void saveTemplateToDate(Date date, WorkoutTemplate template) {
        if (!templateIDByDateMap.containsKey(date) ||
                !templateIDByDateMap.get(date).equals(template.getId())) {
            scheduleFirestoreHandler.saveTemplateIDOnDate(date, template.getId());
        }
    }

    public void getTemplateForDate(Date date) {
        scheduleFirestoreHandler.getTemplateIDForDate(date);
    }

    public void deleteTemplateOnDate(Date date) {
        if (templateIDByDateMap.containsKey(date)) {
            scheduleFirestoreHandler.deleteTemplateOnDate(date);
            templateIDByDateMap.remove(date);
        }
    }

    @Override
    public void templateSavedOnDate(Date date, String templateID) {
        WorkoutTemplate template = templateByIDMap.get(templateID);
        templateIDByDateMap.put(date, templateID);
        notifications.trigger((IDataListener listener) -> listener.templateSavedToDate(date, template));
    }

    @Override
    public void templateRetrievedForDate(Date date, String templateID) {
        WorkoutTemplate template = templateByIDMap.get(templateID);
        templateIDByDateMap.put(date, templateID);
        notifications.trigger((IDataListener listener) -> listener.templateRetrievedForDate(date, template));

    }
}
