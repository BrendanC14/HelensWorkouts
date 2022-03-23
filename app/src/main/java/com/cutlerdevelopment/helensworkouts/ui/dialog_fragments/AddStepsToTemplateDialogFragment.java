package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.RepsTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TimedTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;
import com.google.android.material.textfield.TextInputEditText;

import java.util.LinkedHashMap;
import java.util.List;

public class AddStepsToTemplateDialogFragment extends DialogFragment {

    final StepsAddedListener listener;
    WorkoutTemplate template;
    LinearLayout stepLayout;
    LayoutInflater inflater;
    Spinner exerciseSpinner;
    int numItems;
    private LinkedHashMap<TemplateWorkoutStep, View> stepToViewMap = new LinkedHashMap<>();
    private MyList<TemplateWorkoutStep> repeatableSteps = new MyList<>();

    public AddStepsToTemplateDialogFragment(WorkoutTemplate template, StepsAddedListener listener) {
        this.template = template;
        this.listener = listener;
        numItems = template.getTemplateSteps().size();
    }

    public interface StepsAddedListener {
        void stepsAdded();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = requireActivity().getLayoutInflater();
        View stepsFragment = inflater.inflate(R.layout.dialog_add_steps_to_template, null);
        builder.setView(stepsFragment);
        exerciseSpinner = stepsFragment.findViewById(R.id.addStepsExerciseSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, DataHolder.getInstance().getAllExerciseNames());
        exerciseSpinner.setAdapter(adapter);
        stepsFragment.findViewById(R.id.addStepsAddSetButton).setOnClickListener(view -> addStep(false));
        stepsFragment.findViewById(R.id.addStepsAddRestButton).setOnClickListener(view -> addStep(true));
        stepsFragment.findViewById(R.id.addStepsRepeatButton).setOnClickListener(view -> repeatSteps());
        stepsFragment.findViewById(R.id.addStepsRepeatButton).setOnClickListener(view -> dismiss());

        stepLayout = stepsFragment.findViewById(R.id.addStepsLayout);
        stepsFragment.findViewById(R.id.addStepsSaveButton).setOnClickListener(view -> saveChanges());
        return builder.create();
    }

    private TemplateWorkoutStep addStep(boolean isRest) {
        String selected = exerciseSpinner.getSelectedItem().toString();
        Exercise exercise = isRest
                ? Exercise.getRestExercise()
                : DataHolder.getInstance().getExerciseByName(selected);
        if (exercise != null) {
            numItems++;
            if (exercise.getType() == ExerciseType.TIMED || exercise.getType() == ExerciseType.REST) {
                TimedTemplateWorkoutStep step = new TimedTemplateWorkoutStep(
                        numItems,
                        exercise,
                        template,
                        0,
                        0);
                stepToViewMap.put(step, getTimedStepView(step));
                return step;

            } else {
                RepsTemplateWorkoutStep step = new RepsTemplateWorkoutStep(
                        numItems,
                        exercise,
                        template,
                        0,
                        0);
                stepToViewMap.put(step, getRepsStepView(step));
                return step;
            }
        }
        return null;
    }

    private View getRepsStepView(RepsTemplateWorkoutStep step) {
        View stepView = inflater.inflate(R.layout.view_reps_template_step, null);
        TextView posText = stepView.findViewById(R.id.repsStepPosText);
        TextView exerciseText = stepView.findViewById(R.id.repsStepExerciseText);
        Button deleteButton = stepView.findViewById(R.id.repsStepDeleteButton);

        posText.setText(String.valueOf(numItems));
        exerciseText.setText(step.getExercise().getName());
        deleteButton.setOnClickListener(view -> deleteStep(step));
        stepLayout.addView(stepView);
        return stepView;
    }

    private View getTimedStepView(TimedTemplateWorkoutStep step) {
        View stepView = inflater.inflate(R.layout.view_timed_template_step, null);
        TextView posText = stepView.findViewById(R.id.timedStepPosText);
        TextView exerciseText = stepView.findViewById(R.id.timedStepExerciseText);
        Button deleteButton = stepView.findViewById(R.id.timedStepDeleteButton);

        posText.setText(String.valueOf(numItems));
        exerciseText.setText(step.getExercise().getName());
        deleteButton.setOnClickListener(view -> deleteStep(step));
        stepLayout.addView(stepView);
        return stepView;
    }

    private void repeatSteps() {
        if (repeatableSteps.isEmpty()) {
            repeatableSteps.addAll(stepToViewMap.keySet());
        }
        for (TemplateWorkoutStep step : repeatableSteps) {
            TemplateWorkoutStep newStep = addStep(step.getExercise().getType() == ExerciseType.REST);
            View currentView = stepToViewMap.get(step);
            View newView = stepToViewMap.get(newStep);
            if (currentView != null && newStep != null && newView != null) {
                if (newStep.getExercise().getType() == ExerciseType.TIMED || newStep.getExercise().getType() == ExerciseType.REST) {
                    String currentMins = ((EditText)currentView.findViewById(R.id.timedStepMinEditText)).getText().toString();
                    String currentSecs = ((EditText)currentView.findViewById(R.id.timedStepSecsEditText)).getText().toString();

                    ((EditText)newView.findViewById(R.id.timedStepMinEditText)).setText(currentMins);
                    ((EditText)newView.findViewById(R.id.timedStepSecsEditText)).setText(currentSecs);
                } else {
                    String currentMin = ((EditText)currentView.findViewById(R.id.repsStepMinEditText)).getText().toString();
                    String currentMax = ((EditText)currentView.findViewById(R.id.repsStepsMaxEditText)).getText().toString();

                    ((EditText)newView.findViewById(R.id.repsStepMinEditText)).setText(currentMin);
                    ((EditText)newView.findViewById(R.id.repsStepsMaxEditText)).setText(currentMax);
                }
            }
        }
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
            numItems--;
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

    private void saveChanges() {
        for (TemplateWorkoutStep step : stepToViewMap.keySet()) {
            updateStepWithNumbers(step);
            template.addWorkoutStep(step);
        }
        listener.stepsAdded();
        dismiss();
    }

    private void updateStepWithNumbers(TemplateWorkoutStep step) {
        if (step.getExercise().getType() == ExerciseType.TIMED || step.getExercise().getType() == ExerciseType.REST) {
            TimedTemplateWorkoutStep timedStep = (TimedTemplateWorkoutStep) step;
            View stepView = stepToViewMap.get(step);
            if (stepView != null) {
                int mins = getIntFromString(((EditText) stepView.findViewById(R.id.timedStepMinEditText)).getText().toString());
                int secs = getIntFromString(((EditText) stepView.findViewById(R.id.timedStepSecsEditText)).getText().toString());
                timedStep.setMinutes(mins);
                timedStep.setSeconds(secs);
            }
        } else {
            RepsTemplateWorkoutStep repsStep = (RepsTemplateWorkoutStep) step;
            View stepView = stepToViewMap.get(step);
            if (stepView != null) {
                int min = getIntFromString(((EditText) stepView.findViewById(R.id.repsStepMinEditText)).getText().toString());
                int max = getIntFromString(((EditText) stepView.findViewById(R.id.repsStepsMaxEditText)).getText().toString());
                repsStep.setMinReps(min);
                repsStep.setMaxReps(max);
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
