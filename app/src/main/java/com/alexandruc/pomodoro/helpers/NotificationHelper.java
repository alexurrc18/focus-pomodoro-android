package com.alexandruc.pomodoro.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alexandruc.pomodoro.R;
import com.alexandruc.pomodoro.activities.HomeActivity;

public class NotificationHelper {

    private final String CHANNEL_ID;
    private final int notificationId;
    private final Context context;

    public NotificationHelper(String CHANNEL_ID, int notificationId, Context context) {
        this.context = context;
        this.CHANNEL_ID = CHANNEL_ID;
        this.notificationId = notificationId;
    }

    public void createNotificationChannel() {
        CharSequence name = context.getString(R.string.app_name);
        String description = "Focus notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(int title, int content) {
        String notifTitle = context.getString(title);
        String notifContent = context.getString(content);

        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notifTitle)
                .setContentText(notifContent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
