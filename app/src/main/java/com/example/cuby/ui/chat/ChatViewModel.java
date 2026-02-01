package com.example.cuby.ui.chat;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.cuby.data.AppRepository;
import com.example.cuby.logic.CubyMoodEngine;
import com.example.cuby.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ChatViewModel(@NonNull Application application) {
        super(application);
        // Initial greeting
        addMessage(new ChatMessage("Hello! I'm here if you need to talk.", false));
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public void sendMessage(String text) {
        addMessage(new ChatMessage(text, true));

        // Simulate thinking and reply
        handler.postDelayed(() -> {
            String reply = generateReply(text);
            addMessage(new ChatMessage(reply, false));
        }, 1500);
    }

    private void addMessage(ChatMessage msg) {
        List<ChatMessage> current = messages.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(msg);
        messages.setValue(current);
    }

    private String generateReply(String userText) {
        String lower = userText.toLowerCase();
        if (lower.contains("sad") || lower.contains("bad") || lower.contains("hopeless")) {
            String[] replies = {
                    "I'm so sorry you feel that way. I'm sitting right here with you.",
                    "It's okay to feel sad, you don't need a reason or an explanation",
                    "You're still doing your best, even on low-energy days, and I'm so proud of you!",
                    "Your feelings is allowed to take up space, breathe in and breathe out."
            };
            return replies[new java.util.Random().nextInt(replies.length)];

        } else if (lower.contains("angry") || lower.contains("mad")) {
            String[] replies = {
                    "It's okay to be angry. Let it out. You're safe here",
                    "That's wonderful to hear! Your smile makes my day brighter.",
                    "Let it out if you need to. I can handle it.",
                    "I know you’re angry. It’s okay. You don’t have to hold it in."
            };
            return replies[new java.util.Random().nextInt(replies.length)];
        } else if (lower.contains("happy") || lower.contains("good")) {
            String[] replies = {
                    "That’s so nice to know. You really do bring so much light with you.",
                    "That's wonderful to hear! Your smile makes my day brighter.",
                    "That makes me really happy to hear, your smile honestly brightens my day!",
                    "That’s so nice to know. You really do bring so much light with you."
            };
            return replies[new java.util.Random().nextInt(replies.length)];
        } else if (lower.contains("overwhelmed") || lower.contains("stress")) {
            String[] replies = {
                    "Don't push yourself too much, one step at a time is enough.",
                    "Take it easy on yourself. One step at a time is perfectly okay.",
                    "No need to rush—one step at a time is enough.",
                    "Be gentle with yourself. One step at a time."
            };
            return replies[new java.util.Random().nextInt(replies.length)];
        } else if (lower.contains("irritate") || lower.contains("irritated")) {
            String[] replies = {
                    "That feeling makes sense. Pause for a moment and breathe.",
                    "I can tell you're irritated. It's okay to take a short break.",
                    "Let's slow things down for a second. You're doing fine.",
                    "It's okay to feel irritated sometimes. I'm here with you."
            };
            return replies[new java.util.Random().nextInt(replies.length)];
        } else if (lower.contains("alone") || lower.contains("lonely")) {
            String[] replies = {
                    "Your presence is enough right now.",
                    "You matter, even when it feels so quiet.",
                    "You're not invisible. I'm here with you.",
                    "Being alone doesn't mean you're unloved."
            };
            return replies[new java.util.Random().nextInt(replies.length)];
        } else if (lower.contains("hate") || lower.contains("Despise")) {
            String[] replies = {
                    "Be gentle with yourself. You're doing the best you can!.",
                    "What you feel now is part of healing, not end of your story.",
                    "You're not invisible. I'm here with you.",
                    "Being alone doesn't mean you're unloved."
            };
            return replies[new java.util.Random().nextInt(replies.length)];
        } else if (lower.contains("die") || lower.contains("kill")) {
            String[] replies = {
                    "I understand that things might be difficult at the moment. :(. If you need help [https://findahelpline.com/countries/ph]",
                    "I know things are tough right now. If you need help call : [Philippine Red Cross: 143]",
                    "You are not alone in this, your life matters more than you can see right now. If you need help call : [National Center For Mental Health: 1553]"
            };
            return replies[new java.util.Random().nextInt(replies.length)];
        } else if (lower.contains("anxious") || lower.contains("nervous")) {
                String[] replies = {
                        "It's okay to feel nervous, take a deep breath, and remember you're doing your best!",
                        "You don't have to have it all figured out right now, One step at a time is enough.",
                        "Slow down. One breath at a time",
                        "You are doing better than you think"
                };
                return replies[new java.util.Random().nextInt(replies.length)];
        } else {
            return CubyMoodEngine.getDailyQuote();
        }
    }
}
