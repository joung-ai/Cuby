package com.example.cuby.ui.chat;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.cuby.logic.CubyMoodEngine;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ChatViewModel() {
        List<ChatMessage> initial = new ArrayList<>();
        initial.add(new ChatMessage("Hey there! How are you feeling today? 😊", false));
        messages.setValue(initial);
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public void sendMessage(String text) {
        List<ChatMessage> current = messages.getValue();
        if (current == null) current = new ArrayList<>();
        
        current.add(new ChatMessage(text, true));
        messages.setValue(new ArrayList<>(current));
        
        handler.postDelayed(() -> {
            String reply = CubyMoodEngine.generateReply(text);
            List<ChatMessage> updated = messages.getValue();
            if (updated == null) updated = new ArrayList<>();
            updated.add(new ChatMessage(reply, false));
            messages.setValue(new ArrayList<>(updated));
        }, 1000);
    }
}
