package com.example.cuby.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GardenPlant {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String yearMonth;

    // relative position (0..1)
    public float posX;
    public float posY;

    // 🌸 PATH TO USER DRAWING
    public String imagePath;

    public String plantType;
    public long plantedAt;
}
