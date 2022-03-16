package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.model.Notifications;
import com.cutlerdevelopment.model.Workout;
import com.cutlerdevelopment.model.WorkoutStep;
import com.cutlerdevelopment.model.data.DataHolder;
import com.cutlerdevelopment.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.utils.MyList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class WorkoutStepFirestoreHandler extends AbstractFirestoreHandler{

    public Notifications<IWorkoutFirestoreListener> notifications = new Notifications<>();
    protected CollectionReference getStepsCollectionReference(Workout workout) {
        return db.collection("Templates")
                .document(workout.getNameForSaving())
                .collection("Steps");
    }

    public WorkoutStepFirestoreHandler() {

    }
    public WorkoutStepFirestoreHandler(IWorkoutFirestoreListener listener) {
        notifications.subscribe(listener);
    }

    public void saveWorkoutSteps(MyList<WorkoutStep> steps) {
        for(WorkoutStep step : steps) {
            addDocument(getStepsCollectionReference(step.getWorkout()), step);
        }
    }

    @Override
    protected void documentAdded(AbstractSaveableItem item) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.workoutStepSaved((WorkoutStep) item));
    }

    @Override
    protected void failedToAddDocument(AbstractSaveableItem item, Exception e) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToSaveWorkoutStep((WorkoutStep) item, e));
    }

    @Override
    protected void documentRetrieved(DocumentSnapshot documentSnapshot) {

    }

    @Override
    protected void failedToRetrieveDocument(String documentName, Exception e) {

    }

    public void getStepsFromWorkout(Workout workout) {
        retrieveCollection(getStepsCollectionReference(workout), workout);
    }
    @Override
    protected void collectionRetrieved(QuerySnapshot result, AbstractSaveableItem parent) {
        MyList<WorkoutStep> items = new MyList<>();
        for (DocumentSnapshot documentSnapshot : result) {
            items.add(convertDocumentToItem(documentSnapshot));
        }
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.workoutStepsRetrieved((Workout) parent, items));
    }

    @Override
    protected void failedToRetrieveCollection(String documentName, Exception e) {

        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToRetrieveWorkoutSteps(documentName, e));
    }


    @Override
    protected WorkoutStep convertDocumentToItem(DocumentSnapshot documentSnapshot) {
        int pos = documentSnapshot.getLong(WorkoutStep.POS_IN_WORKOUT_FIRESTORE_KEY).intValue();
        String exerciseName = documentSnapshot.getString(WorkoutStep.EXERCISE_NAME_FIRESTORE_KEY);
        if (exerciseName == null) return null;
        String workoutName = documentSnapshot.getString(WorkoutStep.WORKOUT_NAME_FIRESTORE_KEY);
        if (workoutName == null) return null;
        return new WorkoutStep(pos,
                DataHolder.getInstance().getExerciseByName(exerciseName),
                DataHolder.getInstance().getWorkoutByName(workoutName));
    }
}
