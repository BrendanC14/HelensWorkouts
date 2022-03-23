package com.cutlerdevelopment.helensworkouts.model.saveables;

import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.HashMap;
import java.util.UUID;

public abstract class AbstractSaveableItem {

    public static final String NAME_FIRESTORE_KEY = "Name";
    public static final String ID_FIRESTORE_KEY = "ID";

    private String id;
    public String getId() {
        return id;
    }
    public void generateUUID() {
        id = UUID.randomUUID().toString();
    }

    protected SaveableString nameField;
    public SaveableString getNameField() {return nameField;}
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
    protected AbstractSaveableItem(String id, String name) {
        this.id = id;
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
