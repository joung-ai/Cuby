package com.example.cuby.ui.garden;

import com.example.cuby.model.DailyLog;
import com.example.cuby.model.GardenPlant;

public class GardenPlot {

    public int dayNumber;
    public DailyLog log;
    public GardenPlant plant;

    public GardenPlot(int dayNumber, DailyLog log, GardenPlant plant) {
        this.dayNumber = dayNumber;
        this.log = log;
        this.plant = plant;
    }

    public static GardenPlot empty() {
        return new GardenPlot(-1, null, null);
    }
}
