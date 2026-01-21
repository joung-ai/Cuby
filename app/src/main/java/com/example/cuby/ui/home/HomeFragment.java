package com.example.cuby.ui.home;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.cuby.R;
import com.example.cuby.ui.chat.ChatFragment;
import com.example.cuby.ui.diary.DiaryFragment;
import com.example.cuby.ui.garden.GardenFragment;
import com.example.cuby.ui.detox.DetoxFragment;
import com.example.cuby.ui.settings.SettingsFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private TextView tvGreeting;
    private TextView tvFoodCount;
    private ImageView ivCuby;
    private ObjectAnimator breathingAnimator;
    private Handler animationHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvFoodCount = view.findViewById(R.id.tvFoodCount);
        ivCuby = view.findViewById(R.id.ivCuby);

        setupNavigation(view);
        setupCubyInteraction();
        observeData();
        startBreathingAnimation();
    }

    private void setupNavigation(View view) {
        // Card-based navigation
        view.findViewById(R.id.cardChat).setOnClickListener(v -> navigateWithAnimation(new ChatFragment()));
        view.findViewById(R.id.cardDiary).setOnClickListener(v -> navigateWithAnimation(new DiaryFragment()));
        view.findViewById(R.id.cardGarden).setOnClickListener(v -> navigateWithAnimation(new GardenFragment()));
        view.findViewById(R.id.cardDetox).setOnClickListener(v -> navigateWithAnimation(new DetoxFragment()));
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> navigateWithAnimation(new SettingsFragment()));
        
        view.findViewById(R.id.btnFeed).setOnClickListener(v -> {
            viewModel.feedCuby();
            showHappyCuby();
            Toast.makeText(getContext(), "Yum! Cuby feels happy! 💕", Toast.LENGTH_SHORT).show();
        });
    }

    private void navigateWithAnimation(Fragment fragment) {
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).navigateTo(fragment, true);
        }
    }

    private void setupCubyInteraction() {
        // Tap to make Cuby bounce and wave
        ivCuby.setOnClickListener(v -> {
            Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            ivCuby.startAnimation(bounce);
            showHappyCuby();
        });
    }

    private void showHappyCuby() {
        ivCuby.setImageResource(R.drawable.cuby_happy);
        // Return to idle after 2 seconds
        animationHandler.postDelayed(() -> {
            if (isAdded()) {
                ivCuby.setImageResource(R.drawable.cuby_idle);
            }
        }, 2000);
    }

    private void startBreathingAnimation() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.05f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.05f);
        
        breathingAnimator = ObjectAnimator.ofPropertyValuesHolder(ivCuby, scaleX, scaleY);
        breathingAnimator.setDuration(2000);
        breathingAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        breathingAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        breathingAnimator.start();
    }

    private void observeData() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                tvGreeting.setText("Hi, " + profile.username + "! 👋");
                
                // Update skin tint
                if ("Pink".equals(profile.cubySkin)) {
                    ivCuby.setColorFilter(0x40FFC0CB);
                } else if ("Blue".equals(profile.cubySkin)) {
                    ivCuby.setColorFilter(0x4087CEEB);
                } else {
                    ivCuby.clearColorFilter();
                }
            }
        });

        viewModel.getInventory().observe(getViewLifecycleOwner(), inventory -> {
            if (inventory != null) {
                tvFoodCount.setText("🍎 Bloxy Food: " + inventory.bloxyFoodCount);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (breathingAnimator != null) {
            breathingAnimator.cancel();
        }
        animationHandler.removeCallbacksAndMessages(null);
    }
}
