package com.example.cuby.ui.garden;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.cuby.data.AppRepository;
import com.example.cuby.model.DailyLog;
import com.example.cuby.utils.DateUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.example.cuby.model.GardenPlant;

public class GardenViewModel extends AndroidViewModel {
    
    private final AppRepository repository;
    private final MutableLiveData<Calendar> currentMonth = new MutableLiveData<>(Calendar.getInstance());
    
    // LiveData that updates when currentMonth changes
    private final LiveData<List<DailyLog>> monthlyLogs;

    public GardenViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getInstance(application);
        
        monthlyLogs = Transformations.switchMap(currentMonth, calendar -> {
            String monthPattern = DateUtils.getMonthPattern(calendar.getTime());
            return repository.getMonthlyLogs(monthPattern);
        });

    }

    public LiveData<List<GardenPlant>> getPlantsForMonth() {
        return Transformations.switchMap(currentMonth, calendar -> {
            String ym = DateUtils.getMonthPattern(calendar.getTime());
            return repository.getPlantsForMonthLive(ym);
        });
    }

    public LiveData<List<DailyLog>> getMonthlyLogs() {
        return monthlyLogs;
    }
    
    public LiveData<Calendar> getCurrentMonth() {
        return currentMonth;
    }

    public void nextMonth() {
        Calendar cal = currentMonth.getValue();
        if (cal != null) {
            cal.add(Calendar.MONTH, 1);
            currentMonth.setValue(cal);
        }
    }

    public void prevMonth() {
        Calendar cal = currentMonth.getValue();
        if (cal != null) {
            cal.add(Calendar.MONTH, -1);
            currentMonth.setValue(cal);
        }
    }
}
