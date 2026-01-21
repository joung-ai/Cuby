package com.example.cuby.ui.garden;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuby.R;
import java.util.ArrayList;
import java.util.List;

public class GardenAdapter extends RecyclerView.Adapter<GardenAdapter.PlotViewHolder> {

    private List<GardenPlot> plots = new ArrayList<>();
    private final OnPlotClickListener listener;

    public interface OnPlotClickListener {
        void onPlotClick(GardenPlot plot);
    }

    public GardenAdapter(OnPlotClickListener listener) {
        this.listener = listener;
    }

    public void setPlots(List<GardenPlot> newPlots) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new GardenDiffCallback(plots, newPlots));
        plots = new ArrayList<>(newPlots);
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public PlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_garden_plot, parent, false);
        return new PlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlotViewHolder holder, int position) {
        holder.bind(plots.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return plots.size();
    }

    static class PlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        ImageView ivPlant;
        View viewMoodDot;

        public PlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            ivPlant = itemView.findViewById(R.id.ivPlant);
            viewMoodDot = itemView.findViewById(R.id.viewMoodDot);
        }

        public void bind(GardenPlot plot, OnPlotClickListener listener) {
            tvDay.setText(String.valueOf(plot.dayNumber));
            
            if (plot.log != null && plot.log.seedPlanted) {
                ivPlant.setVisibility(View.VISIBLE);
                ivPlant.setImageResource(android.R.drawable.star_big_on); 
                viewMoodDot.setVisibility(plot.log.mood != null ? View.VISIBLE : View.GONE);
            } else {
                ivPlant.setVisibility(View.GONE);
                viewMoodDot.setVisibility(View.GONE);
            }
            
            itemView.setOnClickListener(v -> listener.onPlotClick(plot));
        }
    }
    
    private static class GardenDiffCallback extends DiffUtil.Callback {
        private final List<GardenPlot> oldList;
        private final List<GardenPlot> newList;

        GardenDiffCallback(List<GardenPlot> oldList, List<GardenPlot> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).dayNumber == newList.get(newPos).dayNumber;
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            GardenPlot o = oldList.get(oldPos);
            GardenPlot n = newList.get(newPos);
            boolean oldPlanted = o.log != null && o.log.seedPlanted;
            boolean newPlanted = n.log != null && n.log.seedPlanted;
            return oldPlanted == newPlanted;
        }
    }
}
