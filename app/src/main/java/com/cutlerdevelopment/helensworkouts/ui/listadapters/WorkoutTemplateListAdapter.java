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
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.ui.dialog_fragments.EditExerciseDialogFragment;
import com.cutlerdevelopment.helensworkouts.ui.dialog_fragments.EditWorkoutTemplateDialogFragment;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class WorkoutTemplateListAdapter extends BaseAdapter {

    private List<WorkoutTemplate> templates = new MyList<>();
    private LayoutInflater inflater;
    private FragmentManager fragmentManager;


    public WorkoutTemplateListAdapter(Context context, FragmentManager fragmentManager) {
        super();
        templates.addAll(DataHolder.getInstance().getAllTemplates());
        this.fragmentManager = fragmentManager;
        inflater = LayoutInflater.from(context);
    }

    public void addTemplate(WorkoutTemplate template) {
        templates.add(template);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return templates.size();
    }

    @Override
    public WorkoutTemplate getItem(int i) {
        return templates.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.view_workout_template, null);
        }

        WorkoutTemplate template = getItem(i);

        TextView templateName = view.findViewById(R.id.templateName);
        templateName.setText(template.getName());
        view.findViewById(R.id.templateEditButton).setOnClickListener(view2 -> openTemplate(template) );
        return view;
    }

    private void openTemplate(WorkoutTemplate template) {
        DialogFragment dialogFragment = new EditWorkoutTemplateDialogFragment(template);
        dialogFragment.show(fragmentManager, "EditTemplateDialogFragment");
    }


}
