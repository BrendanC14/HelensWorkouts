package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.model.Exercise;
import com.cutlerdevelopment.model.ExerciseType;
import com.cutlerdevelopment.model.Notifications;
import com.cutlerdevelopment.model.Workout;
import com.cutlerdevelopment.model.WorkoutStep;
import com.cutlerdevelopment.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.utils.MyList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class WorkoutFirestoreHandler extends AbstractFirestoreHandler {

    public Notifications<IWorkoutFirestoreListener> notifications = new Notifications<>();
    protected CollectionReference workoutCollectionReference = db.collection("Templates");

    public WorkoutFirestoreHandler() {

    }
    public WorkoutFirestoreHandler(IWorkoutFirestoreListener listener) {
        notifications.subscribe(listener);
    }

    public void saveWorkout(Workout workout) {
        MyList<Workout> workouts = new MyList<>(workout);
        saveWorkouts(workouts);
    }
    public void saveWorkouts(MyList<Workout> workouts) {
        for(Workout workout : workouts) {
            addDocument(workoutCollectionReference, workout);
        }
    }
    @Override
    protected void documentAdded(AbstractSaveableItem item) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.workoutSaved((Workout)item));
    }

    @Override
    protected void failedToAddDocument(AbstractSaveableItem item, Exception e) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToSaveWorkout((Workout)item, e));
    }

    public void getWorkoutWithName(String name) {
        retrieveDocument(workoutCollectionReference, name);
    }
    @Override
    protected void documentRetrieved(DocumentSnapshot documentSnapshot) {
        Workout item = convertDocumentToItem(documentSnapshot);
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.workoutRetrieved(item));
    }

    @Override
    protected void failedToRetrieveDocument(String documentName, Exception e) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToRetrieveWorkout(documentName, e));
    }

    public void getAllWorkouts() {
        retrieveCollection(workoutCollectionReference, null);
    }

    @Override
    protected void collectionRetrieved(QuerySnapshot result, AbstractSaveableItem parent) {
        MyList<Workout> items = new MyList<>();
        for (DocumentSnapshot documentSnapshot : result) {
            items.add(convertDocumentToItem(documentSnapshot));
        }
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.workoutsRetrieved(items));
    }

    @Override
    protected void failedToRetrieveCollection(String documentName, Exception e) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToRetrieveWorkouts(documentName, e));
    }

    @Override
    protected Workout convertDocumentToItem(DocumentSnapshot documentSnapshot) {
        String name = documentSnapshot.getString(AbstractSaveableItem.NAME_FIRESTORE_KEY);
        if (name == null) return null;
        return new Workout(name);
    }
}
