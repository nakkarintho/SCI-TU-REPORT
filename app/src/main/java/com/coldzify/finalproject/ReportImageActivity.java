package com.coldzify.finalproject;

import android.graphics.drawable.Drawable;


import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReportImageActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private ProgressBar progressBar;
    private PhotoView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_image);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        progressBar = findViewById(R.id.progressBar);

        photoView = findViewById(R.id.report_photoView);

        if(getIntent().getExtras() !=null){
            String picName = getIntent().getExtras().getString("picName");
            mStorageRef = storage.getReference().child("images/")
                    .child("reports/"+picName);
            glideImage();
        }

    }
    private void glideImage(){
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(mStorageRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
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
                .into(photoView);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's a download in progress, save the reference so you can query it later
        if (mStorageRef != null) {
            outState.putString("reference", mStorageRef.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If there was a download in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        mStorageRef = FirebaseStorage.getInstance().getReference(stringRef);
        glideImage();
    }
}
