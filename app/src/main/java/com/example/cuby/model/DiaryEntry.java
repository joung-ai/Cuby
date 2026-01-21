package com.example.cuby.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary_entry", indices = {@Index(value = "date")})
public class DiaryEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;
    public String content;
    public long createdAt;
    public long updatedAt;
}
