package com.cutlerdevelopment.helensworkouts.model;

import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.HashMap;
import java.util.Map;

public class WorkoutTemplate extends AbstractSaveableItem {

    private final HashMap<Integer, MyList<TemplateWorkoutStep>> steps = new HashMap<>();
    public MyList<TemplateWorkoutStep> getTemplateSteps() {
        MyList<TemplateWorkoutStep> returnList = new MyList<>();
        for (Map.Entry<Integer, MyList<TemplateWorkoutStep>> stepsEntry : steps.entrySet()) {
            returnList.addAll(stepsEntry.getValue());
        }
        return returnList;
    }
    public HashMap<Integer, MyList<TemplateWorkoutStep>> getStepsBySet() { return steps;}
    public void addWorkoutStep(TemplateWorkoutStep step) {
        if (!steps.containsKey(step.getSetNumber())) {
            steps.put(step.getSetNumber(), new MyList<>());
        }
        steps.get(step.getSetNumber()).addIfNew(step);
    }

    public void removeStep(TemplateWorkoutStep step) {
        int setNumber = step.getSetNumber();
        if (steps.containsKey(setNumber)) {
            MyList<TemplateWorkoutStep> list = steps.get(setNumber);
            list.remove(step);
            if (list.isEmpty()) {
                if (setNumber == steps.keySet().size()) {
                    steps.remove(setNumber);
                } else {
                    for (int i = setNumber + 1; i <= steps.keySet().size(); i++) {
                        MyList<TemplateWorkoutStep> stepsForSet = steps.get(i);
                        steps.put(i -1, stepsForSet);
                        for (TemplateWorkoutStep step1 : stepsForSet) {
                            step1.setSetNumber(i - 1);
                            MyList<AbstractSaveableField> fields = new MyList<>(step1.getSetNumberField());
                            DataHolder.getInstance().updateTemplateStep(step1, fields);
                        }
                    }
                    steps.remove(steps.keySet().size());
                }
            }
        }
    }
    public int numberOfSets() {
        return steps.keySet().size();
    }

    public void updateStepsUnderSet(int setNumber, MyList<TemplateWorkoutStep> newSteps) {
        steps.put(setNumber, newSteps);
    }

    public WorkoutTemplate(String name) {
        super(name);
    }
    public WorkoutTemplate(String id, String name) {
        super(id, name);
    }

}
