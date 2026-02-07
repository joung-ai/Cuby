package com.example.cuby.data;

import androidx.lifecycle.LiveData;
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

    @Query("SELECT COUNT(*) FROM GardenPlant WHERE date = :date")
    int hasPlantForDate(String date);

    @Query("SELECT * FROM GardenPlant WHERE yearMonth = :yearMonth")
    LiveData<List<GardenPlant>> getPlantsForMonthLive(String yearMonth);


}
