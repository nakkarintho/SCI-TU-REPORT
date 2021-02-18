package com.coldzify.finalproject.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.coldzify.finalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {
    private final String TAG = "ProfileFragment";
    private String uid;
    private FirebaseFirestore db;
    private TextView name_textView;
    private TextView email_textView;
    private TextView userType_textView;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            uid = getArguments().getString("uid");

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name_textView = view.findViewById(R.id.name_textView);
        email_textView = view.findViewById(R.id.email_textView);
        userType_textView = view.findViewById(R.id.userType_textView);
        getDataProfile(uid);
        return view;
    }
    public void getDataProfile(String uid){
        db.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String name = task.getResult().getString("firstname") +" "+ task.getResult().getString("lastname");
                            String userType =task.getResult().getString("userType");
                            String email =task.getResult().getString("email");
                            name_textView.setText(name);
                            email_textView.setText(email);
                            userType_textView.setText(userType);
                        }
                        else{
                            Log.d(TAG,"error",task.getException());
                        }
                    }
                });
    }

}
