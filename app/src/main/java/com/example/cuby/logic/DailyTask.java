package com.example.cuby.logic;

public class DailyTask {

    public enum TaskType {
        DETOX,
        POMODORO,
        BREATHING
    }

    public String title;
    public String description;
    public TaskType type;

    public DailyTask(String title, String description, TaskType type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }
}
