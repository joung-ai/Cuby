package com.example.cuby.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.cuby.model.DailyLog;
import com.example.cuby.model.DiaryEntry;
import com.example.cuby.model.Inventory;
import com.example.cuby.model.UserProfile;

@Database(entities = {UserProfile.class, DailyLog.class, DiaryEntry.class, Inventory.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract DailyLogDao dailyLogDao();
    public abstract DiaryDao diaryDao();
    public abstract InventoryDao inventoryDao();
}
