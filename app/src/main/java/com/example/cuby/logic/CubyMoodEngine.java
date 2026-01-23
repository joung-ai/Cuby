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
                return "Thanks for checking in. I’m here with you.";
            case "TIRED":
                return "You’ve been doing a lot. It’s okay to slow down.";
            case "OVERWHELMED":
                return "That sounds heavy. Let’s take this one step at a time.";
            case "HAPPY":
                return "Your happiness makes today brighter!";
            default:
                return seedPlanted
                        ? "Look at our garden growing 🌱"
                        : "I’m here for you, no matter what.";
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
