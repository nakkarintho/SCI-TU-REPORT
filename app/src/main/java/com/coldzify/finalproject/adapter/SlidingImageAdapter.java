package com.coldzify.finalproject.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.coldzify.finalproject.GlideApp;
import com.coldzify.finalproject.R;
import com.coldzify.finalproject.ReportImageActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class SlidingImageAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> imagesPath;

    private FirebaseStorage storage;
    private boolean useImageName = false;
    public SlidingImageAdapter(Context context, ArrayList<String> imagesPath){
        this.context = context;
        this.imagesPath = imagesPath;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int index) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View imageLayout = inflater.inflate(R.layout.sliding_image_layout, container, false);
        final ImageView imageView =  imageLayout.findViewById(R.id.sliding_imageView);

        if(!useImageName){
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagesPath.get(index)));

        }else{
            StorageReference imageRef = storage.getReference().child("images/")
                .child("reports/"+imagesPath.get(index));
            GlideApp.with(context)
                .load(imageRef)
                .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent image_intent = new Intent(context, ReportImageActivity.class);
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fade_out);
                    image_intent.putExtra("picName",imagesPath.get(index));
                    container.getContext().startActivity(image_intent, options.toBundle());

                }
            });
        }


        container.addView(imageLayout);
        return imageLayout;
    }

    public void deleteItem(int index){
        imagesPath.remove(index);
        notifyDataSetChanged();
    }
    public void setUseImageName(boolean b){
        useImageName = b;
    }



    @Override
    public int getCount() {
        return imagesPath.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }



}
