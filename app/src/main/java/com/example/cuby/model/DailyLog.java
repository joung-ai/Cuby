package com.example.cuby.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "daily_log", indices = {@Index(value = "date")})
public class DailyLog {
    @PrimaryKey
    @NonNull
    public String date; // ISO yyyy-MM-dd

    public String mood; // HAPPY, SAD, ANXIOUS, etc.
    public String reflectionNote;
    public boolean seedPlanted;
    public String plantType;
    public String drawingPath;
    public long lastUpdated;
    
    public DailyLog(@NonNull String date) {
        this.date = date;
    }
}
