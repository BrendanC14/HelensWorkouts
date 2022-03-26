package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.cutlerdevelopment.helensworkouts.ui.MainActivity;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.LinkedHashMap;

public class AddSetToTemplateDialogFragment extends DialogFragment {

    final StepsChangedListener listener;
    WorkoutTemplate template;
    LinearLayout stepLayout;
    LayoutInflater inflater;
    Spinner exerciseSpinner;
    int numItems;
    int setNumber;
    private LinkedHashMap<TemplateWorkoutStep, View> stepToViewMap = new LinkedHashMap<>();
    private MyList<TemplateWorkoutStep> repeatableSteps = new MyList<>();
    private MyList<TemplateWorkoutStep> newSteps = new MyList<>();
    private MyList<TemplateWorkoutStep> deletedSteps = new MyList<>();

    public AddSetToTemplateDialogFragment(WorkoutTemplate template, StepsChangedListener listener) {
        this.template = template;
        this.listener = listener;
        numItems = 0;
        setNumber = template.numberOfSets() + 1;
    }

    public AddSetToTemplateDialogFragment(WorkoutTemplate template, int setNumber, StepsChangedListener listener) {

        this(template, listener);
        if (template.getStepsBySet().containsKey(setNumber)) {
            MyList<TemplateWorkoutStep> steps = template.getStepsBySet().get(setNumber);
            numItems = steps.size();
            this.setNumber = steps.get(0).getSetNumber();
        }
    }

    public interface StepsChangedListener {
        void stepsAdded(MyList<TemplateWorkoutStep> newSteps);
        void stepsDeleted(MyList<TemplateWorkoutStep> deletedSteps);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = requireActivity().getLayoutInflater();
        View stepsFragment = inflater.inflate(R.layout.dialog_add_set_to_template, null);
        builder.setView(stepsFragment);
        exerciseSpinner = stepsFragment.findViewById(R.id.addStepsExerciseSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, DataHolder.getInstance().getAllExerciseNames());
        exerciseSpinner.setAdapter(adapter);
        stepsFragment.findViewById(R.id.addStepsAddSetButton).setOnClickListener(view -> addStep(false));
        stepsFragment.findViewById(R.id.addStepsAddRestButton).setOnClickListener(view -> addStep(true));
        stepsFragment.findViewById(R.id.addStepsRepeatButton).setOnClickListener(view -> repeatSteps());
        stepsFragment.findViewById(R.id.addStepsCancelButton).setOnClickListener(view -> dismiss());
        stepLayout = stepsFragment.findViewById(R.id.addStepsLayout);
        stepsFragment.findViewById(R.id.addStepsSaveButton).setOnClickListener(view -> saveChanges());
        if (template.getStepsBySet().containsKey(setNumber)) {
            for (TemplateWorkoutStep step : template.getStepsBySet().get(setNumber)) {
                if (step.getExercise().getType() == ExerciseType.REPS || step.getExercise().getType() == ExerciseType.WEIGHT) {
                    RepsTemplateWorkoutStep repsStep = (RepsTemplateWorkoutStep) step;
                    stepToViewMap.put(repsStep, getRepsStepView(repsStep));
                } else {
                    TimedTemplateWorkoutStep timedStep = (TimedTemplateWorkoutStep) step;
                    stepToViewMap.put(timedStep, getTimedStepView(timedStep));

                }
            }
        }
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                dismiss();
                return true;
            }
            return false;
        });
        dialog.show();
        dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
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
                        setNumber,
                        numItems,
                        exercise,
                        template,
                        0,
                        0);
                stepToViewMap.put(step, getTimedStepView(step));
                newSteps.add(step);
                return step;

            } else {
                RepsTemplateWorkoutStep step = new RepsTemplateWorkoutStep(
                        setNumber,
                        numItems,
                        exercise,
                        template,
                        0,
                        0);
                stepToViewMap.put(step, getRepsStepView(step));
                newSteps.add(step);
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

        posText.setText(String.valueOf(step.getPositionInWorkout()));
        exerciseText.setText(step.getExercise().getName());
        deleteButton.setOnClickListener(view -> deleteStep(step));
        if (step.getMinReps() > 0) {
            EditText minRepsText = stepView.findViewById(R.id.repsStepMinEditText);
            minRepsText.setText(String.valueOf(step.getMinReps()));
        }
        if (step.getMaxReps() > 0) {
            EditText maxRepsText = stepView.findViewById(R.id.repsStepsMaxEditText  );
            maxRepsText.setText(String.valueOf(step.getMaxReps()));
        }
        stepLayout.addView(stepView);
        return stepView;
    }

    private View getTimedStepView(TimedTemplateWorkoutStep step) {
        View stepView = inflater.inflate(R.layout.view_timed_template_step, null);
        TextView posText = stepView.findViewById(R.id.timedStepPosText);
        TextView exerciseText = stepView.findViewById(R.id.timedStepExerciseText);
        Button deleteButton = stepView.findViewById(R.id.timedStepDeleteButton);

        posText.setText(String.valueOf(step.getPositionInWorkout()));
        exerciseText.setText(step.getExercise().getName());
        deleteButton.setOnClickListener(view -> deleteStep(step));
        if (step.getMinutes() > 0) {
            EditText minsText = stepView.findViewById(R.id.timedStepMinEditText);
            minsText.setText(String.valueOf(step.getMinutes()));
        }
        if (step.getSeconds() > 0) {
            EditText secsText = stepView.findViewById(R.id.timedStepSecsEditText);
            secsText.setText(String.valueOf(step.getSeconds()));
        }
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
            if (newSteps.contains(step)) {
                newSteps.remove(step);
            } else {
                deletedSteps.add(step);
            }
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
        listener.stepsAdded(newSteps);
        listener.stepsDeleted(deletedSteps);
        close();
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

    @Override
    public void dismiss() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setTitle("Cancel");
        builder.setMessage("Are you sure you want to cancel??");
        builder.setPositiveButton("YES", (dialog, which) -> {
            dialog.dismiss();
            close();
        });
        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.yellow));
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.yellow));
    }


    private void close() {
        super.dismiss();
    }

    private int getIntFromString(String value) {
        if (value == null || value.equals("")) {
            return 0;
        } else {
            return Integer.parseInt(value);
        }
    }
}
