package com.example.cuby.ui.home;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent; // ✅ ADDED
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton; // ✅ ADDED
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cuby.BoxBreathingActivity;
import com.example.cuby.Four78BreathingActivity;
import com.example.cuby.focus.FocusActivity;
import com.example.cuby.logic.DailyTask;
import com.example.cuby.logic.PomodoroFragment;
import com.example.cuby.model.DailyLog;
import com.example.cuby.ui.breathing.BreathingTechniquesFragment; // ✅ ADDED
import com.example.cuby.R;
import com.example.cuby.alarm.AlarmFragment;
import com.example.cuby.data.AppRepository;
import com.example.cuby.logic.CubyMoodEngine;
import com.example.cuby.ui.chat.ChatFragment;
import com.example.cuby.ui.diary.DiaryFragment;
import com.example.cuby.ui.garden.GardenFragment;
import com.example.cuby.ui.detox.DetoxFragment;
import com.example.cuby.ui.productivity.ProductivityFragment;
import com.example.cuby.ui.settings.SettingsFragment;
import com.example.cuby.utils.DateUtils;
import com.example.cuby.PomodoroActivity;

import java.time.LocalDate;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private TextView tvGreeting;

    private TextView tvCubyBubble;
    private View layoutMoodButtons;
    private AppRepository repository;
    private CubyMoodEngine cubyMoodEngine;
    private TextView tvFoodCount;
    private ImageView ivCuby;
    private ObjectAnimator breathingAnimator;
    private View cubyShadow;

    private Handler animationHandler = new Handler(Looper.getMainLooper());

    String today = DateUtils.getTodayDate();

    // Task Widget UI
    private View taskWidget;
    private View taskContent;
    private TextView tvTaskTitle, tvTaskDesc, tvTaskProgress;
    private ProgressBar taskProgressBar;
    private ImageView btnToggle;
    private Button btnGoTask;

    private boolean isTaskExpanded = false;

    private ActivityResultLauncher<Intent> taskLauncher;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        ivCuby = view.findViewById(R.id.ivCuby);

        cubyShadow = view.findViewById(R.id.cubyShadow);

        tvCubyBubble = view.findViewById(R.id.tvCubyBubble);
        layoutMoodButtons = view.findViewById(R.id.layoutMoodButtons);

        repository = AppRepository.getInstance(requireActivity().getApplication());

        cubyMoodEngine = new CubyMoodEngine(repository);

        taskLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                cubyMoodEngine.completeCurrentTask(today);

                                Toast.makeText(
                                        requireContext(),
                                        "🌱 You earned a seed!",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );



        setupNavigation(view);
        setupCubyInteraction();
        observeData();
        startBreathingAnimation();
        setupTaskWidget(view);
        observeTodayLog();

    }

    private void setupNavigation(View view) {
        // New Side Navigation
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> navigateWithAnimation(new SettingsFragment()));
        view.findViewById(R.id.btnChat).setOnClickListener(v -> navigateWithAnimation(new ChatFragment()));
        view.findViewById(R.id.btnDiary).setOnClickListener(v -> navigateWithAnimation(new DiaryFragment()) );

        // Bottom Bar
        view.findViewById(R.id.btnMeditate).setOnClickListener(v -> navigateWithAnimation(new BreathingTechniquesFragment()));
        view.findViewById(R.id.btnGarden).setOnClickListener(v -> navigateWithAnimation(new GardenFragment())); // Diary/Garden
        view.findViewById(R.id.btnProductive).setOnClickListener(v -> navigateWithAnimation(new ProductivityFragment()));
        view.findViewById(R.id.btnMeditate).setOnClickListener(v -> navigateWithAnimation(new BreathingTechniquesFragment()));
        view.findViewById(R.id.btnFeed).setOnClickListener(v -> {
            viewModel.feedCuby();
            showHappyCuby();
            Toast.makeText(getContext(), "Yum! Cuby feels happy! 💕", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnAlarm).setOnClickListener(v -> navigateWithAnimation(new AlarmFragment()));

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
        // Return to idle after 2 seconds
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
        animator.setInterpolator(new android.view.animation.LinearInterpolator());

        animator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();

            // ---- CUBY ----
            float cubyScale = 1.0f + (0.05f * t);
            ivCuby.setScaleX(cubyScale);
            ivCuby.setScaleY(cubyScale);

            // ---- SHADOW ----
            if (cubyShadow != null) {

                // Larger visible change
                float shadowScaleX = 1.0f + (0.08f * t);
                float shadowScaleY = 1.0f + (0.04f * t);

                cubyShadow.setScaleX(shadowScaleX);
                cubyShadow.setScaleY(shadowScaleY);

                // Stronger alpha contrast
                cubyShadow.setAlpha(0.25f + (0.25f * t));
            }
        });

        animator.start();
    }

    private void observeData() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                // tvGreeting.setText("Hi, " + profile.username + "! 👋"); // Removed

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
                // tvFoodCount.setText("🍎 Bloxy Food: " + inventory.bloxyFoodCount);
            }
        });
    }

    private void setupMoodButton(View root, int id, String mood) {
        root.findViewById(id).setOnClickListener(v -> {

            String today = DateUtils.getTodayDate();

            cubyMoodEngine.recordDailyMood(today, mood);

            tvCubyBubble.setText(
                    cubyMoodEngine.getCubyMessage(mood, false, false)
            );

            layoutMoodButtons.setVisibility(View.GONE);
        });
    }

    private void setupTaskWidget(View root) {

        taskWidget = root.findViewById(R.id.taskWidget);
        taskContent = root.findViewById(R.id.taskContent);
        btnToggle = root.findViewById(R.id.btnToggle);

        tvTaskTitle = root.findViewById(R.id.tvTaskTitle);
        tvTaskDesc = root.findViewById(R.id.tvTaskDesc);
        tvTaskProgress = root.findViewById(R.id.tvTaskProgress);
        taskProgressBar = root.findViewById(R.id.taskProgressBar);
        btnGoTask = root.findViewById(R.id.btnGoTask);

        // collapsed by default
        taskContent.setVisibility(View.GONE);

        btnToggle.setOnClickListener(v -> toggleTaskWidget());

        btnGoTask.setOnClickListener(v -> launchCurrentTask());
    }

    private void toggleTaskWidget() {
        isTaskExpanded = !isTaskExpanded;

        taskContent.setVisibility(
                isTaskExpanded ? View.VISIBLE : View.GONE
        );

        btnToggle.setImageResource(R.drawable.ic_collapse);

    }

    private void updateTaskWidget(DailyLog log) {

        if (log == null) {
            taskWidget.setVisibility(View.GONE);
            return;
        }

        DailyTask task = cubyMoodEngine.getCurrentTaskFromLog(log);

        if (task == null) {
            taskWidget.setVisibility(View.GONE);
            return;
        }

        taskWidget.setVisibility(View.VISIBLE);

        tvTaskTitle.setText(task.title);
        tvTaskDesc.setText(task.description);

        int progress = log.taskProgressSeconds;
        int target = task.durationSeconds;

        int percent = (int) ((progress / (float) target) * 100);
        percent = Math.min(percent, 100);

        taskProgressBar.setProgress(percent);
        tvTaskProgress.setText(percent + "%");
    }

    private void launchCurrentTask() {

        repository.getExecutor().execute(() -> {

            DailyLog log = repository.getDailyLogSync(today);
            if (log == null) return;

            DailyTask task = cubyMoodEngine.getCurrentTaskFromLog(log);
            if (task == null) return;

            requireActivity().runOnUiThread(() -> {

                Intent intent = null;

                switch (task.type) {

                    case BREATHING_478:
                        intent = new Intent(requireContext(), Four78BreathingActivity.class);
                        break;

                    case BREATHING_BOX:
                        intent = new Intent(requireContext(), BoxBreathingActivity.class);
                        break;

                    case POMODORO:
                        intent = new Intent(requireContext(), PomodoroActivity.class);
                        break;

                    case FOCUS:
                        intent = new Intent(requireContext(), FocusActivity.class);
                        break;



                    default:
                        return; // no task to launch
                }

                taskLauncher.launch(intent);
            });
        });
    }

    private void observeTodayLog() {
        repository.getDailyLog(today).observe(getViewLifecycleOwner(), log -> {

            updateTaskWidget(log);

            if (log == null || log.mood == null) {
                tvCubyBubble.setText("Hey… how are you feeling today?");
                layoutMoodButtons.setVisibility(View.VISIBLE);
                return;
            }

            layoutMoodButtons.setVisibility(View.GONE);

            DailyTask task = cubyMoodEngine.getCurrentTaskFromLog(log);

            if (task != null) {
                tvCubyBubble.setText(
                        "Today’s task 🌱\n" +
                                task.title + " • " + (task.durationSeconds / 60) + " min\n\n" +
                                task.description
                );
            } else if (log.taskCompleted && log.seedUnlocked) {
                tvCubyBubble.setText(
                        "🌱 You did it!\nI have a seed for you.\nGo check the garden!"
                );
            } else {
                tvCubyBubble.setText(
                        cubyMoodEngine.getCubyMessage(
                                log.mood,
                                false,
                                log.seedUnlocked
                        )
                );
            }

        });
    }


    @Override
    public void onResume() {
        super.onResume();
        today = DateUtils.getTodayDate();
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
