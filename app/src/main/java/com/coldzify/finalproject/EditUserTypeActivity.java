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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditUserTypeActivity extends AppCompatActivity {
    private Spinner userType_spinner;
    private AutoCompleteTextView email_autoComplete;
    private ArrayAdapter<String>userType_adapter;
    private FirebaseFirestore db;
    private String docpath = "";
    private String email_addpermission = "";
    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_type);
        db = FirebaseFirestore.getInstance();
        userType_spinner = findViewById(R.id.userType_spinner);
        email_autoComplete = findViewById(R.id.email_autoComplete);
        String[] arr = getResources().getStringArray(R.array.userType);
        userType_adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,arr);
        userType_spinner.setAdapter(userType_adapter);
    }

    public void onClickChangePermission(View view){
        email_addpermission = email_autoComplete.getText().toString();
        userType =  userType_spinner.getSelectedItem().toString();
        if(userType.equals("ผู้ใช้ทั่วไป")){
            userType = "normal";
        }
        else if(userType.equals("ผู้ดูแลห้องเรียน")){
            userType = "housekeeper";
        }
        else if(userType.equals("เจ้าหน้าที่")){
            userType = "staff";
        }
        else if(userType.equals("หัวหน้างาน")){
            userType = "manager";
        }
        else if(userType.equals("ผู้บริหาร")){
            userType = "ceo";
        }


        db.collection("users")
                .get()
                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserProfile user = document.toObject(UserProfile.class);
                                if (user.getEmail().equals(email_addpermission)) {
                                    docpath = document.getId();
                                    Log.d("tag", "Have Email In Systems");
                                    break;
                                } else {
                                    docpath = "";
                                }
                            }
                            if (docpath.equals("")) {
                                Log.w("tag", "Error Not Have This Email In Systems : ", task.getException());
                                Toast.makeText(EditUserTypeActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                            } else {
                                final DocumentReference docRef = db.collection("users").document(docpath);
                                Map<String, Object> map = new HashMap<>();
                                map.put("userType", userType);
                                docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("tag", "Update Permission Success");
                                        Toast.makeText(EditUserTypeActivity.this, "ระบบได้แก้ไขสิทธื์ของผู้ใช้ดังกล่าวเรียบร้อย", Toast.LENGTH_LONG).show();
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("tag", "Failure : Not Have This Email In System", e);
                                                Toast.makeText(EditUserTypeActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        }
                        else{
                            Log.w("tag","Error Not Have This Email In Systems : ",task.getException());
                            Toast.makeText(EditUserTypeActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                        }

                    }

                });


    }

}
