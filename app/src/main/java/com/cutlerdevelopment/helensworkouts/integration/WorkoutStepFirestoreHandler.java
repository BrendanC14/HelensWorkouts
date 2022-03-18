package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.helensworkouts.model.Notifications;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.utils.DateUtil;
import com.cutlerdevelopment.helensworkouts.utils.MyList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

public class WorkoutStepFirestoreHandler extends AbstractFirestoreHandler{

    public Notifications<IWorkoutFirestoreListener> notifications = new Notifications<>();
    protected CollectionReference getStepsCollectionReference(WorkoutTemplate workout) {
        return db.collection("Templates")
                .document(workout.getNameForSaving())
                .collection("Steps");
    }
    protected CollectionReference getWorkoutCollectionReference(Workout workout) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(workout.getDate());
        String dateString = DateUtil.getMonthText(cal.get(Calendar.MONTH)) + " - " + cal.get(Calendar.YEAR);
        return db.collection("Workouts")
                .document(dateString)
                .collection(DateUtil.getDateWithSuffix(cal.get(Calendar.DAY_OF_MONTH)))
                .document(workout.getNameForSaving())
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

    @Override
    protected void documentUpdated(AbstractSaveableItem item) {

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
        String exerciseName = documentSnapshot.getString(TemplateWorkoutStep.EXERCISE_NAME_FIRESTORE_KEY);
        if (exerciseName == null) return null;
        String workoutName = documentSnapshot.getString(TemplateWorkoutStep.WORKOUT_NAME_FIRESTORE_KEY);
        if (workoutName == null) return null;
        return new TemplateWorkoutStep(pos,
                DataHolder.getInstance().getExerciseByName(exerciseName),
                DataHolder.getInstance().getTemplateByName(workoutName));
    }

    protected WorkoutStep convertDocumentToWorkoutStep(DocumentSnapshot documentSnapshot, Workout workout) {
        int pos = documentSnapshot.getLong(TemplateWorkoutStep.POS_IN_WORKOUT_FIRESTORE_KEY).intValue();
        String exerciseName = documentSnapshot.getString(TemplateWorkoutStep.EXERCISE_NAME_FIRESTORE_KEY);
        if (exerciseName == null) return null;
        String workoutName = documentSnapshot.getString(TemplateWorkoutStep.WORKOUT_NAME_FIRESTORE_KEY);
        if (workoutName == null) return null;
        return new WorkoutStep(pos,
                DataHolder.getInstance().getExerciseByName(exerciseName),
                workout);
    }
}
