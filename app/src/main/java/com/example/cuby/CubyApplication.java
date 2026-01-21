package com.example.cuby;

import android.app.Application;
import androidx.room.Room;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.cuby.data.AppDatabase;
import com.example.cuby.workers.NotificationWorker;
import java.util.concurrent.TimeUnit;

public class CubyApplication extends Application {
    
    private static final String NOTIFICATION_WORK_TAG = "cuby_daily_checkin";
    
    private static CubyApplication instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cuby_database")
                .fallbackToDestructiveMigration()
                .build();
                
        scheduleNotifications();
    }
    
    private void scheduleNotifications() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();
        
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                NotificationWorker.class, 24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(NOTIFICATION_WORK_TAG)
                .build();
        
        // Use enqueueUniquePeriodicWork to prevent duplicate workers
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                NOTIFICATION_WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest);
    }

    public static CubyApplication getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
