package com.coldzify.finalproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.coldzify.finalproject.dataobject.UserProfile;
import com.coldzify.finalproject.dataobject.rooms;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class EditRoleActivity extends AppCompatActivity {
    private Spinner role_spinner;
    private AutoCompleteTextView email_autoComplete;
    private String email_addrole = "";
    private String all_role = "";
    private ArrayAdapter<String>role_adapter;
    private FirebaseFirestore db;
    private String docpath = "";
    private String email_addpermission = "";
    private String role = "";

    private LinearLayout addRole;

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
        addRole = findViewById(R.id.addRole);
    }

    public void OnClickSearchRole(View view){
        email_addrole = email_autoComplete.getText().toString();

        db.collection("users")
                .get()
                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserProfile user = document.toObject(UserProfile.class);
                                if (user.getEmail().equals(email_addrole)) {
                                    docpath = document.getId();
                                    if(document.getString("role")!=null) {
                                        all_role = document.getString("role");
                                            switch (all_role) {
                                                case "ผู้ใช้ทั่วไป":
                                                    role_spinner.setSelection(0);
                                                    break;
                                                case "ผู้ดูแลห้องเรียน":
                                                    role_spinner.setSelection(1);
                                                    break;
                                                case "ฝ่ายซ่อมบำรุง":
                                                    role_spinner.setSelection(2);
                                                    break;
                                                case "ฝ่ายสารสนเทศ":
                                                    role_spinner.setSelection(3);
                                                    break;
                                                case "หัวหน้างาน":
                                                    role_spinner.setSelection(4);
                                                    break;
                                                case "ผู้บริหาร":
                                                    role_spinner.setSelection(5);
                                                    break;
                                                case "ผู้ดูแลระบบ":
                                                    role_spinner.setSelection(6);
                                                    break;
                                                default:
                                                    break;
                                            }




                                    }
                                    Log.d("role", all_role);
                                    Log.d("CheckEmail", "Have Email In Systems");
                                    break;
                                } else {
                                    docpath = "";
                                }
                            }
                            if (docpath.equals("")) {
                                Log.w("tag", "Error Not Have This Email In Systems : ", task.getException());
                                Toast.makeText(EditRoleActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                            } else {
                                addRole.setVisibility(View.VISIBLE);

                            }
                        }
                        else{
                            Log.w("tag","Error Not Have This Email In Systems : ",task.getException());
                            Toast.makeText(EditRoleActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                        }

                    }

                });



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

                });


//        db.collection("buildings").document("บร.2").collection("rooms")
//                .get()
//                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                rooms room = document.toObject(rooms.class);
//                                if(room.gethousekeeper_id().equals("No")) {
//                                    final DocumentReference docRef =   db.collection("buildings").document("บร.2").collection("rooms").document(document.getId());
//                                    Map<String, Object> map = new HashMap<>();
//                                    map.put("housekeeper_id", "");
//                                    docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Log.d("tag", "Update Permission Success");
//                                            Toast.makeText(EditRoleActivity.this, "ระบบได้แก้ไขสิทธื์ของผู้ใช้ดังกล่าวเรียบร้อย", Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }
//
//                            }
//
//
//                        }
//                    }
//
//                });
//
//
//        db.collection("buildings").document("บร.3").collection("rooms")
//                .get()
//                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                rooms room = document.toObject(rooms.class);
//                                if(room.gethousekeeper_id().equals("No")) {
//                                    final DocumentReference docRef =   db.collection("buildings").document("บร.3").collection("rooms").document(document.getId());
//                                    Map<String, Object> map = new HashMap<>();
//                                    map.put("housekeeper_id", "");
//                                    docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Log.d("tag", "Update Permission Success");
//                                            Toast.makeText(EditRoleActivity.this, "ระบบได้แก้ไขสิทธื์ของผู้ใช้ดังกล่าวเรียบร้อย", Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }
//
//                            }
//
//
//                        }
//                    }
//
//                });
//
//
//        db.collection("buildings").document("บร.4").collection("rooms")
//                .get()
//                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                rooms room = document.toObject(rooms.class);
//                                if(room.gethousekeeper_id().equals("No")) {
//                                    final DocumentReference docRef =   db.collection("buildings").document("บร.4").collection("rooms").document(document.getId());
//                                    Map<String, Object> map = new HashMap<>();
//                                    map.put("housekeeper_id", "");
//                                    docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Log.d("tag", "Update Permission Success");
//                                            Toast.makeText(EditRoleActivity.this, "ระบบได้แก้ไขสิทธื์ของผู้ใช้ดังกล่าวเรียบร้อย", Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }
//
//                            }
//
//
//                        }
//                    }
//
//                });
//
//        db.collection("buildings").document("บร.5").collection("rooms")
//                .get()
//                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                rooms room = document.toObject(rooms.class);
//                                if(room.gethousekeeper_id().equals("No")) {
//                                    final DocumentReference docRef =   db.collection("buildings").document("บร.5").collection("rooms").document(document.getId());
//                                    Map<String, Object> map = new HashMap<>();
//                                    map.put("housekeeper_id", "");
//                                    docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Log.d("tag", "Update Permission Success");
//                                            Toast.makeText(EditRoleActivity.this, "ระบบได้แก้ไขสิทธื์ของผู้ใช้ดังกล่าวเรียบร้อย", Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//                                }
//
//                            }
//
//
//                        }
//                    }
//
//                });









        addRole.setVisibility(View.GONE);
    }

}
