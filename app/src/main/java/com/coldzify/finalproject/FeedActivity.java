package com.coldzify.finalproject;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.coldzify.finalproject.adapter.ReportListAdapter;
import com.coldzify.finalproject.dataobject.Report;
import com.facebook.Profile;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;


import java.util.ArrayList;


public class FeedActivity extends AppCompatActivity {

    private final String TAG = "FeedLog";
    private FirestoreController fCon;
    private FirebaseStorage storage;
    private ArrayList<Report> reports;
    private ArrayList<String> reportID;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ReportListAdapter listAdapter;
    private FirebaseFirestore db;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Profile profile;
    private Handler handler;
    private Runnable run;
    private boolean isGetReportFinish = false;


    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        init();
        getReports();
        getTokenId();

        handler = new Handler();

        run = new Runnable() {
            public void run() {
                if(isGetReportFinish ){
                    listAdapter = new ReportListAdapter(getApplicationContext(),getSupportFragmentManager(),reports,reportID);
                    recyclerView.setAdapter(listAdapter);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();
                    swipeRefreshLayout.setRefreshing(false);
                    handler.removeCallbacks(this);
                    Log.d(TAG,"Running");

                }else {
                    handler.postDelayed(this, 100);

                }

            }
        };
        handler.postDelayed(run, 100);

    }
    private void init(){
        fCon = new FirestoreController();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        reports = new ArrayList<>();
        profile = Profile.getCurrentProfile();
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    private void getReports(){

        reports = new ArrayList<>();
        db.collection("reports")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(this,new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            reportID = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Report report = document.toObject(Report.class);
                                reports.add(report);
                                reportID.add(document.getId());
                            }

                            isGetReportFinish = true;
                            Log.d(TAG,"Fetch report is done");

                        }
                        else{
                            Log.w(TAG,"Error : ",task.getException());
                        }

                    }
                });

    }
    private void refresh(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        isGetReportFinish = false;

        getReports();

        handler.postDelayed(run, 100);
    }
    private void getTokenId(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        sendRegistrationToServer(token);
                        // Log and toast

                        String msg = "Token : "+token;
                        Log.d(TAG, msg);

                    }
                });
    }
    private void sendRegistrationToServer(String token) {
        String uid = mAuth.getUid();

        if(uid == null)
            return;
        db.collection("users").document(uid)
                .update("tokenId",token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Update token success");
                        }
                        else{
                            Log.d(TAG,"error ",task.getException());
                        }
                    }
                });
    }



}
