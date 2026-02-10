package com.example.cuby.ui.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cuby.BloxxGame;
import com.example.cuby.BoxBreathingActivity;
import com.example.cuby.Four78BreathingActivity;
import com.example.cuby.focus.FocusActivity;
import com.example.cuby.logic.DailyTask;
import com.example.cuby.model.DailyLog;
import com.example.cuby.model.Inventory;
import com.example.cuby.ui.breathing.BreathingTechniquesFragment; // ✅ ADDED
import com.example.cuby.R;
import com.example.cuby.alarm.AlarmFragment;
import com.example.cuby.data.AppRepository;
import com.example.cuby.logic.CubyMoodEngine;
import com.example.cuby.ui.chat.ChatFragment;
import com.example.cuby.ui.cuby.CubyAnimationController;
import com.example.cuby.ui.diary.DiaryFragment;
import com.example.cuby.ui.garden.GardenFragment;
import com.example.cuby.ui.productivity.ProductivityFragment;
import com.example.cuby.ui.settings.SettingsFragment;
import com.example.cuby.utils.DateUtils;
import com.example.cuby.PomodoroActivity;

// ADD THIS IMPORT for your memory game
import com.example.cuby.memorygame.MemoryGame;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private TextView tvGreeting;

    private TextView tvCubyBubble;
    private View layoutMoodButtons;
    private AppRepository repository;
    private CubyMoodEngine cubyMoodEngine;
    private TextView tvFoodCount;
    //cuby
    private View cubyAvatar;
    private ImageView cubyBase;
    private ImageView cubyCosmetic;
    // animation
    private ValueAnimator breathingAnimator;

    private CubyAnimationController cubyController;


    private View cubyShadow;

    String today = DateUtils.getTodayDate();

    // Task Widget UI
    private View taskWidget;
    private View taskContent;
    private TextView tvTaskTitle, tvTaskDesc, tvTaskProgress;
    private ProgressBar taskProgressBar;
    private ImageView btnToggle;
    private Button btnGoTask;

    //for launching the game
    private View layoutPlayChoice;
    private Button btnPlayYes, btnPlayNo;


    private boolean isTaskExpanded = false;

    private ActivityResultLauncher<Intent> taskLauncher;

    private enum BubblePriority {
        SYSTEM,     // task, seed, important
        USER,       // feed, wardrobe
        IDLE        // default chatter
    }

    private BubblePriority currentPriority = BubblePriority.IDLE;
    private Runnable clearBubbleRunnable;

    private void showCubyMessage(String text, BubblePriority priority, long durationMs) {
        if (priority.ordinal() < currentPriority.ordinal()) return;

        currentPriority = priority;

        tvCubyBubble.setVisibility(View.VISIBLE);
        tvCubyBubble.setText(text);

        if (clearBubbleRunnable != null) {
            tvCubyBubble.removeCallbacks(clearBubbleRunnable);
        }

        clearBubbleRunnable = () -> {
            currentPriority = BubblePriority.IDLE;
            tvCubyBubble.setText(
                    cubyMoodEngine.getIdleMessage()
            );
        };

        tvCubyBubble.postDelayed(clearBubbleRunnable, durationMs);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        cubyAvatar = view.findViewById(R.id.cubyAvatar);
        cubyBase = cubyAvatar.findViewById(R.id.cubyBase);
        cubyCosmetic = cubyAvatar.findViewById(R.id.cubyCosmetic);

        cubyController = new CubyAnimationController(
                cubyBase,
                cubyCosmetic,
                this::getCubyColor
        );

;


        cubyCosmetic = cubyAvatar.findViewById(R.id.cubyCosmetic);


        cubyShadow = view.findViewById(R.id.cubyShadow);

        tvCubyBubble = view.findViewById(R.id.tvCubyBubble);
        layoutMoodButtons = view.findViewById(R.id.layoutMoodButtons);

        tvFoodCount = view.findViewById(R.id.tvFoodCount);

        layoutPlayChoice = view.findViewById(R.id.layoutPlayChoice);
        btnPlayYes = view.findViewById(R.id.btnPlayYes);
        btnPlayNo = view.findViewById(R.id.btnPlayNo);



        repository = AppRepository.getInstance(requireActivity().getApplication());

        cubyMoodEngine = new CubyMoodEngine(repository);

        taskLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            // 🔒 DO NOTHING HERE
                            // Task completion is manual via COMPLETE button
                        }
                );

        setupNavigation(view);
        setupCubyInteraction();
        observeData();
        startBreathingAnimation();
        setupTaskWidget(view);
        observeTodayLog();

        btnPlayYes.setOnClickListener(v -> {
            layoutPlayChoice.setVisibility(View.GONE);
            showSlotMachine();
        });


        btnPlayNo.setOnClickListener(v -> {
            layoutPlayChoice.setVisibility(View.GONE);
            tvCubyBubble.setText("Okay~ maybe later 💙");
        });

        view.findViewById(R.id.btnHanger).setOnClickListener(v -> {
            cubyController.showHappy();
            showWardrobePicker();
        });


    }

    private void setupNavigation(View view) {
        // New Side Navigation
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> navigateWithAnimation(new SettingsFragment()));
        view.findViewById(R.id.btnChat).setOnClickListener(v -> navigateWithAnimation(new ChatFragment()));
        view.findViewById(R.id.btnDiary).setOnClickListener(v -> navigateWithAnimation(new DiaryFragment()));

        // Bottom Bar
        view.findViewById(R.id.btnMeditate).setOnClickListener(v -> navigateWithAnimation(new BreathingTechniquesFragment()));
        view.findViewById(R.id.btnGarden).setOnClickListener(v -> navigateWithAnimation(new GardenFragment())); // Diary/Garden
        view.findViewById(R.id.btnProductive).setOnClickListener(v -> navigateWithAnimation(new ProductivityFragment()));
        view.findViewById(R.id.btnMeditate).setOnClickListener(v -> navigateWithAnimation(new BreathingTechniquesFragment()));
        view.findViewById(R.id.btnFeed).setOnClickListener(v -> {

            repository.getExecutor().execute(() -> {

                Inventory inventory = repository.getInventorySync();
                DailyLog log = repository.getDailyLogSync(today);

                if (inventory == null || inventory.bloxyFoodCount <= 0) {
                    requireActivity().runOnUiThread(() -> {
                        tvCubyBubble.setVisibility(View.VISIBLE);
                        tvCubyBubble.setText("😿 I’m out of Bloxy Food...");
                        Toast.makeText(
                                getContext(),
                                "No Bloxy Food left!",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
                    return;
                }

                // 🍎 Consume food
                repository.consumeFood(1);

                boolean shouldAskToPlay = false;

                if (log != null) {
                    log.feedCount++;

                    // 🎮 EVERY 3 FEEDS
                    if (log.feedCount % 3 == 0) {
                        shouldAskToPlay = true;
                    }

                    repository.insertDailyLog(log);
                }

                boolean finalShouldAskToPlay = shouldAskToPlay;

                requireActivity().runOnUiThread(() -> {

                    cubyController.showHappy();

                    tvCubyBubble.setVisibility(View.VISIBLE);

                    if (finalShouldAskToPlay) {
                        tvCubyBubble.setText(
                                "🎮 I’m full again!\nWanna play with me?"
                        );
                        layoutPlayChoice.setVisibility(View.VISIBLE);
                    } else {
                        showCubyMessage(
                                "Yum! Thank you 💕",
                                BubblePriority.USER,
                                2500
                        );

                    }

                    Toast.makeText(
                            getContext(),
                            "Cuby enjoyed the food!",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            });
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
        cubyAvatar.setOnClickListener(v -> {
            Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            cubyAvatar.startAnimation(bounce);
            cubyController.showHappy();

        });

    }


    private void startBreathingAnimation() {

        breathingAnimator = ValueAnimator.ofFloat(0f, 1f);
        breathingAnimator.setDuration(2200);
        breathingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        breathingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        breathingAnimator.setInterpolator(new android.view.animation.LinearInterpolator());

        breathingAnimator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();

            // ---- CUBY ----
            float cubyScale = 1.0f + (0.05f * t);
            cubyAvatar.setScaleX(cubyScale);
            cubyAvatar.setScaleY(cubyScale);

            if (cubyShadow != null) {

                // Shadow grows as Cuby "presses" the ground
                float shadowScaleX = 1.0f + (0.12f * t);
                float shadowScaleY = 1.0f + (0.06f * t);

                cubyShadow.setScaleX(shadowScaleX);
                cubyShadow.setScaleY(shadowScaleY);

                // Darker when closer to ground
                cubyShadow.setAlpha(0.22f + (0.18f * t));

                // Push shadow slightly DOWN as Cuby grows
                cubyShadow.setTranslationY(-55f + (2f * t));
            }

        });

        breathingAnimator.start();
    }

    private void observeData() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null && cubyController != null) {
                cubyController.startIdle();

                if (profile.cubyCosmetic != null && !profile.cubyCosmetic.isEmpty()) {
                    cubyController.setCosmetic(profile.cubyCosmetic);
                }
            }
        });


        viewModel.getInventory().observe(getViewLifecycleOwner(), inventory -> {
            if (inventory != null) {
                tvFoodCount.setText("🍎 " + inventory.bloxyFoodCount);
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

        btnGoTask.setOnClickListener(v -> {

            repository.getExecutor().execute(() -> {

                DailyLog log = repository.getDailyLogSync(today);
                if (log == null) return;

                DailyTask task = cubyMoodEngine.getCurrentTaskFromLog(log);
                if (task == null) return;

                int percent =
                        (int) ((log.taskProgressSeconds / (float) task.durationSeconds) * 100);

                requireActivity().runOnUiThread(() -> {

                    if (percent >= 100) {

                        cubyMoodEngine.completeCurrentTask(today);
                        repository.addFood(3);

                        // 🌱 UNLOCK SEED PROPERLY
                        repository.getExecutor().execute(() -> {
                            DailyLog freshLog = repository.getDailyLogSync(today);
                            if (freshLog != null) {
                                freshLog.taskCompleted = true;   // ✅ guaranteed
                                freshLog.seedUnlocked = true;
                                freshLog.seedShown = false;      // allow message ONCE
                                repository.insertDailyLog(freshLog);
                            }
                        });

                        tvCubyBubble.setVisibility(View.VISIBLE);
                        tvCubyBubble.setText(
                                "✨ Task completed!\nYou earned Bloxy Food 💕"
                        );

                        Toast.makeText(
                                getContext(),
                                "Task completed!",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {
                        // ▶ Start task
                        launchCurrentTask();
                    }
                });
            });
        });

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

        // 🔒 LOCK COMPLETE STATE
        if (percent >= 100) {
            btnGoTask.setText("COMPLETE");
            btnGoTask.setEnabled(true);
        } else {
            btnGoTask.setText("GO");
            btnGoTask.setEnabled(true);
        }
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
                        return;
                }

                taskLauncher.launch(intent);
            });
        });
    }

    private void observeTodayLog() {
        repository.getDailyLog(today).observe(getViewLifecycleOwner(), log -> {

            updateTaskWidget(log);

            if (log == null || log.mood == null) {
                tvCubyBubble.setVisibility(View.VISIBLE);
                tvCubyBubble.setText("Hey… how are you feeling today?");
                layoutMoodButtons.setVisibility(View.VISIBLE);
                return;
            }

            layoutMoodButtons
                    .setVisibility(View.GONE);

            DailyTask task = cubyMoodEngine.getCurrentTaskFromLog(log);

            // 🟢 ACTIVE TASK
            if (task != null) {
                tvCubyBubble.setText(
                        "Today’s task \n" +
                                task.title + " \n\n " +
                                task.description
                );
                return;
            }

// 🌱 SEED MESSAGE — SHOW ONCE (HARD GUARANTEE)
            if (log.taskCompleted && log.seedUnlocked && !log.seedShown) {

                // ✅ mark as shown IMMEDIATELY
                log.seedShown = true;
                repository.getExecutor().execute(() -> {
                    repository.insertDailyLog(log);
                });

                showCubyMessage(
                        "🌱 You did it!\nI have a seed for you.\nGo check the garden!",
                        BubblePriority.SYSTEM,
                        4000
                );

                return;
            }


            // NORMAL CUBY MESSAGE
            tvCubyBubble.setVisibility(View.VISIBLE);
            tvCubyBubble.setText(
                    cubyMoodEngine.getCubyMessage(
                            log.mood,
                            false,
                            log.seedUnlocked
                    )
            );
        });
    }

    private void showSlotMachine() {

        AlertDialog dialog = new AlertDialog.Builder(requireContext()).create();
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_game_slot, null);

        TextView tvSlot = view.findViewById(R.id.tvSlot);
        Button btnSpin = view.findViewById(R.id.btnSpin);

        String[] games = {
                "Memory Game",
                "Bloxx Game"
        };

        Handler handler = new Handler(Looper.getMainLooper());

        btnSpin.setOnClickListener(v -> {
            btnSpin.setEnabled(false);

            java.util.Random random = new java.util.Random();

            Runnable spinner = new Runnable() {
                int spins = 0;

                @Override
                public void run() {
                    // purely visual spin
                    int visualIndex = random.nextInt(games.length);
                    tvSlot.setText(games[visualIndex]);
                    spins++;

                    if (spins < 16) {
                        handler.postDelayed(this, 120);
                    } else {
                        dialog.dismiss();

                        // 🎯 REAL RANDOM PICK (FINAL)
                        int finalIndex = random.nextInt(games.length);
                        launchGame(games[finalIndex]);
                    }
                }
            };

            handler.post(spinner);
        });

        dialog.setView(view);
        dialog.show();
    }

    private void launchGame(String game) {

        Intent intent;

        switch (game) {
            case "Memory Game":
                intent = new Intent(requireContext(), MemoryGame.class);
                break;

            default: // 🟩Bloxx Game
                intent = new Intent(requireContext(), BloxxGame.class);
                break;
        }

        startActivity(intent);
    }

    private String getCubyColor() {
        if (viewModel.getUserProfile().getValue() != null) {
            String skin = viewModel.getUserProfile().getValue().cubySkin;

            if ("Pink".equalsIgnoreCase(skin)) return "pink";
            if ("Blue".equalsIgnoreCase(skin)) return "blue";
        }
        return "blue"; // safe fallback
    }

    private void showWardrobePicker() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.bottomsheet_wardrobe, null);

        view.findViewById(R.id.btnCosmeticCat).setOnClickListener(v -> {
            cubyController.setCosmetic("cat");
            repository.updateCubyCosmetic("cat");
            showCubyMessage("Meow~", BubblePriority.USER, 2000);

            dialog.dismiss();
        });

        view.findViewById(R.id.btnCosmeticDog).setOnClickListener(v -> {
            cubyController.setCosmetic("dog");
            repository.updateCubyCosmetic("dog");
            showCubyMessage("Woof", BubblePriority.USER, 2000);
            dialog.dismiss();
        });

        view.findViewById(R.id.btnCosmeticGlasses).setOnClickListener(v -> {
            cubyController.setCosmetic("glasses");
            repository.updateCubyCosmetic("glasses");
            showCubyMessage("I look smart", BubblePriority.USER, 2000);
            dialog.dismiss();
        });

        view.findViewById(R.id.btnCosmeticEmployed).setOnClickListener(v -> {
            cubyController.setCosmetic("employed");
            repository.updateCubyCosmetic("employed");
            showCubyMessage("I have a job now.", BubblePriority.USER, 2000);
            dialog.dismiss();
        });

        view.findViewById(R.id.btnCosmeticNone).setOnClickListener(v -> {
            cubyController.clearCosmetic();
            showCubyMessage("All comfy again ✨", BubblePriority.USER, 2000);


            // 💾 Save to DB
            repository.updateCubyCosmetic(null);

            dialog.dismiss();
        });


        dialog.setContentView(view);
        dialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        today = DateUtils.getTodayDate();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (breathingAnimator != null) breathingAnimator.cancel();

        if (cubyController != null) {
            cubyController.stop();
            cubyController = null;
        }
    }
}
