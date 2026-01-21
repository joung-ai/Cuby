package com.example.cuby.ui.diary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuby.R;
import com.example.cuby.model.DiaryEntry;
import com.example.cuby.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private List<DiaryEntry> entries = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DiaryEntry entry);
    }

    public DiaryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setEntries(List<DiaryEntry> newEntries) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiaryDiffCallback(entries, newEntries));
        entries = new ArrayList<>(newEntries);
        result.dispatchUpdatesTo(this);
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
        TextView tvDate;
        TextView tvContent;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvContent = itemView.findViewById(R.id.tvContent);
        }

        public void bind(DiaryEntry entry, OnItemClickListener listener) {
            tvDate.setText(DateUtils.formatFriendly(entry.date));
            tvContent.setText(entry.content);
            itemView.setOnClickListener(v -> listener.onItemClick(entry));
        }
    }
    
    private static class DiaryDiffCallback extends DiffUtil.Callback {
        private final List<DiaryEntry> oldList;
        private final List<DiaryEntry> newList;

        DiaryDiffCallback(List<DiaryEntry> oldList, List<DiaryEntry> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).id == newList.get(newPos).id;
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            DiaryEntry o = oldList.get(oldPos);
            DiaryEntry n = newList.get(newPos);
            return Objects.equals(o.content, n.content) && Objects.equals(o.date, n.date);
        }
    }
}
