package com.coldzify.finalproject.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import com.coldzify.finalproject.R;


public class EditChecklistDialog extends DialogFragment {
    private OnClickSaveListener onClickSaveListener;
    private EditText editText;
    private String checklist_name,checklist_id;
    private Button save_button,cancel_button;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            checklist_name = getArguments().getString("checklist_name");
            checklist_id = getArguments().getString("checklist_id");
        }

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_checklist,null);

        editText = view.findViewById(R.id.editText);
        save_button = view.findViewById(R.id.save_button);
        cancel_button = view.findViewById(R.id.cancel_button);
        editText.setText(checklist_name);
        editText.setSelection(checklist_name.length());
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                if(text.length() != 0){

                    onClickSaveListener.onClickSave(checklist_id,text);
                    dismiss();
                }

            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
    public void setOnClickSaveListener(OnClickSaveListener listener){
        this.onClickSaveListener = listener;
    }

    public interface OnClickSaveListener {
        void onClickSave(String checklist_id,String newName);
    }
}
