package com.example.cuby.ui.garden;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;

import com.example.cuby.data.AppRepository;
import com.example.cuby.model.GardenPlant;
import com.example.cuby.ui.drawing.DrawingActivity;

import com.example.cuby.R;
import com.example.cuby.audio.MusicManager;

public class GardenFragment extends Fragment {

    private ViewGroup plotArea;
    private ImageView seedIcon;

    private float dX, dY;
    private float seedStartX, seedStartY;

    private static final int REQ_DRAW_PLANT = 101;
    private float pendingPlantX, pendingPlantY;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
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
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar
        TextView title = view.findViewById(R.id.toolbarTitleText);
        View backBtn = view.findViewById(R.id.btnBack);
        title.setText("Memory Garden");

        backBtn.setOnClickListener(v ->
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed()
        );

        // Calendar
        view.findViewById(R.id.btnCalendar).setOnClickListener(v ->
                new CalendarDialog().show(
                        getChildFragmentManager(),
                        "calendar"
                )
        );

        // 🌱 Garden elements
        plotArea = view.findViewById(R.id.plotArea);
        seedIcon = view.findViewById(R.id.seedIcon);

        checkSeedAvailability();

        // Save original seed position (for reset)
        seedIcon.post(() -> {
            seedStartX = seedIcon.getX();
            seedStartY = seedIcon.getY();
        });

        makeSeedDraggable();

        String yearMonth =
                com.example.cuby.utils.DateUtils.getMonthPattern(new java.util.Date());

        loadPlantsForMonth(yearMonth);

    }

    // 🌰 DRAG LOGIC
    private void makeSeedDraggable() {
        seedIcon.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    v.setX(event.getRawX() + dX);
                    v.setY(event.getRawY() + dY);
                    return true;

                case MotionEvent.ACTION_UP:
                    handleDrop(v);
                    return true;
            }
            return false;
        });
    }

    // 📍 DROP HANDLING
    private void handleDrop(View seed) {
        Rect plotRect = new Rect();
        Rect seedRect = new Rect();

        plotArea.getGlobalVisibleRect(plotRect);
        seed.getGlobalVisibleRect(seedRect);

        if (Rect.intersects(plotRect, seedRect)) {
            snapAndSave(seed);
        } else {
            resetSeed(seed);
        }
    }

    // 🌱 SNAP + SAVE POSITION
    private void snapAndSave(View seed) {

        float centerX = seed.getX() + seed.getWidth() / 2f;
        float centerY = seed.getY() + seed.getHeight() / 2f;

        pendingPlantX = centerX / plotArea.getWidth();
        pendingPlantY = centerY / plotArea.getHeight();

        pendingPlantX = Math.max(0f, Math.min(1f, pendingPlantX));
        pendingPlantY = Math.max(0f, Math.min(1f, pendingPlantY));

        Intent intent = new Intent(requireContext(), DrawingActivity.class);
        startActivityForResult(intent, REQ_DRAW_PLANT);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQ_DRAW_PLANT) return;
        if (resultCode != Activity.RESULT_OK) return;
        if (data == null) return;

        final String imagePath = data.getStringExtra("drawing_path");
        if (imagePath == null) return;

        final AppRepository repo =
                AppRepository.getInstance(requireActivity().getApplication());

        final String yearMonth =
                com.example.cuby.utils.DateUtils.getMonthPattern(new java.util.Date());

        repo.getExecutor().execute(new Runnable() {
            @Override
            public void run() {

                GardenPlant plant = new GardenPlant();
                plant.yearMonth = yearMonth;
                plant.posX = pendingPlantX;
                plant.posY = pendingPlantY;
                plant.imagePath = imagePath;
                plant.plantType = "drawing";
                plant.plantedAt = System.currentTimeMillis();

                repo.gardenPlantDao().insert(plant);

                String today = com.example.cuby.utils.DateUtils.getTodayDate();
                com.example.cuby.model.DailyLog log =
                        repo.getDailyLogSync(today);

                if (log != null) {
                    log.seedPlanted = true;
                    repo.insertDailyLog(log);
                }

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seedIcon.setVisibility(View.GONE);
                        loadPlantsForMonth(yearMonth);
                    }
                });
            }
        });
    }


    //  RESET IF MISSED
    private void resetSeed(View seed) {
        seed.animate()
                .x(seedStartX)
                .y(seedStartY)
                .setDuration(200)
                .start();
    }

    //  SAVE (relative position)

    //open drawing activity
    private void launchDrawingActivity(float relX, float relY) {
        Intent intent = new Intent(requireContext(), DrawingActivity.class);

        // Pass planted position (optional but future-proof)
        intent.putExtra("plant_x", relX);
        intent.putExtra("plant_y", relY);

        startActivity(intent);
    }

    private void placePlantView(GardenPlant plant) {

        ImageView flower = new ImageView(requireContext());

        // 🌸 THIS is the plant in the garden
        flower.setImageResource(R.drawable.ic_plant);

        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(120, 120);
        flower.setLayoutParams(params);

        plotArea.addView(flower);

        flower.post(() -> {
            float x = plant.posX * plotArea.getWidth();
            float y = plant.posY * plotArea.getHeight();

            flower.setX(x - flower.getWidth() / 2f);
            flower.setY(y - flower.getHeight() / 2f);
        });
    }

    private void checkSeedAvailability() {
        AppRepository repo =
                AppRepository.getInstance(requireActivity().getApplication());

        String today = com.example.cuby.utils.DateUtils.getTodayDate();

        repo.getExecutor().execute(() -> {
            com.example.cuby.model.DailyLog log =
                    repo.getDailyLogSync(today);

            if (log == null) return;

            requireActivity().runOnUiThread(() -> {

                // 🌱 Show seed ONLY if unlocked and NOT planted
                if (log.seedUnlocked && !log.seedPlanted) {
                    seedIcon.setVisibility(View.VISIBLE);
                } else {
                    seedIcon.setVisibility(View.GONE);
                }
            });
        });
    }
    private void markSeedAsPlanted() {
        AppRepository repo =
                AppRepository.getInstance(requireActivity().getApplication());

        String today = com.example.cuby.utils.DateUtils.getTodayDate();

        repo.getExecutor().execute(() -> {
            com.example.cuby.model.DailyLog log =
                    repo.getDailyLogSync(today);

            if (log == null) return;

            log.seedPlanted = true;
            repo.insertDailyLog(log);
        });
    }

    private void loadPlantsForMonth(String yearMonth) {
        AppRepository repo =
                AppRepository.getInstance(requireActivity().getApplication());

        repo.getExecutor().execute(() -> {
            java.util.List<GardenPlant> plants =
                    repo.gardenPlantDao().getPlantsForMonth(yearMonth);

            requireActivity().runOnUiThread(() -> {
                plotArea.removeAllViews();
                for (GardenPlant plant : plants) {
                    placePlantView(plant);
                }
            });
        });
    }



}
