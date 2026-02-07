package com.example.cuby.logic;

import com.example.cuby.data.AppRepository;
import com.example.cuby.model.DailyLog;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CubyMoodEngine {

    private final AppRepository repository;

    public CubyMoodEngine(AppRepository repository) {
        this.repository = repository;
    }

    // What Cuby says on the main screen
    public String getCubyMessage(String userMood, boolean isInactive, boolean seedPlanted) {

        if (seedPlanted) {
            return "I have a seed for you!\nYou earned it today.";
        }

        if (isInactive) {
            return "Welcome back! I’m really happy to see you again.";
        }

        if (userMood == null) {
            return "Hey, how are you feeling today?";
        }

        switch (userMood.toUpperCase()) {
            case "CALM":
                return "You seem calm today. Let’s keep that gentle pace.";
            case "OKAY":
                return "Nice work today. I’m proud of you.";
            case "TIRED":
                return "You showed up even when tired. That matters.";
            case "OVERWHELMED":
                return "You handled a tough day. I’m here.";
            case "HAPPY":
                return "Your happiness makes today brighter!";
            default:
                return "I’m here for you.";
        }
    }

    // Save the user’s self-reported mood for the day
    public void recordDailyMood(String date, String mood) {

        repository.getExecutor().execute(() -> {

            DailyLog log = repository.getDailyLogSync(date);

            if (log == null) {
                log = new DailyLog(date);
                log.currentTaskIndex = 0;
                log.taskCompleted = false;
                log.seedUnlocked = false;
            }

            log.mood = mood;
            log.lastUpdated = System.currentTimeMillis();

            repository.insertDailyLog(log);
        });
    }

    public DailyTask getCurrentTaskFromLog(DailyLog log) {

        if (log == null || log.mood == null || log.taskCompleted) {
            return null;
        }

        List<DailyTask> tasks =
                DailyTaskEngine.generateTaskSequence(log.mood);

        if (tasks.isEmpty()) return null;

        int index = log.currentTaskIndex;

        if (index < 0 || index >= tasks.size()) {
            index = 0;
        }

        return tasks.get(index);
    }

    public void completeCurrentTask(String date) {

        repository.getExecutor().execute(() -> {

            DailyLog log = repository.getDailyLogSync(date);
            if (log == null) return;

            List<DailyTask> tasks =
                    DailyTaskEngine.generateTaskSequence(log.mood);

            log.taskProgressSeconds = 0;

            log.currentTaskIndex++;

            if (log.currentTaskIndex >= tasks.size()) {
                // 🎉 All tasks done
                log.taskCompleted = true;
                log.seedUnlocked = true;
            }

            log.lastUpdated = System.currentTimeMillis();
            repository.insertDailyLog(log);
        });
    }


    public void addTaskProgress(String date, int seconds) {
        repository.getExecutor().execute(() -> {

            DailyLog log = repository.getDailyLogSync(date);
            if (log == null || log.taskCompleted || log.mood == null) return;

            DailyTask task = getCurrentTaskFromLog(log);
            if (task == null) return;

            // Add progress
            log.taskProgressSeconds += seconds;

            // ✅ AUTO-COMPLETE when finished
            if (log.taskProgressSeconds >= task.durationSeconds) {
                log.taskProgressSeconds = 0;
                log.currentTaskIndex++;

                if (log.currentTaskIndex >=
                        DailyTaskEngine.generateTaskSequence(log.mood).size()) {
                    log.taskCompleted = true;
                    log.seedUnlocked = true;
                }
            }

            log.lastUpdated = System.currentTimeMillis();
            repository.insertDailyLog(log);
        });
    }





    // Independent daily quote
    public static String getDailyQuote() {
        List<String> quotes = Arrays.asList(
                "You are enough just as you are.",
                "One step at a time is still progress.",
                "Breathe in calm, breathe out worry.",
                "You deserve kindness, especially from yourself.",
                "Every storm runs out of rain."
        );

        return quotes.get(new Random().nextInt(quotes.size()));
    }
}
