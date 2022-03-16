package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.model.Exercise;
import com.cutlerdevelopment.model.ExerciseType;
import com.cutlerdevelopment.model.Notifications;
import com.cutlerdevelopment.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.utils.MyList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ExerciseFirestoreHandler extends AbstractFirestoreHandler{

    public Notifications<IExerciseFirestoreListener> notifications = new Notifications<>();
    protected CollectionReference exerciseCollectionReference = db.collection("Exercises");

    public ExerciseFirestoreHandler() {

    }

    public ExerciseFirestoreHandler(IExerciseFirestoreListener listener) {
        notifications.subscribe(listener);
    }

    public void saveExercise(Exercise exercise) {
        MyList<Exercise> exercises = new MyList<>(exercise);
        saveExercises(exercises);
    }
    public void saveExercises(MyList<Exercise> exercises) {
        for(Exercise exercise : exercises) {
            addDocument(exerciseCollectionReference, exercise);
        }
    }
    @Override
    protected void documentAdded(AbstractSaveableItem item) {
        notifications.trigger((IExerciseFirestoreListener listener) -> listener.exerciseSaved((Exercise)item));
    }
    @Override
    protected void failedToAddDocument(AbstractSaveableItem item, Exception e) {
        notifications.trigger((IExerciseFirestoreListener listener) -> listener.failedToSaveExercise((Exercise)item, e));
    }

    public void getExerciseWithName(String name) {
        retrieveDocument(exerciseCollectionReference, name);
    }
    @Override
    protected void documentRetrieved(DocumentSnapshot documentSnapshot) {
        Exercise item = convertDocumentToItem(documentSnapshot);
        notifications.trigger((IExerciseFirestoreListener listener) -> listener.exerciseRetrieved(item));
    }
    @Override
    protected void failedToRetrieveDocument(String documentName, Exception e) {
        notifications.trigger((IExerciseFirestoreListener listener) -> listener.failedToRetrieveExercise(documentName, e));
    }

    @Override
    protected void collectionRetrieved(QuerySnapshot result, AbstractSaveableItem parent) {
        MyList<Exercise> items = new MyList<>();
        for (DocumentSnapshot documentSnapshot : result) {
            items.add(convertDocumentToItem(documentSnapshot));
        }
        notifications.trigger((IExerciseFirestoreListener listener) -> listener.exercisesRetrieved(items));
    }

    @Override
    protected void failedToRetrieveCollection(String documentName, Exception e) {
        notifications.trigger((IExerciseFirestoreListener listener) -> listener.failedToRetrieveExercises(documentName, e));
    }

    public void getAllExercises() {
        retrieveCollection(exerciseCollectionReference, null);
    }

    @Override
    protected Exercise convertDocumentToItem(DocumentSnapshot documentSnapshot) {
        String name = documentSnapshot.getString(AbstractSaveableItem.NAME_FIRESTORE_KEY);
        if (name == null) return null;
        ExerciseType type = ExerciseType.valueOf(documentSnapshot.getString(Exercise.TYPE_FIRESTORE_KEY));
        return new Exercise(name, type);
    }
}
