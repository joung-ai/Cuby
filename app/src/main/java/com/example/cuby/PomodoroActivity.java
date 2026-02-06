package com.example.cuby;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cuby.data.AppRepository;
import com.example.cuby.logic.CubyMoodEngine;
import com.example.cuby.utils.DateUtils;



import java.util.Locale;

public class PomodoroActivity extends AppCompatActivity {

    private TextView tvTimer;
    private Button btnPomodoro, btnShortBreak, btnLongBreak, btnPause, btnResetTimer;

    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;

    private long timeLeftInMillis;
    private boolean isTimerRunning = false;

    private static final long POMODORO_TIME = 25 * 60 * 1000;
    private static final long SHORT_BREAK_TIME = 5 * 60 * 1000;
    private static final long LONG_BREAK_TIME = 15 * 60 * 1000;

    private CubyMoodEngine cubyMoodEngine;
    private Handler progressHandler;
    private Runnable progressRunnable;
    private boolean isFocusSession = false;

    private String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pomodoro); // reuse the same layout ✅

        cubyMoodEngine =
                new CubyMoodEngine(
                        AppRepository.getInstance(getApplication())


                );

        progressHandler = new Handler(Looper.getMainLooper());

        today = DateUtils.getTodayDate();

        tvTimer = findViewById(R.id.tvTimer);
        btnPomodoro = findViewById(R.id.btnPomodoro);
        btnShortBreak = findViewById(R.id.btnShortBreak);
        btnLongBreak = findViewById(R.id.btnLongBreak);
        btnPause = findViewById(R.id.btnPause);
        btnResetTimer = findViewById(R.id.btnResetTimer);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarmtone);

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

        isFocusSession = (duration == POMODORO_TIME); // ✅ ONLY focus counts

        timeLeftInMillis = duration;
        startTimer();
        btnPause.setText("Pause");
        lockModeButtons();
    }

    private void startTimer() {
        if (isTimerRunning) return;

        startTaskProgressTracking(); // ✅ ADD THIS

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                stopTaskProgressTracking(); // ✅ ADD THIS

                tvTimer.setText("Time's up!");
                playAlarm();
                unlockModeButtons();

                finishPomodoroTask(); // ✅ ADD THIS
            }
        }.start();

        isTimerRunning = true;
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopTaskProgressTracking(); // ✅ ADD
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

    private void startTaskProgressTracking() {
        if (!isFocusSession) return; // 🚫 no progress during breaks

        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isTimerRunning) return;

                cubyMoodEngine.addTaskProgress(
                        today,
                        1
                );

                progressHandler.postDelayed(this, 1000);
            }
        };

        progressHandler.postDelayed(progressRunnable, 1000);
    }

    private void finishPomodoroTask() {
        isFocusSession = false;
        cubyMoodEngine.completeCurrentTask(today);

        setResult(Activity.RESULT_OK);
        finish();
    }


    private void stopTaskProgressTracking() {
        if (progressHandler != null && progressRunnable != null) {
            progressHandler.removeCallbacks(progressRunnable);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        stopTaskProgressTracking();
    }
}
