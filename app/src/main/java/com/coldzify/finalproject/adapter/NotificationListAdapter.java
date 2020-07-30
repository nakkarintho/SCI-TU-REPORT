package com.coldzify.finalproject.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.coldzify.finalproject.GlideApp;
import com.coldzify.finalproject.OneReportActivity;
import com.coldzify.finalproject.R;
import com.coldzify.finalproject.dataobject.Notifications;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder>{
    private ArrayList<Notifications> notis;
    //private ArrayList<String> notis_ID;
    private FirebaseFirestore db;
    private FirebaseStorage  storage;

    public NotificationListAdapter(ArrayList<Notifications> notis) {
        this.notis = notis;
        //this.notis_ID = notis_ID;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noti_row_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {
        setNotificationData(holder,i);
        setOnClickListener(holder,i);
    }

    private void setNotificationData(final MyViewHolder holder , final int index){
        final String message = notis.get(index).getMessage();
        final Timestamp timestamp = notis.get(index).getTimestamp();

        holder.message_textView.setText(message);
        holder.time_textView.setText(getTimeAgo(timestamp));

        String commenter = notis.get(index).getCommenter();
        if(commenter == null || commenter.equals("")){
            holder.message_textView.setText(message);
            holder.time_textView.setText(getTimeAgo(timestamp));
            GlideApp.with(holder.view)
                    .load(R.drawable.ic_logo_red)
                    .into(holder.noti_imageView);
        }
        else{
            db.collection("users").document(commenter)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()&& task.getResult() != null){
                                String picture = task.getResult().getString("picture");
                                holder.message_textView.setText(message);
                                holder.time_textView.setText(getTimeAgo(timestamp));
                                StorageReference userImageRef = storage.getReference().child("images/")
                                        .child("users/"+picture);
                                GlideApp.with(holder.view)
                                        .load(userImageRef)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(holder.noti_imageView);
                            }
                        }
                    });
        }

    }
    private void setOnClickListener(final MyViewHolder holder,final int i){
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(), OneReportActivity.class);
                intent.putExtra("reportID",notis.get(i).getReportID());
                holder.view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notis.size();
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



    static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView message_textView,time_textView;
        ImageView noti_imageView;
        MyViewHolder(View v) {
            super(v);
            view = v;
            message_textView = view.findViewById(R.id.message_textView);
            noti_imageView = view.findViewById(R.id.noti_imageView);
            time_textView = view.findViewById(R.id.time_textView);
        }
    }
}
