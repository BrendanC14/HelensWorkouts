package com.cutlerdevelopment.helensworkouts.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.data.IDataListener;
import com.cutlerdevelopment.helensworkouts.ui.dialog_fragments.ExercisesDialogFragment;
import com.cutlerdevelopment.helensworkouts.ui.dialog_fragments.WorkoutTemplatesDialogFragment;
import com.cutlerdevelopment.helensworkouts.utils.DateUtil;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements IDataListener {

    private LinearLayout layout;
    private LinkedHashMap<Date, Spinner> spinnerByDateMap = new LinkedHashMap<>();
    private HashMap<Date, Button> startButtonByDateMap = new HashMap<>();
    private HashMap<String, WorkoutTemplate> templatesByNameMap = new HashMap<>();
    MyList<String> templateNames = new MyList<>();
    private final String NO_TEMPLATE_NAME = "No Workout scheduled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.calendarLayout);
        findViewById(R.id.exercisesButton).setOnClickListener(view -> openExercises());
        findViewById(R.id.workoutsButton).setOnClickListener(view -> openWorkoutTemplates());
        DataHolder.getInstance(this);
        templateNames.add(NO_TEMPLATE_NAME);

        Date today = DateUtil.currentDate();
        for (int i = 0; i < 31; i++) {
            Date date = DateUtil.addDays(today, i);
            addDateToLayout(date);
        }
    }

    private void addDateToLayout(Date date) {
        View dateView = getLayoutInflater().inflate(R.layout.view_date, null);
        TextView dateText = dateView.findViewById(R.id.dateText);
        Spinner spinner = dateView.findViewById(R.id.dateTemplateSpinner);
        String dateString = DateUtil.toLongString(date);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, templateNames);
        dateText.setText(dateString);
        spinner.setAdapter(adapter);
        spinnerByDateMap.put(date, spinner);
        startButtonByDateMap.put(date, dateView.findViewById(R.id.dateStartWorkoutButton));
        layout.addView(dateView);
    }

    private void openExercises() {
        DialogFragment exerciseFragment = new ExercisesDialogFragment();
        exerciseFragment.show(getSupportFragmentManager(), "ExerciseFragment");
    }

    private void openWorkoutTemplates() {
        DialogFragment templateFragment = new WorkoutTemplatesDialogFragment();
        templateFragment.show(getSupportFragmentManager(), "TemplateFragment");

    }

    @Override
    public void exerciseAdded(Exercise exercise) {
    }

    @Override
    public void exerciseChanged(Exercise exercise) {

    }

    @Override
    public void allTemplatesRetrieved(MyList<WorkoutTemplate> templates) {
        for (WorkoutTemplate template : templates) {
            String name = template.getName();
            templateNames.add(name);
            templatesByNameMap.put(name, template);
        }
        //Collections.sort(templateNames);
        setDateSpinners();

    }

    private void setDateSpinners() {
        for (Map.Entry<Date, Spinner> spinnerDatePair : spinnerByDateMap.entrySet()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, templateNames);
            Spinner spinner = spinnerDatePair.getValue();
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selected = adapterView.getItemAtPosition(i).toString();
                    spinnerItemSelected(spinnerDatePair.getKey(), selected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        Date today = DateUtil.currentDate();
        for (int i = 0; i < 31; i++) {
            Date date = DateUtil.addDays(today, i);
            DataHolder.getInstance().getTemplateForDate(date);
        }
    }

    private void spinnerItemSelected(Date date, String templateSelected) {
        if (templateSelected == NO_TEMPLATE_NAME) {
            DataHolder.getInstance().deleteTemplateOnDate(date);
            Button button = startButtonByDateMap.get(date);
            if (button != null) {
                button.setVisibility(View.GONE);
            }
        } else {
            WorkoutTemplate template = templatesByNameMap.get(templateSelected);
            if (template != null) DataHolder.getInstance().saveTemplateToDate(date, template);
            Button button = startButtonByDateMap.get(date);
            if (button != null) {
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(view -> startWorkout(template));
            }

        }
    }

    private void startWorkout(WorkoutTemplate template) {
        Intent intent = new Intent(MainActivity.this, CompleteWorkoutActivity.class);
        intent.putExtra("TemplateID", template.getId());
        startActivity(intent);
    }

    @Override
    public void templateAdded(WorkoutTemplate template) {
    }


    @Override
    public void templateChanged(WorkoutTemplate template) {

    }

    @Override
    public void workoutAdded(Workout workout) {
    }

    @Override
    public void workoutChanged(Workout workout) {

    }

    @Override
    public void templateSavedToDate(Date date, WorkoutTemplate template) {

    }

    @Override
    public void templateRetrievedForDate(Date date, WorkoutTemplate template) {
        Spinner spinner = spinnerByDateMap.getOrDefault(date, null);
        if (spinner != null) {
            spinner.setSelection(templateNames.indexOf(template.getName()));
        }
    }

    @Override
    public void templateDeleted(WorkoutTemplate template) {
        templateNames.remove(template.getName());
    }
}