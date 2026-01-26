package com.example.cuby.logic;


import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cuby.R;

import java.util.Locale;

public class Pomodoro extends AppCompatActivity {

    private TextView tvTimer;
    private Button btnPomodoro, btnShortBreak, btnLongBreak, btnPause, btnResetTimer;

    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;

    private long timeLeftInMillis;
    private boolean isTimerRunning = false;

    private static final long POMODORO_TIME = 25 * 60 * 1000;
    private static final long SHORT_BREAK_TIME = 5 * 60 * 1000;
    private static final long LONG_BREAK_TIME = 15 * 60 * 1000;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pomodoro);

        tvTimer = findViewById(R.id.tvTimer);
        btnPomodoro = findViewById(R.id.btnPomodoro);
        btnShortBreak = findViewById(R.id.btnShortBreak);
        btnLongBreak = findViewById(R.id.btnLongBreak);
        btnPause = findViewById(R.id.btnPause);
        btnResetTimer = findViewById(R.id.btnResetTimer);

        // 🔔 Initialize alarm sound
        mediaPlayer = MediaPlayer.create(this, R.raw.alarmtone);

        timeLeftInMillis = POMODORO_TIME;
        updateTimerText();

        btnPomodoro.setOnClickListener(v -> startNewTimer(POMODORO_TIME));
        btnShortBreak.setOnClickListener(v -> startNewTimer(SHORT_BREAK_TIME));
        btnLongBreak.setOnClickListener(v -> startNewTimer(LONG_BREAK_TIME));

        // ⏸ Pause / ▶ Resume
        btnPause.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
                btnPause.setText("Resume");
            } else {
                startTimer();
                btnPause.setText("Pause");
            }
        });

        btnResetTimer.setOnClickListener(v -> resetTimer());
    }

    // ▶ Start a NEW Pomodoro / Break
    private void startNewTimer(long duration) {
        pauseTimer();
        stopAlarm();

        timeLeftInMillis = duration;
        startTimer();
        btnPause.setText("Pause");
        lockModeButtons();
    }

    // ▶ Start or Resume timer
    private void startTimer() {
        if (isTimerRunning) return;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                timeLeftInMillis = 0;

                // 🎉 Time's up message
                tvTimer.setText("Time's up!");

                // 🔔 Play alarm
                playAlarm();

                btnPause.setText("Pause");
                unlockModeButtons();
            }
        }.start();

        isTimerRunning = true;
    }

    // ⏸ Pause timer
    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopAlarm();
        isTimerRunning = false;
    }

    // 🔄 Reset timer
    private void resetTimer() {
        pauseTimer();
        timeLeftInMillis = POMODORO_TIME;
        updateTimerText();
        btnPause.setText("Pause");
        unlockModeButtons();
    }

    // ⏱ Update timer text
    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        tvTimer.setText(String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds
        ));
    }

    // 🔔 Play alarm sound
    private void playAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    // 🔕 Stop alarm sound
    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.prepareAsync();
        }
    }

    // 🔒 Disable Pomodoro / Break buttons
    private void lockModeButtons() {
        btnPomodoro.setEnabled(false);
        btnShortBreak.setEnabled(false);
        btnLongBreak.setEnabled(false);
    }

    // 🔓 Enable Pomodoro / Break buttons
    private void unlockModeButtons() {
        btnPomodoro.setEnabled(true);
        btnShortBreak.setEnabled(true);
        btnLongBreak.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}