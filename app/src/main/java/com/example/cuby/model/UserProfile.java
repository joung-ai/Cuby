package com.example.cuby.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile")
public class UserProfile {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;
    public String cubyName;
    public String cubySkin; // "Default", "Pink", "Blue"
    public long createdAt;
}
