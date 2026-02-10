package com.example.cuby.ui.cuby;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import java.util.function.Supplier;

public class CubyAnimationController {

    private final ImageView cubyBase;
    private final ImageView cubyCosmetic;
    private final Supplier<String> colorProvider;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean isHappy = false;
    private String currentCosmetic = null;

    private static final long BLINK_DURATION = 120;
    private static final long BLINK_MIN = 3000;
    private static final long BLINK_MAX = 6000;

    public CubyAnimationController(
            ImageView cubyBase,
            ImageView cubyCosmetic,
            Supplier<String> colorProvider
    ) {
        this.cubyBase = cubyBase;
        this.cubyCosmetic = cubyCosmetic;
        this.colorProvider = colorProvider;
    }

    /* ---------- EXPRESSIONS ---------- */

    public void startIdle() {
        isHappy = false;
        setExpression("idle");
        scheduleBlink();
    }

    public void showHappy() {
        isHappy = true;
        setExpression("happy");
        scheduleBlink();

        handler.postDelayed(this::startIdle, 2500);
    }

    /* ---------- COSMETICS ---------- */

    public void setCosmetic(String cosmeticKey) {
        if (cosmeticKey == null || cosmeticKey.isEmpty()) {
            cubyCosmetic.setImageDrawable(null);
            cubyCosmetic.setVisibility(View.GONE);
            return;
        }

        String resName = "cuby_cosmetic_" + cosmeticKey;

        int resId = cubyCosmetic.getResources().getIdentifier(
                resName,
                "drawable",
                cubyCosmetic.getContext().getPackageName()
        );

        if (resId == 0) return;

        cubyCosmetic.setImageResource(resId);
        cubyCosmetic.setVisibility(View.VISIBLE);

        // reset first (important)
        cubyCosmetic.setTranslationX(0f);
        cubyCosmetic.setTranslationY(0f);

        switch (cosmeticKey) {
            case "glasses":
                cubyCosmetic.setTranslationY(dp(-4));
                break;

            case "cat":
                cubyCosmetic.setTranslationY(dp(-3));
                break;

            case "dog":
                cubyCosmetic.setTranslationY(dp(-12));
                break;

            case "employed":
                cubyCosmetic.setTranslationY(dp(-18));
                break;
        }
    }

    public void clearCosmetic() {
        currentCosmetic = null;
        cubyCosmetic.setImageDrawable(null);
        cubyCosmetic.setVisibility(ImageView.GONE);
    }

    /* ---------- BLINK ---------- */

    private void scheduleBlink() {
        handler.removeCallbacks(blinkRunnable);
        long delay = BLINK_MIN + (long) (Math.random() * (BLINK_MAX - BLINK_MIN));
        handler.postDelayed(blinkRunnable, delay);
    }

    private final Runnable blinkRunnable = () -> {
        String base = isHappy ? "happy" : "idle";
        setExpression(base + "_blink");

        handler.postDelayed(() -> setExpression(base), BLINK_DURATION);
        scheduleBlink();
    };

    private void setExpression(String expression) {
        String color = colorProvider.get();
        String resName = "cuby_base_" + color + "_" + expression;

        int resId = cubyBase.getResources().getIdentifier(
                resName,
                "drawable",
                cubyBase.getContext().getPackageName()
        );

        if (resId != 0) {
            cubyBase.setImageResource(resId);
        }
    }

    public void stop() {
        handler.removeCallbacksAndMessages(null);
    }

    private float dp(float value) {
        return value * cubyCosmetic
                .getResources()
                .getDisplayMetrics()
                .density;
    }

}
