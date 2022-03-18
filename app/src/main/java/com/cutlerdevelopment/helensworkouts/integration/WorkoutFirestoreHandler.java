package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.helensworkouts.model.Notifications;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.utils.DateUtil;
import com.cutlerdevelopment.helensworkouts.utils.MyList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

public class WorkoutFirestoreHandler extends AbstractFirestoreHandler {

    public Notifications<IWorkoutFirestoreListener> notifications = new Notifications<>();
    protected CollectionReference templateCollectionReference = db.collection("Templates");
    protected CollectionReference getWorkoutCollectionReference(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String dateString = DateUtil.getMonthText(cal.get(Calendar.MONTH)) + " - " + cal.get(Calendar.YEAR);
        return db.collection("Workouts")
                .document(dateString)
                .collection(DateUtil.getDateWithSuffix(cal.get(Calendar.DAY_OF_MONTH)));
    }
    private final boolean templateHandler;

    public WorkoutFirestoreHandler(boolean retrieveTemplates) {
        templateHandler = retrieveTemplates;
    }
    public WorkoutFirestoreHandler(IWorkoutFirestoreListener listener, boolean retrieveTemplates) {
        notifications.subscribe(listener);
        templateHandler = retrieveTemplates;
    }

    public void saveTemplate(WorkoutTemplate template) {
        MyList<WorkoutTemplate> workouts = new MyList<>(template);
        saveTemplates(workouts);
    }
    public void saveTemplates(MyList<WorkoutTemplate> templates) {
        for(WorkoutTemplate template : templates) {
            addDocument(templateCollectionReference, template);
        }
    }

    public void saveWorkout(Workout workout) {
        MyList<Workout> workouts = new MyList<>(workout);
        saveWorkouts(workouts);
    }
    public void saveWorkouts(MyList<Workout> workouts) {
        for(Workout workout : workouts) {
            addDocument(getWorkoutCollectionReference(workout.getDate()), workout);
        }
    }
    @Override
    protected void documentAdded(AbstractSaveableItem item) {
        if (templateHandler) {
            notifications.trigger((IWorkoutFirestoreListener listener) -> listener.templateSaved((WorkoutTemplate)item));
        } else {
            notifications.trigger((IWorkoutFirestoreListener listener) -> listener.workoutSaved((Workout) item));
        }
    }

    @Override
    protected void failedToAddDocument(AbstractSaveableItem item, Exception e) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToSaveTemplate((WorkoutTemplate)item, e));
    }

    public void getTemplateWithName(String name) {
        retrieveDocument(templateCollectionReference, name);
    }
    public void getWorkoutWithDate(Date date) {
        retrieveCollection(getWorkoutCollectionReference(date), null);
    }
    @Override
    protected void documentRetrieved(DocumentSnapshot documentSnapshot) {
        WorkoutTemplate item = convertDocumentToItem(documentSnapshot);
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.templateRetrieved(item));
    }

    @Override
    protected void failedToRetrieveDocument(String documentName, Exception e) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToRetrieveTemplate(documentName, e));
    }

    public void getAllTemplates() {
        retrieveCollection(templateCollectionReference, null);
    }

    @Override
    protected void collectionRetrieved(QuerySnapshot result, AbstractSaveableItem parent) {
        if (templateHandler) {
            MyList<WorkoutTemplate> items = new MyList<>();
            for (DocumentSnapshot documentSnapshot : result) {
                items.add(convertDocumentToItem(documentSnapshot));
            }
            notifications.trigger((IWorkoutFirestoreListener listener) -> listener.templatesRetrieved(items));
        } else {
            MyList<Workout> items = new MyList<>();
            for (DocumentSnapshot documentSnapshot : result) {
                items.add(convertDocumentToWorkout(documentSnapshot));
            }
            notifications.trigger((IWorkoutFirestoreListener listener) -> listener.workoutRetrieved(items.first()));
        }
    }

    @Override
    protected void documentUpdated(AbstractSaveableItem item) {

    }

    @Override
    protected void failedToUpdateDocument(AbstractSaveableItem item, Exception e) {

    }

    @Override
    protected void failedToRetrieveCollection(String documentName, Exception e) {
        notifications.trigger((IWorkoutFirestoreListener listener) -> listener.failedToRetrieveTemplates(documentName, e));
    }

    @Override
    protected WorkoutTemplate convertDocumentToItem(DocumentSnapshot documentSnapshot) {
        String name = documentSnapshot.getString(AbstractSaveableItem.NAME_FIRESTORE_KEY);
        if (name == null) return null;
        return new WorkoutTemplate(name);
    }

    protected Workout convertDocumentToWorkout(DocumentSnapshot documentSnapshot) {
        String name = documentSnapshot.getString(AbstractSaveableItem.NAME_FIRESTORE_KEY);
        if (name == null) return null;
        Date date = documentSnapshot.getDate(Workout.DATE_FIRESTORE_KEY);
        if (date == null) return null;
        return new Workout(name, date);

    }
}
