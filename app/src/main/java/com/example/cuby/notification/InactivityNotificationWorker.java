package com.example.cuby.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.cuby.R;
import com.example.cuby.ui.onboarding.OnboardingActivity;

import java.util.concurrent.TimeUnit;

public class InactivityNotificationWorker extends Worker {

    private static final String CHANNEL_ID = "inactivity_channel";
    private static final String PREFS = "inactivity_prefs";
    private static final String KEY_LAST_ACTIVE = "last_active_time";
    private static final String WORK_NAME = "INACTIVITY_WORK";

    // EXACTLY 8 HOURS
    private static final long INACTIVITY_TIME =
            TimeUnit.HOURS.toMillis(8);

    public InactivityNotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context context = getApplicationContext();
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        long lastActive = prefs.getLong(KEY_LAST_ACTIVE, 0);
        long now = System.currentTimeMillis();

        // If user is still active, do nothing
        if (lastActive == 0 || now - lastActive < INACTIVITY_TIME) {
            scheduleNext();
            return Result.success();
        }

        // Open app when notification is clicked
        Intent intent = new Intent(context, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Inactivity Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Cuby")
                        .setContentText("Hi my friend! How are you?")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        manager.notify(1, builder.build());

        // Schedule next inactivity check
        scheduleNext();
        return Result.success();
    }

    private void scheduleNext() {
        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(InactivityNotificationWorker.class)
                        .setInitialDelay(INACTIVITY_TIME, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork(
                        WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        request
                );
    }
}
