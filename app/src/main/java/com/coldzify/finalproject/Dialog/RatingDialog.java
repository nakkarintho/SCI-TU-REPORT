package com.coldzify.finalproject.Dialog;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coldzify.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RatingDialog extends DialogFragment {

    private static final String TAG = "RatingDialog";


    private Button ok_button;
    private int progress;
    private String reportID,user_name;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RatingBar ratingBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if(getArguments() != null){
            reportID = getArguments().getString("reportID");
        }

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.rating_dialog,null);


        ok_button = (Button) view.findViewById(R.id.ok_button);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        ok_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                float rating = ratingBar.getRating();
                int ans = (int) rating;

                String rate = Integer.toString(ans);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference ref =  db.collection("reports").document(reportID);
                ref.update("rating",ans)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(),"ขอบคุณสำหรับการประเมิน",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(getContext(),"เกิดข้อผิดพลาด ไมสามารถประเมินได้",Toast.LENGTH_LONG).show();
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

