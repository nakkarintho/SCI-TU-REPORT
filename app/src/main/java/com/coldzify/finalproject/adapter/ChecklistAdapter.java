package com.coldzify.finalproject.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coldzify.finalproject.ChecklistActivity;
import com.coldzify.finalproject.OneChecklistActivity;
import com.coldzify.finalproject.R;
import com.coldzify.finalproject.dataobject.Checklist;
import com.coldzify.finalproject.dataobject.ChecklistItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.MyViewHolder> {
    private final String TAG = "ChecklistAdapter";
    private ArrayList<Checklist> checklists;
    private FirebaseFirestore db;
    private ChecklistActivity.OnClickChecklistListener listener;
    public ChecklistAdapter(ArrayList<Checklist> checklists){
        this.checklists = checklists;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checklist_row_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull MyViewHolder holder, int i) {
        checkSuccess(holder);
        holder.checklist_name_textView.setText(checklists.get(i).getListName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.view.getContext(), OneChecklistActivity.class);
                intent.putExtra("checklist_id",checklists.get(holder.getAdapterPosition()).getId());
                intent.putExtra("checklist_name",checklists.get(holder.getAdapterPosition()).getListName());
                holder.view.getContext().startActivity(intent);
            }
        });
        if(listener != null){
            holder.delete_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(holder.view.getContext())
                            //.setTitle("ลบรายการ")
                            .setMessage("คุณต้องการลบรายการ '"+checklists.get(holder.getAdapterPosition()).getListName()+"' ใช่หรือไม่")
                            .setNegativeButton("ใช่", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG,"delete checklist : "+checklists.get(holder.getAdapterPosition()).getListName());
                                    listener.onClickDelete(checklists.get(holder.getAdapterPosition()));
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setPositiveButton("ยกเลิก", null)
                            //.setIcon(android.R.drawable.ic_dialog_dialer)
                            .show();
                }
            });
            holder.edit_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickEdit(checklists.get(holder.getAdapterPosition()));
                }
            });

        }
    }
    private void checkSuccess(final MyViewHolder holder){
        int index = holder.getAdapterPosition();
        db.collection("checklists").document(checklists.get(index).getId())
                .collection("checklistItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult()!= null){
                            boolean allCheck = true;
                            for(DocumentSnapshot doc : task.getResult()){
                                ChecklistItem item = doc.toObject(ChecklistItem.class);
                                if(!item.isCheck())
                                    allCheck = false;
                            }
                            if(allCheck && task.getResult().size() != 0)
                                holder.status_imageView.setImageResource(R.drawable.ic_done_green);
                            else
                                holder.status_imageView.setImageResource(R.drawable.ic_time);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return checklists.size();
    }

    public void setOnDeleteItemListener(ChecklistActivity.OnClickChecklistListener listener){
        this.listener = listener;

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView checklist_name_textView;
        ImageView status_imageView,delete_imageView,edit_imageView;
        MyViewHolder(View v) {
            super(v);
            view = v;

            checklist_name_textView = view.findViewById(R.id.checklist_name_textView);
            status_imageView = view.findViewById(R.id.status_imageView);
            delete_imageView = view.findViewById(R.id.delete_imageView);
            edit_imageView= view.findViewById(R.id.edit_imageView);
        }
    }

}
