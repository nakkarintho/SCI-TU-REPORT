package com.coldzify.finalproject;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.coldzify.finalproject.dataobject.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReportStatusActivity extends AppCompatActivity {
    private final String TAG = "ReportStatusActivity";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<Report> reports;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_status);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        getUserReport();



    }

    private void getUserReport(){
        reports = new ArrayList<>();
        db.collection("reports")
                .whereEqualTo("creator",mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                System.out.println(doc);
                                reports.add(doc.toObject(Report.class));
                            }
                        }
                        else{
                            if(task.getException() != null)
                                Log.d(TAG,task.getException().getMessage());
                        }
                    }
                });
    }
}
