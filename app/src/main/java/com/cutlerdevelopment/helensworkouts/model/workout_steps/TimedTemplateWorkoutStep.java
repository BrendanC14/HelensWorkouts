package com.cutlerdevelopment.helensworkouts.model.workout_steps;

import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.SaveableInt;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

public class TimedTemplateWorkoutStep extends TemplateWorkoutStep {

    public static final String MINUTES_FIRESTORE_KEY = "Minutes";
    public static final String SECONDS_FIRESTORE_KEY = "Seconds";

    private final SaveableInt minutes;
    public SaveableInt getMinutesField() { return minutes; }
    public int getMinutes() { return minutes.getFieldValue(); }
    public void setMinutes(int newReps) { minutes.setFieldValue(newReps);}

    private final SaveableInt seconds;
    public SaveableInt getSecondsField() { return seconds; }
    public int getSeconds() { return seconds.getFieldValue(); }
    public void setSeconds(int newReps) { seconds.setFieldValue(newReps);}

    public TimedTemplateWorkoutStep(int positionInWorkout, Exercise exercise, WorkoutTemplate workout, int minutes, int seconds) {
        super(positionInWorkout, exercise, workout);
        this.minutes = new SaveableInt(MINUTES_FIRESTORE_KEY, minutes);
        this.seconds = new SaveableInt(SECONDS_FIRESTORE_KEY, seconds);
    }

    public TimedTemplateWorkoutStep(String id, int positionInWorkout, Exercise exercise, WorkoutTemplate workout, int minutes, int seconds) {
        super(id, positionInWorkout, exercise, workout);
        this.minutes = new SaveableInt(MINUTES_FIRESTORE_KEY, minutes);
        this.seconds = new SaveableInt(SECONDS_FIRESTORE_KEY, seconds);
    }

    @Override
    protected MyList<AbstractSaveableField> getSaveableFields() {
        MyList<AbstractSaveableField> fields = super.getSaveableFields();
        fields.add(minutes);
        fields.add(seconds);
        return fields;
    }
}
