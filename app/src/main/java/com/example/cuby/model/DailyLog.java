package com.example.cuby.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_log", indices = {@Index(value = "date")})
public class DailyLog {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date; // yyyy-MM-dd
    public String mood;
    public String reflectionNote;
    public boolean seedPlanted;
    public String plantType;
    public String drawingPath;
    public long lastUpdated;
}
