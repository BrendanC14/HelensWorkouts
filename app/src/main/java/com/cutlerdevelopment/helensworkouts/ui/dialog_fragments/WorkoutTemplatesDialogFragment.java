package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.ui.listadapters.WorkoutTemplateListAdapter;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.Date;
import java.util.Map;

public class WorkoutTemplatesDialogFragment extends AbstractSaveableItemDialogFragment{

    private WorkoutTemplateListAdapter adapter;
    @Override
    protected View inflateDialogView(AlertDialog.Builder builder) {
        inflater.inflate(R.layout.dialog_exercises, null);
        View templatesFragment = inflater.inflate(R.layout.dialog_workout_templates, null);
        itemParent = templatesFragment.findViewById(R.id.workoutTemplatesLayout);
        templatesFragment.findViewById(R.id.addNewWorkoutTemplateButton).setOnClickListener(view -> createNewWorkoutTemplate());
        templatesFragment.findViewById(R.id.workoutCancelButton).setOnClickListener(view -> dismiss());
        adapter = new WorkoutTemplateListAdapter(getContext(), getParentFragmentManager());
        return templatesFragment;
    }

    @Override
    protected ListAdapter getListAdapter() {
        return adapter;
    }

    private void createNewWorkoutTemplate() {
        DialogFragment dialogFragment = new EditWorkoutTemplateDialogFragment(null);
        dialogFragment.show(getParentFragmentManager(), "EditTemplateDialogFragment");

    }

    @Override
    public void exerciseAdded(Exercise exercise) {
    }

    @Override
    public void exerciseChanged(Exercise exercise) {
    }

    @Override
    public void templateAdded(WorkoutTemplate workout) {
        adapter.addTemplate(workout);
    }

    @Override
    public void templateChanged(WorkoutTemplate template) {
        adapter.notifyDataSetChanged();
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
