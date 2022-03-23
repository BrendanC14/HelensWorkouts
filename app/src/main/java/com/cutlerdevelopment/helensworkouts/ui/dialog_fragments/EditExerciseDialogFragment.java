package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.integration.ExerciseFirestoreHandler;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.utils.MyList;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

public class EditExerciseDialogFragment extends DialogFragment {

    Exercise exercise;
    TextInputEditText nameText;
    RadioButton repsWorkoutButton;
    RadioButton weightWorkoutButton;
    RadioButton timedWorkoutButton;

    public EditExerciseDialogFragment(Exercise exercise) {
        this.exercise = exercise;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View exercisesFragment = inflater.inflate(R.layout.dialog_edit_exercises, null);
        builder.setView(exercisesFragment);
        nameText = exercisesFragment.findViewById(R.id.editExerciseNameInputEditText);
        repsWorkoutButton = exercisesFragment.findViewById(R.id.editExerciseRepsRadioButton);
        timedWorkoutButton = exercisesFragment.findViewById(R.id.editExerciseTimedRadioButton);
        weightWorkoutButton = exercisesFragment.findViewById(R.id.editExerciseWeightRadioButton);
        exercisesFragment.findViewById(R.id.editExerciseSaveButton).setOnClickListener(view -> saveChanges());
        exercisesFragment.findViewById(R.id.editExerciseCancelButton).setOnClickListener(view -> dismiss());
        if (exercise != null) {
            nameText.setText(exercise.getName());
            getButtonForType(exercise.getType()).setChecked(true);
        } else {
            repsWorkoutButton.setChecked(true);
        }

        return builder.create();
    }

    private void saveChanges() {
        if (exercise == null) {
            saveNewExercise();
        } else {
            updateExistingExercise();
        }
        dismiss();
    }

    private void saveNewExercise() {
        String name = nameText.getText().toString();
        if (name.equals("")) return;
        DataHolder.getInstance().saveNewExercise(new Exercise(
                name,
                getSelectedExercise()
        ));
    }

    private void updateExistingExercise() {
        MyList<AbstractSaveableField> updatedFields = new MyList<>();
        String newName = nameText.getText().toString();
        if (!newName.equalsIgnoreCase(exercise.getName())) {
            exercise.setName(newName);
            updatedFields.add(exercise.getNameField());
        }
        ExerciseType typeSelected = getSelectedExercise();
        if (typeSelected != exercise.getType()) {
            exercise.setType(typeSelected);
            updatedFields.add(exercise.getTypeField());
        }

        if (!updatedFields.isEmpty()) {
            DataHolder.getInstance().updateExercise(exercise, updatedFields);
        }

    }

    private RadioButton getButtonForType(ExerciseType type) {
        switch (type) {
            case REPS: return repsWorkoutButton;
            case WEIGHT: return weightWorkoutButton;
            default: return timedWorkoutButton;
        }
    }

    private ExerciseType getSelectedExercise() {
        if (repsWorkoutButton.isChecked()) {
            return ExerciseType.REPS;
        } else if (weightWorkoutButton.isChecked()) {
            return ExerciseType.WEIGHT;
        } else {
            return ExerciseType.TIMED;
        }
    }

}
