package com.cutlerdevelopment.helensworkouts.model;

import com.cutlerdevelopment.helensworkouts.utils.MyList;

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
