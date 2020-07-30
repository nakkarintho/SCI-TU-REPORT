package com.coldzify.finalproject;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.coldzify.finalproject.adapter.CommentListAdapter;
import com.coldzify.finalproject.dataobject.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    private static String TAG = "CommentActivity";
    private RecyclerView recyclerView;
    private CommentListAdapter listAdapter;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private ArrayList<Comment> comments;
    private String reportID;
    private EditText comment_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        comment_editText = findViewById(R.id.comment_editText);


        comments = new ArrayList<>();
        listAdapter = new CommentListAdapter(this,comments);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        if(getIntent().getExtras()!= null){
            reportID = getIntent().getExtras().getString("reportID");
            listenComments();
            //Toast.makeText(this,reportID,Toast.LENGTH_SHORT).show();
        }


    }

    private void listenComments(){

        db.collection("reports/"+reportID+"/comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
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
                                    Comment comment = dc.getDocument().toObject(Comment.class);
                                    Log.d(TAG, "New Comment: " + comment);
                                    comments.add(comment);
                                    break;
                                }

                                case MODIFIED:{
                                    Log.d(TAG, "Modified Comment: " + dc.getDocument().toObject(Comment.class));
                                    break;
                                }

                                case REMOVED:{
                                    Log.d(TAG, "Removed Comment: " + dc.getDocument().toObject(Comment.class));
                                    break;
                                }

                            }
                        }
                        //Collections.sort(comments);
                        listAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void addComment(Comment comment){

        db.collection("reports/"+reportID+"/comments")
                .add(comment)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"Added comment!");
                        }
                    }
                });


    }

    public void onClickAddComment(View view){
        String text = comment_editText.getText().toString();
        if(text.length() != 0){
            addComment(new Comment(reportID,text,mAuth.getUid()));
            comment_editText.setText("");

        }
    }


}
