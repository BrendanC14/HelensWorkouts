package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import androidx.fragment.app.DialogFragment;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.WorkoutTemplate;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableField;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.model.workout_steps.TemplateWorkoutStep;
import com.cutlerdevelopment.helensworkouts.ui.listadapters.TemplateWorkoutStepListAdapter;
import com.cutlerdevelopment.helensworkouts.utils.MyList;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class EditWorkoutTemplateDialogFragment extends DialogFragment
        implements AddSetToTemplateDialogFragment.StepsChangedListener, TemplateWorkoutStepListAdapter.SetSelectedListener {

    WorkoutTemplate template;
    boolean newTemplate = false;
    TextInputEditText nameText;
    private LayoutInflater inflater;
    private ExpandableListView stepLayout;
    //private LinkedHashMap<TemplateWorkoutStep, View> stepToViewMap = new LinkedHashMap<>();
    private TemplateWorkoutStepListAdapter adapter;
    private MyList<TemplateWorkoutStep> newSteps = new MyList<>();
    private HashMap<TemplateWorkoutStep, MyList<AbstractSaveableField>> updatedSteps = new HashMap<>();

    public EditWorkoutTemplateDialogFragment(WorkoutTemplate template) {

        this.template = template;
        if (template == null) {
            newTemplate = true;
            this.template = new WorkoutTemplate("");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = requireActivity().getLayoutInflater();
        View templateFragment = inflater.inflate(R.layout.dialog_edit_workout_template, null);
        builder.setView(templateFragment);
        nameText = templateFragment.findViewById(R.id.editTemplateNameInputEditText);
        stepLayout = templateFragment.findViewById(R.id.editTemplateLayout);
        templateFragment.findViewById(R.id.editTemplateSaveButton).setOnClickListener(view -> saveChanges());
        templateFragment.findViewById(R.id.editTemplateAddStepsButton).setOnClickListener(view -> openAddStepsFragment());
        templateFragment.findViewById(R.id.editTemplateDeleteButton).setOnClickListener(view -> deleteTemplate());
        templateFragment.findViewById(R.id.editTemplateCancelButton).setOnClickListener(view -> dismiss());
        nameText.setText(template.getName());
        adapter = new TemplateWorkoutStepListAdapter(getContext(), template, this);
        stepLayout.setAdapter(adapter);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                dismiss();
                return true;
            }
            return false;
        });
        dialog.show();
        dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public void stepsDeleted(MyList<TemplateWorkoutStep> deletedSteps) {
        for (TemplateWorkoutStep deletedStep : deletedSteps) {
            DataHolder.getInstance().deleteTemplateStep(deletedStep);
        }
        adapter.refresh();
    }

    private void openAddStepsFragment() {
        AddSetToTemplateDialogFragment dialogFragment = new AddSetToTemplateDialogFragment(this.template, this);
        dialogFragment.show(getParentFragmentManager(), "AddStepsFragment");
    }

    private void editExistingSet(int set) {
        AddSetToTemplateDialogFragment dialogFragment = new AddSetToTemplateDialogFragment(this.template, set, this);
        dialogFragment.show(getParentFragmentManager(), "AddStepsFragment");
    }

    @Override
    public void selectSet(int set) {
        editExistingSet(set);
    }

    @Override
    public void moveSetUp(int set) {
        if (set == 1) return;
        HashMap<Integer, MyList<TemplateWorkoutStep>> stepsBySet = template.getStepsBySet();
        MyList<TemplateWorkoutStep> stepsToMoveUp = stepsBySet.get(set);
        MyList<TemplateWorkoutStep> stepsToMoveDown = stepsBySet.get(set -1);
        updateStepsSetNumber(stepsToMoveUp, set - 1);
        updateStepsSetNumber(stepsToMoveDown, set);
        adapter.refresh();
    }

    @Override
    public void moveSetDown(int set) {
        if (set == template.numberOfSets()) return;
        HashMap<Integer, MyList<TemplateWorkoutStep>> stepsBySet = template.getStepsBySet();
        MyList<TemplateWorkoutStep> stepsToMoveDown = stepsBySet.get(set);
        MyList<TemplateWorkoutStep> stepsToMoveUp = stepsBySet.get(set +1);
        updateStepsSetNumber(stepsToMoveDown, set + 1);
        updateStepsSetNumber(stepsToMoveUp, set);
        adapter.refresh();
    }

    private void updateStepsSetNumber(MyList<TemplateWorkoutStep> steps, int newSet) {
        for (TemplateWorkoutStep step : steps) {
            step.setSetNumber(newSet);
            MyList<AbstractSaveableField> fields = new MyList<>(step.getSetNumberField());
            DataHolder.getInstance().updateTemplateStep(step, fields);
        }
        template.updateStepsUnderSet(newSet, steps);
    }

    private void saveChanges() {
        if (newTemplate) {
            saveNewTemplate();
        } else {
            updateExistingTemplate();
        }
        close();
    }

    @Override
    public void stepsAdded(MyList<TemplateWorkoutStep> steps) {
        newSteps.addAll(steps);
        adapter.refresh();
    }

    private void saveNewTemplate() {
        String name = nameText.getText().toString();
        if (name.equals("")) return;
        template.setName(name);
        DataHolder.getInstance().saveNewTemplate(template);
    }

    private void updateExistingTemplate() {
        MyList<AbstractSaveableField> updatedFields = new MyList<>();
        String newName = nameText.getText().toString();
        if (!newName.equalsIgnoreCase(template.getName())) {
            template.setName(newName);
            updatedFields.add(template.getNameField());
        }
        if (!updatedFields.isEmpty()) {
            DataHolder.getInstance().updateTemplate(template, updatedFields);
        }
        saveNewTemplateSteps();
        updateExistingTemplateSteps();
    }

    private void saveNewTemplateSteps() {
        DataHolder.getInstance().saveTemplateSteps(newSteps);
    }

    private void updateExistingTemplateSteps() {
        for (Map.Entry<TemplateWorkoutStep, MyList<AbstractSaveableField>> stepFieldsPair : updatedSteps.entrySet()) {
            DataHolder.getInstance().updateTemplateStep(stepFieldsPair.getKey(), stepFieldsPair.getValue());
        }
    }

    public void deleteTemplate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete " + template.getName() + "??");
        builder.setPositiveButton("YES", (dialog, which) -> {
            DataHolder.getInstance().deleteTemplate(template);
            close();
        });
        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.yellow));
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.yellow));

    }

    @Override
    public void dismiss() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setTitle("Cancel");
        builder.setMessage("Are you sure you want to cancel??");
        builder.setPositiveButton("YES", (dialog, which) -> {
            dialog.dismiss();
            close();
        });
        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(alert.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.yellow));
        alert.getButton(alert.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.yellow));
    }

    private void close() {
        super.dismiss();
    }
}
