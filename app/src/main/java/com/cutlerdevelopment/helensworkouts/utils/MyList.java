package com.cutlerdevelopment.helensworkouts.utils;

import java.util.ArrayList;

public class MyList<T> extends ArrayList<T> {
    public void addIfNew(T item) {
        if (item != null && !this.contains(item)) {
            this.add(item);
        }
    }

    public MyList() {}
    public MyList(T item) {
        this.add(item);
    }

    public T first() {
        return this.size() > 0 ? this.get(0) : null;
    }
}
