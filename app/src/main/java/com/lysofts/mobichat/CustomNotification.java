package com.lysofts.mobichat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class CustomNotification{

    public void show(Context context,int notify_id, String text){
        String chanel_id = "101";
        String chanel_name = "chanel101";
        String description = "Mobichat Notification channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,chanel_id)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("MobiChat")
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(chanel_id, chanel_name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }else{
                builder.setDefaults(Notification.DEFAULT_ALL);
            }

            /*Notify*/
            if (notificationManager != null){
                notificationManager.notify(notify_id,builder.build());
            }
        }

    }

}
