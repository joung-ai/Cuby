package com.example.cuby.logic;

import java.util.ArrayList;
import java.util.List;

public class DailyTaskEngine {

    public static List<DailyTask> generateTaskSequence(String mood) {
        List<DailyTask> tasks = new ArrayList<>();

        if (mood == null) return tasks;

        switch (mood.toUpperCase()){
            case "OVERWHELMED":
                tasks.add(new DailyTask(
                        "Let's Breathe with Cuby",
                        "Use the 4-7-8 breathing method to calm ourselves.",
                        DailyTask.TaskType.BREATHING
                ));
                tasks.add(new DailyTask(
                        "Let's take a break from Social Media.",
                        "Detox from Social Media",
                        DailyTask.TaskType.DETOX
                ));
                break;

            case "TIRED":
                tasks.add(new DailyTask(
                        "Gentle breathing",
                        "Slow your breathing and relax.",
                        DailyTask.TaskType.BREATHING
                ));
                tasks.add(new DailyTask(
                        "Light focus",
                        "Try one short Pomodoro session.",
                        DailyTask.TaskType.POMODORO
                ));
                break;

            case "OKAY":
                tasks.add(new DailyTask(
                        "Focus session",
                        "Complete one Pomodoro session.",
                        DailyTask.TaskType.POMODORO
                ));
                break;

            case "CALM":
                tasks.add(new DailyTask(
                        "Focused momentum",
                        "Do one Pomodoro session mindfully.",
                        DailyTask.TaskType.POMODORO
                ));
                tasks.add(new DailyTask(
                        "Mindful pause",
                        "Take a short detox break.",
                        DailyTask.TaskType.DETOX
                ));
                break;

            case "HAPPY":
                tasks.add(new DailyTask(
                        "Channel your energy",
                        "Use your energy in a Pomodoro session.",
                        DailyTask.TaskType.POMODORO
                ));
                tasks.add(new DailyTask(
                        "Balance and breathe",
                        "Calm your body with breathing.",
                        DailyTask.TaskType.BREATHING
                ));
                break;
        }

        return tasks;
    }
}
