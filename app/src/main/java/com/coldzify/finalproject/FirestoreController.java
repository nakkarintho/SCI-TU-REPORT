package com.coldzify.finalproject;

import android.support.annotation.NonNull;
import android.util.Log;


import com.coldzify.finalproject.dataobject.Comment;
import com.coldzify.finalproject.dataobject.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class FirestoreController {
    private FirebaseFirestore db;
    private final String TAG = "FirestoreController : ";

     FirestoreController(){

         db = FirebaseFirestore.getInstance();
         FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                 .setPersistenceEnabled(true)
                 .build();
         db.setFirestoreSettings(settings);
     }
    void addUser(final String uid){

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);

        // Add a new document with a generated ID
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User : "+uid+ "successfully written!");
                        //System.out.println("Success adding id :"+uid);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding user ", e);
                        //System.out.println(e.getMessage());
                    }
                });


    }
    public void getAllUsers(final FirestoreCallBack callback){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                System.out.println(document.getId()+" => "+document.getData());


                            }
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
    public void getUser(String uid,final FirestoreCallBack callback){
        db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {


                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void getAllReports(final FirestoreCallBack callback){
        CollectionReference reports = db.collection("reports");
                reports.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen error", e);
                            return;
                        }

                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                            if (change.getType() == DocumentChange.Type.ADDED) {
                                Log.d(TAG, "New report:" + change.getDocument().getData());
                            }

                            String source = querySnapshot.getMetadata().isFromCache() ?
                                    "local cache" : "server";
                            Log.d(TAG, "Data fetched from " + source);
                        }
                    }
                });


                reports.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Report> list = new ArrayList<>();
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                Report report = document.toObject(Report.class);
                                System.out.println(document.getId()+" => "+document.getData());
                                String urlPic = document.getString("urlPic");
                                int type = document.getLong("type").intValue();
                                String detail =document.getString("detail");
                                GeoPoint latLng = document.getGeoPoint("coordinate");

                                String creatorID = document.getString("creatorID");
                                Timestamp timestamp = document.getTimestamp("timestamp");
                                int status = document.getLong("status").intValue();
                                //Report report = new Report(urlPic,type,detail,latLng,status,creatorID,timestamp);
                                //list.add(new Report(urlPic,type,detail,latLng,status,creatorID,timestamp));
                                list.add(report);

                            }
                            callback.onQueryListComplete(list);
                        } else {
                            Log.d("Firestore",task.getException().getMessage());
                            //Log.w(TAG, "Error getting documents.", task.getException());
                        }
                        callback.onQueryListComplete(list);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.err.println(e.getMessage());
            }
        });

    }
    void getUserReports(final String uid,final FirestoreCallBack callback){
        CollectionReference reportsRef = db.collection("reports");
        reportsRef.whereEqualTo("creatorID",uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Report> list = new ArrayList<>();
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Report report = document.toObject(Report.class);
                                String urlPic = document.getString("urlPic");
                                int type = document.getLong("type").intValue();
                                String detail =document.getString("detail");
                                GeoPoint latLng = document.getGeoPoint("coordinate");
                                String creatorID = document.getString("creatorID");
                                Timestamp timestamp = document.getTimestamp("timestamp");
                                int status = document.getLong("status").intValue();
                                //Report report = new Report(urlPic,type,detail,latLng,status,creatorID,timestamp);
                                //list.add(new Report(urlPic,type,detail,latLng,status,creatorID,timestamp));
                                list.add(report);
                            }
                            callback.onQueryListComplete(list);
                        }
                    }
                });

    }

    void checkDuplicateUser(String uid, final FirestoreCallBack callback){
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("uid", uid);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() ) {

                            //System.out.println("Duplicate : "+task.getResult().isEmpty());
                            callback.onCheckDuplicateComplete(!task.getResult().isEmpty());
                        } else {
                            if(task.getException() != null)
                            task.getException().printStackTrace();
                        }
                    }
                });
    }
    void addReport(Report report){

        db.collection("reports")
                .add(report)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        System.out.println("written with ID: "+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error adding doc: "+e.getMessage());
                    }
                });
    }
    public void addComment(Comment comment){

        CollectionReference newCommentRef = db.collection("reports")
                .document(comment.getReportID())
                .collection("comments");

        newCommentRef.add(comment)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    System.out.println("Add Comment Success");
                }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }
    public void getReportComment(final String reportID,final FirestoreCallBack callback){
        db.collection("reports")
                .document(reportID)
                .collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Comment> list = new ArrayList<>();
                        if(task.isSuccessful() && task.getResult() != null){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                Comment comment = doc.toObject(Comment.class);
                               /* String comment = doc.getString("comment");
                                String commenter = doc.getString("commenter");
                                Timestamp timestamp = doc.getTimestamp("timestamp");
                                list.add(new Comment(reportID,comment,commenter,timestamp));*/
                                list.add(comment);
                            }
                        }
                        else{
                            Log.d(TAG,task.getException().getMessage());
                        }
                        callback.onQueryListComplete(list);
                    }
                });
    }
    public void subscribeReport(final OnSuccessListener<Void> callback,final String uid,final String reportID){
        Map<String, Object> data = new HashMap<>();
        data.put("reportID", reportID);
        DocumentReference subscribeRef = db.collection("users")
                .document(uid)
                .collection("subscribe")
                .document(reportID);

        subscribeRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User : "+uid+" subscribed Report :"+reportID);
                        callback.onSuccess(aVoid);
                    }
                });
    }
    public void getSubscribe(final String uid,final FirestoreCallBack callback){
        db.collection("users")
                .document(uid)
                .collection("subscribe")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> list = new ArrayList<>();
                        if(task.isSuccessful() && task.getResult() != null){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                list.add(doc.getString("reportID"));
                                //System.out.println("ReportID : "+doc.getString("reportID"));
                            }
                        }
                        else{
                            Log.d(TAG,task.getException().getMessage());
                        }
                        callback.onQueryListComplete(list);
                    }
                });
    }



}

