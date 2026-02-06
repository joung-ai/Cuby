package com.example.cuby;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cuby.data.AppRepository;
import com.example.cuby.logic.CubyMoodEngine;
import com.example.cuby.utils.DateUtils;


import androidx.appcompat.app.AppCompatActivity;

public class Four78BreathingActivity extends AppCompatActivity {

    private ImageView ivCuby;
    private TextView tvInstruction, tvCount, tvCycle;
    private Button btnStart;

    private static final int MAX_CYCLES = 4;

    private int currentCycle = 0;
    private boolean isRunning = false;

    private CubyMoodEngine cubyMoodEngine;
    private Runnable progressRunnable;


    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_478_breathing);

        ivCuby = findViewById(R.id.ivCuby);
        tvInstruction = findViewById(R.id.tvInstruction);
        tvCount = findViewById(R.id.tvCount);
        tvCycle = findViewById(R.id.tvCycle);
        btnStart = findViewById(R.id.btnStart);
        cubyMoodEngine =
                new CubyMoodEngine(
                        AppRepository.getInstance(getApplication())
                );


        // Ensure counter stays on top of Cuby
        tvCount.bringToFront();

        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                currentCycle = 0;
                isRunning = true;

                btnStart.setVisibility(View.GONE);
                tvCycle.setVisibility(View.VISIBLE);

                updateCycleText();
                startCycle();
                startProgressTracking();

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

        // INHALE (4s)
        startPhase("Breathe in Through the Nose", 4);
        animateScale(1f, 2f, 4000);

        // HOLD (7s)
        handler.postDelayed(
                () -> startPhase("Hold", 7),
                4000
        );

        // EXHALE (8s)
        handler.postDelayed(() -> {
            startPhase("Breathe out Through the Mouth", 8);
            animateScale(2f, 1f, 8000);
        }, 4000 + 7000);

        // NEXT CYCLE
        handler.postDelayed(
                this::startCycle,
                4000 + 7000 + 8000
        );
    }

    private void startPhase(String label, int seconds) {
        tvInstruction.setText(label);
        tvCount.setText("1");

        for (int i = 2; i <= seconds; i++) {
            int value = i;
            handler.postDelayed(
                    () -> tvCount.setText(String.valueOf(value)),
                    (i - 1) * 1000L
            );
        }
    }

    private void animateScale(float from, float to, int duration) {
        ObjectAnimator scaleX =
                ObjectAnimator.ofFloat(ivCuby, "scaleX", from, to);
        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(ivCuby, "scaleY", from, to);

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

        tvInstruction.setText("Done 🌱");
        tvCount.setText("");
        tvCycle.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);

        setResult(RESULT_OK);

        handler.removeCallbacks(progressRunnable);

        finish();
    }

    private void startProgressTracking() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                cubyMoodEngine.addTaskProgress(
                        DateUtils.getTodayDate(),
                        1 // +1 second
                );

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(progressRunnable, 1000);
    }

}
