package com.coldzify.finalproject.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coldzify.finalproject.R;
import com.coldzify.finalproject.dataobject.Notifications;
import com.coldzify.finalproject.dataobject.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManageStatusDialog extends DialogFragment {
    private static final String TAG = "ManageStatusDialog";


    private Button ok_button;
    private int progress;
    private String reportID;
    private Spinner spinner;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            progress = getArguments().getInt("progress");
            reportID = getArguments().getString("reportID");
        }

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.manage_status_dialog,null);

        spinner = view.findViewById(R.id.spinner);

        String[] arr = getResources().getStringArray(R.array.report_status);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_dropdown_item_1line,arr);
        spinner.setAdapter(adapter);
        spinner.setSelection(progress-1);
        ok_button = view.findViewById(R.id.ok_button);


        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok_button.setText("กำลัังโหลด...");
                ok_button.setEnabled(false);
                if(spinner.getSelectedItemPosition()+1 == progress){
                    dismiss();
                    return;
                }
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("reports").document(reportID)
                        .update("status",spinner.getSelectedItemPosition()+1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(),"เปลี่ยนสถานะเรียบร้อยแล้ว",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(getContext(),"เกิดข้อผิดพลาด ไมสามารถเปลี่ยนสถานะได้",Toast.LENGTH_LONG).show();
                                }
                                dismiss();
                            }
                        });



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



}
