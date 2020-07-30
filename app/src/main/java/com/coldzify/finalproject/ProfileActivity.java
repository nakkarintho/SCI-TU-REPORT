package com.coldzify.finalproject;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;

import android.support.v4.app.FragmentActivity;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.coldzify.finalproject.adapter.ViewPagerAdapter;
import com.coldzify.finalproject.fragment.ProfileFragment;
import com.coldzify.finalproject.fragment.ReportFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

public class ProfileActivity extends AppCompatActivity {
    private final String TAG = "ProfileActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ImageView profilePic,noti_imageView;
    private TextView name_textView,noti_textView;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;

    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storage = FirebaseStorage.getInstance();
        profilePic = findViewById(R.id.user_imageView);
        db = FirebaseFirestore.getInstance();

        name_textView = findViewById(R.id.name_textView);
        noti_textView = findViewById(R.id.noti_textView);
        noti_imageView = findViewById(R.id.noti_imageView);
        noti_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NotificationsActivity.class);
                startActivity(intent);

            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String uid;
        if(getIntent().getExtras() !=null){
            uid = getIntent().getExtras().getString("uid");
        }
        else{
            uid = user.getUid();
        }
        getDataProfile(uid);
        getNewNoti();
        viewPager =  findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ReportFragment reportFragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        reportFragment.setArguments(args);

        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(args);

        adapter.addFragment(reportFragment,"ประวัติการแจ้งปัญหา");
        adapter.addFragment(profileFragment,"ข้อมูลส่วนตัว");
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        getUserReports(uid);


        //Toast.makeText(this,mAuth.getUid()+"",Toast.LENGTH_SHORT).show();
        //fCon.addReport(new Report("pic1",1,"testDetail",new LatLng(10,10),"2063439437107254"));
        //fCon.addComment(new Comment("TMcSZTndH4HB9t8Kg5B9","myComment","2063439437107254"));

    }



    public void getDataProfile(String uid){
        db.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String firstname =task.getResult().getString("firstname");
                            String lastname =task.getResult().getString("lastname");


                            String name = firstname+" "+lastname;
                            String pic = task.getResult().getString("picture");
                            name_textView.setText(name);



                            StorageReference userImageRef = storage.getReference().child("images/")
                                    .child("users/"+pic);
                            GlideApp.with(ProfileActivity.this)
                                    .load(userImageRef)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profilePic);
                            //Log.d(TAG,"success");
                        }
                        else{
                            Log.d(TAG,"error",task.getException());
                        }
                    }
                });
    }
    private void getUserReports(String uid){

        db.collection("reports")
                .whereEqualTo("creatorID",uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if(snapshots != null){
                            int size = snapshots.size();
                            adapter.setTitle(0,"ประวัติการแจ้งปัญหา("+size+")");
                            adapter.notifyDataSetChanged();

                        }

                    }
                });


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
                                        noti_textView.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        noti_textView.setVisibility(View.INVISIBLE);
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


    public void onClickArrowBack(View view){
        finish();
    }

    // Adapter for the viewpager using FragmentPagerAdapter




}
