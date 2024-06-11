package com.example.emotionapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.emotionapp.R;


public class AlarmBroadcast extends BroadcastReceiver {
    private static final String CHANNEL_ID = "id";
    private static final String CHANNEL_NAME = "name";
    private static final String CHANNEL_DESC = "des";
    Uri alarmSound;
    public static MediaPlayer mp;
    private static final String TAG = "AlarmBroadcast";

    public AlarmBroadcast() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        String data =intent.getStringExtra("data");
        Long time=intent.getLongExtra("time",System.currentTimeMillis());
        ScheduleModel model=new ScheduleModel(data,time);
        mp = MediaPlayer.create(context, R.raw.alarm);
        mp.setLooping(true);
        Log.e(TAG, "onReceive: alarm call: "+data);

        StartSound();

        AppNotification appNotification = new AppNotification(context);
        appNotification.showNotification(model, "");
    }

    public void StartSound() {
//        Log.i(TAG, "StartSound: " + mp);

        mp.setVolume(8f, 8f);
        mp.start();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                StopSound();
            }
        }, 5000);
    }

    public void StopSound() {
//        Log.i(TAG, "StopSound: Map Value call " + mp);
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }
}    