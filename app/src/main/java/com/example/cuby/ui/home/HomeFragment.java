package com.example.cuby.ui.home;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cuby.BreathingTechniquesActivity;
import com.example.cuby.BloxxGame;              // ✅ ADDED
import com.example.cuby.R;
import com.example.cuby.alarm.AlarmFragment;
import com.example.cuby.data.AppRepository;
import com.example.cuby.logic.CubyMoodEngine;
import com.example.cuby.ui.chat.ChatFragment;
import com.example.cuby.ui.diary.DiaryFragment;
import com.example.cuby.ui.garden.GardenFragment;
import com.example.cuby.ui.settings.SettingsFragment;
import com.google.android.material.button.MaterialButton; // ✅ ADDED

import java.time.LocalDate;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    private TextView tvCubyBubble;
    private View layoutMoodButtons;
    private AppRepository repository;
    private CubyMoodEngine cubyMoodEngine;

    private ImageView ivCuby;
    private View cubyShadow;

    private Handler animationHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        ivCuby = view.findViewById(R.id.ivCuby);
        cubyShadow = view.findViewById(R.id.cubyShadow);

        tvCubyBubble = view.findViewById(R.id.tvCubyBubble);
        layoutMoodButtons = view.findViewById(R.id.layoutMoodButtons);

        repository = AppRepository.getInstance(requireActivity().getApplication());
        cubyMoodEngine = new CubyMoodEngine(repository);

        // 🔵 Meditate Button
        ImageButton btnMeditate = view.findViewById(R.id.btnMeditate);
        btnMeditate.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), BreathingTechniquesActivity.class);
            startActivity(intent);
        });

        // 🎮 CUBY BLOXX BUTTON (NEW)
        MaterialButton btnCubyBloxx = view.findViewById(R.id.btnCubyBloxx);
        btnCubyBloxx.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), BloxxGame.class);
            startActivity(intent);
        });

        setupNavigation(view);
        setupCubyInteraction();
        observeData();
        startBreathingAnimation();

        String today = LocalDate.now().toString();

        repository.getDailyLog(today).observe(getViewLifecycleOwner(), log -> {

            if (log == null || log.mood == null) {
                tvCubyBubble.setText("Hey… how are you feeling today?");
                tvCubyBubble.setVisibility(View.VISIBLE);
                layoutMoodButtons.setVisibility(View.VISIBLE);
            } else {
                tvCubyBubble.setText(
                        cubyMoodEngine.getCubyMessage(
                                log.mood,
                                false,
                                log.seedUnlocked
                        )
                );
                tvCubyBubble.setVisibility(View.VISIBLE);
                layoutMoodButtons.setVisibility(View.GONE);
            }
        });
    }

    private void setupNavigation(View view) {

        view.findViewById(R.id.btnSettings)
                .setOnClickListener(v -> navigateWithAnimation(new SettingsFragment()));

        view.findViewById(R.id.btnChat)
                .setOnClickListener(v -> navigateWithAnimation(new ChatFragment()));

        view.findViewById(R.id.btnDiary)
                .setOnClickListener(v -> navigateWithAnimation(new DiaryFragment()));

        view.findViewById(R.id.btnGarden)
                .setOnClickListener(v -> navigateWithAnimation(new GardenFragment()));

        view.findViewById(R.id.btnFeed).setOnClickListener(v -> {
            viewModel.feedCuby();
            showHappyCuby();
            Toast.makeText(getContext(), "Yum! Cuby feels happy! 💕", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnAlarm)
                .setOnClickListener(v -> navigateWithAnimation(new AlarmFragment()));

        setupMoodButton(view, R.id.btnCalm, "CALM");
        setupMoodButton(view, R.id.btnOkay, "OKAY");
        setupMoodButton(view, R.id.btnTired, "TIRED");
        setupMoodButton(view, R.id.btnOverwhelmed, "OVERWHELMED");
        setupMoodButton(view, R.id.btnHappy, "HAPPY");
    }

    private void navigateWithAnimation(Fragment fragment) {
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).navigateTo(fragment, true);
        }
    }

    private void setupCubyInteraction() {
        ivCuby.setOnClickListener(v -> {
            Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            ivCuby.startAnimation(bounce);
            showHappyCuby();
        });
    }

    private void showHappyCuby() {
        ivCuby.setImageResource(R.drawable.cuby_happy);

        animationHandler.postDelayed(() -> {
            if (isAdded()) {
                ivCuby.setImageResource(R.drawable.cuby_idle);
            }
        }, 2000);
    }

    private void startBreathingAnimation() {

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(2200);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        animator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();

            float cubyScale = 1.0f + (0.05f * t);
            ivCuby.setScaleX(cubyScale);
            ivCuby.setScaleY(cubyScale);

            if (cubyShadow != null) {
                cubyShadow.setScaleX(1.0f + (0.08f * t));
                cubyShadow.setScaleY(1.0f + (0.04f * t));
                cubyShadow.setAlpha(0.25f + (0.25f * t));
            }
        });

        animator.start();
    }

    private void observeData() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                if ("Pink".equals(profile.cubySkin)) {
                    ivCuby.setColorFilter(0x40FFC0CB);
                } else if ("Blue".equals(profile.cubySkin)) {
                    ivCuby.setColorFilter(0x4087CEEB);
                } else {
                    ivCuby.clearColorFilter();
                }
            }
        });
    }

    private void setupMoodButton(View root, int id, String mood) {
        root.findViewById(id).setOnClickListener(v -> {

            String today = LocalDate.now().toString();
            cubyMoodEngine.recordDailyMood(today, mood);

            tvCubyBubble.setText(
                    cubyMoodEngine.getCubyMessage(mood, false, false)
            );

            layoutMoodButtons.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        animationHandler.removeCallbacksAndMessages(null);
    }
}
