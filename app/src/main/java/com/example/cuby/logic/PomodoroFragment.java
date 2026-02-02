package com.example.cuby.logic;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cuby.R;

import java.util.Locale;

public class PomodoroFragment extends Fragment {

    private TextView tvTimer;
    private Button btnPomodoro, btnShortBreak, btnLongBreak, btnPause, btnResetTimer;

    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;

    private long timeLeftInMillis;
    private boolean isTimerRunning = false;

    private static final long POMODORO_TIME = 25 * 60 * 1000;
    private static final long SHORT_BREAK_TIME = 5 * 60 * 1000;
    private static final long LONG_BREAK_TIME = 15 * 60 * 1000;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_pomodoro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTimer = view.findViewById(R.id.tvTimer);
        btnPomodoro = view.findViewById(R.id.btnPomodoro);
        btnShortBreak = view.findViewById(R.id.btnShortBreak);
        btnLongBreak = view.findViewById(R.id.btnLongBreak);
        btnPause = view.findViewById(R.id.btnPause);
        btnResetTimer = view.findViewById(R.id.btnResetTimer);

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarmtone);

        timeLeftInMillis = POMODORO_TIME;
        updateTimerText();

        btnPomodoro.setOnClickListener(v -> startNewTimer(POMODORO_TIME));
        btnShortBreak.setOnClickListener(v -> startNewTimer(SHORT_BREAK_TIME));
        btnLongBreak.setOnClickListener(v -> startNewTimer(LONG_BREAK_TIME));

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

    private void startNewTimer(long duration) {
        pauseTimer();
        stopAlarm();

        timeLeftInMillis = duration;
        startTimer();
        btnPause.setText("Pause");
        lockModeButtons();
    }

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
                tvTimer.setText("Time's up!");
                playAlarm();
                unlockModeButtons();
            }
        }.start();

        isTimerRunning = true;
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopAlarm();
        isTimerRunning = false;
    }

    private void resetTimer() {
        pauseTimer();
        timeLeftInMillis = POMODORO_TIME;
        updateTimerText();
        btnPause.setText("Pause");
        unlockModeButtons();
    }

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

    private void playAlarm() {
        if (mediaPlayer != null) mediaPlayer.start();
    }

    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.prepareAsync();
        }
    }

    private void lockModeButtons() {
        btnPomodoro.setEnabled(false);
        btnShortBreak.setEnabled(false);
        btnLongBreak.setEnabled(false);
    }

    private void unlockModeButtons() {
        btnPomodoro.setEnabled(true);
        btnShortBreak.setEnabled(true);
        btnLongBreak.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
