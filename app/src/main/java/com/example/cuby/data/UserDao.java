package com.example.cuby.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.cuby.model.UserProfile;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    LiveData<UserProfile> getUserProfile();
    
    @Query("SELECT * FROM user_profile LIMIT 1")
    UserProfile getUserProfileSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfile userProfile);
}
