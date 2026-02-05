package com.example.cuby.alarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.cuby.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AlarmListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        AlarmStore.load(requireContext());

        LinearLayout containerLayout = view.findViewById(R.id.alarmListContainer);
        containerLayout.removeAllViews();

        for (AlarmItem alarm : AlarmStore.alarms) {

            View row = createAlarmRow(alarm);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            params.bottomMargin =
                    (int) (12 * getResources().getDisplayMetrics().density);

            row.setLayoutParams(params);
            containerLayout.addView(row);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton btnAddAlarm = view.findViewById(R.id.btnAddAlarm);
        btnAddAlarm.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    private View createAlarmRow(AlarmItem alarm) {

        View row = LayoutInflater.from(getContext())
                .inflate(R.layout.item_alarm_row, null, false);

        TextView txtTime = row.findViewById(R.id.txtAlarmTime);
        Switch switchAlarm = row.findViewById(R.id.switchAlarm);
        TextView btnDelete = row.findViewById(R.id.btnDeleteAlarm);

        txtTime.setText(alarm.time);
        switchAlarm.setChecked(alarm.enabled);

        switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                String[] parts = alarm.time.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);

                Calendar now = Calendar.getInstance();
                Calendar target = Calendar.getInstance();

                target.set(Calendar.HOUR_OF_DAY, hour);
                target.set(Calendar.MINUTE, minute);
                target.set(Calendar.SECOND, 0);
                target.set(Calendar.MILLISECOND, 0);

                if (target.getTimeInMillis() <= now.getTimeInMillis()) {
                    target.add(Calendar.DAY_OF_MONTH, 1);
                }

                long delay = target.getTimeInMillis() - now.getTimeInMillis();

                OneTimeWorkRequest request =
                        new OneTimeWorkRequest.Builder(ReminderWorker.class)
                                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                .build();

                WorkManager.getInstance(requireContext()).enqueue(request);

                alarm.workId = request.getId();
                alarm.enabled = true;

            } else {
                WorkManager.getInstance(requireContext())
                        .cancelWorkById(alarm.workId);

                alarm.enabled = false;
            }
            AlarmStore.save(requireContext());
        });

        btnDelete.setOnClickListener(v -> {

            if (alarm.enabled) {
                WorkManager.getInstance(requireContext())
                        .cancelWorkById(alarm.workId);
            }

            AlarmStore.alarms.remove(alarm);
            AlarmStore.save(requireContext());

            ((ViewGroup) row.getParent()).removeView(row);
        });

        return row;
    }
}
