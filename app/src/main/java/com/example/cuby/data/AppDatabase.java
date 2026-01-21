package com.example.cuby.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.cuby.model.*;

@Database(entities = {UserProfile.class, DailyLog.class, DiaryEntry.class, Inventory.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract DailyLogDao dailyLogDao();
    public abstract DiaryDao diaryDao();
    public abstract InventoryDao inventoryDao();
}
