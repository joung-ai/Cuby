package com.example.cuby;

import android.animation.AnimatorSet;
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

public class BoxBreathingActivity extends AppCompatActivity {

    private ImageView dot;
    private View boxContainer;

    private TextView tvInstruction, tvCount, tvCycle;
    private Button btnStart;

    private static final int PHASE_DURATION = 4000; // 4 seconds
    private static final int MAX_CYCLES = 4;

    private int currentCycle = 0;
    private boolean isRunning = false;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private float startX, endX, startY, endY;

    private CubyMoodEngine cubyMoodEngine;
    private Runnable progressRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_breathing);

        cubyMoodEngine =
                new CubyMoodEngine(
                        AppRepository.getInstance(getApplication())
                );


        // Views
        btnStart = findViewById(R.id.btnStart);
        tvCycle = findViewById(R.id.tvCycle);

        boxContainer = findViewById(R.id.boxContainer);
        dot = findViewById(R.id.dot);
        tvInstruction = findViewById(R.id.tvInstruction);
        tvCount = findViewById(R.id.tvCount);

        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                currentCycle = 0;
                isRunning = true;

                btnStart.setVisibility(View.GONE);
                tvCycle.setVisibility(View.VISIBLE);

                updateCycleText();
                startProgressTracking();
                startBoxBreathing();


            }
        });
    }

    private void startBoxBreathing() {

        if (!isRunning) return;

        if (currentCycle >= MAX_CYCLES) {
            finishBreathing();
            return;
        }

        float boxWidth = boxContainer.getWidth();
        float boxHeight = boxContainer.getHeight();
        float dotSize = dot.getWidth();

        startX = 0;
        endX = boxWidth - dotSize;
        startY = 0;
        endY = boxHeight - dotSize;

        dot.setTranslationX(startX);
        dot.setTranslationY(startY);

        ObjectAnimator inhale =
                ObjectAnimator.ofFloat(dot, "translationX", startX, endX);
        inhale.setDuration(PHASE_DURATION);

        ObjectAnimator holdRight =
                ObjectAnimator.ofFloat(dot, "translationY", startY, endY);
        holdRight.setDuration(PHASE_DURATION);

        ObjectAnimator exhale =
                ObjectAnimator.ofFloat(dot, "translationX", endX, startX);
        exhale.setDuration(PHASE_DURATION);

        ObjectAnimator holdLeft =
                ObjectAnimator.ofFloat(dot, "translationY", endY, startY);
        holdLeft.setDuration(PHASE_DURATION);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(inhale, holdRight, exhale, holdLeft);
        set.start();

        startCounting("Inhale", 0);
        startCounting("Hold", 4000);
        startCounting("Exhale", 8000);
        startCounting("Hold", 12000);

        currentCycle++;
        updateCycleText();

        handler.postDelayed(this::startBoxBreathing, 16000);
    }

    private void updateCycleText() {
        tvCycle.setText("Cycle " + currentCycle + " / " + MAX_CYCLES);
    }

    private void startCounting(String label, int delay) {
        handler.postDelayed(() -> {
            tvInstruction.setText(label);
            tvCount.setText("1");

            handler.postDelayed(() -> tvCount.setText("2"), 1000);
            handler.postDelayed(() -> tvCount.setText("3"), 2000);
            handler.postDelayed(() -> tvCount.setText("4"), 3000);
        }, delay);
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

    private void finishBreathing() {
        isRunning = false;

        handler.removeCallbacks(progressRunnable);

        tvInstruction.setText("Done");
        tvCount.setText("");
        tvCycle.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);

        // notify HomeFragment
        setResult(RESULT_OK);
        finish();
    }

}
