package com.example.cuby.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.cuby.model.DailyLog;
import java.util.List;

@Dao
public interface DailyLogDao {
    @Query("SELECT * FROM daily_log WHERE date = :date LIMIT 1")
    LiveData<DailyLog> getDailyLog(String date);
    
    @Query("SELECT * FROM daily_log WHERE date = :date LIMIT 1")
    DailyLog getDailyLogSync(String date);

    @Query("SELECT * FROM daily_log WHERE date LIKE :monthPattern ORDER BY date")
    LiveData<List<DailyLog>> getLogsForMonth(String monthPattern);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyLog log);
}
