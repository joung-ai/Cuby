package com.example.cuby.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.cuby.model.DiaryEntry;
import androidx.lifecycle.LiveData;
import java.util.List;

@Dao
public interface DiaryDao {
    @Query("SELECT * FROM diary_entry ORDER BY date DESC, createdAt DESC")
    LiveData<List<DiaryEntry>> getAllEntries();

    @Query("SELECT * FROM diary_entry WHERE date = :date")
    LiveData<List<DiaryEntry>> getEntriesForDate(String date);

    @Insert
    void insert(DiaryEntry entry);

    @Update
    void update(DiaryEntry entry);

    @Delete
    void delete(DiaryEntry entry);
}
