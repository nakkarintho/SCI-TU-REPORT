package com.coldzify.finalproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.coldzify.finalproject.Dialog.EditChecklistDialog;
import com.coldzify.finalproject.adapter.ChecklistAdapter;
import com.coldzify.finalproject.dataobject.Checklist;
import com.coldzify.finalproject.dataobject.ChecklistItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nullable;

public class ChecklistActivity extends AppCompatActivity {
    private final String TAG = "ChecklistActivity";
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ChecklistAdapter adapter;
    private LinearLayout addList_linearLayout;

    private EditText checklistName_editText;
    private FirebaseAuth mAuth;
    private ArrayList<Checklist> checklists;
    private OnClickChecklistListener onClickChecklistListener;
    private int n;
    public static ArrayList<ChecklistItem> default_checklistItem =new ArrayList<>(Arrays.asList(
            new ChecklistItem("เปิด/ปิดแอร์",ProblemType.MATERIAL.name()),
            new ChecklistItem("เพิ่มกระดาษ"),
            new ChecklistItem("เช็คปากกา"),
            new ChecklistItem("เช็คโปรเจคเตอร์",ProblemType.MATERIAL.name()),
            new ChecklistItem("เตรียมความพร้อมคอมพิวเตอร์",ProblemType.MATERIAL.name()),
            new ChecklistItem("เตรียมน้ำดื่ม")
    ));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setHasFixedSize(true);
        addList_linearLayout = findViewById(R.id.addList_linearLayout);
        //main_layout = findViewById(R.id.main_layout);

        checklistName_editText = findViewById(R.id.checklistName_editText);

        checklists = new ArrayList<>();
        adapter = new ChecklistAdapter(checklists);
        recyclerView.setAdapter(adapter);
        getChecklist();

        onClickChecklistListener = new OnClickChecklistListener() {
            @Override
            public void onClickDelete(Checklist checklist) {
                deleteItem(checklist);
            }


            @Override
            public void onClickEdit(Checklist checklist) {
                EditChecklistDialog dialog = new EditChecklistDialog();
                dialog.setOnClickSaveListener(new EditChecklistDialog.OnClickSaveListener() {
                    @Override
                    public void onClickSave(String checklist_id,String newName) {
                        updateChecklistName(checklist_id,newName);
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("checklist_name",checklist.getListName());
                bundle.putString("checklist_id",checklist.getId());
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(),"EditChecklistDialog");
            }
        };
        adapter.setOnDeleteItemListener(onClickChecklistListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new ChecklistAdapter(checklists);
        recyclerView.setAdapter(adapter);
        adapter.setOnDeleteItemListener(onClickChecklistListener);
        Log.d(TAG,"refresh");
    }

    private void getChecklist(){

        db.collection("checklists").whereEqualTo("housekeeper",mAuth.getUid())
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
                                    Checklist checklist = dc.getDocument().toObject(Checklist.class);
                                    Log.d(TAG, "New Checklist: " + checklist.getListName());
                                    checklists.add(checklist);
                                    adapter.notifyItemInserted(checklists.size()-1);
                                    break;
                                }

                                case MODIFIED:{
                                    Checklist checklist = dc.getDocument().toObject(Checklist.class);
                                    Log.d(TAG, "Modified Checklist: " + checklist.getListName());
                                    for(int i = 0 ; i< checklists.size() ; i ++){
                                        Checklist c = checklists.get(i);
                                        if(checklist.getId().equals(c.getId())){
                                            c.setListName(checklist.getListName());
                                            adapter.notifyItemChanged(i);
                                            break;
                                        }
                                    }

                                    break;
                                }

                                case REMOVED:{
                                    Log.d(TAG, "Removed Checklist: " + dc.getDocument().toObject(Checklist.class));
                                    Checklist checklist = dc.getDocument().toObject(Checklist.class);
                                    for(int i = 0 ; i < checklists.size() ; i++){
                                        Checklist c = checklists.get(i);
                                        if(checklists.get(i).getId().equals(checklist.getId())){
                                            checklists.remove(c);
                                            adapter.notifyItemRemoved(i);
                                            //adapter.notifyItemRangeChanged(0,adapter.getItemCount()-1);
                                        }
                                    }
                                    break;
                                }

                            }

                            //adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void updateChecklistName(String checklist_id,String newName){
        db.collection("checklists").document(checklist_id)
                .update("listName",newName);
    }
    private void deleteItem(Checklist checklist){
        db.collection("checklists").document(checklist.getId())
                .delete();
    }

    private void addChecklist(Checklist checklist){
        n = 0;

        DocumentReference ref = db.collection("checklists").document();
        final String ref_id = ref.getId();
        Map<String,Object> map = checklist.toMap();
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("id",ref_id);
        ref.set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            initChecklistItem(ref_id);

                        }
                    }
                });
    }

    private void initChecklistItem(String checklist_id){
        for(ChecklistItem item : default_checklistItem){
            DocumentReference ref = db.collection("checklists").document(checklist_id)
                    .collection("checklistItems").document();
            Map<String,Object> map = item.toMap();

            map.put("timestamp",FieldValue.serverTimestamp());
            map.put("id",ref.getId());
            ref.set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                n++;
                                if(n == default_checklistItem.size()){
                                    Log.d(TAG,"init checklist item success!");

                                }
                            }
                        }
                    });
        }
    }


    public void onClickSave(View view){
        String listName = checklistName_editText.getText().toString();
        String housekeeper = mAuth.getUid();
        if(listName.length() == 0){
            return;
        }
        hideAddListLayout();
        addChecklist(new Checklist(listName,housekeeper));
    }


    public void onClickAddList(View view){
        //addList_textView.setVisibility(View.GONE);
        addList_linearLayout.setVisibility(View.VISIBLE);
        checklistName_editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.showSoftInput(checklistName_editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideAddListLayout(){
        addList_linearLayout.setVisibility(View.GONE);
        checklistName_editText.clearFocus();
        checklistName_editText.setText("");
        //addList_textView.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.hideSoftInputFromWindow(checklistName_editText.getWindowToken(), 0);
    }


    public void onClickCancel(View view){
        hideAddListLayout();
    }

    public interface OnClickChecklistListener{
        void onClickDelete(Checklist checklist);
        void onClickEdit(Checklist checklist);
    }
}
