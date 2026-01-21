package com.example.cuby.ui.detox;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.cuby.R;
import com.example.cuby.data.AppRepository;
import com.example.cuby.model.DailyLog;
import com.example.cuby.model.Inventory;
import com.example.cuby.ui.drawing.DrawingActivity;
import com.example.cuby.utils.DateUtils;

public class DetoxFragment extends Fragment {

    private AppRepository repository;
    private TextView tvTimer;
    private Button btnCollect;
    private CountDownTimer timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = AppRepository.getInstance(getActivity().getApplication());
        
        tvTimer = view.findViewById(R.id.tvTimer);
        btnCollect = view.findViewById(R.id.btnCollect);
        
        btnCollect.setOnClickListener(v -> collectFreeFood());
        
        view.findViewById(R.id.taskWalk).setOnClickListener(v -> completeTask("Walk"));
        view.findViewById(R.id.taskBreathe).setOnClickListener(v -> completeTask("Breathe"));
        
        updateTimer();
    }

    private void completeTask(String task) {
        String today = DateUtils.getTodayDate();
        repository.getExecutor().execute(() -> {
             DailyLog log = repository.getDailyLogSync(today);
             boolean alreadyPlanted = log != null && log.seedPlanted;

             repository.addFood(2);
             
             if (getActivity() != null) {
                 getActivity().runOnUiThread(() -> {
                     if (alreadyPlanted) {
                         Toast.makeText(getContext(), "Good job! You earned food 🎉", Toast.LENGTH_SHORT).show();
                     } else {
                         Toast.makeText(getContext(), "Great job! You earned food & a seed 🌱", Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(getActivity(), DrawingActivity.class));
                     }
                 });
             }
        });
    }
    
    private void collectFreeFood() {
        repository.addFood(1);
        Inventory inv = new Inventory();
        inv.id = 1;
        inv.lastFreeFoodAt = System.currentTimeMillis();
        repository.insertInventory(inv);
        Toast.makeText(getContext(), "Collected free food! 🍎", Toast.LENGTH_SHORT).show();
        updateTimer();
    }

    private void updateTimer() {
        repository.getInventory().observe(getViewLifecycleOwner(), inv -> {
            if (inv != null) {
                long last = inv.lastFreeFoodAt;
                long now = System.currentTimeMillis();
                long diff = now - last;
                long interval = 3 * 60 * 60 * 1000; // 3 hours
                
                if (diff >= interval) {
                    btnCollect.setEnabled(true);
                    btnCollect.setText("Collect");
                    tvTimer.setText("Ready to collect!");
                } else {
                    btnCollect.setEnabled(false);
                    startCountDown(interval - diff);
                }
            }
        });
    }

    private void startCountDown(long millis) {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("Next food in: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Ready to collect!");
                btnCollect.setEnabled(true);
                btnCollect.setText("Collect");
            }
        }.start();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) timer.cancel();
    }
}
