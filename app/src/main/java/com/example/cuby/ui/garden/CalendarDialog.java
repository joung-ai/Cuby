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

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }
    }



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

        title.setText("Memories");

        view.findViewById(R.id.btnBack).setOnClickListener(v -> dismiss());

        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(GardenViewModel.class);

        tvMonth = view.findViewById(R.id.tvMonth);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        adapter = new GardenAdapter(this::showPlotDetail);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        recyclerView.setAdapter(adapter);

        recyclerView.post(() -> {
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            if (lm instanceof GridLayoutManager) {
                ((GridLayoutManager) lm).requestLayout();
            }
        });


        view.findViewById(R.id.btnPrev).setOnClickListener(v -> viewModel.prevMonth());
        view.findViewById(R.id.btnNext).setOnClickListener(v -> viewModel.nextMonth());

        observeData();
        recyclerView.setItemViewCacheSize(0);


    }

    private void observeData() {

        viewModel.getCurrentMonth().observe(getViewLifecycleOwner(), cal -> {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            tvMonth.setText(sdf.format(cal.getTime()));
        });

        viewModel.getMonthlyLogs().observe(getViewLifecycleOwner(), logs -> {
            if (logs == null || cachedPlants == null) return;

            updateGrid(
                    viewModel.getCurrentMonth().getValue(),
                    logs
            );
        });

        viewModel.getPlantsForMonth().observe(getViewLifecycleOwner(), plants -> {
            if (plants == null) return;

            cachedPlants = plants;

            List<DailyLog> logs = viewModel.getMonthlyLogs().getValue();
            if (logs == null) return;

            updateGrid(
                    viewModel.getCurrentMonth().getValue(),
                    logs
            );
        });
    }

    private void updateGrid(Calendar currentMonth, List<DailyLog> logs) {
        if (currentMonth == null) return;

        if (logs == null) logs = new ArrayList<>();
        if (cachedPlants == null) cachedPlants = new ArrayList<>();

        List<GardenPlot> plots = new ArrayList<>();

        Calendar cal = (Calendar) currentMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // Sunday = 1, Saturday = 7

        int leadingEmptyCells = firstDayOfWeek - Calendar.SUNDAY;
        if (leadingEmptyCells < 0) leadingEmptyCells += 7;

        // 🟦 ADD EMPTY CELLS FIRST
        for (int i = 0; i < leadingEmptyCells; i++) {
            plots.add(GardenPlot.empty());
        }

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
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

            plots.add(new GardenPlot(day, matchedLog, matchedPlant));
        }

        adapter.setPlots(plots);

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.post(recyclerView::requestLayout);

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
