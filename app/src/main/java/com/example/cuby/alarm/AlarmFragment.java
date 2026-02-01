package com.example.cuby.alarm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.cuby.R;
import com.example.cuby.ui.home.HomeActivity;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AlarmFragment extends Fragment {

    private static final String PREFS_NAME = "alarm_prefs";
    private static final String KEY_SOUND_WARNING_SHOWN = "sound_warning_shown";

    private TimePicker timePicker;
    private TextView txtAlarmStatus;
    private Button btnSetAlarm, btnCancelAlarm, btnViewAlarms;

    private OneTimeWorkRequest currentAlarm;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        // ✅ LOAD SAVED ALARMS
        AlarmStore.load(requireContext());

        requestPermission();

        timePicker = view.findViewById(R.id.timePicker);
        txtAlarmStatus = view.findViewById(R.id.txtAlarmStatus);
        btnSetAlarm = view.findViewById(R.id.btnSetAlarm);
//        btnCancelAlarm = view.findViewById(R.id.btnCancelAlarm);
        btnViewAlarms = view.findViewById(R.id.btnViewAlarms);

        btnSetAlarm.setOnClickListener(v -> {
            if (!hasShownSoundWarning()) {
                showSoundWarningDialog();
                markSoundWarningShown();
                return;
            }
            setAlarm();
        });

//        btnCancelAlarm.setOnClickListener(v -> cancelAlarm());

        // ✅ GO TO ALARM LIST PAGE
        btnViewAlarms.setOnClickListener(v ->
                ((HomeActivity) requireActivity())
                        .navigateTo(new AlarmListFragment(), true)
        );

        return view;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1001
            );
        }
    }

    private void setAlarm() {

        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        if (target.getTimeInMillis() <= now.getTimeInMillis()) {
            target.add(Calendar.DAY_OF_MONTH, 1);
        }

        long delay = target.getTimeInMillis() - now.getTimeInMillis();

        currentAlarm =
                new OneTimeWorkRequest.Builder(ReminderWorker.class)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build();

        WorkManager.getInstance(requireContext()).enqueue(currentAlarm);

        String time = String.format("%02d:%02d", hour, minute);

        // ✅ STORE ALARM + SAVE
        AlarmStore.alarms.add(
                new AlarmItem(time, currentAlarm.getId())
        );
        AlarmStore.save(requireContext());

        txtAlarmStatus.setText("Alarm set for " + time);
        Toast.makeText(getContext(), "Alarm set", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm() {
        if (currentAlarm != null) {
            WorkManager.getInstance(requireContext())
                    .cancelWorkById(currentAlarm.getId());
            txtAlarmStatus.setText("Alarm cancelled");
        }
    }

    private void showSoundWarningDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Enable Alarm Sound")
                .setMessage("Please enable sound so your alarm will ring.")
                .setPositiveButton("Open Settings", (d, w) -> openAlarmSoundSettings())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openAlarmSoundSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
            startActivity(intent);
        }
    }

    private boolean hasShownSoundWarning() {
        return requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_SOUND_WARNING_SHOWN, false);
    }

    private void markSoundWarningShown() {
        requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_SOUND_WARNING_SHOWN, true)
                .apply();
    }
}
