package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.RepsTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TimedTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;
import com.google.android.material.textfield.TextInputEditText;

import java.util.LinkedHashMap;
import java.util.Map;

public class EditWorkoutTemplateDialogFragment extends DialogFragment {

    WorkoutTemplate template;
    boolean newTemplate = false;
    TextInputEditText nameText;
    private LayoutInflater inflater;
    private LinearLayout stepLayout;
    private LinkedHashMap<TemplateWorkoutStep, View> stepToViewMap = new LinkedHashMap<>();

    public EditWorkoutTemplateDialogFragment(WorkoutTemplate template) {

        this.template = template;
        if (template == null) {
            newTemplate = true;
            this.template = new WorkoutTemplate("");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = requireActivity().getLayoutInflater();
        View templateFragment = inflater.inflate(R.layout.dialog_edit_workout_template, null);
        builder.setView(templateFragment);
        nameText = templateFragment.findViewById(R.id.editTemplateNameInputEditText);
        stepLayout = templateFragment.findViewById(R.id.editTemplateLayout);
        templateFragment.findViewById(R.id.editTemplateSaveButton).setOnClickListener(view -> saveChanges());
        templateFragment.findViewById(R.id.editTemplateAddStepsButton).setOnClickListener(view -> openAddStepsFragment());
        templateFragment.findViewById(R.id.editTemplateCancelButton).setOnClickListener(view -> dismiss());
        nameText.setText(template.getName());
        showSteps();
        return builder.create();
    }

    private void showSteps() {
        for (TemplateWorkoutStep step : template.getTemplateSteps()) {
            if (!stepToViewMap.containsKey(step)) {
                if (step.getExercise().getType() == ExerciseType.REST || step.getExercise().getType() == ExerciseType.TIMED) {
                    stepToViewMap.put(step, getTimedStepView((TimedTemplateWorkoutStep) step));
                } else {
                    stepToViewMap.put(step, getRepsStepView((RepsTemplateWorkoutStep) step));
                }
            }
        }
    }


    private View getRepsStepView(RepsTemplateWorkoutStep step) {
        View stepView = inflater.inflate(R.layout.view_reps_template_step, null);
        TextView posText = stepView.findViewById(R.id.repsStepPosText);
        TextView exerciseText = stepView.findViewById(R.id.repsStepExerciseText);
        Button deleteButton = stepView.findViewById(R.id.repsStepDeleteButton);
        EditText minRepsText = stepView.findViewById(R.id.repsStepMinEditText);
        EditText maxRepsText = stepView.findViewById(R.id.repsStepsMaxEditText);

        posText.setText(String.valueOf(step.getPositionInWorkout()));
        exerciseText.setText(step.getExercise().getName());
        deleteButton.setOnClickListener(view -> deleteStep(step));
        minRepsText.setText(String.valueOf(step.getMinReps()));
        maxRepsText.setText(String.valueOf(step.getMaxReps()));
        stepLayout.addView(stepView);
        return stepView;
    }

    private View getTimedStepView(TimedTemplateWorkoutStep step) {
        View stepView = inflater.inflate(R.layout.view_timed_template_step, null);
        TextView posText = stepView.findViewById(R.id.timedStepPosText);
        TextView exerciseText = stepView.findViewById(R.id.timedStepExerciseText);
        Button deleteButton = stepView.findViewById(R.id.timedStepDeleteButton);
        EditText minsText = stepView.findViewById(R.id.timedStepMinEditText);
        EditText secsText = stepView.findViewById(R.id.timedStepSecsEditText);

        posText.setText(String.valueOf(step.getPositionInWorkout()));
        exerciseText.setText(step.getExercise().getName());
        deleteButton.setOnClickListener(view -> deleteStep(step));
        minsText.setText(String.valueOf(step.getMinutes()));
        secsText.setText(String.valueOf(step.getSeconds()));
        stepLayout.addView(stepView);
        return stepView;
    }

    private void deleteStep(TemplateWorkoutStep step) {
        if (stepToViewMap.containsKey(step)) {
            int position = getIndexOfStepInMap(step);
            if (position < stepToViewMap.size() - 1) {
                reducePositionOfEachStep(position);
            }
            View view = stepToViewMap.get(step);
            stepLayout.removeView(view);
            stepToViewMap.remove(step);
        }
    }

    private void reducePositionOfEachStep(int startingPosition) {
        MyList<TemplateWorkoutStep> steps = new MyList<>();
        steps.addAll(stepToViewMap.keySet());
        for (int i = startingPosition; i < steps.size(); i++) {
            TemplateWorkoutStep step = steps.get(i);
            int newPosition = step.getPositionInWorkout() -1;
            step.setPositionInWorkout(newPosition);
            View stepView = stepToViewMap.get(step);
            if (stepView != null) {
                TextView posText = step.getExercise().getType() == ExerciseType.TIMED || step.getExercise().getType() == ExerciseType.REST
                        ? stepView.findViewById(R.id.timedStepPosText)
                        : stepView.findViewById(R.id.repsStepPosText);
                posText.setText(String.valueOf(newPosition));
            }
        }
    }

    private int getIndexOfStepInMap(TemplateWorkoutStep step) {
        int pos = 0;
        for (TemplateWorkoutStep mapStep : stepToViewMap.keySet()) {
            if (step == mapStep) {
                return pos;
            }
            pos++;
        }
        return pos;
    }

    private void openAddStepsFragment() {
        AddStepsToTemplateDialogFragment dialogFragment = new AddStepsToTemplateDialogFragment(this.template, this::stepsAdded);
        dialogFragment.show(getParentFragmentManager(), "AddStepsFragment");
    }

    private void saveChanges() {
        if (newTemplate) {
            saveNewTemplate();
        } else {
            updateExistingTemplate();
        }
        dismiss();
    }

    private void stepsAdded() {
        showSteps();
    }

    private void saveNewTemplate() {
        String name = nameText.getText().toString();
        if (name.equals("")) return;
        template.setName(name);
        DataHolder.getInstance().saveNewTemplate(template);
    }

    private void updateExistingTemplate() {
        MyList<AbstractSaveableField> updatedFields = new MyList<>();
        String newName = nameText.getText().toString();
        if (!newName.equalsIgnoreCase(template.getName())) {
            template.setName(newName);
            updatedFields.add(template.getNameField());
        }
        if (!updatedFields.isEmpty()) {
            DataHolder.getInstance().updateTemplate(template, updatedFields);
        }
        updateTemplateSteps();
    }

    private void updateTemplateSteps() {
        for (Map.Entry<TemplateWorkoutStep, View> stepViewEntry : stepToViewMap.entrySet()) {
            TemplateWorkoutStep step = stepViewEntry.getKey();
            View stepView = stepViewEntry.getValue();
            MyList<AbstractSaveableField> updatedFields = new MyList<>();
            if (step.getExercise().getType() == ExerciseType.REST || step.getExercise().getType() == ExerciseType.TIMED) {
                TimedTemplateWorkoutStep timedStep = (TimedTemplateWorkoutStep) step;
                EditText minsText = stepView.findViewById(R.id.timedStepMinEditText);
                EditText secsText = stepView.findViewById(R.id.timedStepSecsEditText);
                int mins = getIntFromString(minsText.getText().toString());
                int secs = getIntFromString(secsText.getText().toString());
                if (mins != timedStep.getMinutes()) {
                    timedStep.setMinutes(mins);
                    updatedFields.add(timedStep.getMinutesField());
                }
                if (secs != timedStep.getSeconds()) {
                    timedStep.setSeconds(secs);
                    updatedFields.add(timedStep.getSecondsField());
                }
            } else {
                RepsTemplateWorkoutStep repsStep = (RepsTemplateWorkoutStep) step;
                EditText minRepsText = stepView.findViewById(R.id.repsStepMinEditText);
                EditText maxRepsText = stepView.findViewById(R.id.repsStepsMaxEditText);
                int min = getIntFromString(minRepsText.getText().toString());
                int max = getIntFromString(maxRepsText.getText().toString());
                if (min != repsStep.getMinReps()) {
                    repsStep.setMinReps(min);
                    updatedFields.add(repsStep.getMinRepsField());
                }
                if (max != repsStep.getMaxReps()) {
                    repsStep.setMaxReps(max);
                    updatedFields.add(repsStep.getMaxRepsField());
                }
            }
            if (!updatedFields.isEmpty()) {
                DataHolder.getInstance().updateTemplateStep(step, updatedFields);
            }
        }
    }

    private int getIntFromString(String value) {
        if (value == null || value.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(value);
        }
    }
}
