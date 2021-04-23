package com.coldzify.finalproject.Dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.coldzify.finalproject.OneReportActivity;
import com.coldzify.finalproject.R;
import com.coldzify.finalproject.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ManageStatusDialog extends DialogFragment {
    private static final String TAG = "ManageStatusDialog";


    private Button ok_button;
    private int progress;
    private String reportID,user_name;
    private Spinner spinner;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView dateFinish_editText;
    private String dateFinish;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.getString("role") != null){
                                String firstname = doc.getString("firstname");
                                String lastname = doc.getString("lastname");
                                user_name = firstname +  " "+lastname;
                            }
                        }
                    }
                });
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
        dateFinish_editText = view.findViewById(R.id.dateFinish_editText);

        String[] arr = getResources().getStringArray(R.array.report_status);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                R.layout.font_spinner,arr);
        adapter.setDropDownViewResource(R.layout.font_spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(progress-1);
        ok_button = view.findViewById(R.id.ok_button);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                dateFinish_editText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        dateFinish_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Objects.requireNonNull(getActivity()), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ok_button.setText("กำลังโหลด...");
                 ok_button.setEnabled(false);
                dateFinish = dateFinish_editText.getText().toString();
                if(spinner.getSelectedItemPosition()+1 < progress){
                    dismiss();
                    return;
                }
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference ref =  db.collection("reports").document(reportID);
                ref.update("status",spinner.getSelectedItemPosition()+1)
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
//                db.collection("reports").document(reportID)
//                .set("dateFinish"+dateFinish, SetOptions.merge());
                Date d = new Date();
                ref.update("takecareBy",user_name);
                ref.update("dateFinish",dateFinish);
                ref.update("lastModified", new Timestamp(d));

                Map<String, Object> docData = new HashMap<>();
                docData.put("staffname", user_name);
                docData.put("date", new Timestamp(d));

//                Map<String, Object> docData2 = new HashMap<>();
//                docData2.put("dateFinish", dateFinish);

                ref.collection("takecareBy").add(docData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

//                ref.collection("dateFinish").add(docData2).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//                    }
//                })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error adding document", e);
//                            }
//                        });
            }
        });

   //subscribe
            Log.d(TAG, "subscribing: "+reportID);
            Map<String, Object> data = new HashMap<>();
            data.put("reportID", reportID);
            db.collection("users")
                    .document(mAuth.getUid())
                    .collection("subscribe")
                    .document(reportID)
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "subscribed : "+reportID);
                            progress++;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error to subscribe", e);
                            progress++;
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
