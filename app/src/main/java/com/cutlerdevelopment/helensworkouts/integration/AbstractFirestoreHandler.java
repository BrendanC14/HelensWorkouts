package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.utils.MyList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public abstract class AbstractFirestoreHandler {
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected AbstractFirestoreHandler() {

    }

    protected void addDocument(CollectionReference collectionReference, AbstractSaveableItem item) {
        item.generateUUID();
        collectionReference
                .document(item.getId())
                .set(item.convertToSaveableMap())
                .addOnSuccessListener( result -> documentAdded(item))
                .addOnFailureListener(exception -> failedToAddDocument(item, exception));
    }
    protected abstract void documentAdded(AbstractSaveableItem item);
    protected abstract void failedToAddDocument(AbstractSaveableItem item, Exception e);

    protected void retrieveDocument(CollectionReference collectionReference, String documentName) {
        collectionReference
                .document(documentName).get()
                .addOnSuccessListener(this::documentRetrieved)
                .addOnFailureListener(exception -> failedToRetrieveDocument(documentName, exception));
    }
    protected abstract void documentRetrieved(DocumentSnapshot documentSnapshot);
    protected abstract void failedToRetrieveDocument(String documentName, Exception e);

    protected void retrieveCollection(CollectionReference collectionReference, AbstractSaveableItem parent) {
        collectionReference
                .get()
                .addOnSuccessListener(result -> collectionRetrieved(result, parent))
                .addOnFailureListener(exception -> failedToRetrieveCollection(collectionReference.getId(), exception));
    }

    protected abstract void collectionRetrieved(QuerySnapshot result, AbstractSaveableItem parent);
    protected abstract void failedToRetrieveCollection(String documentName, Exception e);

    protected void updateDocumentField(AbstractSaveableItem item, AbstractSaveableField field, DocumentReference documentReference) {
        MyList<AbstractSaveableField> list = new MyList<>();
        list.add(field);
        updateDocumentFields(item, list, documentReference);
    }

    protected void updateDocumentFields(AbstractSaveableItem item, MyList<AbstractSaveableField> fields, DocumentReference documentReference) {
        HashMap<String, Object> map = new HashMap<>();
        for(AbstractSaveableField field : fields) {
            map.put(field.getFieldName(), field.getValueAsObject());
        }
        documentReference
                .set(map, SetOptions.merge())
                .addOnSuccessListener(result -> documentUpdated(item))
                .addOnFailureListener(exception -> failedToUpdateDocument(item, exception));
    }
    protected abstract void documentUpdated(AbstractSaveableItem item);
    protected abstract void failedToUpdateDocument(AbstractSaveableItem item, Exception e);

    protected void deleteDocument(AbstractSaveableItem item, DocumentReference reference) {

        reference.delete()
        .addOnSuccessListener(result -> documentDeleted(item));
    }
    protected  abstract  void documentDeleted(AbstractSaveableItem item);

    protected abstract AbstractSaveableItem convertDocumentToItem(DocumentSnapshot documentSnapshot);
}
