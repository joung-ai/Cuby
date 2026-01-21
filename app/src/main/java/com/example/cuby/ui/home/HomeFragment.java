package com.example.cuby.ui.home;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
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
    private AnimatorSet bounceAnimator;
    private Handler animationHandler = new Handler(Looper.getMainLooper());
    private boolean isAnimating = false;

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
        animateCardsIn(view);
    }

    private void setupNavigation(View view) {
        view.findViewById(R.id.cardChat).setOnClickListener(v -> {
            animateCardPress(v);
            animationHandler.postDelayed(() -> navigateTo(new ChatFragment()), 150);
        });
        view.findViewById(R.id.cardDiary).setOnClickListener(v -> {
            animateCardPress(v);
            animationHandler.postDelayed(() -> navigateTo(new DiaryFragment()), 150);
        });
        view.findViewById(R.id.cardGarden).setOnClickListener(v -> {
            animateCardPress(v);
            animationHandler.postDelayed(() -> navigateTo(new GardenFragment()), 150);
        });
        view.findViewById(R.id.cardDetox).setOnClickListener(v -> {
            animateCardPress(v);
            animationHandler.postDelayed(() -> navigateTo(new DetoxFragment()), 150);
        });
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> navigateTo(new SettingsFragment()));
        
        view.findViewById(R.id.btnFeed).setOnClickListener(v -> {
            viewModel.feedCuby();
            playCubyHappyAnimation();
            Toast.makeText(getContext(), "Yum! Cuby feels happy!", Toast.LENGTH_SHORT).show();
        });
    }

    private void animateCardPress(View view) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction(() -> 
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start())
            .start();
    }

    private void animateCardsIn(View root) {
        int[] cardIds = {R.id.cardChat, R.id.cardDiary, R.id.cardGarden, R.id.cardDetox};
        for (int i = 0; i < cardIds.length; i++) {
            View card = root.findViewById(cardIds[i]);
            card.setAlpha(0f);
            card.setTranslationY(50f);
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(i * 100L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        }
    }

    private void navigateTo(Fragment fragment) {
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).navigateTo(fragment, true);
        }
    }

    private void setupCubyInteraction() {
        ivCuby.setOnClickListener(v -> {
            if (!isAnimating) {
                playCubyBounceAnimation();
            }
        });
    }

    private void playCubyBounceAnimation() {
        if (isAnimating) return;
        isAnimating = true;

        // Stop breathing animation temporarily
        if (breathingAnimator != null) {
            breathingAnimator.pause();
        }

        // Bounce animation with overshoot
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivCuby, View.SCALE_X, 1f, 1.15f, 0.9f, 1.05f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivCuby, View.SCALE_Y, 1f, 0.85f, 1.1f, 0.95f, 1f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(ivCuby, View.ROTATION, 0f, -8f, 8f, -4f, 0f);

        bounceAnimator = new AnimatorSet();
        bounceAnimator.playTogether(scaleX, scaleY, rotate);
        bounceAnimator.setDuration(600);
        bounceAnimator.setInterpolator(new OvershootInterpolator(1.2f));
        
        bounceAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                isAnimating = false;
                ivCuby.setImageResource(R.drawable.cuby_happy);
                animationHandler.postDelayed(() -> {
                    if (isAdded()) {
                        ivCuby.setImageResource(R.drawable.cuby_idle);
                        if (breathingAnimator != null) {
                            breathingAnimator.resume();
                        }
                    }
                }, 1500);
            }
        });
        
        bounceAnimator.start();
    }

    private void playCubyHappyAnimation() {
        if (isAnimating) return;
        isAnimating = true;

        if (breathingAnimator != null) {
            breathingAnimator.pause();
        }

        ivCuby.setImageResource(R.drawable.cuby_happy);

        // Jump animation
        ObjectAnimator jump = ObjectAnimator.ofFloat(ivCuby, View.TRANSLATION_Y, 0f, -30f, 0f, -15f, 0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivCuby, View.SCALE_X, 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivCuby, View.SCALE_Y, 1f, 0.9f, 1.1f, 1f);

        AnimatorSet happyAnim = new AnimatorSet();
        happyAnim.playTogether(jump, scaleX, scaleY);
        happyAnim.setDuration(500);
        happyAnim.setInterpolator(new OvershootInterpolator());
        
        happyAnim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                animationHandler.postDelayed(() -> {
                    if (isAdded()) {
                        ivCuby.setImageResource(R.drawable.cuby_idle);
                        isAnimating = false;
                        if (breathingAnimator != null) {
                            breathingAnimator.resume();
                        }
                    }
                }, 1000);
            }
        });
        
        happyAnim.start();
    }

    private void startBreathingAnimation() {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.03f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.03f);
        
        breathingAnimator = ObjectAnimator.ofPropertyValuesHolder(ivCuby, scaleX, scaleY);
        breathingAnimator.setDuration(2500);
        breathingAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        breathingAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        breathingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        breathingAnimator.start();
    }

    private void observeData() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                tvGreeting.setText("Hi, " + profile.username + "!");
            }
        });

        viewModel.getInventory().observe(getViewLifecycleOwner(), inventory -> {
            if (inventory != null) {
                tvFoodCount.setText("Bloxy Food: " + inventory.bloxyFoodCount);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (breathingAnimator != null) {
            breathingAnimator.cancel();
        }
        if (bounceAnimator != null) {
            bounceAnimator.cancel();
        }
        animationHandler.removeCallbacksAndMessages(null);
    }
}
