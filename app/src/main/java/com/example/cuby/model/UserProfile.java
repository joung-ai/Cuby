package com.example.cuby.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile")
public class UserProfile {
    @PrimaryKey(autoGenerate = false)
    public int id = 1; // Single user

    public String username;
    public String cubyName;
    public String cubySkin;
    public long createdAt;
}
