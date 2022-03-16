package com.cutlerdevelopment.model.saveables;

import com.cutlerdevelopment.utils.MyList;

import java.util.HashMap;

public abstract class AbstractSaveableItem {

    public static final String NAME_FIRESTORE_KEY = "Name";

    protected SaveableString nameField;
    public String getName() {
        return nameField.getFieldValue();
    }
    public void setName(String name) {
        this.nameField.setFieldValue(name);
    }
    public String getNameForSaving() {
        return nameField.getFieldValue();
    }

    protected AbstractSaveableItem(String name) {
        nameField = new SaveableString(NAME_FIRESTORE_KEY, name);
    }

    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = new MyList<>();
        fields.add(nameField);
        return fields;
    }

    public HashMap<String, Object> convertToSaveableMap() {
        HashMap<String, Object> map = new HashMap<>();
        for(AbstractSaveableField field : getSaveableFields()) {
            map.put(field.getFieldName(), field.getValueAsObject());
        }
        return map;
    }

}
