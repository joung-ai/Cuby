package com.example.cuby.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.cuby.data.AppRepository;
import com.example.cuby.model.Inventory;
import com.example.cuby.model.UserProfile;

public class HomeViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private final LiveData<UserProfile> userProfile;
    private final LiveData<Inventory> inventory;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
        userProfile = repository.getUserProfile();
        inventory = repository.getInventory();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    public LiveData<Inventory> getInventory() {
        return inventory;
    }
    
    public void feedCuby() {
        repository.consumeFood(1);
    }
}
