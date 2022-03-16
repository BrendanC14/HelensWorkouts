package com.cutlerdevelopment.helensworkouts.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.model.Exercise;
import com.cutlerdevelopment.model.ExerciseType;
import com.cutlerdevelopment.model.Workout;
import com.cutlerdevelopment.model.data.DataHolder;
import com.cutlerdevelopment.model.data.IDataListener;

public class MainActivity extends AppCompatActivity implements IDataListener {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.testTextView);
        DataHolder dataHolder = DataHolder.getInstance(this);
    }

    @Override
    public void exerciseAdded(Exercise exercise) {
        textView.setText(exercise.getName());
    }

    @Override
    public void workoutAdded(Workout workout) {
        String currentText = textView.getText().toString();
        currentText += "\n" + workout.getName() + " - " + workout.getSteps().size() + " steps";
        textView.setText(currentText);
    }
}