package com.example.emotionapp.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.emotionapp.R;
import com.example.emotionapp.Utils;
import com.example.emotionapp.activity.SplashActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppNotification {
    private static final int SNOOZE_INTERVAL_1 = 10 * 60 * 1000; // 10 minutes
    private static final int SNOOZE_INTERVAL_2 = 5 * 60 * 1000;
    private static Context mContext;
    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "Channel Name";
    private static final String CHANNEL_DESCRIPTION = "Channel Description";

    public final NotificationManagerCompat mNotificationManager;
    public String title, message;

    public AppNotification(Context context) {
        mContext = context;
        mNotificationManager = NotificationManagerCompat.from(mContext);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    public void showNotification(ScheduleModel data, String message) {
        // Create intents for the button actions
        Intent button1Intent = new Intent(mContext, Button1Receiver.class);
        PendingIntent button1PendingIntent = PendingIntent.getBroadcast(mContext, 100, button1Intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

        Intent button2Intent = new Intent(mContext, Button2Receiver.class);
        button2Intent.putExtra("data", data);
        PendingIntent button2PendingIntent = PendingIntent.getBroadcast(mContext, 0, button2Intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(CHANNEL_DESCRIPTION);
        }
        long vibrate[] = {1000, 1000, 1000, 1000, 1000};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_calendar_month_24)
                .setContentTitle("Emotion App \n"+data.getDetails())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(data.getDetails())
                .setVibrate(vibrate)
                .setContentIntent(button2PendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(data.hashCode(), builder.build());
    }

    // Define the button action receivers
    public static class Button1Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle button 1 click action here
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(10);
        }
    }

    public static class Button2Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle button 2 click action here
            Bundle bundle = intent.getExtras();

            ScheduleModel scheduleResponse = (ScheduleModel) bundle.getSerializable("data");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(scheduleResponse.hashCode());
            Intent intent1 = new Intent(context, SplashActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent1.putExtra("data", scheduleResponse);
            context.startActivity(intent1);

        }


    }

}
