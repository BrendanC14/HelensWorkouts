package com.cutlerdevelopment.helensworkouts.ui.listadapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.ExerciseType;
import com.cutlerdevelopment.helensworkouts.model.Workout;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.RepsTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TimedTemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.ui.dialog_fragments.AddSetToTemplateDialogFragment;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateWorkoutStepListAdapter extends BaseExpandableListAdapter {

    private final Context _context;
    private List<Integer> sets = new MyList<>();
    WorkoutTemplate template;
    private HashMap<Integer, MyList<TemplateWorkoutStep>> stepBySetMap;
    private SetSelectedListener setSelectedListener;

    public interface SetSelectedListener {
        void selectSet(int set);
        void moveSetUp(int set);
        void moveSetDown(int set);
    }

    public TemplateWorkoutStepListAdapter(Context context, WorkoutTemplate template, SetSelectedListener setSelectedListener) {
        this._context = context;
        this.template = template;
        this.setSelectedListener = setSelectedListener;
        refresh();
    }

    public void refresh() {
        stepBySetMap = template.getStepsBySet();
        sets = new MyList<>();
        sets.addAll(stepBySetMap.keySet());
        this.notifyDataSetChanged();
    }

    @Override
    public TemplateWorkoutStep getChild(int groupPosition, int childPosition) {
        return this.stepBySetMap.get(this.sets.get(groupPosition)).get(childPosition);
    }

    @Override
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final TemplateWorkoutStep step = getChild(groupPosition, childPosition);
        ExerciseType type = step.getExercise().getType();
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            LayoutInflater infalInflater = LayoutInflater.from(_context);
            if (type == ExerciseType.REPS || type == ExerciseType.WEIGHT) {
                convertView = infalInflater.inflate(R.layout.view_reps_template_step, null);
            } else {
                convertView = infalInflater.inflate(R.layout.view_timed_template_step, null);
            }
            childViewHolder = new ChildViewHolder(type, convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.positionText.setText(String.valueOf(step.getPositionInWorkout()));
        childViewHolder.exerciseText.setText(step.getExercise().getName());
        if (type == ExerciseType.REPS || type == ExerciseType.WEIGHT) {
            RepsTemplateWorkoutStep repsStep = (RepsTemplateWorkoutStep) step;
            childViewHolder.firstText.setText(String.valueOf(repsStep.getMinReps()));
            childViewHolder.secondText.setText(String.valueOf(repsStep.getMaxReps()));
        } else {
            TimedTemplateWorkoutStep timedStep = (TimedTemplateWorkoutStep) step;
            childViewHolder.firstText.setText(String.valueOf(timedStep.getMinutes()));
            childViewHolder.secondText.setText(String.valueOf(timedStep.getSeconds()));
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.stepBySetMap.get(this.sets.get(groupPosition)).size();
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        TemplateWorkoutStep childItem = getChild(groupPosition, childPosition);
        return (childItem.getExercise().getType() == ExerciseType.REPS ||
                childItem.getExercise().getType() == ExerciseType.WEIGHT) ? 0 : 1;
    }

    @Override
    public Integer getGroup(int groupPosition) {
        return this.sets.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.sets.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        int set = getGroup(groupPosition);
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            LayoutInflater infalInflater = LayoutInflater.from(_context);
            convertView = infalInflater.inflate(R.layout.view_set, null);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.setName.setText(getSetName(set));
        groupViewHolder.openSetButton.setOnClickListener(view -> setSelectedListener.selectSet(set));
        if (set == 1) {
            groupViewHolder.moveSetUpButton.setVisibility(View.INVISIBLE);
        } else {
            groupViewHolder.moveSetUpButton.setVisibility(View.VISIBLE);
            groupViewHolder.moveSetUpButton.setOnClickListener(view -> setSelectedListener.moveSetUp(set));
        }
        if (set == sets.size()) {
            groupViewHolder.moveSetDownButton.setVisibility(View.INVISIBLE);
        } else {
            groupViewHolder.moveSetDownButton.setVisibility(View.VISIBLE);
            groupViewHolder.moveSetDownButton.setOnClickListener(view -> setSelectedListener.moveSetDown(set));
        }
        return convertView;
    }

    private String getSetName(int set) {
        MyList<TemplateWorkoutStep> steps = stepBySetMap.get(set);
        String exercise = steps.get(0).getExercise().getName();
        for (TemplateWorkoutStep step : steps) {
            if (step.getExercise().getType() != ExerciseType.REST && !step.getExercise().getName().equals(exercise)) {
                return "Superset";
            }
        }
        return exercise;
    }

    private static class GroupViewHolder {
        final TextView setName;
        final Button openSetButton;
        final Button moveSetUpButton;
        final Button moveSetDownButton;

        GroupViewHolder(View v) {
            this.setName = v.findViewById(R.id.setNameText);
            this.openSetButton = v.findViewById(R.id.setOpenButton);
            this.moveSetUpButton = v.findViewById(R.id.setMoveUpButton);
            this.moveSetDownButton = v.findViewById(R.id.setMoveDownButton);
        }
    }

    private static class ChildViewHolder {
        final TextView positionText;
        final TextView exerciseText;
        final EditText firstText;
        final EditText secondText;
        final Button deleteButton;

        ChildViewHolder(ExerciseType type, View v) {
            if (type == ExerciseType.REPS || type == ExerciseType.WEIGHT) {
                this.positionText = v.findViewById(R.id.repsStepPosText);
                this.exerciseText = v.findViewById(R.id.repsStepExerciseText);
                this.firstText = v.findViewById(R.id.repsStepMinEditText);
                this.secondText = v.findViewById(R.id.repsStepsMaxEditText);
                this.deleteButton = v.findViewById(R.id.repsStepDeleteButton);
            } else {
                this.positionText = v.findViewById(R.id.timedStepPosText);
                this.exerciseText = v.findViewById(R.id.timedStepExerciseText);
                this.firstText = v.findViewById(R.id.timedStepMinEditText);
                this.secondText = v.findViewById(R.id.timedStepSecsEditText);
                this.deleteButton = v.findViewById(R.id.timedStepDeleteButton);
            }
            this.firstText.setEnabled(false);
            this.firstText.setBackgroundResource(R.color.blue);
            this.secondText.setEnabled(false);
            this.secondText.setBackgroundResource(R.color.blue);
            this.deleteButton.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
