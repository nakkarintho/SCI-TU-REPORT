package com.coldzify.finalproject;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class CommentDialog extends DialogFragment {
    private static String TAG = "CommentDialog";
    private RecyclerView recyclerView;
    private CommentListAdapter listAdapter;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private ArrayList<Comment> comments;
    private String reportID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if(getArguments() != null){
            reportID = getArguments().getString("reportID");
        }

    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //setCancelable(false);
        // Use the Builder class for convenient dialog construction
        /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.comment_layout,null);
        builder.setView(view);*/


        return new Dialog(getActivity(),R.style.CommentDialog);
        //return builder.create();
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_layout ,container, false);
        final Button comment_button = view.findViewById(R.id.comment_button);
        final EditText comment_editText = view.findViewById(R.id.comment_editText);
        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = comment_editText.getText().toString();
                if(text.length() != 0){
                    addComment(new Comment(reportID,text,mAuth.getUid()));
                    comment_editText.setText("");
                }
            }
        });
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return view;
    }

    private void getComments(){
        comments = new ArrayList<>();
        db.collection("reports/"+reportID+"/comments")
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
                                    Log.d(TAG, "New Msg: " + dc.getDocument().toObject(Comment.class));
                                    break;
                                }

                                case MODIFIED:{
                                    Log.d(TAG, "Modified Msg: " + dc.getDocument().toObject(Comment.class));
                                    break;
                                }

                                case REMOVED:{
                                    Log.d(TAG, "Removed Msg: " + dc.getDocument().toObject(Comment.class));
                                    break;
                                }

                            }
                        }
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



    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

}

