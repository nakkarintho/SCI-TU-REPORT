package com.coldzify.finalproject;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coldzify.finalproject.Dialog.AddListDialog;
import com.coldzify.finalproject.dataobject.Checklist;
import com.coldzify.finalproject.dataobject.ChecklistItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class OneChecklistActivity extends AppCompatActivity {
    private final String TAG = "OneChecklistActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TitleBarView titleBar;
    private RecyclerView uncheck_recyclerView,checked_recyclerView;
    private ChecklistItemAdapter unChecklistAdapter,checkedlistAdapter;
    private ArrayList<ChecklistItem> unchecked_list,checked_list;


    private LinearLayout finish_header_layout;
    private TextView finish_textView;
    private AddListDialog.OnClickOKListener okListener;
    private AddListDialog addListDialog;
    private String checklist_id,checklist_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_checklist);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        titleBar = findViewById(R.id.titleBar);
        finish_header_layout = findViewById(R.id.finish_header_layout);
        finish_textView = findViewById(R.id.finish_textView);
        uncheck_recyclerView = findViewById(R.id.uncheck_recyclerView);
        checked_recyclerView = findViewById(R.id.checked_recyclerView);
        uncheck_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checked_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checked_list = new ArrayList<>();
        unchecked_list = new ArrayList<>();

        unChecklistAdapter = new ChecklistItemAdapter(unchecked_list);
        uncheck_recyclerView.setAdapter(unChecklistAdapter);
        checkedlistAdapter = new ChecklistItemAdapter(checked_list);
        checked_recyclerView.setAdapter(checkedlistAdapter);

        if(getIntent().getExtras() != null){
            checklist_id = getIntent().getExtras().getString("checklist_id");
            checklist_name = getIntent().getExtras().getString("checklist_name");
            titleBar.setTitle("รายการ : "+checklist_name);
            getChecklistItems();
        }


        okListener = new AddListDialog.OnClickOKListener() {
            @Override
            public void onClickOK(String name,String type) {
                ChecklistItem checklistItem = new ChecklistItem(name,type);
                addChecklistItem(checklistItem);
                //unchecked_list.add(str);
            }
        };

        if (savedInstanceState != null) {
            addListDialog = (AddListDialog) getSupportFragmentManager()
                    .findFragmentByTag("Add list dialog");
            if (addListDialog != null) {
                addListDialog.setOnClickOKListener(okListener);
            }
        }

    }


    private void getChecklistItems(){
        db.collection("checklists").document(checklist_id).collection("checklistItems")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:{
                                    ChecklistItem checklist = dc.getDocument().toObject(ChecklistItem.class);
                                    Log.d(TAG, "New Checklist: " + checklist.getItemName());
                                    if(!checklist.isCheck()){
                                        unchecked_list.add(checklist);
                                    }
                                    else{
                                        checked_list.add(checklist);
                                    }
                                    //checklists.add(checklist);
                                    //adapter.notifyItemInserted(checklists.size()-1);
                                    break;
                                }

                                case MODIFIED:{

                                    ChecklistItem checklist = dc.getDocument().toObject(ChecklistItem.class);



                                    Log.d(TAG, "Modified Checklist: " + checklist.getItemName());
                                    if(!checklist.isCheck()){
                                        boolean duplicate = false;
                                        for(int i =0 ; i< unchecked_list.size() ; i++){
                                            if(checklist.getId().equals(unchecked_list.get(i).getId())){
                                                duplicate = true;
                                                break;
                                            }
                                        }
                                        if(!duplicate){
                                            unchecked_list.add(checklist);
                                            unChecklistAdapter.notifyItemInserted(unchecked_list.size()-1);
                                            for(int i = 0 ; i < checked_list.size() ; i++){
                                                ChecklistItem c = checked_list.get(i);
                                                if(checklist.getId().equals(c.getId())){
                                                    checked_list.remove(c);
                                                    checkedlistAdapter.notifyItemRemoved(i);
                                                }
                                            }
                                        }

                                    }
                                    else{

                                        checked_list.add(checklist);
                                        checkedlistAdapter.notifyItemInserted(checked_list.size()-1);
                                        for(int i = 0 ; i < unchecked_list.size() ; i++){
                                            ChecklistItem c = unchecked_list.get(i);
                                            if(checklist.getId().equals(c.getId())){
                                                unchecked_list.remove(c);
                                                unChecklistAdapter.notifyItemRemoved(i);
                                            }
                                        }
                                    }
                                    break;
                                }

                                case REMOVED:{

                                    ChecklistItem checklist = dc.getDocument().toObject(ChecklistItem.class);
                                    Log.d(TAG, "Removed Checklist: " +checklist.getItemName());
                                    if(checklist.isCheck()){
                                        for(int i = 0; i < checked_list.size();i++){
                                            ChecklistItem c = checked_list.get(i);
                                            if(checklist.getId().equals(c.getId())){
                                                checked_list.remove(c);
                                                checkedlistAdapter.notifyItemRemoved(i);
                                                break;
                                            }
                                        }
                                    }else{
                                        for(int i = 0; i < unchecked_list.size();i++){
                                            ChecklistItem c = unchecked_list.get(i);
                                            if(checklist.getId().equals(c.getId())){
                                                unchecked_list.remove(c);
                                                unChecklistAdapter.notifyItemRemoved(i);
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }

                            }

                            //adapter.notifyDataSetChanged();
                        }
                        if(checked_list.size() != 0){
                            finish_header_layout.setVisibility(View.VISIBLE);
                        }
                        else{
                            finish_header_layout.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void addChecklistItem(ChecklistItem checklistItem){
        DocumentReference ref = db.collection("checklists").document(checklist_id)
                .collection("checklistItems")
                .document();
        Map<String,Object> map = checklistItem.toMap();
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("id",ref.getId());
        ref.set(map);
    }
    public void deleteChecklistItem(String id){
        db.collection("checklists").document(checklist_id)
                .collection("checklistItems")
                .document(id)
                .delete();
    }
    public void checkItem(String item_id){
        db.collection("checklists").document(checklist_id)
                .collection("checklistItems")
                .document(item_id)
                .update("check",true);
    }
    public void unCheckItem(ChecklistItem item){
        db.collection("checklists").document(checklist_id)
                .collection("checklistItems")
                .document(item.getId())
                .update("check",false);
    }
    public void onClickAddList(View view){
        addListDialog = new AddListDialog();
        addListDialog.setOnClickOKListener(okListener);
        addListDialog.show(getSupportFragmentManager(),"Add list dialog");
    }
    public void onClickReset(View view){

        for(ChecklistItem item  : checked_list){
            unCheckItem(item);
        }
    }

    class ChecklistItemAdapter extends RecyclerView.Adapter<OneChecklistActivity.MyHolder>{

        private ArrayList<ChecklistItem> list;
        public ChecklistItemAdapter(ArrayList<ChecklistItem> list){
            this.list = list;
        }
        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_row_item,parent,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
            holder.ch.setText(list.get(i).getItemName());
            final String item_id = list.get(i).getId();
            final int index  = holder.getAdapterPosition();
            holder.ch.setOnCheckedChangeListener(null);
            holder.ch.setChecked(false);
            if(!list.get(i).isCheck()){
                holder.ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        //Log.d(TAG,"checked item : "+list.get(index).getItemName());
                        //list.get(index).setCheck(b);
                        checkItem(item_id);
                        //notifyDataSetChanged();
                    }
                });
            }
            else{
                holder.ch.setButtonDrawable(R.drawable.ic_done_red);
            }
            holder.report_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.view.getContext(),ReportActivity.class);
                    intent.putExtra("type",list.get(index).getType());
                    holder.view.getContext().startActivity(intent);
                }
            });
            holder.delete_imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(holder.view.getContext())
                            //.setTitle("ลบรายการ")
                            .setMessage("คุณต้องการลบรายการ '"+list.get(holder.getAdapterPosition()).getItemName()+"' ใช่หรือไม่?")
                            .setNegativeButton("ใช่", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Log.d(TAG,"delete checklist : "+list.get(holder.getAdapterPosition()).getItemName());
                                    deleteChecklistItem(item_id);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setPositiveButton("ยกเลิก", null)
                            //.setIcon(android.R.drawable.ic_dialog_dialer)
                            .show();
                }
            });

        }


        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    static class MyHolder extends RecyclerView.ViewHolder {

        View view ;
        CheckBox ch;
        ImageView report_imageView,delete_imageView;
        MyHolder(View v) {
            super(v);
            view = v;
            ch = view.findViewById(R.id.checkBox);
            report_imageView = view.findViewById(R.id.report_imageView);
            delete_imageView = view.findViewById(R.id.delete_imageView);
            ch.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.my_font));
        }
    }


}
