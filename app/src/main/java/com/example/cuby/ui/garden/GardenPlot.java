package com.example.cuby.ui.garden;

import com.example.cuby.model.DailyLog;

public class GardenPlot {
    public int dayWithType; // 0=Empty, 1=DayCell
    public int dayNumber;
    public DailyLog log; // Can be null
    
    public GardenPlot(int dayNumber, DailyLog log) {
        this.dayWithType = 1;
        this.dayNumber = dayNumber;
        this.log = log;
    }
}
