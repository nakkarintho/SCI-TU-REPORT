package com.coldzify.finalproject;
import android.app.Application;
import android.app.job.JobInfo;
import android.content.Context;
import android.widget.Toast;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

@AcraCore(buildConfigClass = BuildConfig.class,
        reportFormat = StringFormat.JSON)
@AcraHttpSender(uri = "https://asia-northeast1-scitu-problemboy-uvigpa.cloudfunctions.net/ACRAAdapter",
        httpMethod = HttpSender.Method.POST)
@AcraToast(resText=R.string.acra_toast_text,
        length = Toast.LENGTH_LONG)
@AcraScheduler(requiresNetworkType = JobInfo.NETWORK_TYPE_UNMETERED,
        requiresBatteryNotLow = true)
@AcraLimiter
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}