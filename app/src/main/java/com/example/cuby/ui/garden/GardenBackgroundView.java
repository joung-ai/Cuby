package com.example.cuby.ui.garden;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class GardenBackgroundView extends View {

    private Paint skyPaint = new Paint();
    private Paint grassPaint = new Paint();
    private Paint horizonShadowPaint = new Paint();

    private Paint cloudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint housePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint roofPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float horizonY;
    private float cloudOffset = 0f;

    public GardenBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        startCloudAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        horizonY = h * 0.35f;

        // 🌤 SKY
        skyPaint.setShader(new LinearGradient(
                0, 0, 0, horizonY,
                0xFFEAF6FF,
                0xFFD6ECFA,
                Shader.TileMode.CLAMP
        ));

        // 🌿 GRASS
        grassPaint.setShader(new LinearGradient(
                0, horizonY, 0, h,
                new int[]{0xFF8FBF7A, 0xFF6FA15E, 0xFF4F7F44},
                new float[]{0f, 0.6f, 1f},
                Shader.TileMode.CLAMP
        ));

        // 🌫 HORIZON SHADOW
        horizonShadowPaint.setShader(new LinearGradient(
                0, horizonY - 20, 0, horizonY + 40,
                0x33000000,
                0x00000000,
                Shader.TileMode.CLAMP
        ));

        // CLOUDS
        cloudPaint.setColor(0x88FFFFFF);

        // HOUSE
        housePaint.setColor(0xFFC7BEB4); // warm concrete
        roofPaint.setColor(0xFF9E948B);  // soft roof

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        // 1️⃣ SKY
        canvas.drawRect(0, 0, w, horizonY, skyPaint);

        // 2️⃣ CLOUDS
        drawCloud(canvas, w * 0.2f + cloudOffset, horizonY * 0.35f, 80);
        drawCloud(canvas, w * 0.6f + cloudOffset * 0.6f, horizonY * 0.2f, 100);
        drawCloud(canvas, w * 0.85f + cloudOffset * 0.8f, horizonY * 0.3f, 70);

        // 3️⃣ HORIZON SHADOW
        canvas.drawRect(0, horizonY - 20, w, horizonY + 40, horizonShadowPaint);

        // 4️⃣ GRASS
        canvas.drawRect(0, horizonY, w, h, grassPaint);

        // 5️⃣ HOUSE GLIMPSE (RIGHT SIDE)
        drawModernHouse(canvas, w);

    }

    // ☁️ Simple cloud made of circles
    private void drawCloud(Canvas c, float x, float y, float size) {
        c.drawCircle(x, y, size * 0.4f, cloudPaint);
        c.drawCircle(x + size * 0.3f, y - size * 0.1f, size * 0.35f, cloudPaint);
        c.drawCircle(x + size * 0.6f, y, size * 0.4f, cloudPaint);
    }

    private void drawModernHouse(Canvas c, float w) {
        float houseW = w * 0.22f;
        float houseH = houseW * 0.6f;
        float baseY = horizonY + 35;

        // Wall (partially off-screen)
        c.drawRect(
                w - houseW * 0.6f,
                baseY,
                w + houseW * 0.4f,
                baseY + houseH,
                housePaint
        );

        // Flat roof
        c.drawRect(
                w - houseW * 0.6f,
                baseY - 10,
                w + houseW * 0.4f,
                baseY,
                roofPaint
        );
    }

    // 🌬 Slow cloud movement
    private void startCloudAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 300f);
        animator.setDuration(60000); // 60s slow drift
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            cloudOffset = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }
}
