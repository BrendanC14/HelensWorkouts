package com.cutlerdevelopment.helensworkouts.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.RepsWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TimedWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.WeightWorkoutStep;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CompleteWorkoutActivity extends AppCompatActivity {

    LinearLayout stepsParent;
    TextView timerTextView;
    CountDownTimer timer;

    WorkoutTemplate template;
    Workout workout;

    private HashMap<TemplateWorkoutStep, View> stepToViewMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_workout);
        String templateID = getIntent().getExtras().getString("TemplateID");
        template = DataHolder.getInstance().getTemplateByName(templateID);
        stepsParent = findViewById(R.id.completeWorkoutLayout);
        timerTextView = findViewById(R.id.completeWorkoutTimer);
        findViewById(R.id.completeWorkoutCancelButton).setOnClickListener(view -> cancel());
        findViewById(R.id.completeWorkoutSaveButton).setOnClickListener(view -> save());
        workout = new Workout(template, new Date());
        displaySteps();
    }

    private void displaySteps() {
        int previousSet = 1;
        for (TemplateWorkoutStep step : workout.getTemplateSteps()) {
            if (step.getSetNumber() != previousSet) {
                stepsParent.addView(getLayoutInflater().inflate(R.layout.view_set_divider, null));
                previousSet = step.getSetNumber();
            }
            View view = getViewForStep(step);
            stepToViewMap.put(step, view);
            stepsParent.addView(view);
        }
    }

    private View getViewForStep(TemplateWorkoutStep step) {
        ExerciseType type = step.getExercise().getType();
        if (type == ExerciseType.TIMED) {
            return getTimedView((TimedWorkoutStep) step);
        } else if (type == ExerciseType.REST) {
            return getRestView((TimedWorkoutStep) step);
        } else if (type == ExerciseType.WEIGHT) {
            return getWeightView((WeightWorkoutStep) step);
        } else {
            return getRepsView((RepsWorkoutStep) step);
        }
    }

    private View getRepsView(RepsWorkoutStep step) {
        View view = getLayoutInflater().inflate(R.layout.view_reps_step, null);
        TextView posText = view.findViewById(R.id.repsStepPosText);
        TextView exerciseText = view.findViewById(R.id.repsStepExerciseText);

        posText.setText(String.valueOf(step.getPositionInWorkout()));
        String exerciseString = step.getExercise().getName();
        if (step.getMinReps() > 0) {
            exerciseString += ": " + step.getMinReps();
        }
        if (step.getMaxReps() > 0) {
            exerciseString += " - " + step.getMaxReps();
        }
        exerciseString += " reps.";
        exerciseText.setText(exerciseString);
        return view;
    }

    private View getWeightView(WeightWorkoutStep step) {
        View view = getLayoutInflater().inflate(R.layout.view_weight_step, null);
        TextView posText = view.findViewById(R.id.weightStepPosText);
        TextView exerciseText = view.findViewById(R.id.weightStepExerciseText);
        TextView recordWeightText = view.findViewById(R.id.weightStepRecordWeight);

        posText.setText(String.valueOf(step.getPositionInWorkout()));
        String exerciseString = step.getExercise().getName();
        if (step.getMinReps() > 0) {
            exerciseString += ": " + step.getMinReps();
        }
        if (step.getMaxReps() > 0) {
            exerciseString += " - " + step.getMaxReps();
        }
        exerciseString += " reps.";
        exerciseText.setText(exerciseString);
        if (step.getExercise().getRecordWeight().compareTo(BigDecimal.ZERO) > 0) {
            recordWeightText.setText(String.valueOf(step.getExercise().getRecordWeight() + "kgs"));
        }
        return view;
    }

    private View getTimedView(TimedWorkoutStep step) {
        View view = getLayoutInflater().inflate(R.layout.view_timed_step, null);
        TextView posText = view.findViewById(R.id.timedStepPosText);
        TextView exerciseText = view.findViewById(R.id.timedStepExerciseText);
        Button startTimeButton = view.findViewById(R.id.timedStepStartTimerButton);

        posText.setText(String.valueOf(step.getPositionInWorkout()));
        String exerciseString = step.getExercise().getName() + ": ";
        if (step.getMinutes() > 0) {
            exerciseString += step.getMinutes() + "m ";
        }
        if (step.getSeconds() > 0) {
            exerciseString += step.getSeconds() + "s";
        }
        exerciseText.setText(exerciseString);
        startTimeButton.setOnClickListener(view2 -> startTimer(step.getMinutes(), step.getSeconds()));
        return view;
    }

    private View getRestView(TimedWorkoutStep step) {
        View view = getLayoutInflater().inflate(R.layout.view_rest_step, null);
        TextView exerciseText = view.findViewById(R.id.restStepExerciseText);
        Button startTimeButton = view.findViewById(R.id.restStepStartTimerButton);

        String exerciseString = step.getExercise().getName() + ": ";
        if (step.getMinutes() > 0) {
            exerciseString += step.getMinutes() + "m ";
        }
        if (step.getSeconds() > 0) {
            exerciseString += step.getSeconds() + "s";
        }
        exerciseText.setText(exerciseString);
        startTimeButton.setOnClickListener(view2 -> startTimer(step.getMinutes(), step.getSeconds()));
        return view;
    }

    private void startTimer(int mins, int seconds) {
        if (timer != null) {
            timer.cancel();
        }
        long millis = (seconds * 1000L) + (mins * 60000L);
        timerTextView.setText(mins + ":" + seconds);
        timer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long l) {
                timerTextView.setText(new SimpleDateFormat("mm:ss").format(new Date( l)));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("");
                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                tg.startTone(ToneGenerator.TONE_DTMF_0);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tg.stopTone();
                    }
                }, 500);
            }
        }.start();

    }

    private void cancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Cancel");
        builder.setMessage("Are you sure you want to cancel??");
        builder.setPositiveButton("YES", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.yellow));
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.yellow));
    }

    private void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Save");
        builder.setMessage("Are you sure you want to save??");
        builder.setPositiveButton("YES", (dialog, which) -> {
            dialog.dismiss();
            saveChangesFromViews();
            DataHolder.getInstance().saveNewWorkout(workout);
            finish();
        });
        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.yellow));
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.yellow));
    }

    private void saveChangesFromViews() {
        for (Map.Entry<TemplateWorkoutStep, View> stepViewPair : stepToViewMap.entrySet()) {
            TemplateWorkoutStep step = stepViewPair.getKey();
            if (step.getExercise().getType() == ExerciseType.REPS) {
                updateRepsStep((RepsWorkoutStep) step, stepViewPair.getValue());
            } else if (step.getExercise().getType() == ExerciseType.WEIGHT) {
                updateWeightStep((WeightWorkoutStep) step, stepViewPair.getValue());
            } else if (step.getExercise().getType() == ExerciseType.TIMED) {
                updateTimedStep((TimedWorkoutStep) step, stepViewPair.getValue());
            }
        }

    }

    private void updateRepsStep(RepsWorkoutStep step, View view) {
        EditText repsText = view.findViewById(R.id.repsStepRepsEditText);
        String reps = repsText.getText().toString();
        if (!reps.equals("")) {
            step.setActualReps(Integer.parseInt(reps));
        }
    }
    private void updateWeightStep(WeightWorkoutStep step, View view) {
        EditText repsText = view.findViewById(R.id.weightStepRepsEditText);
        EditText weightText = view.findViewById(R.id.weightStepWeightEditText);
        String reps = repsText.getText().toString();
        if (!reps.equals("")) {
            step.setActualReps(Integer.parseInt(reps));
        }
        String weight = weightText.getText().toString();
        if (!weight.equals("")) {
            BigDecimal weightDecimal = BigDecimal.valueOf(Integer.parseInt(weight));
            step.setActualWeight(weightDecimal);
            if (weightDecimal.compareTo(step.getExercise().getRecordWeight()) > 0) {
                Exercise exercise = step.getExercise();
                exercise.setRecordWeight(weightDecimal);
                MyList<AbstractSaveableField> fields = new MyList<>(exercise.getRecordWeightField());
                DataHolder.getInstance().updateExercise(exercise, fields);
            }
        }

    }
    private void updateTimedStep(TimedWorkoutStep step, View view) {
        EditText minsText = view.findViewById(R.id.timedStepMinsEditText);
        EditText secsText = view.findViewById(R.id.timedStepSecsEditText);
        String mins = minsText.getText().toString();
        if (!mins.equals("")) {
            step.setActualMins(Integer.parseInt(mins));
        }
        String secs = secsText.getText().toString();
        if (!secs.equals("")) {
            step.setActualSecs(Integer.parseInt(secs));
        }

    }
}