package com.coldzify.finalproject;

import android.content.Context;


import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.coldzify.finalproject.Dialog.MenuDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;


public class TitleBarView extends FrameLayout {
    private final String TAG = "TitleBarView";
    private TextView title_textView,noti_textView,menu_textView;
    private ImageView noti_imageView,report_menu_imageView;
    private String title;
    private MenuDialog dialog;
    private int icon;
    private String userType = "normal",user_name;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    public TitleBarView(Context context) {
        super(context);
        setup(null);
    }
    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public TitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }



    private void setup(AttributeSet attrs) {
        inflate(getContext(), R.layout.titlebar_layout, this);
        bindView();
        dialog = new MenuDialog();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setupStyleable(attrs);
        setupView();
    }
    private void bindView() {
        title_textView = findViewById(R.id.title_textView);
        menu_textView = findViewById(R.id.menu_textView);
        noti_imageView = findViewById(R.id.noti_imageView);
        noti_textView = findViewById(R.id.noti_textView);
        report_menu_imageView = findViewById(R.id.report_menu_imageView);
    }
    private void setupStyleable(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBarView);
        title = typedArray.getString(R.styleable.TitleBarView_title_name);
        icon = typedArray.getResourceId(R.styleable.TitleBarView_icon_pic,0);
        typedArray.recycle();
    }
    private void setupView() {
        getIsHouseKeeper();
        getNewNoti();
        title_textView.setText(title);
        //menu_imageView.setImageResource(icon);
        menu_textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dialog.isVisible()){
                    FragmentActivity fragment = (FragmentActivity)getContext();
                    Bundle bundle = new Bundle();
                    bundle.putString("userType", userType);
                    bundle.putString("user_name",user_name);
                    dialog.setArguments(bundle);
                    dialog.show(fragment.getSupportFragmentManager(),"Menu");
                }


            }
        });
        noti_imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),NotificationsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getContext().startActivity(intent);

            }
        });
        report_menu_imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ReportActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getContext().startActivity(intent);
            }
        });
    }
    public void setTitle(String str){
        title_textView.setText(str);
    }
    private void getNewNoti(){

        if(mAuth.getUid() != null){
            db.collection("users").document(mAuth.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                if(snapshot.getLong("new_noti") != null){
                                    int new_noti = snapshot.getLong("new_noti").intValue();
                                    if(new_noti != 0){
                                        noti_textView.setText(String.valueOf(new_noti));
                                        noti_textView.setVisibility(VISIBLE);
                                    }
                                    else{
                                        noti_textView.setVisibility(INVISIBLE);
                                    }
                                }
                                Log.d(TAG, "Current data: " + snapshot.getData());
                            } else {
                                Log.d(TAG, "Current data: null");
                            }
                        }
                    });


        }


    }
    private void getIsHouseKeeper(){
        if(mAuth.getUid() == null)
            return;
        db.collection("users").document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot doc = task.getResult();
                            if(doc.getString("userType") != null){
                                userType = doc.getString("userType");
                                String firstname = doc.getString("firstname");
                                String lastname = doc.getString("lastname");
                                user_name = firstname +  " "+lastname;
                            }

                        }
                    }
                });
    }

}
