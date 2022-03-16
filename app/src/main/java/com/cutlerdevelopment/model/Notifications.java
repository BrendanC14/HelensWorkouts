package com.cutlerdevelopment.model;

import com.cutlerdevelopment.helensworkouts.integration.IFirestoreListener;
import com.cutlerdevelopment.utils.MyList;

import java.util.function.Consumer;

public class Notifications<T> {

    MyList<T> listeners = new MyList<>();
    public void subscribe(T listener) {
        listeners.addIfNew(listener);
    }
    public void unsubscribe(T listener) {
        listeners.remove(listener);
    }
    public void trigger(Consumer<T> function) {
        for(T listener : listeners) {
            function.accept(listener);
        }
    }
}
