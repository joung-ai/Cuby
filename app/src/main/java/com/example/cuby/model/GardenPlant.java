package com.example.cuby.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GardenPlant {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // yyyy-MM-dd  ← REQUIRED
    public String date;

    // yyyy-MM
    public String yearMonth;

    public float posX;
    public float posY;

    public String imagePath;
    public String plantType;
    public long plantedAt;
}
