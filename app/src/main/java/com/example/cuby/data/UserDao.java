package com.example.cuby.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.cuby.model.UserProfile;
import androidx.lifecycle.LiveData;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    LiveData<UserProfile> getUserProfile();
    
    @Query("SELECT * FROM user_profile WHERE id = 1")
    UserProfile getUserProfileSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfile userProfile);

    @Query("UPDATE user_profile SET cubyCosmetic = :cosmetic WHERE id = 1")
    void updateCosmetic(String cosmetic);

}
