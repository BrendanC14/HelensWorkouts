package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.data.IDataListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExercisesDialogFragment extends DialogFragment implements IDataListener {

    private HashMap<Exercise, View> exerciseViewMap = new HashMap<>();
    private LayoutInflater inflater;
    LinearLayout layout;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = requireActivity().getLayoutInflater();
        View exercisesFragment = inflater.inflate(R.layout.dialog_exercises, null);
        builder.setView(exercisesFragment);
        DataHolder.getInstance().notifications.subscribe(this);
        layout = exercisesFragment.findViewById(R.id.exercisesLayout);
        for (Exercise exercise : DataHolder.getInstance().getAllExercises()) {
            addNewExercise(exercise);
        }
        return builder.create();
    }


    private void addNewExercise(Exercise exercise) {
        View exerciseView = inflater.inflate(R.layout.view_exercise, null);
        populateViewWithExerciseDetails(exercise, exerciseView);
        layout.addView(exerciseView);
        exerciseViewMap.put(exercise, exerciseView);
    }

    private void populateViewWithExerciseDetails(Exercise exercise, View exerciseView) {
        TextView exerciseName = exerciseView.findViewById(R.id.exerciseName);
        exerciseName.setText(exercise.getName());

        TextView exerciseType = exerciseView.findViewById(R.id.exerciseType);
        String type = exercise.getType().name();
        type = type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase();
        exerciseType.setText(type);

        exerciseView.findViewById(R.id.exerciseEditButton).setOnClickListener(view -> openExercise(exercise) );

    }

    private void openExercise(Exercise exercise) {
        DialogFragment dialogFragment = new EditExerciseDialogFragment(exercise);
        dialogFragment.show(getParentFragmentManager(), "EditExerciseDialogFragment");
    }

    @Override
    public void exerciseAdded(Exercise exercise) {
        addNewExercise(exercise);
    }

    @Override
    public void exerciseChanged(Exercise exercise) {
        if (exerciseViewMap.containsKey(exercise)) {
            View view = exerciseViewMap.get(exercise);
            populateViewWithExerciseDetails(exercise, view);
        }
    }

    @Override
    public void templateAdded(WorkoutTemplate workout) {

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
    public void dismiss() {
        DataHolder.getInstance().notifications.unsubscribe(this);
        super.dismiss();
    }
}
