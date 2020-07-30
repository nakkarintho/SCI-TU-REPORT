package com.coldzify.finalproject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.coldzify.finalproject.Dialog.ManageStatusDialog;
import com.coldzify.finalproject.adapter.SlidingImageAdapter;
import com.coldzify.finalproject.dataobject.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OneReportActivity extends AppCompatActivity {
    private final String TAG = "OneReportActivity";
    private String userType = "normal";
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private TextView time_textView,detail_textView,creator_textView,place_textView;
    private ImageView user_imageView,report_setting_imageView;
    private Button subscribe_button,comment_button;
    private ProgressBar statusBar;
    private CirclePageIndicator indicator;
    private ViewPager mPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_one_report);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        time_textView = findViewById(R.id.time_textView);
        detail_textView = findViewById(R.id.detail_textView);
        creator_textView = findViewById(R.id.creator_textView);
        statusBar = findViewById(R.id.statusBar);
        user_imageView = findViewById(R.id.user_imageView);
        subscribe_button = findViewById(R.id.subscribe_button);
        comment_button = findViewById(R.id.comment_button);
        mPager = findViewById(R.id.image_pager);
        indicator = findViewById(R.id.indicator);
        report_setting_imageView = findViewById(R.id.report_setting_imageView);
        place_textView = findViewById(R.id.place_textView);
        if(mAuth.getCurrentUser() == null){
            Intent login = new Intent(this,LoginActivity.class);
            startActivity(login);
            finish();
        }
        if(getIntent().getExtras() !=null){

            String reportID = getIntent().getExtras().getString("reportID");
            String uid = mAuth.getUid();
            //Toast.makeText(this,reportID,Toast.LENGTH_SHORT).show();
            checkSubscribe(uid,reportID);
            getReportData(reportID);
            getUserType();
        }
    }

    private void getReportData(final String reportID){
        db.collection("reports").document(reportID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String detail = snapshot.getString("detail");
                            String creatorID = snapshot.getString("creatorID");
                            int status = snapshot.getLong("status").intValue();
                            ArrayList<String> pictures = (ArrayList<String>)snapshot.get("pictures");
                            int placeCode = snapshot.getLong("placeCode").intValue();
                            String room = snapshot.getString("room");
                            Timestamp timestamp = snapshot.getTimestamp("timestamp");
                            String type = snapshot.getString("type");
                            Report report = new Report(pictures,type,detail,placeCode,room,status,creatorID,timestamp);
                            setUserData(creatorID);
                            setReportData(report);
                            setOnClickListener(report,reportID);
                        }
                    }
                });
    }
    private void setReportData(Report report){
        String place = LocationHandle.locationCodeToString(report.getPlaceCode());
        String room = report.getRoom();
        if(room != null&& !room.equals(""))
            place+= " "+room;
        time_textView.setText(getTimeAgo(report.getTimestamp()));
        detail_textView.setText(report.getDetail());
        statusBar.setProgress(report.getStatus()-1);
        place_textView.setText(place);
        setImagesAdapter((ArrayList<String>) report.getPictures());

    }
    private void setUserData(final String id){
        db.collection("users").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String firstname =task.getResult().getString("firstname");
                            String lastname =task.getResult().getString("lastname");
                            String name = firstname+" "+lastname;
                            String pic = task.getResult().getString("picture");
                            creator_textView.setText(name);


                            StorageReference userImageRef = storage.getReference().child("images/")
                                    .child("users/"+pic);
                            GlideApp.with(getApplicationContext())
                                    .load(userImageRef)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(user_imageView);

                        }
                    }
                });


    }
    private void checkSubscribe(String uid,String reportID){
        db.collection("users").document(uid).collection("subscribe")
                .whereEqualTo("reportID",reportID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getException() == null && task.getResult() != null){

                            if(task.getResult().isEmpty()){
                                subscribe_button.setText(getResources().getString(R.string.subscribe_th));
                            }
                            else{
                                subscribe_button.setText(getResources().getString(R.string.unsubscribe_th));
                                subscribe_button.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.statusBar_color));
                                Drawable noti_red_ic = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_notifications_red);
                                subscribe_button.setCompoundDrawablesWithIntrinsicBounds(noti_red_ic,null,null,null);
                            }
                        }
                        else{
                            Log.w(TAG,task.getException());
                        }
                    }
                });


    }
    private void subscribe(final String uid, final String reportID){
        subscribe_button.setText(getResources().getString(R.string.unsubscribe_th));
        Map<String, Object> data = new HashMap<>();
        data.put("reportID", reportID);
        db.collection("users")
                .document(uid)
                .collection("subscribe")
                .document(reportID)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "subscribed : "+reportID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error to subscribe", e);
                    }
                });

    }
    private void unSubscribe(String uid, final String reportID){
        subscribe_button.setText(getResources().getString(R.string.subscribe_th));
        db.collection("users").document(uid)
                .collection("subscribe")
                .document(reportID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "Unsubscribed : "+reportID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error to unsubscribe", e);
                    }
                });
    }
    private void setOnClickListener(Report report,final String reportID){
        final String currentUserUid = mAuth.getUid();
        final String creator = report.getCreatorID();
        final int report_status = report.getStatus();

        subscribe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subscribe_button.getText().equals(getResources().getString(R.string.subscribe_th))){
                    subscribe_button.setText(getResources().getString(R.string.unsubscribe_th));
                    subscribe_button.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.statusBar_color));
                    Drawable noti_red_ic = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_notifications_red);
                    subscribe_button.setCompoundDrawablesWithIntrinsicBounds(noti_red_ic,null,null,null);
                    subscribe(currentUserUid,reportID);
                }
                else{
                    unSubscribe(currentUserUid,reportID);
                    subscribe_button.setText(getResources().getString(R.string.subscribe_th));
                    subscribe_button.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.color_black));
                    Drawable noti_grey_ic = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_notifications_grey);
                    subscribe_button.setCompoundDrawablesWithIntrinsicBounds(noti_grey_ic,null,null,null);

                }

            }
        });
        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(OneReportActivity.this,CommentActivity.class);
                intent.putExtra("reportID",reportID);

                startActivity(intent);
            }
        });
        user_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OneReportActivity.this,ProfileActivity.class);
                intent.putExtra("uid",creator);
                startActivity(intent);
            }
        });
        creator_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OneReportActivity.this,ProfileActivity.class);
                intent.putExtra("uid",creator);
                startActivity(intent);
            }
        });
        report_setting_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(OneReportActivity.this, report_setting_imageView);
                //Inflating the Popup using xml file
                int menu = R.menu.report_menu;
                if(userType!=null &&userType.equals("normal"))
                    menu = R.menu.report_menu_normal;
                popup.getMenuInflater()
                        .inflate(menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.manage_status_item){
                            ManageStatusDialog dialog = new ManageStatusDialog();
                            Bundle bundle = new Bundle();
                            bundle.putInt("progress", report_status);
                            bundle.putString("reportID",reportID);
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), "Manage Status Dialog");
                        }

                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });

    }
    private void getUserType(){
        if(mAuth.getUid() == null)
            return;
        db.collection("users").document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult()!=null){
                            userType = task.getResult().getString("userType");
                        }
                    }
                });
    }
    private void setImagesAdapter(ArrayList<String> pictures){
        SlidingImageAdapter adapter = new SlidingImageAdapter(this,pictures);
        adapter.setUseImageName(true);
        mPager.setAdapter(adapter);
        indicator.setViewPager(mPager);

    }
    private String getTimeAgo(Timestamp timestamp){
        Date past = timestamp.toDate();
        Date now = new Date();
        //long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
        int minutes=(int) TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
        int hours= (int) TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
        int days= (int) TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
        if(days > 0){
            return days+" วัน";
        }
        else{
            if(hours > 0)
                return hours+" ชม.";
            else {
                if(minutes > 0 )
                    return minutes+" นาที";
                else
                    return "เมื่อสักครู่";
            }
        }

    }
}
