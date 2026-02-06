package com.example.cuby.logic;

public class DailyTask {

    public enum TaskType {
        BREATHING_478,
        BREATHING_BOX,
        POMODORO,
        FOCUS
    }

    public String title;
    public String description;
    public TaskType type;

    // duration in seconds
    public int durationSeconds;

    public DailyTask(
            String title,
            String description,
            TaskType type,
            int durationSeconds
    ) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.durationSeconds = durationSeconds;
    }
}
