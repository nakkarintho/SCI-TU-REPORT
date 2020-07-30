package com.coldzify.finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.request.RequestOptions;

import com.coldzify.finalproject.CommentActivity;
import com.coldzify.finalproject.Dialog.ManageStatusDialog;
import com.coldzify.finalproject.GlideApp;
import com.coldzify.finalproject.LocationHandle;
import com.coldzify.finalproject.OneReportActivity;
import com.coldzify.finalproject.ProfileActivity;
import com.coldzify.finalproject.R;
import com.coldzify.finalproject.dataobject.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.viewpagerindicator.CirclePageIndicator;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.MyViewHolder> {
    private final String TAG = "ReportAdapter";
    private ArrayList<Report> reports;
    private ArrayList<String> reportsID;

    private FirebaseStorage storage;
    private  FirebaseAuth mAuth;

    private Context context;
    private FirebaseFirestore db;
    private FragmentManager fm;
    private String userType="normal";

    public ReportListAdapter(Context context, FragmentManager fm, ArrayList<Report> reports, ArrayList<String> reportsID) {
        this.reports = reports;
        this.reportsID = reportsID;
        storage = FirebaseStorage.getInstance();
        this.fm=fm;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        getUserType();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_row,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public int getItemCount() { return reports.size(); }
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {
        String place = LocationHandle.locationCodeToString(reports.get(i).getPlaceCode());

        String room = reports.get(i).getRoom();
        if(room != null && !room.equals(""))
            place+= " "+room;
        setUserData(reports.get(i).getCreatorID(),holder);
        holder.time_textView.setText(getTimeAgo(reports.get(i).getTimestamp()));
        holder.detail_textView.setText(reports.get(i).getDetail());
        holder.statusBar.setProgress(reports.get(i).getStatus()-1);
        holder.place_textView.setText(place);
        final ArrayList<String> pictures = (ArrayList<String>) reports.get(i).getPictures();

        setImagesAdapter(holder.mPager,holder.indicator,pictures);


        final String reportID = reportsID.get(i);
        final String currentUserUid = mAuth.getUid();
        checkSubscribe(currentUserUid,reportID,holder);
        setOnClickListener(i,holder);

    }
    private void setOnClickListener(final int i ,final MyViewHolder holder){
        final String reportID = reportsID.get(i);
        final String currentUserUid = mAuth.getUid();
        final String creator = reports.get(i).getCreatorID();
        final int report_status = reports.get(i).getStatus();
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(), OneReportActivity.class);
                intent.putExtra("reportID",reportID);

                holder.view.getContext().startActivity(intent);
            }
        });
        holder.subscribe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.subscribe_button.getText().equals(context.getResources().getString(R.string.subscribe_th))){
                    holder.subscribe_button.setText(context.getResources().getString(R.string.unsubscribe_th));
                    holder.subscribe_button.setTextColor(ContextCompat.getColor(context,R.color.statusBar_color));
                    Drawable noti_red_ic = AppCompatResources.getDrawable(context, R.drawable.ic_notifications_red);
                    holder.subscribe_button.setCompoundDrawablesWithIntrinsicBounds(noti_red_ic,null,null,null);
                    subscribe(currentUserUid,reportID,holder);
                }
                else{
                    unSubscribe(currentUserUid,reportID,holder);
                    holder.subscribe_button.setText(context.getResources().getString(R.string.subscribe_th));
                    holder.subscribe_button.setTextColor(ContextCompat.getColor(context,R.color.color_black));
                    Drawable noti_grey_ic = AppCompatResources.getDrawable(context, R.drawable.ic_notifications_grey);
                    holder.subscribe_button.setCompoundDrawablesWithIntrinsicBounds(noti_grey_ic,null,null,null);

                }

            }
        });
        holder.comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(holder.view.getContext(), CommentActivity.class);
                intent.putExtra("reportID",reportID);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.view.getContext().startActivity(intent);
            }
        });
        holder.user_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(), ProfileActivity.class);
                intent.putExtra("uid",creator);
                holder.view.getContext().startActivity(intent);
            }
        });
        holder.creator_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(),ProfileActivity.class);
                intent.putExtra("uid",creator);
                holder.view.getContext().startActivity(intent);
            }
        });
        holder.report_setting_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(holder.view.getContext(), holder.report_setting_imageView);
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
                            dialog.show(fm, "Manage Status Dialog");
                        }

                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });

    }


    private void setUserData(final String id, final MyViewHolder holder){
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
                           holder.creator_textView.setText(name);


                           StorageReference userImageRef = storage.getReference().child("images/")
                                    .child("users/"+pic);
                            GlideApp.with(holder.view)
                                    .load(userImageRef)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(holder.user_imageView);

                        }
                    }
                });


    }
    private void checkSubscribe(String uid,String reportID,final MyViewHolder holder){
        db.collection("users").document(uid).collection("subscribe")
                .whereEqualTo("reportID",reportID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getException() == null && task.getResult() != null){

                            if(task.getResult().isEmpty()){
                                holder.subscribe_button.setText(context.getResources().getString(R.string.subscribe_th));
                            }
                            else{
                                holder.subscribe_button.setText(context.getResources().getString(R.string.unsubscribe_th));
                                holder.subscribe_button.setTextColor(ContextCompat.getColor(context,R.color.statusBar_color));
                                Drawable noti_red_ic = AppCompatResources.getDrawable(context, R.drawable.ic_notifications_red);
                                holder.subscribe_button.setCompoundDrawablesWithIntrinsicBounds(noti_red_ic,null,null,null);
                            }
                        }
                        else{
                            Log.w(TAG,task.getException());
                        }
                    }
                });


    }
    private void subscribe(final String uid, final String reportID, final MyViewHolder holder){
        holder.subscribe_button.setText(context.getResources().getString(R.string.unsubscribe_th));
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
        FirebaseMessaging.getInstance().subscribeToTopic(reportID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!task.isSuccessful()) {
                            Log.d(TAG, "subscribe topic fail");
                        }
                        else{
                            Log.d(TAG, "subscribed topic");
                        }


                    }
                });

    }
    private void unSubscribe(String uid, final String reportID, final MyViewHolder holder){
        holder.subscribe_button.setText(context.getResources().getString(R.string.subscribe_th));
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
        FirebaseMessaging.getInstance().unsubscribeFromTopic(reportID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!task.isSuccessful()) {
                            Log.d(TAG, "unsubscribe topic fail");
                        }
                        else{
                            Log.d(TAG, "unsubscribed topic");
                        }


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
    private String getTimeAgo(Timestamp timestamp){
        Date past = timestamp.toDate();
        Date now = new Date();
        //long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
        int minutes=(int)TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
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
    private void setImagesAdapter(ViewPager mPager,CirclePageIndicator indicator,ArrayList<String> pictures){
        SlidingImageAdapter adapter = new SlidingImageAdapter(context,pictures);
        adapter.setUseImageName(true);
        mPager.setAdapter(adapter);
        indicator.setViewPager(mPager);

    }
    private Bitmap resizeBitmap(Bitmap bitmap, int targetWidth){
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        float ratio = targetWidth*1.0f/imageWidth;
        int targetHeight = (int)(imageHeight*ratio);
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap,targetWidth,targetHeight,false);
        return newBitmap;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView time_textView,detail_textView,creator_textView,place_textView;
        ImageView user_imageView,report_setting_imageView;
        Button subscribe_button,comment_button;
        ProgressBar statusBar;
        CirclePageIndicator indicator;
        ViewPager mPager;

        public MyViewHolder(View v) {
            super(v);
            view = v;
            time_textView = view.findViewById(R.id.time_textView);
            detail_textView = view.findViewById(R.id.detail_textView);
            creator_textView = view.findViewById(R.id.creator_textView);
            statusBar = view.findViewById(R.id.statusBar);
            user_imageView = view.findViewById(R.id.user_imageView);
            subscribe_button = view.findViewById(R.id.subscribe_button);
            comment_button = view.findViewById(R.id.comment_button);
            mPager = view.findViewById(R.id.image_pager);
            indicator = view.findViewById(R.id.indicator);
            report_setting_imageView = view.findViewById(R.id.report_setting_imageView);
            place_textView = view.findViewById(R.id.place_textView);
        }
    }
}
