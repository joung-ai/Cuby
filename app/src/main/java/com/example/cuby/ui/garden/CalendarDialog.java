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
    }

    private void updateGrid(Calendar currentMonth, List<DailyLog> logs) {
        if (currentMonth == null) return;

        List<GardenPlot> plots = new ArrayList<>();
        Calendar cal = (Calendar) currentMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            String dayStr = String.format(Locale.getDefault(), "%02d", i);
            DailyLog log = null;

            for (DailyLog l : logs) {
                if (l.date.endsWith("-" + dayStr)) {
                    log = l;
                    break;
                }
            }
            plots.add(new GardenPlot(i, log));
        }

        adapter.setPlots(plots);
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
        if (plot.log.drawingPath != null) {
            File f = new File(plot.log.drawingPath);
            if (f.exists()) {
                iv.setImageBitmap(
                        BitmapFactory.decodeFile(f.getAbsolutePath())
                );
            }
        }

        builder.setView(view)
                .setPositiveButton("Close", null)
                .show();
    }
}
