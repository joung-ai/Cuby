package com.example.cuby.ui.diary;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.cuby.data.AppRepository;
import com.example.cuby.model.DiaryEntry;
import java.util.List;

public class DiaryViewModel extends AndroidViewModel {
    private final AppRepository repository;
    private final LiveData<List<DiaryEntry>> allEntries;

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
        allEntries = repository.getAllDiaryEntries();
    }

    public LiveData<List<DiaryEntry>> getAllEntries() {
        return allEntries;
    }

    public void insert(DiaryEntry entry) {
        repository.insertDiaryEntry(entry);
    }

    public void update(DiaryEntry entry) {
        repository.updateDiaryEntry(entry);
    }

    public void delete(DiaryEntry entry) {
        repository.deleteDiaryEntry(entry);
    }
}
