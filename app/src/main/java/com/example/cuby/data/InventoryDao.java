package com.example.cuby.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.cuby.model.Inventory;
import androidx.lifecycle.LiveData;

@Dao
public interface InventoryDao {
    @Query("SELECT * FROM inventory WHERE id = 1")
    LiveData<Inventory> getInventory();
    
    @Query("SELECT * FROM inventory WHERE id = 1")
    Inventory getInventorySync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Inventory inventory);
}
