package com.example.cuby.ui.home;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.cuby.data.AppRepository;
import com.example.cuby.model.Inventory;
import com.example.cuby.model.UserProfile;

public class HomeViewModel extends AndroidViewModel {
    private final AppRepository repository;

    public HomeViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }

    public LiveData<UserProfile> getUserProfile() {
        return repository.getUserProfile();
    }

    public LiveData<Inventory> getInventory() {
        return repository.getInventory();
    }

    public void feedCuby() {
        repository.consumeFood(1);
    }
}
