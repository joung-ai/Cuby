package com.example.cuby.ui.room;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class RoomBackgroundView extends View {
    private Paint ceilingSurfacePaint = new Paint();
    private Paint ceilingShadowPaint = new Paint();

    private Paint wallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint floorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ceilingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint windowLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint seamShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float floorY;
    private float lightShift = 0f;

    public RoomBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 🔴 REQUIRED
        setWillNotDraw(false);

        // Hardware accel OK
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        floorY = h * 0.65f;

        // Ceiling glow
        ceilingPaint.setShader(new LinearGradient(
                0, 0, 0, h * 0.25f,
                0x66FFFFFF,
                0x00FFFFFF,
                Shader.TileMode.CLAMP
        ));

        float ceilingHeight = h * 0.18f;

// Ceiling surface
        ceilingSurfacePaint.setShader(new LinearGradient(
                0, 0, 0, ceilingHeight,
                0xFFF5F7FA,
                0xFFE1E6EC,
                Shader.TileMode.CLAMP
        ));

// Ceiling shadow (where it meets the wall)
        ceilingShadowPaint.setShader(new LinearGradient(
                0, ceilingHeight - 20, 0, ceilingHeight + 10,
                0x33000000,
                0x00000000,
                Shader.TileMode.CLAMP
        ));


        // Wall gradient
        wallPaint.setShader(new LinearGradient(
                0, ceilingHeight,
                0, floorY,
                new int[]{
                        0xFFEAF4FF,
                        0xFFD8E6F3,
                        0xFFC4D6E8
                },
                new float[]{0f, 0.6f, 1f},
                Shader.TileMode.CLAMP
        ));


        // Floor gradient
        floorPaint.setShader(new LinearGradient(
                0, floorY,
                0, getHeight(),
                new int[]{
                        0xFFB6C9DD,
                        0xFF9FB5CC,
                        0xFF7F97AE
                },
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP
        ));


        // Floor seam shadow
        seamShadowPaint.setShader(new LinearGradient(
                0, floorY - 20, 0, floorY + 20,
                0x44000000,
                0x00000000,
                Shader.TileMode.CLAMP
        ));

        // 🔴 Initialize light shader immediately
        updateWindowLight();
        startLightAnimation();
    }

    private void updateWindowLight() {
        float w = getWidth();
        float h = getHeight();

        if (w <= 0 || h <= 0) return; // 🔒 SAFETY GUARD

        float radius = Math.max(h * 0.6f, 1f); // 🔒 never 0

        windowLightPaint.setShader(new RadialGradient(
                w * (0.15f + lightShift),
                h * 0.25f,
                radius,
                new int[]{
                        0x55FFFFFF,
                        0x22FFFFFF,
                        0x00FFFFFF
                },
                new float[]{0f, 0.4f, 1f},
                Shader.TileMode.CLAMP
        ));

        windowLightPaint.setBlendMode(BlendMode.SCREEN);
    }

    private void startLightAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(-0.05f, 0.05f);
        animator.setDuration(12000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            lightShift = (float) animation.getAnimatedValue();
            updateWindowLight();
            invalidate();
        });

        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

            if (isInEditMode()) {
                // Preview-only fallback
                canvas.drawColor(0xFFE3ECF5); // light room color
                return;
            }

            // REAL drawing below

        float ceilingHeight = getHeight() * 0.18f;

// Ceiling
        canvas.drawRect(0, 0, getWidth(), ceilingHeight, ceilingSurfacePaint);

// Ceiling shadow
        canvas.drawRect(0, ceilingHeight - 20, getWidth(), ceilingHeight + 10, ceilingShadowPaint);

// Wall
        canvas.drawRect(0, ceilingHeight, getWidth(), floorY, wallPaint);


        // Floor
        canvas.drawRect(0, floorY, getWidth(), getHeight(), floorPaint);

        // Seam shadow
        canvas.drawRect(0, floorY - 20, getWidth(), floorY + 20, seamShadowPaint);
    }
}
