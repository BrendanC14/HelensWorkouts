package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import com.cutlerdevelopment.helensworkouts.R;
import com.cutlerdevelopment.helensworkouts.model.Exercise;
import com.cutlerdevelopment.helensworkouts.model.data.DataHolder;
import com.cutlerdevelopment.helensworkouts.model.data.IDataListener;
import com.cutlerdevelopment.helensworkouts.model.saveables.AbstractSaveableItem;
import com.cutlerdevelopment.helensworkouts.utils.MyList;

import java.util.HashMap;

public abstract class AbstractSaveableItemDialogFragment extends DialogFragment implements IDataListener {

    protected LayoutInflater inflater;
    ListView itemParent;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = requireActivity().getLayoutInflater();
        builder.setView(inflateDialogView(builder));
        itemParent.setAdapter(getListAdapter());
        DataHolder.getInstance().notifications.subscribe(this);
        return builder.create();
    }

    protected abstract View inflateDialogView(AlertDialog.Builder builder);
    protected abstract ListAdapter getListAdapter();


    @Override
    public void dismiss() {
        DataHolder.getInstance().notifications.unsubscribe(this);
        super.dismiss();
    }
}
