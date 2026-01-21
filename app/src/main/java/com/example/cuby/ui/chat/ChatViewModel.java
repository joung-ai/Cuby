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
            return "I'm so sorry you feel that way. I'm sitting right here with you.";
        } else if (lower.contains("angry") || lower.contains("mad")) {
            return "It's okay to be angry. Let it out. You're safe here.";
        } else if (lower.contains("happy") || lower.contains("good")) {
            return "That's wonderful to hear! Your smile makes my day brighter.";
        } else {
            return CubyMoodEngine.getDailyQuote();
        }
    }
}
