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
    private String report_detail,nameans, tutorialsName;
    private ArrayList<String> staff;
    Boolean check;


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

        }

        AppCompatTextView report_problem_type_text = (AppCompatTextView)findViewById(R.id.report_problem_type);
        AppCompatTextView report_placecode_text = (AppCompatTextView)findViewById(R.id.report_placecode);
        AppCompatTextView report_rooms_text = (AppCompatTextView)findViewById(R.id.report_rooms);
        AppCompatTextView report_detail_text = (AppCompatTextView)findViewById(R.id.report_detail);


        String problemtypestring[] = getResources().getStringArray(R.array.filter_feed2);

        switch(report_problem_type) {
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

        switch(report_placecode) {
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



        if(report_problem_type.equals("MATERIAL") || report_problem_type.equals("TECHNOLOGY")  || report_problem_type.equals("BUILDING_ENVIRON")
                || report_problem_type.equals("CLEAN_SECURITY") ) {


            db.collection("buildings")
                    .document(placecodestringans)
                    .collection("rooms")
                    .get()
                    .addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    rooms room = document.toObject(rooms.class);
                                    String housekeeper_id = room.gethousekeeper_id();
                                    if(!housekeeper_id.equals("")) {
                                        db.collection("users")
                                                .document(housekeeper_id)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            nameans = task.getResult().getString("firstname");
                                                            check = true;

                                                            for(int i=0;i<staff.size();i++){
                                                                if(staff.get(i).equals(nameans)){
                                                                    check = false;
                                                                }
                                                            }

                                                            if(check == true){
                                                                staff.add(nameans);
                                                            }

                                                        } else {
                                                            //Log.w(TAG, "Error getting documents.", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                }


                            } else {
                            }

                        }

                    });

        }






        else{


        }



        staff_adapter = new ArrayAdapter<>(this, R.layout.font_spinner, staff);
        staff_adapter.setDropDownViewResource( R.layout.font_spinner);

        staff_spinner.setAdapter(staff_adapter);
        staff_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), staff_spinner.getItemAtPosition(position).toString() + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

    }







}