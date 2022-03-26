package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.data.IDataListener;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.ui.listadapters.ExerciseListAdapter;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExercisesDialogFragment extends AbstractSaveableItemDialogFragment {

    private CheckBox repsFilter;
    private CheckBox weightFilter;
    private CheckBox timedFilter;

    private ExerciseListAdapter adapter;

    @Override
    protected View inflateDialogView(AlertDialog.Builder builder) {
        inflater.inflate(R.layout.dialog_exercises, null);
        View exercisesFragment = inflater.inflate(R.layout.dialog_exercises, null);
        itemParent = exercisesFragment.findViewById(R.id.exercisesLayout);
        repsFilter = exercisesFragment.findViewById(R.id.exercisesRepsFilter);
        weightFilter = exercisesFragment.findViewById(R.id.exercisesWeightFilter);
        timedFilter = exercisesFragment.findViewById(R.id.exercisesTimedFilter);
        repsFilter.setOnCheckedChangeListener((compoundButton, b) -> {
            repsFilter.setChecked(b);
            adapter.setShowReps(b);
        });
        weightFilter.setOnCheckedChangeListener((compoundButton, b) -> {
            weightFilter.setChecked(b);
            adapter.setShowWeight(b);
        });
        timedFilter.setOnCheckedChangeListener((compoundButton, b) -> {
            timedFilter.setChecked(b);
            adapter.setShowTimed(b);
        });
        exercisesFragment.findViewById(R.id.addNewExerciseButton).setOnClickListener(view -> createNewExercise());
        exercisesFragment.findViewById(R.id.exercisesCancelButton).setOnClickListener(view -> dismiss());
        adapter = new ExerciseListAdapter(getContext(), getParentFragmentManager());
        itemParent.setAdapter(adapter);
        return exercisesFragment;
    }

    @Override
    protected ListAdapter getListAdapter() {
        return adapter;
    }

    private void createNewExercise() {
        DialogFragment dialogFragment = new EditExerciseDialogFragment(null);
        dialogFragment.show(getParentFragmentManager(), "EditExerciseDialogFragment");

    }

    @Override
    public void exerciseAdded(Exercise exercise) {
        adapter.addExercise(exercise);
    }

    @Override
    public void exerciseChanged(Exercise exercise) {
        adapter.notifyDataSetChanged();
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
    public void allTemplatesRetrieved(MyList<WorkoutTemplate> templates) {

    }

    @Override
    public void templateSavedToDate(Date date, WorkoutTemplate template) {

    }

    @Override
    public void templateRetrievedForDate(Date date, WorkoutTemplate template) {

    }
}
