package com.coldzify.finalproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.coldzify.finalproject.dataobject.UserProfile;
import com.coldzify.finalproject.dataobject.rooms;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private String report_takecareBy;
    private int report_status;
    private String report_id;
    private String name,namelast = "";
    private ArrayList<String> staff;
    private ArrayList<String> temp;
    private Boolean addstaff;
    private String id_owner;
    private String name_owner;
    private String check_owner;
    private  String checkans;
    private String ans = "";
    private String uid = "";
    private LinearLayout report_takecareBy_layout;
    private LinearLayout note_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_works);
        db = FirebaseFirestore.getInstance();
        staff = new ArrayList<>();
        temp = new ArrayList<>();
        staff_spinner = findViewById(R.id.staff_spinner);
        report_takecareBy_layout = findViewById(R.id.report_takecareBy_layout);
        note_layout = findViewById(R.id.note_layout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            report_problem_type = bundle.getString("report_problem_type");
            report_placecode = bundle.getInt("report_placecode");
            report_rooms = bundle.getString("report_rooms");
            report_detail = bundle.getString("report_detail");
            report_takecareBy = bundle.getString("report_takecareBy");
            report_status = bundle.getInt("report_status");
            report_id = bundle.getString("report_id");

        }

        AppCompatTextView report_problem_type_text = (AppCompatTextView) findViewById(R.id.finish_problem_ans);
        AppCompatTextView report_placecode_text = (AppCompatTextView) findViewById(R.id.report_placecode);
        AppCompatTextView report_rooms_text = (AppCompatTextView) findViewById(R.id.report_rooms);
        AppCompatTextView report_detail_text = (AppCompatTextView) findViewById(R.id.report_detail);
        AppCompatTextView report_takecareBy_text = (AppCompatTextView) findViewById(R.id.report_takecareBy);


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

        if(report_takecareBy==null){
            report_takecareBy_layout.setVisibility(View.GONE);
        }


        report_takecareBy_text.setText(report_takecareBy);

        if(report_problem_type.equals("MATERIAL") || report_problem_type.equals("TECHNOLOGY")
                || report_problem_type.equals("CLEAN_SECURITY"))
        { //ผู้ดูแลห้องเรียน

        db.collection("buildings").document(placecodestringans).collection("rooms")
                .get()
                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                rooms room = document.toObject(rooms.class);
                                if(room.getname().equals(report_rooms) && !room.gethousekeeper_id().equals("")) {
                                    id_owner = room.gethousekeeper_id();
                                    Log.d("id_owner",id_owner);
                                }

                            }


                        }
                    }

    });

        db.collection("users")
                .get()
                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserProfile user = document.toObject(UserProfile.class);
                                if(user.getUid().equals(id_owner)) {
                                   name_owner = user.getFirstname() + " "+ user.getLastname();
                                   Log.d("name_owner",name_owner);
                                   checkans = name_owner +  " *";
                                    staff.add(checkans);
                                    check_owner = name_owner;
                                }

                            }


                        }
                    }

                });









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
                                                        if(addstaff == true && !name.equals(check_owner)){
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
        }


        else{
            db.collection("buildings").document(placecodestringans).collection("staff").document(report_problem_type)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> listStaff = new ArrayList<>();

                            Map<String, Object> map = document.getData();
                            if (map != null) {
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    listStaff.add(entry.getValue().toString());
                                }
                            }

                            //So what you need to do with your list
                            for (String staff_name : listStaff) {
                                Log.d("TAG", staff_name);
                                db.collection("users").document(staff_name)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()&& task.getResult() != null){
                                                    String firstname = task.getResult().getString("firstname");
                                                    String lastname = task.getResult().getString("lastname");
                                                    name = firstname+" "+lastname;
                                                        staff.add(name);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
            });




        }


        staff.add("กรุณาเลือกผู้รับผิดชอบ");
        staff_adapter = new ArrayAdapter<>(this,R.layout.font_spinner,staff);
        staff_spinner.setAdapter(staff_adapter);





    }

    public void onClickAddWork(View view){

        String worker =  staff_spinner.getSelectedItem().toString();

        ans = worker;

        if(worker.equals(checkans)){
            String[] temp = worker.split(" ");
            ans = temp[0] + " " + temp[1];
        }



        Toast.makeText(getApplicationContext(),"มอบหมายงานเรียบร้อย",Toast.LENGTH_LONG).show();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref =  db.collection("reports").document(report_id);
        ref.update("takecareBy",ans);

        db.collection("users")
                .get()
                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserProfile user = document.toObject(UserProfile.class);
                                String temp1 = user.getFirstname() + " " + user.getLastname();
                                if (temp1.equals(ans)) {
                                    ref.update("takecareBy_id",user.getUid());
                                }
                            }

                        }
                        else{

                        }

                    }
                });






//        final FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference ref =  db.collection("reports").document(report_id);
//        ref.update("status",2)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            Toast.makeText(getApplicationContext(),"มอบหมายงานเรียบร้อย",Toast.LENGTH_LONG).show();
//                        }
//                        else{
//                            Toast.makeText(getApplicationContext(),"เกิดข้อผิดพลาด ไม่สามารถมอบหมายงานเรียบร้อย",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//        ref.update("takecareBy",ans);
    }


}