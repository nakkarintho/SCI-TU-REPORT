package com.coldzify.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.coldzify.finalproject.dataobject.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ContactHousekeeperActivity extends AppCompatActivity {
    private String housekeeper_id,building,room;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private UserProfile housekeeper;
    private ImageView user_imageView;
    private TextView room_textView,name_textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_housekeeper);


        user_imageView = findViewById(R.id.user_imageView);
        room_textView = findViewById(R.id.room_textView);
        name_textView = findViewById(R.id.name_textView);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        if(getIntent().getExtras() != null){
            housekeeper_id = getIntent().getExtras().getString("housekeeper_id");
            building = getIntent().getExtras().getString("building");
            room = getIntent().getExtras().getString("room");
            getHousekeeperData();
        }
    }

    private void getHousekeeperData(){

        db.collection("users").document(housekeeper_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){

                            housekeeper = task.getResult().toObject(UserProfile.class);
                            //Toast.makeText(getApplicationContext(),housekeeper.getFirstname(),Toast.LENGTH_LONG).show();
                            String name = housekeeper.getFirstname()+" "+housekeeper.getLastname();
                            String picture = housekeeper.getPicture();
                            name_textView.setText(name);
                            String place = building+" "+room;
                            room_textView.setText(place);
                            StorageReference userImageRef = storage.getReference().child("images/")
                                    .child("users/"+picture);
                            GlideApp.with(getApplicationContext())
                                    .load(userImageRef)
                                    .skipMemoryCache(true)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(user_imageView);
                        }
                    }
                });
    }

    public void onClickPhoneCall(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0912659536"));
        startActivity(intent);
    }

}
