package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.utils.DateUtil;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ScheduleFirestoreHandler {
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    private IScheduleListener listener;
    public ScheduleFirestoreHandler(IScheduleListener listener) {
        this.listener = listener;
    }
    private DocumentReference getDocumentReference(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return db.collection("Schedule")
                .document(String.valueOf(cal.get(Calendar.YEAR)))
                .collection(DateUtil.getMonthText(cal.get(Calendar.MONTH)))
                .document(DateUtil.getDateWithSuffix(cal.get(Calendar.DAY_OF_MONTH)));
    }

    public void getTemplateIDForDate(Date date) {
        getDocumentReference(date).get()
                .addOnSuccessListener(documentSnapshot -> documentRetrieved(documentSnapshot, date))
                .addOnFailureListener(exception -> failedToRetrieveDocument(exception));
    }

    private void documentRetrieved(DocumentSnapshot snapshot, Date date) {
        String templateID = snapshot.getString("TemplateID");
        if (templateID == null) return;
        listener.templateRetrievedForDate(date, templateID);
    }

    private void failedToRetrieveDocument(Exception e) {

    }

    public void saveTemplateIDOnDate(Date date, String templateID) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("TemplateID", templateID);
        getDocumentReference(date)
                .set(map)
                .addOnSuccessListener( result -> documentAdded(date, templateID))
                .addOnFailureListener(this::failedToAddDocument);
    }

    private void documentAdded(Date date, String templateID) {
        listener.templateSavedOnDate(date, templateID);
    }

    private void failedToAddDocument(Exception e) {

    }

    public void deleteTemplateOnDate(Date date) {
        getDocumentReference(date).delete();
    }
}
