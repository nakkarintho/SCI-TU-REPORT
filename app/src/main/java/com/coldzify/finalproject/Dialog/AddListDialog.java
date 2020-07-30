package com.coldzify.finalproject.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.coldzify.finalproject.ProblemType;
import com.coldzify.finalproject.R;

import java.util.ArrayList;

public class AddListDialog extends DialogFragment {
    private OnClickOKListener okListener;
    private EditText editText;
    private Spinner spinner;
    private Button add_button,cancel_button;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_list,null);
        spinner = view.findViewById(R.id.spinner);
        editText = view.findViewById(R.id.editText);
        add_button = view.findViewById(R.id.addList_button);
        cancel_button = view.findViewById(R.id.cancel_button);

        String[] arr = getResources().getStringArray(R.array.problemType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_dropdown_item_1line,arr);
        spinner.setAdapter(adapter);

        if(okListener != null){
            add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(editText.getText().toString().length() != 0){
                        String name = editText.getText().toString();
                        int type_index = spinner.getSelectedItemPosition();
                        String type =  typePositionToString(type_index);
                        okListener.onClickOK(name,type);
                        dismiss();
                    }

                }
            });
        }
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
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

    public void setOnClickOKListener(OnClickOKListener okListener){
        this.okListener=okListener;

    }

    private String typePositionToString(int index){
        String type = "";
        switch (index){
            case 1:
                type = ProblemType.ELECTRICS.name();
                break;
            case 2:
                type = ProblemType.WATER.name();
                break;
            case 3:
                type = ProblemType.POLLUTION.name();
                break;
            case 4:
                type = ProblemType.MATERIAL.name();
                break;
            case 5:
                type = ProblemType.CLEAN.name();
                break;
            case 6:
                type = ProblemType.SECURITY.name();
                break;
            case 7:
                type = ProblemType.ENVIRONMENT.name();
                break;
            case 8:
                type = ProblemType.TRAFFIC.name();
                break;
            case 9:
                type = ProblemType.BUILDING.name();
                break;
            default:
                type ="";
        }
        return type;
    }
    public interface OnClickOKListener{
        void onClickOK(String name,String type);
    }

}
