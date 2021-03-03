package com.coldzify.finalproject;

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

public class EditRoleActivity extends AppCompatActivity {
    private Spinner role_spinner;
    private AutoCompleteTextView email_autoComplete;
    private ArrayAdapter<String>role_adapter;
    private FirebaseFirestore db;
    private String docpath = "";
    private String email_addpermission = "";
    private String role = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_type);
        db = FirebaseFirestore.getInstance();
        role_spinner = findViewById(R.id.staff_spinner);
        email_autoComplete = findViewById(R.id.email_autoComplete);
        String[] arr = getResources().getStringArray(R.array.role);
        role_adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,arr);
        role_spinner.setAdapter(role_adapter);
    }

    public void onClickChangePermission(View view){
        email_addpermission = email_autoComplete.getText().toString();
        role =  role_spinner.getSelectedItem().toString();

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
                                Toast.makeText(EditRoleActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                            } else {
                                final DocumentReference docRef = db.collection("users").document(docpath);
                                Map<String, Object> map = new HashMap<>();
                                map.put("role", role);
                                docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("tag", "Update Permission Success");
                                        Toast.makeText(EditRoleActivity.this, "ระบบได้แก้ไขสิทธื์ของผู้ใช้ดังกล่าวเรียบร้อย", Toast.LENGTH_LONG).show();
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("tag", "Failure : Not Have This Email In System", e);
                                                Toast.makeText(EditRoleActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        }
                        else{
                            Log.w("tag","Error Not Have This Email In Systems : ",task.getException());
                            Toast.makeText(EditRoleActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                        }

                    }

                });


    }

}
