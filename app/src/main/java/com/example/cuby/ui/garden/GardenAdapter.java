package com.example.cuby.ui.garden;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuby.R;

import java.io.File;
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

            if (plot.dayNumber == -1) {
                tvDay.setText("");
                ivPlant.setVisibility(View.GONE);
                viewMoodDot.setVisibility(View.GONE);
                itemView.setOnClickListener(null);
                return;
            }

            tvDay.setText(String.valueOf(plot.dayNumber));

            ivPlant.setVisibility(View.GONE);
            viewMoodDot.setVisibility(View.GONE);

            if (plot.plant != null && plot.plant.imagePath != null) {
                File file = new File(plot.plant.imagePath);
                if (file.exists()) {
                    ivPlant.setImageBitmap(
                            BitmapFactory.decodeFile(file.getAbsolutePath())
                    );
                    ivPlant.setVisibility(View.VISIBLE);
                }
            }

            if (plot.log != null && plot.log.mood != null) {
                viewMoodDot.setVisibility(View.VISIBLE);
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
            GardenPlot oldItem = oldList.get(oldPos);
            GardenPlot newItem = newList.get(newPos);

            // Empty cells are position-based
            if (oldItem.dayNumber == 0 && newItem.dayNumber == 0) {
                return oldPos == newPos;
            }

            return oldItem.dayNumber == newItem.dayNumber;
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            GardenPlot o = oldList.get(oldPos);
            GardenPlot n = newList.get(newPos);

            // seed state
            boolean oldPlanted = o.log != null && o.log.seedPlanted;
            boolean newPlanted = n.log != null && n.log.seedPlanted;

            // plant image presence
            String oldImage = o.plant != null ? o.plant.imagePath : null;
            String newImage = n.plant != null ? n.plant.imagePath : null;

            if (oldPlanted != newPlanted) return false;

            if (oldImage == null && newImage == null) return true;
            if (oldImage == null || newImage == null) return false;

            return oldImage.equals(newImage);
        }
    }
}
