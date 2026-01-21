package com.example.cuby.ui.diary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuby.R;
import com.example.cuby.model.DiaryEntry;
import com.example.cuby.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(DiaryEntry entry);
    }

    private List<DiaryEntry> entries = new ArrayList<>();
    private final OnItemClickListener listener;

    public DiaryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setEntries(List<DiaryEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary_entry, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        holder.bind(entries.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvPreview;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPreview = itemView.findViewById(R.id.tvPreview);
        }

        public void bind(DiaryEntry entry, OnItemClickListener listener) {
            tvDate.setText(DateUtils.formatFriendly(entry.date));
            String preview = entry.content.length() > 50 ? entry.content.substring(0, 50) + "..." : entry.content;
            tvPreview.setText(preview);
            itemView.setOnClickListener(v -> listener.onItemClick(entry));
        }
    }
}
