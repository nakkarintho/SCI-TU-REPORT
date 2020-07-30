package com.coldzify.finalproject.Dialog;

import android.app.Dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coldzify.finalproject.R;


public class ProgressDialog extends DialogFragment {
    //private double progress = 0;
    private TextView progress_textView;
    private ProgressBar progressBar;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.progress_dialog,null);
        progress_textView = view.findViewById(R.id.progress_textView);
        progressBar = view.findViewById(R.id.progress_bar);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
    public void setProgressText(String text){
        if(progress_textView != null){
            progress_textView.setText(text);

        }
    }


}
