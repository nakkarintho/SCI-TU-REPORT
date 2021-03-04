package com.coldzify.finalproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.coldzify.finalproject.dataobject.Report;
import com.coldzify.finalproject.dataobject.UserProfile;
import com.coldzify.finalproject.dataobject.rooms;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageWorksActivity extends AppCompatActivity {
    private Spinner staff_spinner;
    private ArrayAdapter<String> staff_adapter;
    private FirebaseFirestore db;
    private String report_problem_type;
    private int report_placecode;
    private String report_rooms;
    private String report_detail;
    private int report_status;
    private String report_id;

    private String name,namelast = "";
    private ArrayList<String> staff;
    private Boolean addstaff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_works);
        db = FirebaseFirestore.getInstance();
        staff = new ArrayList<>();
        staff_spinner = findViewById(R.id.staff_spinner);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            report_problem_type = bundle.getString("report_problem_type");
            report_placecode = bundle.getInt("report_placecode");
            report_rooms = bundle.getString("report_rooms");
            report_detail = bundle.getString("report_detail");
            report_status = bundle.getInt("report_status");
            report_id = bundle.getString("report_id");

        }

        AppCompatTextView report_problem_type_text = (AppCompatTextView) findViewById(R.id.report_problem_type);
        AppCompatTextView report_placecode_text = (AppCompatTextView) findViewById(R.id.report_placecode);
        AppCompatTextView report_rooms_text = (AppCompatTextView) findViewById(R.id.report_rooms);
        AppCompatTextView report_detail_text = (AppCompatTextView) findViewById(R.id.report_detail);


        String problemtypestring[] = getResources().getStringArray(R.array.filter_feed2);

        switch (report_problem_type) {
            case "ELECTRICS":
                report_problem_type_text.setText(problemtypestring[1]);
                break;
            case "WATER":
                report_problem_type_text.setText(problemtypestring[2]);
                break;
            case "CONDITIONER":
                report_problem_type_text.setText(problemtypestring[3]);
                break;
            case "MATERIAL":
                report_problem_type_text.setText(problemtypestring[4]);
                break;
            case "TECHNOLOGY":
                report_problem_type_text.setText(problemtypestring[5]);
                break;
            case "INTERNET":
                report_problem_type_text.setText(problemtypestring[6]);
                break;
            case "BUILDING_ENVIRON":
                report_problem_type_text.setText(problemtypestring[7]);
                break;
            case "CLEAN_SECURITY":
                report_problem_type_text.setText(problemtypestring[8]);
                break;
            case "TELEPHONE":
                report_problem_type_text.setText(problemtypestring[9]);
                break;
            default:
                break;
        }

        String placecodestring[] = getResources().getStringArray(R.array.building);
        String placecodestringans = "";

        switch (report_placecode) {
            case 0:
                placecodestringans = placecodestring[0];
                report_placecode_text.setText(placecodestringans);
                break;
            case 1:
                placecodestringans = placecodestring[1];
                report_placecode_text.setText(placecodestringans);
                break;
            case 2:
                placecodestringans = placecodestring[2];
                report_placecode_text.setText(placecodestringans);
                break;
            case 3:
                placecodestringans = placecodestring[3];
                report_placecode_text.setText(placecodestringans);
                break;
            default:
                break;
        }


        report_rooms_text.setText(report_rooms);
        report_detail_text.setText(report_detail);








        db.collection("buildings").document(placecodestringans).collection("rooms")
                .get()
                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                rooms room = document.toObject(rooms.class);
                                if(!room.gethousekeeper_id().equals("")){
                                    db.collection("users").document(room.gethousekeeper_id())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()&& task.getResult() != null){
                                                        String firstname = task.getResult().getString("firstname");
                                                        String lastname = task.getResult().getString("lastname");
                                                        name = firstname+" "+lastname;
                                                        addstaff = true;
                                                        for(int i=0;i<staff.size();i++){
                                                            if(staff.get(i).equals(name)){
                                                                addstaff = false;
                                                            }
                                                        }
                                                        if(addstaff == true){
                                                            staff.add(name);
                                                        }

                                                    }
                                                }
                                            });
                                }


                            }
                        }
                        else{
                            //Log.w("tag","Error Not Have This Email In Systems : ",task.getException());
                            //Toast.makeText(EditRoleActivity.this, "ไม่มีอีเมลดังกล่าวในระบบ", Toast.LENGTH_LONG).show();
                        }

                    }

                });




        staff.add("กรุณาเลือกผู้รับผิดชอบ");
        staff_adapter = new ArrayAdapter<>(this,R.layout.font_spinner,staff);
        staff_spinner.setAdapter(staff_adapter);



    }

    public void onClickAddWork(View view){
        String worker =  staff_spinner.getSelectedItem().toString();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref =  db.collection("reports").document(report_id);
        ref.update("status",2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"มอบหมายงานเรียบร้อย",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"เกิดข้อผิดพลาด ไม่สามารถมอบหมายงานเรียบร้อย",Toast.LENGTH_LONG).show();
                        }
                    }
                });
        ref.update("takecareBy",worker);
    }


}