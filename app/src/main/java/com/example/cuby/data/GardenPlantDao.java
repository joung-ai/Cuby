package com.example.cuby.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cuby.model.GardenPlant;

import java.util.List;

@Dao
public interface GardenPlantDao {

    @Insert
    void insert(GardenPlant plant);

    @Query("SELECT * FROM GardenPlant WHERE yearMonth = :yearMonth")
    List<GardenPlant> getPlantsForMonth(String yearMonth);
}
