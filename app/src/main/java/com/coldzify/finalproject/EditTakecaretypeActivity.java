package com.coldzify.finalproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditTakecaretypeActivity extends AppCompatActivity {
    private AutoCompleteTextView email_autoComplete;
    private FirebaseFirestore db;
    private String docpath = "";
    private String email_addtakecaretype = "";
    private String role = "";
    private ArrayList<String> alldatacheck = new ArrayList<>();
    private String alldatacheck_finish = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittakecaretype);
        db = FirebaseFirestore.getInstance();
        email_autoComplete = findViewById(R.id.email_autoComplete);
        CheckBox problem1 = (CheckBox) findViewById(R.id.problemcheck1);
        CheckBox problem2 = (CheckBox) findViewById(R.id.problemcheck2);
        CheckBox problem3 = (CheckBox) findViewById(R.id.problemcheck3);
        CheckBox problem4 = (CheckBox) findViewById(R.id.problemcheck4);
        CheckBox problem5 = (CheckBox) findViewById(R.id.problemcheck5);
        CheckBox problem6 = (CheckBox) findViewById(R.id.problemcheck6);
        CheckBox problem7 = (CheckBox) findViewById(R.id.problemcheck7);
        CheckBox problem8 = (CheckBox) findViewById(R.id.problemcheck8);
        CheckBox problem9 = (CheckBox) findViewById(R.id.problemcheck9);

        problem1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    alldatacheck.add("ELECTRICS");
                }
                else{
                    alldatacheck.remove("ELECTRICS");
                }
            }
        });



        problem2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    alldatacheck.add("WATER");
                }
                else{
                    alldatacheck.remove("WATER");
                }
            }
        });

        problem3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    alldatacheck.add("CONDITIONER");
                }
                else{
                    alldatacheck.remove("CONDITIONER");
                }
            }
        });

        problem4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    alldatacheck.add("MATERIAL");
                }
                else{
                    alldatacheck.remove("MATERIAL");
                }
            }
        });

        problem5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    alldatacheck.add("TECHNOLOGY");
                }
                else{
                    alldatacheck.remove("TECHNOLOGY");
                }
            }
        });

        problem6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    alldatacheck.add("INTERNET");
                }
                else{
                    alldatacheck.remove("INTERNET");
                }
            }
        });

        problem7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    alldatacheck.add("BUILDING_ENVIRON");
                }
                else{
                    alldatacheck.remove("BUILDING_ENVIRON");
                }
            }
        });

        problem8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    alldatacheck.add("TELEPHONE");
                }
                else{
                    alldatacheck.remove("TELEPHONE");
                }
            }
        });

        problem9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    alldatacheck.add("CLEAN_SECURITY");
                }
                else{
                    alldatacheck.remove("CLEAN_SECURITY");
                }
            }
        });


    }


    public void OnClickChangeTakecaretype(View view){
        email_addtakecaretype = email_autoComplete.getText().toString();
     for(int i=0;i<alldatacheck.size();i++){
         if(alldatacheck_finish.equals("")){
             alldatacheck_finish = alldatacheck.get(i);
         }
         else {
             alldatacheck_finish = alldatacheck_finish + "," + alldatacheck.get(i);
         }
     }

        db.collection("users")
                .get()
                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserProfile user = document.toObject(UserProfile.class);
                                if (user.getEmail().equals(email_addtakecaretype)) {
                                    docpath = document.getId();
                                    Log.d("tag", "Have Email In Systems");
                                    break;
                                } else {
                                    docpath = "";
                                }
                            }
                            if (docpath.equals("")) {
                                Log.w("tag", "Error Not Have This Email In Systems : ", task.getException());
                                Toast.makeText(EditTakecaretypeActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                            } else {
                                final DocumentReference docRef = db.collection("users").document(docpath);
                                Map<String, Object> map = new HashMap<>();
                                map.put("takecareType",alldatacheck_finish );


                                docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        alldatacheck.clear();
                                        alldatacheck_finish = "";
                                        Log.d("tag", "Update Takecaretype Success");
                                        Toast.makeText(EditTakecaretypeActivity.this, "ระบบได้แก้ไขงานที่รอบผิดชอบเรียบร้อย", Toast.LENGTH_LONG).show();
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("tag", "Failure : Not Have This Email In System", e);
                                                Toast.makeText(EditTakecaretypeActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        }
                        else{
                            Log.w("tag","Error Not Have This Email In Systems : ",task.getException());
                            Toast.makeText(EditTakecaretypeActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                        }

                    }

                });

        CheckBox problem1s = (CheckBox) findViewById(R.id.problemcheck1);
        CheckBox problem2s = (CheckBox) findViewById(R.id.problemcheck2);
        CheckBox problem3s = (CheckBox) findViewById(R.id.problemcheck3);
        CheckBox problem4s = (CheckBox) findViewById(R.id.problemcheck4);
        CheckBox problem5s = (CheckBox) findViewById(R.id.problemcheck5);
        CheckBox problem6s = (CheckBox) findViewById(R.id.problemcheck6);
        CheckBox problem7s = (CheckBox) findViewById(R.id.problemcheck7);
        CheckBox problem8s = (CheckBox) findViewById(R.id.problemcheck8);
        CheckBox problem9s = (CheckBox) findViewById(R.id.problemcheck9);
        problem1s.setChecked(false);
        problem2s.setChecked(false);
        problem3s.setChecked(false);
        problem4s.setChecked(false);
        problem5s.setChecked(false);
        problem6s.setChecked(false);
        problem7s.setChecked(false);
        problem8s.setChecked(false);
        problem9s.setChecked(false);

    }


}
