package com.example.cuby.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory")
public class Inventory {
    @PrimaryKey
    public int id = 1;

    public int bloxyFoodCount;
    public long lastFreeFoodAt;
    public int feedCountToday;


}
