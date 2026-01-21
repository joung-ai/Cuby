package com.example.cuby.ui.chat;

public class ChatMessage {
    public String text;
    public boolean isFromUser;
    public long timestamp;

    public ChatMessage(String text, boolean isFromUser) {
        this.text = text;
        this.isFromUser = isFromUser;
        this.timestamp = System.currentTimeMillis();
    }
}
