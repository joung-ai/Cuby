package com.example.cuby;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Four78BreathingActivity extends AppCompatActivity {

    private View breathCircle1, breathCircle2, breathCircle3;
    private ImageView ivCuby;
    private TextView tvInstruction, tvCount, tvCycle;
    private Button btnStart;

    private static final int MAX_CYCLES = 4;
    private int currentCycle = 0;
    private boolean isRunning = false;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_478_breathing);

        // Initialize Views
        breathCircle1 = findViewById(R.id.breathCircle1);
        breathCircle2 = findViewById(R.id.breathCircle2);
        breathCircle3 = findViewById(R.id.breathCircle3);
        ivCuby = findViewById(R.id.ivCuby);
        tvInstruction = findViewById(R.id.tvInstruction);
        tvCount = findViewById(R.id.tvCount);
        tvCycle = findViewById(R.id.tvCycle);
        btnStart = findViewById(R.id.btnStart);

        // Keep count text on top
        tvCount.bringToFront();

        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                currentCycle = 0;
                isRunning = true;

                btnStart.setVisibility(View.GONE);
                tvCycle.setVisibility(View.VISIBLE);

                updateCycleText();
                startCycle();
            }
        });
    }

    private void startCycle() {
        if (!isRunning) return;

        if (currentCycle >= MAX_CYCLES) {
            finishBreathing();
            return;
        }

        currentCycle++;
        updateCycleText();

        // --- INHALE (4s) ---
        startPhase("Inhale", 4);
        animateScale(ivCuby, 1f, 2f, 4000);
        animateScale(breathCircle1, 1f, 1.4f, 4000);
        animateScale(breathCircle2, 1f, 1.3f, 4000);
        animateScale(breathCircle3, 1f, 1.2f, 4000);

        // --- HOLD (7s) ---
        handler.postDelayed(() -> startPhase("Hold", 7), 4000);

        // --- EXHALE (8s) ---
        handler.postDelayed(() -> {
            startPhase("Exhale", 8);
            animateScale(ivCuby, 2f, 1f, 8000);
            animateScale(breathCircle1, 1.4f, 1f, 8000);
            animateScale(breathCircle2, 1.3f, 1f, 8000);
            animateScale(breathCircle3, 1.2f, 1f, 8000);
        }, 4000 + 7000);

        // --- Next cycle ---
        handler.postDelayed(this::startCycle, 4000 + 7000 + 8000);
    }

    private void startPhase(String label, int seconds) {
        tvInstruction.setText(label);
        tvCount.setText("1");

        for (int i = 2; i <= seconds; i++) {
            int value = i;
            handler.postDelayed(() -> tvCount.setText(String.valueOf(value)), (i - 1) * 1000L);
        }
    }

    private void animateScale(View view, float from, float to, int duration) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", from, to);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", from, to);
        scaleX.setDuration(duration);
        scaleY.setDuration(duration);
        scaleX.start();
        scaleY.start();
    }

    private void updateCycleText() {
        tvCycle.setText("Cycle " + currentCycle + " / " + MAX_CYCLES);
    }

    private void finishBreathing() {
        isRunning = false;
        tvInstruction.setText("Done");
        tvCount.setText("");
        tvCycle.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);
    }
}
