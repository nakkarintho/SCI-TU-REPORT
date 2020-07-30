package com.coldzify.finalproject.adapter;

import android.content.Context;
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
import com.coldzify.finalproject.ProfileActivity;
import com.coldzify.finalproject.R;
import com.coldzify.finalproject.dataobject.Comment;
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

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {
    private static final String TAG = "CommentAdapter";
    private Context context;
    private ArrayList<Comment> comments;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public CommentListAdapter(Context context,ArrayList<Comment> comments){
        this.context = context;
        this.comments = comments;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        setCreatorData(holder,i);
        setOnClickListener(holder,i);
    }

    private void setCreatorData(final MyViewHolder holder , final int index){
        String creatorID = comments.get(index).getCommenter();
        db.collection("users").document(creatorID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()&& task.getResult() != null){
                            String picture = task.getResult().getString("picture");
                            String firstname = task.getResult().getString("firstname");
                            String lastname = task.getResult().getString("lastname");
                            String name = firstname+" "+lastname;

                            holder.time_textView.setText(getTimeAgo(comments.get(index).getTimestamp()));
                            holder.comment_detail_textView.setText(comments.get(index).getComment());
                            holder.commenter_textView.setText(name);
                            StorageReference userImageRef = storage.getReference().child("images/")
                                    .child("users/"+picture);
                            GlideApp.with(holder.view)
                                    .load(userImageRef)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(holder.commenter_imageView);
                        }
                    }
                });
    }

    private void setOnClickListener(final MyViewHolder holder,int index){
        final String commenter = comments.get(index).getCommenter();

        holder.commenter_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(), ProfileActivity.class);
                intent.putExtra("uid",commenter);
                holder.view.getContext().startActivity(intent);
            }
        });
        holder.commenter_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(),ProfileActivity.class);
                intent.putExtra("uid",commenter);
                holder.view.getContext().startActivity(intent);
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

    @Override
    public int getItemCount() {
        return comments.size();
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView comment_detail_textView,commenter_textView,time_textView;
        ImageView commenter_imageView;
        MyViewHolder(View v) {
            super(v);
            view = v;
            comment_detail_textView = view.findViewById(R.id.comment_detail_textView);
            commenter_imageView = view.findViewById(R.id.commenter_imageView);
            commenter_textView = view.findViewById(R.id.commenter_textView);
            time_textView = view.findViewById(R.id.time_textView);
        }
    }
}
