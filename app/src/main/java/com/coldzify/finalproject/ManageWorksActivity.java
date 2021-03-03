package com.coldzify.finalproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.coldzify.finalproject.dataobject.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ManageWorksActivity extends AppCompatActivity {
    private Spinner staff_spinner;
    private ArrayAdapter<String> staff_adapter;
    private FirebaseFirestore db;
    private String report_problem_type;
    private int report_placecode;
    private String report_rooms;
    private String report_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_works);
        db = FirebaseFirestore.getInstance();
        staff_spinner = findViewById(R.id.staff_spinner);
        String[] arr = getResources().getStringArray(R.array.role);
        staff_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, arr);
        staff_spinner.setAdapter(staff_adapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            report_problem_type = bundle.getString("report_problem_type");
            report_placecode = bundle.getInt("report_placecode");
            report_rooms = bundle.getString("report_rooms");
            report_detail = bundle.getString("report_detail");
        
        }

    }




}