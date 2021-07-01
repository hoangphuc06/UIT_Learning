package com.example.uit_learning.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.uit_learning.PostDetailActivity;
import com.example.uit_learning.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private SharedPreferences preferences;
    private NotificationManager manager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");
        String postId = remoteMessage.getData().get("postId");

        preferences = getSharedPreferences("NOTIFICATION_PREFS", MODE_PRIVATE);
        if(preferences.getBoolean("NOTIFICATION_ENABLE", false)) {
            createNotificationChannel();
            Intent notification_intent = new Intent(getApplicationContext(), PostDetailActivity.class);
            notification_intent.putExtra("postId", postId);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntentWithParentStack(notification_intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(getNotificationId(), PendingIntent.FLAG_UPDATE_CURRENT);

//            notification_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this,getNotificationId()
//                    ,notification_intent,PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(getApplicationContext(), "Learning")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_notifications_24)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    getManager().notify(getNotificationId(), notification);
                }
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Learning", "UIT_Learning", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    public NotificationManager getManager() {
        if(manager == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager = getSystemService(NotificationManager.class);
            }
        }
        return manager;
    }
}
