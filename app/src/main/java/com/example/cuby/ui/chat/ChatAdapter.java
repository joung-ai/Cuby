package com.example.cuby.ui.chat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuby.R;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }
    
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardMessage;
        TextView tvMessage;
        TextView tvTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            cardMessage = itemView.findViewById(R.id.cardMessage);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(ChatMessage message) {
            tvMessage.setText(message.text);

            // Format timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
            tvTime.setText(sdf.format(new Date(message.timestamp)));

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cardMessage.getLayoutParams();

            if (message.isFromUser) {
                // User message → white bubble, right
                params.gravity = Gravity.END;
                cardMessage.setBackgroundResource(R.drawable.bg_message_user);
                tvMessage.setTextColor(0xFF000000); // black text
                tvTime.setTextColor(0xFF666666);    // dark gray time
            } else {
                // Respondent message → blue bubble, left
                params.gravity = Gravity.START;
                cardMessage.setBackgroundResource(R.drawable.bg_message_respondent);
                tvMessage.setTextColor(0xFFFFFFFF); // white text
                tvTime.setTextColor(0xCCFFFFFF);    // slightly transparent white time
            }

            cardMessage.setLayoutParams(params);
        }

    }
}
