package com.coldzify.finalproject.Dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.coldzify.finalproject.GlideApp;
import com.coldzify.finalproject.LocationHandle;
import com.coldzify.finalproject.OneReportActivity;
import com.coldzify.finalproject.R;
import com.coldzify.finalproject.dataobject.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DuplicateReportDialog extends DialogFragment {
    private String report_ID;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private TextView creator_textView,time_textView,detail_textView,place_textView;
    private ImageView user_imageView,report_imageView;
    private Button continue_button,goReport_button;
    private ProgressBar progressBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        if(getArguments() != null){
            //checklist_name = getArguments().getString("checklist_name");
            report_ID = getArguments().getString("reportID");
        }


    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_duplicate_report,null);
        creator_textView= view.findViewById(R.id.creator_textView);
        time_textView = view.findViewById(R.id.time_textView);
        detail_textView = view.findViewById(R.id.detail_textView);
        place_textView = view.findViewById(R.id.place_textView);
        user_imageView = view.findViewById(R.id.user_imageView);
        report_imageView = view.findViewById(R.id.report_imageView);
        continue_button = view.findViewById(R.id.continue_button);
        goReport_button = view.findViewById(R.id.goReport_button);
        progressBar = view.findViewById(R.id.progressBar);
        //editText = view.findViewById(R.id.editText);
        getReportData();
        setButton();

        builder.setView(view);
        return builder.create();
    }

    private void getReportData(){
        db.collection("reports").document(report_ID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Report report = task.getResult().toObject(Report.class);
                            if(report != null){
                                String detail = report.getDetail();
                                String creatorID = report.getCreatorID();
                                Timestamp timestamp = report.getTimestamp();
                                List<String> pictures = report.getPictures();
                                String place = LocationHandle.locationCodeToString(report.getPlaceCode());
                                String room = report.getRoom();
                                if(room != null&& !room.equals(""))
                                    place+= " "+room;
                                getCreatorData(creatorID);
                                setReportImage(pictures.get(0));
                                detail_textView.setText(detail);
                                time_textView.setText(getTimeAgo(timestamp));
                                place_textView.setText(place);
                            }

                        }
                    }
                });
    }
    private void getCreatorData(String uid){
        db.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot doc = task.getResult();
                            String firstname = doc.getString("firstname");
                            String lastname = doc.getString("lastname");
                            String name = firstname + " "+lastname;
                            String picture = doc.getString("picture");
                            creator_textView.setText(name);
                            setUserImage(picture);
                        }
                    }
                });
    }


    private void setReportImage(String picture){
        if(getContext() == null)
            return;
        StorageReference reportImageRef = storage.getReference().child("images/")
                .child("reports/"+picture);
        GlideApp.with(getContext())
                .load(reportImageRef)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(report_imageView);
    }
    private void setUserImage(String picture){
        if(getContext() == null)
            return;
        StorageReference userImageRef = storage.getReference().child("images/")
                .child("users/"+picture);
        GlideApp.with(getContext())
                .load(userImageRef)
                .apply(RequestOptions.circleCropTransform())
                .into(user_imageView);
    }

    private void setButton(){
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        goReport_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OneReportActivity.class);
                intent.putExtra("reportID",report_ID);
                startActivity(intent);
            }
        });
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
    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
