package com.coldzify.finalproject.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coldzify.finalproject.R;

public class UploadProgressDialog extends DialogFragment {
    private ProgressBar progressBar;
    private TextView upload_textView;
    private int max;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            max = getArguments().getInt("max");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.upload_progress_layout,null);
        upload_textView = view.findViewById(R.id.upload_textView);
        progressBar = view.findViewById(R.id.upload_progressBar);
        progressBar.setMax(max);

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
    public void setText(){
        String str = "กำลังอัพโหลด "+progressBar.getProgress()+"/"+progressBar.getMax();
        upload_textView.setText(str);

    }

    public void setProgress(int progress){
        progressBar.setProgress(progress);

    }
}
