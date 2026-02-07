package com.example.cuby.ui.garden;

import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuby.R;
import com.example.cuby.model.DailyLog;
import com.example.cuby.model.GardenPlant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarDialog extends DialogFragment {

    private GardenViewModel viewModel;
    private GardenAdapter adapter;
    private TextView tvMonth;
    private List<GardenPlant> cachedPlants = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_garden_calendar, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        TextView title = view.findViewById(R.id.toolbarTitleText);

        View backBtn = view.findViewById(R.id.btnBack);

        title.setText("Memories");

        view.findViewById(R.id.btnBack).setOnClickListener(v -> dismiss());

        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(GardenViewModel.class);

        tvMonth = view.findViewById(R.id.tvMonth);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        adapter = new GardenAdapter(this::showPlotDetail);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btnPrev).setOnClickListener(v -> viewModel.prevMonth());
        view.findViewById(R.id.btnNext).setOnClickListener(v -> viewModel.nextMonth());

        observeData();
    }

    private void observeData() {
        viewModel.getCurrentMonth().observe(getViewLifecycleOwner(), cal -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            tvMonth.setText(sdf.format(cal.getTime()));
        });

        viewModel.getMonthlyLogs().observe(getViewLifecycleOwner(), logs -> {
            updateGrid(viewModel.getCurrentMonth().getValue(), logs);
        });

        viewModel.getPlantsForMonth().observe(getViewLifecycleOwner(), plants -> {
            cachedPlants = plants;
            updateGrid(viewModel.getCurrentMonth().getValue(),
                    viewModel.getMonthlyLogs().getValue());
        });

    }

    private void updateGrid(Calendar currentMonth, List<DailyLog> logs) {
        if (currentMonth == null) return;

        List<GardenPlot> plots = new ArrayList<>();
        Calendar cal = (Calendar) currentMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 1; i <= daysInMonth; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            String dateStr = sdf.format(cal.getTime());

            DailyLog matchedLog = null;
            for (DailyLog l : logs) {
                if (dateStr.equals(l.date)) {
                    matchedLog = l;
                    break;
                }
            }

            GardenPlant matchedPlant = null;
            for (GardenPlant p : cachedPlants) {
                if (isSameDay(p.plantedAt, dateStr)) {
                    matchedPlant = p;
                    break;
                }
            }


            plots.add(new GardenPlot(i, matchedLog, matchedPlant));

        }

        adapter.setPlots(plots);
    }
    private boolean isSameDay(long timeMillis, String dateStr) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String plantDate = sdf.format(new java.util.Date(timeMillis));
        return plantDate.equals(dateStr);
    }


    private void showPlotDetail(GardenPlot plot) {
        if (plot.log == null || !plot.log.seedPlanted) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_plot_detail, null);

        ((TextView) view.findViewById(R.id.tvDate))
                .setText("Day " + plot.dayNumber);

        ((TextView) view.findViewById(R.id.tvMood))
                .setText("Mood: " + plot.log.mood);

        ((TextView) view.findViewById(R.id.tvReflection))
                .setText(plot.log.reflectionNote);

        ImageView iv = view.findViewById(R.id.ivDrawing);

        if (plot.plant != null && plot.plant.imagePath != null) {
            File f = new File(plot.plant.imagePath);
            if (f.exists()) {
                iv.setImageBitmap(
                        BitmapFactory.decodeFile(f.getAbsolutePath())
                );
                iv.setVisibility(View.VISIBLE);
            } else {
                iv.setVisibility(View.GONE);
            }
        } else {
            iv.setVisibility(View.GONE);
        }


        builder.setView(view)
                .setPositiveButton("Close", null)
                .show();
    }
}
