package com.example.cuby.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("⏰ Alarm")
                        .setContentText("Your alarm is ringing!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true);

        manager.notify(1000, builder.build());

        return Result.success();
    }
}
