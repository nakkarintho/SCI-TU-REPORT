package com.coldzify.finalproject.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.coldzify.finalproject.GlideApp;
import com.coldzify.finalproject.R;
import com.coldzify.finalproject.dataobject.Report;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Report> reports;

    private FirebaseStorage storage;


    public CustomAdapter(Context context, ArrayList<Report> reports) {
        this.mContext= context;
        this.reports = reports;

        storage = FirebaseStorage.getInstance();
    }
    @Override
    public int getCount() {
        return reports.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater mInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null) {
            assert mInflater != null;
            view = mInflater.inflate(R.layout.listview_row, viewGroup, false);
        }




        final TextView type_textView = view.findViewById(R.id.time_textView);
        final TextView detail_textView = view.findViewById(R.id.detail_textView);
        final TextView creator_textView = view.findViewById(R.id.creator_textView);
        //final TextView status_textView = view.findViewById(R.id.status_textView);

        //final ImageView report_imageView = view.findViewById(R.id.report_imageView);

        ImageView user_imageView = view.findViewById(R.id.user_imageView);

        type_textView.setText(reports.get(i).getType()+"");
        detail_textView.setText(reports.get(i).getDetail());
        //status_textView.setText(reports.get(i).getStatus()+"");

        getUserData(reports.get(i).getCreatorID(),creator_textView,user_imageView);

        StorageReference imageRef = storage.getReference().child("images/"+reports.get(i).getPictures());

        //GlideApp.with(mContext).load(imageRef).into(report_imageView);
        type_textView.setText(reports.get(i).getType()+"");
        detail_textView.setText(reports.get(i).getDetail());
       // status_textView.setText(reports.get(i).getStatus()+"");
        return view;
    }



    private Bitmap resizeBitmap(Bitmap bitmap,int targetWidth){
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        float ratio = targetWidth*1.0f/imageWidth;
        int targetHeight = (int)(imageHeight*ratio);
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap,targetWidth,targetHeight,false);
        return newBitmap;
    }
    private void getUserData(String id, final TextView textView,final ImageView imageView){
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,picture.type(small)");
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                id,
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        if(object != null){
                            String name = object.optString("name");
                            String urlPic=null;
                            try {
                                urlPic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                GlideApp.with(mContext).load(urlPic).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            textView.setText(name);

                        }
                    }
                }
        );
        request.executeAsync();
    }
}
