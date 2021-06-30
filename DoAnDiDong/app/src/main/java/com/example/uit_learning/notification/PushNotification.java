package com.example.uit_learning.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.uit_learning.R;

public class PushNotification {
    SharedPreferences preferences;
    Boolean isEnabled;
    Context context;

    NotificationManager manager;

    public PushNotification(Context context) {
        this.context = context;
        preferences = context.getApplicationContext().getSharedPreferences("NOTIFICATION_PREFS", Context.MODE_PRIVATE);
        isEnabled = preferences.getBoolean("NOTIFICATION_ENABLE", true);
    }

    public Notification getNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Learning");
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.ic_notifications_24);
        return builder.build();
    }

    public boolean isEnableNotification() {
        return isEnabled;
    }
    public NotificationManager getManager() {
        if(manager == null) {
            manager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}
