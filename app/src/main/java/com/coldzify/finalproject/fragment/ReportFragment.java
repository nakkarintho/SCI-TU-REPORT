package com.coldzify.finalproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.coldzify.finalproject.R;
import com.coldzify.finalproject.adapter.ReportListAdapter;
import com.coldzify.finalproject.dataobject.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;



public class ReportFragment extends Fragment {

    // TODO: Customize parameter argument names

    private static final String TAG = "ReportFragment";
    // TODO: Customize parameters

    private String uid;

    private FirebaseFirestore db;
    private ArrayList<Report> reports;
    private ArrayList<String> reportID;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    public ReportFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_list, container, false);
        Context context = view.getContext();
        // Set the adapter
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getUserReports(uid);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserReports(uid);
            }
        });
        return view;
    }
    private void getUserReports(String uid){
        reports = new ArrayList<>();
        reportID = new ArrayList<>();
        db.collection("reports")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereEqualTo("creatorID",uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            reportID = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Report report = document.toObject(Report.class);
                                reports.add(report);
                                reportID.add(document.getId());
                            }

                            Log.d(TAG,"Fetch report is done");

                        }
                        else{
                            Log.w(TAG,"Error : ",task.getException());
                        }
                        recyclerView.setAdapter(new ReportListAdapter(getContext(),getFragmentManager(),reports,reportID));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

 /*   @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/

}
