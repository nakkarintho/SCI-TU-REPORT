package com.coldzify.finalproject.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.coldzify.finalproject.CommentActivity;
import com.coldzify.finalproject.OneReportActivity;
import com.coldzify.finalproject.R;
import com.coldzify.finalproject.ReportActivity;
import com.coldzify.finalproject.dataobject.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService  extends FirebaseMessagingService {
    private static String TAG = "FirebaseMessagingService";
    private FirebaseAuth mAuth;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            return;
        }
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0 && remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String,String> data = remoteMessage.getData();
            String type = data.get("type");
            if(type != null){
                Intent report_intent = new Intent(this, OneReportActivity.class);
                Intent comment_intent = new Intent(this, CommentActivity.class);
                if(type.equals("status")){
                    sendNotification(remoteMessage.getNotification().getBody(),data,report_intent);
                }
                else if(type.equals("comment") && mAuth.getCurrentUser() != null){
                    String commenter = data.get("commenter");

                    sendNotification(remoteMessage.getNotification().getBody(),data,comment_intent);
                }
                else if(type.equals("reportProblem")){

                    sendNotification(remoteMessage.getNotification().getBody(),data,report_intent);
                }
                else if(type.equals("room")){

                    sendNotification(remoteMessage.getNotification().getBody(),data,report_intent);
                }
            }



        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    private void sendNotification(String message,Map<String,String> data,Intent intent){
        String reportID = data.get("reportID");
        intent.putExtra("reportID",reportID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"report_notifications")
                .setSmallIcon(R.drawable.ic_white_logo)
                .setColor(ContextCompat.getColor(getApplicationContext(),R.color.color_titleBar_bg))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(getRandomId(), notificationBuilder.build());
    }

    private int getRandomId(){
        Random rand = new Random();
        return rand.nextInt(100);
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    private void sendRegistrationToServer(String token) {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(uid == null)
            return;
        db.collection("users").document(uid)
                .update("tokenId",token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Update token success");
                        }
                        else{
                            Log.d(TAG,"error ",task.getException());
                        }
                    }
                });
    }
}
