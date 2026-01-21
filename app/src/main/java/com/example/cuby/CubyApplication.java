package com.example.cuby;

import android.app.Application;
import androidx.room.Room;
import com.example.cuby.data.AppDatabase;

public class CubyApplication extends Application {
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(
            getApplicationContext(),
            AppDatabase.class,
            "cuby_database"
        )
        .fallbackToDestructiveMigration()  // Clears old data on version mismatch
        .build();
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
