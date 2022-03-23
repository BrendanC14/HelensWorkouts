package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.Notifications;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.RepsTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TimedTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.WorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.utils.DateUtil;
import com.cutlerdevelopment.helensworkouts.utils.MyList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class WorkoutStepFirestoreHandler extends AbstractFirestoreHandler{

    public Notifications<IWorkoutFirestoreListener> notifications = new Notifications<>();
    protected CollectionReference getStepsCollectionReference(WorkoutTemplate workout) {
        return db.collection("Templates")
                .document(workout.getId())
                .collection("Steps");
    }
    protected CollectionReference getWorkoutCollectionReference(Workout workout) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(workout.getDate());
        String dateString = DateUtil.getMonthText(cal.get(Calendar.MONTH)) + " - " + cal.get(Calendar.YEAR);
        return db.collection("Workouts")
                .document(dateString)
                .collection(DateUtil.getDateWithSuffix(cal.get(Calendar.DAY_OF_MONTH)))
                .document(workout.getId())
                .collection("Steps");
    }
    private final boolean templateHandler;

    public WorkoutStepFirestoreHandler(boolean returnTemplates) {
        templateHandler = returnTemplates;
    }
    public WorkoutStepFirestoreHandler(IWorkoutFirestoreListener listener, boolean returnTemplates) {
        notifications.subscribe(listener);
        templateHandler = returnTemplates;
    }

    public void saveTemplateSteps(MyList<TemplateWorkoutStep> steps) {
        for(TemplateWorkoutStep step : steps) {
            addDocument(getStepsCollectionReference(step.getWorkout()), step);
        }
    }

    public void saveWorkoutSteps(MyList<WorkoutStep> steps) {
        for(WorkoutStep step : steps) {
            addDocument(getWorkoutCollectionReference(step.getWorkout()), step);
        }
    }

    @Override
    protected void documentAdded(AbstractSaveableItem item) {
        if (templateHandler) {
            notifications.trigger((IWorkoutFirestoreListener listener) -> listener.templateStepSaved((TemplateWorkoutStep) item));
        } else {
            notifications.trigger((IWorkoutFirestoreListener listener) -> listener.workoutStepSaved((WorkoutStep) item));
        }
    }

    @Override
    protected void failedToAddDocument(AbstractSaveableItem item, Exception e) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToSaveTemplateStep((TemplateWorkoutStep) item, e));
    }

    @Override
    protected void documentRetrieved(DocumentSnapshot documentSnapshot) {

    }

    @Override
    protected void failedToRetrieveDocument(String documentName, Exception e) {

    }

    public void updateExistingTemplateStep(TemplateWorkoutStep step, MyList<AbstractSaveableField> updatedFields) {
        this.updateDocumentFields(step, updatedFields, getStepsCollectionReference(step.getWorkout()).document(step.getId()));
    }


    @Override
    protected void documentUpdated(AbstractSaveableItem item) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.templateStepUpdated((TemplateWorkoutStep) item));
    }

    @Override
    protected void failedToUpdateDocument(AbstractSaveableItem item, Exception e) {

    }

    public void getStepsFromTemplate(WorkoutTemplate workout) {
        retrieveCollection(getStepsCollectionReference(workout), workout);
    }
    public void getStepsFromWorkout(Workout workout) {
        retrieveCollection(getWorkoutCollectionReference(workout), workout);
    }
    @Override
    protected void collectionRetrieved(QuerySnapshot result, AbstractSaveableItem parent) {
        if(templateHandler) {
            MyList<TemplateWorkoutStep> items = new MyList<>();
            for (DocumentSnapshot documentSnapshot : result) {
                items.add(convertDocumentToItem(documentSnapshot));
            }
            notifications.trigger((IWorkoutFirestoreListener listener) -> listener.templateStepsRetrieved((WorkoutTemplate) parent, items));
        } else {
            MyList<WorkoutStep> items = new MyList<>();
            for (DocumentSnapshot documentSnapshot : result) {
                items.add(convertDocumentToWorkoutStep(documentSnapshot, (Workout) parent));
            }
            notifications.trigger((IWorkoutFirestoreListener listener) -> listener.workoutStepsRetrieved((Workout) parent, items));
        }
    }

    @Override
    protected void failedToRetrieveCollection(String documentName, Exception e) {

        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToRetrieveTemplateSteps(documentName, e));
    }


    @Override
    protected TemplateWorkoutStep convertDocumentToItem(DocumentSnapshot documentSnapshot) {
        int pos = documentSnapshot.getLong(TemplateWorkoutStep.POS_IN_WORKOUT_FIRESTORE_KEY).intValue();
        String exerciseName = documentSnapshot.getString(TemplateWorkoutStep.EXERCISE_ID_FIRESTORE_KEY);
        String workoutName = documentSnapshot.getString(TemplateWorkoutStep.WORKOUT_ID_FIRESTORE_KEY);
        if (workoutName == null) return null;
        Exercise exercise = exerciseName == null
                ? Exercise.getRestExercise()
                : DataHolder.getInstance().getExerciseByID(exerciseName);
        if (exercise.getType() == ExerciseType.TIMED || exercise.getType() == ExerciseType.REST) {
            int minutes = documentSnapshot.getLong(TimedTemplateWorkoutStep.MINUTES_FIRESTORE_KEY).intValue();
            int seconds = documentSnapshot.getLong(TimedTemplateWorkoutStep.SECONDS_FIRESTORE_KEY).intValue();
            return new TimedTemplateWorkoutStep(
                    documentSnapshot.getId(),
                    pos,
                    exercise,
                    DataHolder.getInstance().getTemplateByName(workoutName),
                    minutes,
                    seconds);

        }  else {
            int minReps = documentSnapshot.getLong(RepsTemplateWorkoutStep.MIN_REPS_FIRESTORE_KEY).intValue();
            int maxReps = documentSnapshot.getLong(RepsTemplateWorkoutStep.MAX_REPS_FIRESTORE_KEY).intValue();
            return new RepsTemplateWorkoutStep(
                    documentSnapshot.getId(),
                    pos,
                    exercise,
                    DataHolder.getInstance().getTemplateByName(workoutName),
                    minReps,
                    maxReps
            );
        }
    }

    protected WorkoutStep convertDocumentToWorkoutStep(DocumentSnapshot documentSnapshot, Workout workout) {
        int pos = documentSnapshot.getLong(TemplateWorkoutStep.POS_IN_WORKOUT_FIRESTORE_KEY).intValue();
        String exerciseName = documentSnapshot.getString(TemplateWorkoutStep.EXERCISE_ID_FIRESTORE_KEY);
        if (exerciseName == null) return null;
        String workoutName = documentSnapshot.getString(TemplateWorkoutStep.WORKOUT_ID_FIRESTORE_KEY);
        if (workoutName == null) return null;
        return new WorkoutStep(documentSnapshot.getId(),pos,
                DataHolder.getInstance().getExerciseByID(exerciseName),
                workout);
    }
}
