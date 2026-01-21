package com.example.cuby.ui.garden;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.example.cuby.data.AppRepository;

public class GardenViewModel extends AndroidViewModel {
    private final AppRepository repository;

    public GardenViewModel(Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
    }
}
