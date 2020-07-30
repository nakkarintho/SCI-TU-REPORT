package com.coldzify.finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.coldzify.finalproject.adapter.NotificationListAdapter;
import com.coldzify.finalproject.dataobject.Notifications;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {
    private final String TAG="NotificationsActivty";
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private ArrayList<Notifications> notis;
    private ArrayList<String> notis_ID;
    private NotificationListAdapter listAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        db  = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth  = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        notis = new ArrayList<>();
        notis_ID = new ArrayList<>();
        listAdapter = new NotificationListAdapter(notis);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        clearNewNoti();
        getNotifications();
    }


    private void getNotifications(){

        db.collection("users/"+mAuth.getUid()+"/notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:{
                                    Notifications noti = dc.getDocument().toObject(Notifications.class);
                                    notis.add(noti);
                                    notis_ID.add(dc.getDocument().getId());
                                    Log.d(TAG, "Added Comment: " + dc.getDocument().toObject(Notifications.class));
                                    break;
                                }

                                case MODIFIED:{
                                    Log.d(TAG, "Modified Comment: " + dc.getDocument().toObject(Notifications.class));
                                    break;
                                }

                                case REMOVED:{
                                    Log.d(TAG, "Removed Comment: " + dc.getDocument().toObject(Notifications.class));
                                    break;
                                }

                            }
                        }
                        //Collections.sort(comments);
                        listAdapter.notifyDataSetChanged();
                    }
                });
    }
    private void clearNewNoti(){
        if(mAuth.getUid()  != null){
            db.collection("users").document(mAuth.getUid())
                    .update("new_noti",0);
        }


    }

}
