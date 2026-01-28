package com.example.cuby.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cuby.R;

public class ReminderWorker extends Worker {

    public static final String CHANNEL_ID = "ALARM_SOUND_CHANNEL";
    private static final int NOTIFICATION_ID = 1000;

    public ReminderWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context context = getApplicationContext();

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 🔊 CHANNEL WITH ALARM SOUND
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Sound Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setSound(alarmUri, audioAttributes);
            channel.enableVibration(true);
            channel.enableLights(true);

            manager.createNotificationChannel(channel);
        }

        // 🛑 CANCEL ACTION (THIS IS THE ONLY ADDITION)
        Intent cancelIntent = new Intent(context, AlarmCancelReceiver.class);
        PendingIntent cancelPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        0,
                        cancelIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("⏰ Alarm")
                        .setContentText("Your alarm is ringing!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setOngoing(true)
                        .addAction(0, "CANCEL", cancelPendingIntent);

        manager.notify(NOTIFICATION_ID, builder.build());

        return Result.success();
    }
}
