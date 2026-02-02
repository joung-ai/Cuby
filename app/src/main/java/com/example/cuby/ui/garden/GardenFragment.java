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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuby.R;
import com.example.cuby.audio.MusicManager;
import com.example.cuby.model.DailyLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GardenFragment extends Fragment {

    private GardenViewModel viewModel;
    private GardenAdapter adapter;
    private TextView tvMonth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_garden, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MusicManager.play(requireContext(), MusicManager.Track.GARDEN);
    }

    @Override
    public void onPause() {
        super.onPause();
        MusicManager.play(requireContext(), MusicManager.Track.HOME);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.toolbarTitleText);
        View backBtn = view.findViewById(R.id.btnBack);

        title.setText("Memory Garden");


        backBtn.findViewById(R.id.btnBack)
                .setOnClickListener(v ->
                        requireActivity()
                                .getOnBackPressedDispatcher()
                                .onBackPressed()
                );

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
        
        // Simple 1 to N grid for now (ignoring day-of-week offset for simplicity, or add empty plots)
        for (int i = 1; i <= daysInMonth; i++) {
            String dayStr = String.format(Locale.getDefault(), "%02d", i);
            DailyLog log = null;
            // Find log for this day
            // logs pattern is yyyy-MM-dd
            // We need to match dd
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
        if (plot.log == null || !plot.log.seedPlanted) {
            return; // Or show "Plant a seed today!" message
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_plot_detail, null);
        
        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvMood = view.findViewById(R.id.tvMood);
        TextView tvReflection = view.findViewById(R.id.tvReflection);
        ImageView ivDrawing = view.findViewById(R.id.ivDrawing);
        
        tvDate.setText("Day " + plot.dayNumber);
        tvMood.setText(plot.log.mood != null ? "Mood: " + plot.log.mood : "Mood: Unknown");
        tvReflection.setText(plot.log.reflectionNote != null ? plot.log.reflectionNote : "No reflection note.");
        
        if (plot.log.drawingPath != null) {
            File imgFile = new File(plot.log.drawingPath);
            if (imgFile.exists()) {
                ivDrawing.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                ivDrawing.setVisibility(View.VISIBLE);
            }
        }
        
        builder.setView(view)
                .setPositiveButton("Close", null)
                .show();
    }
}
