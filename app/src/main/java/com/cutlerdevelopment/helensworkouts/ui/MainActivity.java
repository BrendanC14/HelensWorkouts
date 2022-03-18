package com.cutlerdevelopment.helensworkouts.ui;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.data.IDataListener;
import com.cutlerdevelopment.helensworkouts.ui.dialog_fragments.ExercisesDialogFragment;
import com.cutlerdevelopment.helensworkouts.utils.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity implements IDataListener {

    private LinearLayout layout;
    private LinkedHashMap<String, Button> buttonByDateMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.calendarLayout);
        findViewById(R.id.exercisesButton).setOnClickListener(view -> openExercises());
        findViewById(R.id.workoutsButton).setOnClickListener(view -> openWorkouts());
        DataHolder dataHolder = DataHolder.getInstance(this);
        Date today = new Date();
        for (int i = 0; i < 31; i++) {
            Date date = DateUtil.addDays(today, i);
            addDateToLayout(date);
            dataHolder.getWorkoutByDate(date);
        }
    }

    private void addDateToLayout(Date date) {
        View dateView = getLayoutInflater().inflate(R.layout.view_date, null);
        TextView dateText = dateView.findViewById(R.id.dateText);
        Button button = dateView.findViewById(R.id.dateButton);
        String dateString = DateUtil.toLongString(date);
        dateText.setText(dateString);
        buttonByDateMap.put(DateUtil.toLongString(date), button);
        layout.addView(dateView);
    }

    private void openExercises() {
        DialogFragment exerciseFragment = new ExercisesDialogFragment();
        exerciseFragment.show(getSupportFragmentManager(), "ExerciseFragment");
    }

    private void openWorkouts() {

    }

    @Override
    public void exerciseAdded(Exercise exercise) {

    }

    @Override
    public void exerciseChanged(Exercise exercise) {

    }

    @Override
    public void templateAdded(WorkoutTemplate template) {
        //DataHolder.getInstance().saveNewWorkout(new Workout(template, new Date()));
    }


    @Override
    public void templateChanged(WorkoutTemplate template) {

    }

    @Override
    public void workoutAdded(Workout workout) {
        Button button = buttonByDateMap.getOrDefault(DateUtil.toLongString(workout.getDate()), null);
        if (button != null) {
            button.setText(workout.getName());
        }
    }

    @Override
    public void workoutChanged(Workout workout) {

    }
}