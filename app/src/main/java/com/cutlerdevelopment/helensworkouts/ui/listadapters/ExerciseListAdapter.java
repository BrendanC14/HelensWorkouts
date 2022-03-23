package com.cutlerdevelopment.helensworkouts.ui.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.ui.dialog_fragments.EditExerciseDialogFragment;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ExerciseListAdapter extends BaseAdapter {

    private HashMap<ExerciseType, MyList<Exercise>> exercisesByTypeMap = new HashMap<>();
    private List<Exercise> exerciseToDisplay = new MyList<>();
    private LayoutInflater inflater;
    private FragmentManager fragmentManager;


    private boolean showReps = true;
    private boolean showWeight = true;
    private boolean showTimed = true;

    public void setShowReps(boolean showReps) {
        if (this.showReps != showReps) {
            this.showReps = showReps;
            updateDisplay();
        }
    }

    public void setShowWeight(boolean showWeight) {
        if (this.showWeight != showWeight) {
            this.showWeight = showWeight;
            updateDisplay();
        }
    }

    public void setShowTimed(boolean showTimed) {
        if (this.showTimed != showTimed) {
            this.showTimed = showTimed;
            updateDisplay();
        }
    }

    public ExerciseListAdapter(Context context, FragmentManager fragmentManager) {
        super();
        createExercisesByTypeMap();
        exerciseToDisplay.addAll(DataHolder.getInstance().getAllExercises());
        this.fragmentManager = fragmentManager;
        inflater = LayoutInflater.from(context);
    }

    private void createExercisesByTypeMap() {
        for (Exercise exercise : DataHolder.getInstance().getAllExercises()) {
            if (!exercisesByTypeMap.containsKey(exercise.getType())) {
                exercisesByTypeMap.put(exercise.getType(), new MyList<>());
            }
            exercisesByTypeMap.get(exercise.getType()).add(exercise);
        }
    }

    public void addExercise(Exercise exercise) {
        exercisesByTypeMap.get(exercise.getType()).add(exercise);
        updateDisplay();
    }

    public void updateDisplay() {
        exerciseToDisplay = new MyList<>();
        if (showReps) {
            exerciseToDisplay.addAll(exercisesByTypeMap.get(ExerciseType.REPS));
        }
        if (showWeight) {
            exerciseToDisplay.addAll(exercisesByTypeMap.get(ExerciseType.WEIGHT));
        }
        if (showTimed) {
            exerciseToDisplay.addAll(exercisesByTypeMap.get(ExerciseType.TIMED));
        }
        exerciseToDisplay.sort(Comparator.comparing(AbstractSaveableItem::getName));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return exerciseToDisplay.size();
    }

    @Override
    public Exercise getItem(int i) {
        return exerciseToDisplay.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.view_exercise, null);
        }

        Exercise exercise = getItem(i);

        TextView exerciseName = view.findViewById(R.id.exerciseName);
        exerciseName.setText(exercise.getName());
        TextView exerciseType = view.findViewById(R.id.exerciseType);
        String type = exercise.getType().name();
        type = type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase();
        exerciseType.setText(type);
        view.findViewById(R.id.exerciseEditButton).setOnClickListener(view2 -> openExercise(exercise) );
        return view;
    }

    private void openExercise(Exercise exercise) {
        DialogFragment dialogFragment = new EditExerciseDialogFragment(exercise);
        dialogFragment.show(fragmentManager, "EditExerciseDialogFragment");
    }

}
