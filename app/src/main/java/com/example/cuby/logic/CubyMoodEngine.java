package com.example.cuby.logic;

import com.example.cuby.model.DailyLog;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CubyMoodEngine {

    public static String getCubyMessage(String userMood, boolean isInactive, boolean seedPlanted) {
        if (isInactive) {
            return "I missed you! I'm so happy you're back.";
        }
        
        if (userMood == null) {
            return "How are you feeling today?";
        }
        
        switch (userMood.toUpperCase()) {
            case "SAD":
            case "ANXIOUS":
            case "TIRED":
                return "It's okay to feel this way. Take a deep breath with me.";
            case "HAPPY":
            case "EXCITED":
                return "Yay! Your happiness makes me glow!";
            case "ANGRY":
                return "I'm listening. Let it out if you need to.";
            default:
                if (seedPlanted) {
                    return "Look at our garden grow!";
                }
                return "I'm here for you, always.";
        }
    }
    
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
