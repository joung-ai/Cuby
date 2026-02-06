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
    public String mood; // CALM, OKAY, TIRED, OVERWHELMED, HAPPY & only made once per day
    public String reflectionNote;
    public boolean seedPlanted;
    public String drawingPath;
    public long lastUpdated;

    // added
    public int currentTaskIndex;
    public boolean taskCompleted;
    public boolean seedUnlocked;
    public String taskId; // refference to daily task
    public int taskProgressSeconds;
    public boolean seedShown;



    public DailyLog(@NonNull String date) {
        this.date = date;
    }
}
