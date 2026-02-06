package com.example.cuby.logic;

import java.util.ArrayList;
import java.util.List;

public class DailyTaskEngine {

    public static List<DailyTask> generateTaskSequence(String mood) {
        List<DailyTask> tasks = new ArrayList<>();

        if (mood == null) return tasks;

        switch (mood.toUpperCase()) {

            // OVERWHELMED — calm first, productivity last
            case "OVERWHELMED":
                tasks.add(new DailyTask(
                        "Breathe with Cuby",
                        "4-7-8 breathing to calm your nervous system",
                        DailyTask.TaskType.BREATHING_478,
                        4 * (4 + 7 + 8)
                ));

                tasks.add(new DailyTask(
                        "Slow Box Breathing",
                        "Stay grounded and steady",
                        DailyTask.TaskType.BREATHING_BOX,
                        4 * 16
                ));
                break;

            // TIRED — restore energy, short effort only
            case "TIRED":
                tasks.add(new DailyTask(
                        "Box Breathing",
                        "Reset your energy gently",
                        DailyTask.TaskType.BREATHING_BOX,
                        4 * 16
                ));

                tasks.add(new DailyTask(
                        "Short Pomodoro",
                        "A light focus session",
                        DailyTask.TaskType.POMODORO,
                        15 * 60
                ));
                break;

            // OKAY — healthy momentum
            case "OKAY":
                tasks.add(new DailyTask(
                        "Pomodoro Focus",
                        "25 minutes of focused work",
                        DailyTask.TaskType.POMODORO,
                        25 * 60
                ));

                tasks.add(new DailyTask(
                        "Box Breathing",
                        "Release tension after focusing",
                        DailyTask.TaskType.BREATHING_BOX,
                        3 * 16
                ));

                tasks.add(new DailyTask(
                        "Second Focus Block",
                        "One more focused session if you want",
                        DailyTask.TaskType.POMODORO,
                        20 * 60
                ));
                break;

            // CALM — sustain balance
            case "CALM":
                tasks.add(new DailyTask(
                        "Deep Focus",
                        "Work calmly and steadily",
                        DailyTask.TaskType.FOCUS,
                        30 * 60
                ));

                tasks.add(new DailyTask(
                        "Box Breathing",
                        "Maintain your calm state",
                        DailyTask.TaskType.BREATHING_BOX,
                        3 * 16
                ));
                break;

            // HAPPY — use energy without draining
            case "HAPPY":
                tasks.add(new DailyTask(
                        "Focused Momentum",
                        "Use your positive energy",
                        DailyTask.TaskType.FOCUS,
                        30 * 60
                ));

                tasks.add(new DailyTask(
                        "Pomodoro Boost",
                        "One focused sprint",
                        DailyTask.TaskType.POMODORO,
                        25 * 60
                ));
                break;
        }

        return tasks;
    }
}
