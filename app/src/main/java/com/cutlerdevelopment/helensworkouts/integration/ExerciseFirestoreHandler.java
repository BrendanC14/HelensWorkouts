package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.Notifications;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.utils.MyList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;


public class ExerciseFirestoreHandler extends AbstractFirestoreHandler{

    public Notifications<IExerciseFirestoreListener> notifications = new Notifications<>();
    protected CollectionReference exerciseCollectionReference = db.collection("Exercises");
    public ExerciseFirestoreHandler() {

    }

    public ExerciseFirestoreHandler(IExerciseFirestoreListener listener) {
        notifications.subscribe(listener);
    }

    public void saveNewExercise(Exercise exercise) {
        MyList<Exercise> exercises = new MyList<>(exercise);
        saveNewExercises(exercises);
    }
    public void saveNewExercises(MyList<Exercise> exercises) {
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

    public void updateExistingExercise(Exercise exercise, MyList<AbstractSaveableField> updatedFields) {
        this.updateDocumentFields(exercise, updatedFields, exerciseCollectionReference.document(exercise.getNameForSaving()));
    }

    public void updateExistingExerciseIncludingName(Exercise exercise, String oldName) {
        this.addDocument(exerciseCollectionReference, exercise);
        this.deleteDocument(exerciseCollectionReference.document(oldName));
    }

    @Override
    protected void documentUpdated(AbstractSaveableItem item) {
        notifications.trigger((IExerciseFirestoreListener listener) -> listener.exerciseUpdated((Exercise)item));
    }

    @Override
    protected void failedToUpdateDocument(AbstractSaveableItem item, Exception e) {

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
