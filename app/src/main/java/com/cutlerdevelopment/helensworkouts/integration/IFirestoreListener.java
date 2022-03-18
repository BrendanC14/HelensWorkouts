package com.cutlerdevelopment.helensworkouts.integration;

import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.utils.MyList;


public interface IFirestoreListener {
    void itemSaved(AbstractSaveableItem item);
    void failedToSaveItem(AbstractSaveableItem item, Exception exception);
    void itemRetrieved(AbstractSaveableItem item);
    void failedToRetrieveItem(String documentName, Exception exception);
    void itemsRetrieved(MyList<AbstractSaveableItem> items);
    void failedToRetrieveItems(String collectionName, Exception exception);
}
