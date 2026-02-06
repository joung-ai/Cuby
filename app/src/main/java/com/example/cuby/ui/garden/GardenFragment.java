package com.example.cuby.ui.garden;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.cuby.ui.drawing.DrawingActivity;

import com.example.cuby.R;
import com.example.cuby.audio.MusicManager;

public class GardenFragment extends Fragment {

    private View plotArea;
    private ImageView seedIcon;

    private float dX, dY;
    private float seedStartX, seedStartY;

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

        // Save original seed position (for reset)
        seedIcon.post(() -> {
            seedStartX = seedIcon.getX();
            seedStartY = seedIcon.getY();
            restoreSeedPosition();
        });

        makeSeedDraggable();
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

        float plotX = plotArea.getX();
        float plotY = plotArea.getY();

        float relX = (centerX - plotX) / plotArea.getWidth();
        float relY = (centerY - plotY) / plotArea.getHeight();

        // Clamp safety
        relX = Math.max(0f, Math.min(1f, relX));
        relY = Math.max(0f, Math.min(1f, relY));

        // 💾 Save planted position
        saveSeedPosition(relX, relY);

        // 🌱 Snap seed visually
        seed.setX(plotX + relX * plotArea.getWidth() - seed.getWidth() / 2f);
        seed.setY(plotY + relY * plotArea.getHeight() - seed.getHeight() / 2f);

        // 🚀 Launch Drawing Activity
        launchDrawingActivity(relX, relY);
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
    private void saveSeedPosition(float x, float y) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("garden", Context.MODE_PRIVATE);

        prefs.edit()
                .putFloat("seed_x", x)
                .putFloat("seed_y", y)
                .apply();
    }

    //open drawing activity
    private void launchDrawingActivity(float relX, float relY) {
        Intent intent = new Intent(requireContext(), DrawingActivity.class);

        // Pass planted position (optional but future-proof)
        intent.putExtra("plant_x", relX);
        intent.putExtra("plant_y", relY);

        startActivity(intent);
    }


    //  RESTORE ON REOPEN
    private void restoreSeedPosition() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("garden", Context.MODE_PRIVATE);

        if (!prefs.contains("seed_x")) return;

        float relX = prefs.getFloat("seed_x", 0.5f);
        float relY = prefs.getFloat("seed_y", 0.5f);

        plotArea.post(() -> {
            float x = plotArea.getX() + relX * plotArea.getWidth();
            float y = plotArea.getY() + relY * plotArea.getHeight();

            seedIcon.setX(x - seedIcon.getWidth() / 2f);
            seedIcon.setY(y - seedIcon.getHeight() / 2f);
        });
    }
}
