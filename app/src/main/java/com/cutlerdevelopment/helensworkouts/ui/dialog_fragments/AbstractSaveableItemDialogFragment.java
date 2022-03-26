package com.cutlerdevelopment.helensworkouts.ui.dialog_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
        Dialog dialog =  builder.create();
        dialog.show();
        dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    protected abstract View inflateDialogView(AlertDialog.Builder builder);
    protected abstract ListAdapter getListAdapter();


    @Override
    public void dismiss() {
        DataHolder.getInstance().notifications.unsubscribe(this);
        super.dismiss();
    }
}
