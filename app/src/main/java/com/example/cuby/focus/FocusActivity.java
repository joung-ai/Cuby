package com.example.cuby.focus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cuby.FocusOverlayService;
import com.example.cuby.R;
import com.example.cuby.data.AppRepository;
import com.example.cuby.logic.CubyMoodEngine;
import com.example.cuby.logic.DailyTask;
import com.example.cuby.model.DailyLog;
import com.example.cuby.utils.DateUtils;

public class FocusActivity extends AppCompatActivity {

    private static final int OVERLAY_REQ_CODE = 101;

    private Handler handler;
    private Runnable progressRunnable;

    private CubyMoodEngine cubyMoodEngine;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        cubyMoodEngine =
                new CubyMoodEngine(
                        AppRepository.getInstance(getApplication())
                );

        handler = new Handler(Looper.getMainLooper());

        TextView tvInfo = findViewById(R.id.tvFocusInfo);
        Button btnStart = findViewById(R.id.btnStartFocus);
        Button btnCancel = findViewById(R.id.btnCancelFocus);

        tvInfo.setText("Focus mode helps you avoid distractions.\nYou can cancel anytime.");

        btnStart.setOnClickListener(v -> checkOverlayPermission());
        btnCancel.setOnClickListener(v -> finishFocus(false));
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, OVERLAY_REQ_CODE);
        } else {
            startFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_REQ_CODE && Settings.canDrawOverlays(this)) {
            startFocus();
        }
    }

    private void startFocus() {
        startService(new Intent(this, FocusOverlayService.class));

        isRunning = true;
        startProgressTracking();
    }

    private void startProgressTracking() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                cubyMoodEngine.addTaskProgress(
                        DateUtils.getTodayDate(),
                        1
                );

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(progressRunnable, 1000);
    }

    private void finishFocus(boolean completed) {
        if (!isRunning) return;

        isRunning = false;

        stopService(new Intent(this, FocusOverlayService.class));
        handler.removeCallbacks(progressRunnable);

        if (completed) {
            setResult(RESULT_OK);
        }

        finish();
    }
}
