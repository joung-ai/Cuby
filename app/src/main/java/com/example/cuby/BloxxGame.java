package com.example.cuby;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Random;

public class BloxxGame extends AppCompatActivity {

    private FrameLayout gameArea;
    private FrameLayout overlayContainer;
    private LinearLayout gameOverOverlay;
    private Button btnTryAgain;
    private TextView tvMotivation;

    private Handler handler = new Handler();

    private final ArrayList<MaterialCardView> blocks = new ArrayList<>();
    private MaterialCardView currentBlock;

    private boolean moving = true;
    private boolean dropping = false;
    private boolean isGameOver = false;

    private int direction = 1;
    private final int speed = 8;
    private final int fallSpeed = 18;

    private final int blockWidth = 260;
    private final int blockHeight = 140;

    private float cameraTriggerY;

    // 💬 MOTIVATIONAL PHRASES
    private final String[] motivationalPhrases = {
            "You got this 💪",
            "Keep going 🌱",
            "One more try ✨",
            "Cuby believes in you 💙",
            "Progress, not perfection 🌈",
            "You’re learning every try \uD83E\uDDF8",
            "Mistakes help you grow 🌻"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuby_bloxx);

        gameArea = findViewById(R.id.gameArea);
        overlayContainer = findViewById(R.id.overlayContainer);
        gameOverOverlay = findViewById(R.id.gameOverOverlay);
        btnTryAgain = findViewById(R.id.btnTryAgain);
        tvMotivation = findViewById(R.id.tvMotivation);

        overlayContainer.setClickable(false);

        btnTryAgain.setOnClickListener(v -> restartGame());

        gameArea.post(() -> {
            cameraTriggerY = gameArea.getHeight() / 2f;
            spawnMovingBlock();
            handler.post(gameLoop);
        });

        gameArea.setOnTouchListener((v, e) -> {
            if (!isGameOver && e.getAction() == MotionEvent.ACTION_DOWN && moving) {
                moving = false;
                dropping = true;
            }
            return true;
        });
    }

    // ================= GAME LOOP =================

    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {

            if (isGameOver || currentBlock == null) return;

            // Horizontal movement
            if (moving) {
                float x = currentBlock.getX() + direction * speed;

                if (x <= 0 || x + currentBlock.getWidth() >= gameArea.getWidth()) {
                    direction *= -1;
                }
                currentBlock.setX(x);
            }

            // Falling
            if (dropping) {

                float nextY = currentBlock.getY() + fallSpeed;
                float nextBottom = nextY + currentBlock.getHeight();

                if (blocks.isEmpty()) {
                    if (nextY >= getGroundY()) {
                        currentBlock.setY(getGroundY());
                        dropping = false;
                        blocks.add(currentBlock);
                        spawnMovingBlock();
                    } else {
                        currentBlock.setY(nextY);
                    }
                } else {
                    MaterialCardView top = blocks.get(blocks.size() - 1);

                    if (nextBottom >= top.getY()) {
                        dropping = false;
                        resolveCollision(top);
                    } else {
                        currentBlock.setY(nextY);
                    }
                }
            }

            handler.postDelayed(this, 16);
        }
    };

    // ================= COLLISION =================

    private void resolveCollision(MaterialCardView top) {

        float centerX = currentBlock.getX() + currentBlock.getWidth() / 2f;
        float topLeft = top.getX();
        float topRight = topLeft + top.getWidth();

        if (centerX <= topLeft || centerX >= topRight) {
            showMotivationAndStop();
            return;
        }

        currentBlock.setY(top.getY() - currentBlock.getHeight());
        blocks.add(currentBlock);
        adjustCameraIfNeeded();
        spawnMovingBlock();
    }

    // ================= CAMERA =================

    private void adjustCameraIfNeeded() {
        MaterialCardView topBlock = blocks.get(blocks.size() - 1);

        if (topBlock.getY() <= cameraTriggerY) {
            float shift = cameraTriggerY - topBlock.getY();

            for (MaterialCardView block : blocks) {
                block.setY(block.getY() + shift);
            }
        }
    }

    // ================= SPAWN =================

    private void spawnMovingBlock() {
        currentBlock = createBlock();
        currentBlock.setX(0);
        currentBlock.setY(80);
        moving = true;
        direction = 1;
        gameArea.addView(currentBlock);
    }

    // ================= BLOCK FACTORY =================

    private MaterialCardView createBlock() {

        MaterialCardView card = new MaterialCardView(this);

        card.setRadius(32f);
        card.setCardElevation(0f);
        card.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);
        card.setUseCompatPadding(false);
        card.setPreventCornerOverlap(false);
        card.setStrokeWidth(0);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(blockWidth, blockHeight);
        card.setLayoutParams(params);

        ImageView cuby = new ImageView(this);
        cuby.setImageResource(R.drawable.bloxx_cuby);
        cuby.setScaleType(ImageView.ScaleType.FIT_CENTER);
        cuby.setAdjustViewBounds(true);

        card.addView(
                cuby,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );

        return card;
    }

    // ================= MOTIVATION =================

    private void showMotivationAndStop() {
        isGameOver = true;
        handler.removeCallbacks(gameLoop);

        // Pick random phrase
        String phrase = motivationalPhrases[new Random().nextInt(motivationalPhrases.length)];
        tvMotivation.setText(phrase);

        gameOverOverlay.setVisibility(View.VISIBLE);
        overlayContainer.setClickable(true);
    }

    private void restartGame() {
        finish();
        startActivity(new Intent(this, BloxxGame.class));
    }

    // ================= UTIL =================

    private float getGroundY() {
        return gameArea.getHeight() - blockHeight;
    }
}
