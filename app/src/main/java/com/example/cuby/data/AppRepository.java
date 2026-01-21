package com.example.cuby.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.cuby.CubyApplication;
import com.example.cuby.model.DailyLog;
import com.example.cuby.model.DiaryEntry;
import com.example.cuby.model.Inventory;
import com.example.cuby.model.UserProfile;

public class AppRepository {
    private static AppRepository instance;
    
    private final UserDao userDao;
    private final DailyLogDao dailyLogDao;
    private final DiaryDao diaryDao;
    private final InventoryDao inventoryDao;
    
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private AppRepository(Application application) {
        AppDatabase db = ((CubyApplication) application).getDatabase();
        userDao = db.userDao();
        dailyLogDao = db.dailyLogDao();
        diaryDao = db.diaryDao();
        inventoryDao = db.inventoryDao();
    }

    public static synchronized AppRepository getInstance(Application application) {
        if (instance == null) {
            instance = new AppRepository(application);
        }
        return instance;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    // User Operations
    public LiveData<UserProfile> getUserProfile() {
        return userDao.getUserProfile();
    }
    
    public UserProfile getUserProfileSync() {
        return userDao.getUserProfileSync();
    }

    public void insertUserProfile(UserProfile userProfile) {
        executor.execute(() -> userDao.insert(userProfile));
    }

    // DailyLog Operations
    public LiveData<DailyLog> getDailyLog(String date) {
        return dailyLogDao.getDailyLog(date);
    }
    
    public DailyLog getDailyLogSync(String date) {
        return dailyLogDao.getDailyLogSync(date);
    }

    public LiveData<List<DailyLog>> getMonthlyLogs(String monthPattern) {
        return dailyLogDao.getLogsForMonth(monthPattern);
    }

    public void insertDailyLog(DailyLog log) {
        executor.execute(() -> dailyLogDao.insert(log));
    }

    // Diary Operations
    public LiveData<List<DiaryEntry>> getAllDiaryEntries() {
        return diaryDao.getAllEntries();
    }

    public LiveData<List<DiaryEntry>> getDiaryEntriesForDate(String date) {
        return diaryDao.getEntriesForDate(date);
    }

    public void insertDiaryEntry(DiaryEntry entry) {
        executor.execute(() -> diaryDao.insert(entry));
    }

    public void updateDiaryEntry(DiaryEntry entry) {
        executor.execute(() -> diaryDao.update(entry));
    }

    public void deleteDiaryEntry(DiaryEntry entry) {
        executor.execute(() -> diaryDao.delete(entry));
    }

    // Inventory Operations
    public LiveData<Inventory> getInventory() {
        return inventoryDao.getInventory();
    }
    
    public Inventory getInventorySync() {
        return inventoryDao.getInventorySync();
    }

    public void insertInventory(Inventory inventory) {
        executor.execute(() -> inventoryDao.insert(inventory));
    }
    
    public void addFood(int amount) {
        executor.execute(() -> {
            Inventory inv = inventoryDao.getInventorySync();
            if (inv == null) {
                inv = new Inventory();
                inv.bloxyFoodCount = 0;
            }
            inv.bloxyFoodCount += amount;
            inventoryDao.insert(inv);
        });
    }
    
    public void consumeFood(int amount) {
        executor.execute(() -> {
            Inventory inv = inventoryDao.getInventorySync();
            if (inv != null && inv.bloxyFoodCount >= amount) {
                inv.bloxyFoodCount -= amount;
                inventoryDao.insert(inv);
            }
        });
    }
}
