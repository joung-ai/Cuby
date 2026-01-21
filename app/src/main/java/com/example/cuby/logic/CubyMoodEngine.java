package com.example.cuby.logic;

import java.util.Random;

public class CubyMoodEngine {
    private static final String[] QUOTES = {
        "Every small step counts. You're doing great!",
        "It's okay to rest. Tomorrow is a new day.",
        "Your feelings are valid. I'm here for you.",
        "Take a deep breath. You've got this!",
        "Remember to drink some water today!",
        "You're stronger than you think.",
        "It's okay not to be okay sometimes."
    };
    
    private static final String[] HAPPY_RESPONSES = {
        "That's wonderful to hear! 🌟",
        "I'm so happy for you! Keep shining!",
        "Your happiness makes me happy too! 💕"
    };
    
    private static final String[] SAD_RESPONSES = {
        "I'm here for you. It's okay to feel this way.",
        "Sending you a virtual hug 🤗",
        "Take your time. I'll be right here."
    };
    
    private static final String[] NEUTRAL_RESPONSES = {
        "I see! Tell me more about your day.",
        "Thanks for sharing with me!",
        "How can I help you feel better today?"
    };
    
    public static String getResponseForMood(String mood) {
        Random random = new Random();
        if ("happy".equalsIgnoreCase(mood)) {
            return HAPPY_RESPONSES[random.nextInt(HAPPY_RESPONSES.length)];
        } else if ("sad".equalsIgnoreCase(mood)) {
            return SAD_RESPONSES[random.nextInt(SAD_RESPONSES.length)];
        }
        return NEUTRAL_RESPONSES[random.nextInt(NEUTRAL_RESPONSES.length)];
    }
    
    public static String getDailyQuote() {
        return QUOTES[new Random().nextInt(QUOTES.length)];
    }
    
    public static String generateReply(String userMessage) {
        String lower = userMessage.toLowerCase();
        if (lower.contains("happy") || lower.contains("good") || lower.contains("great")) {
            return getResponseForMood("happy");
        } else if (lower.contains("sad") || lower.contains("bad") || lower.contains("tired")) {
            return getResponseForMood("sad");
        }
        return getResponseForMood("neutral");
    }
}
