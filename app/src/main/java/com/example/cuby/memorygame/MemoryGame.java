package com.example.cuby.memorygame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cuby.R;
import com.example.cuby.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MemoryGame extends AppCompatActivity {

    private GridLayout gridLayout;

    private final int[] images = {
            R.drawable.cubyanxious, R.drawable.cubyanxious,
            R.drawable.cubyanxiouspink, R.drawable.cubyanxiouspink,
            R.drawable.cubycalm, R.drawable.cubycalm,
            R.drawable.cubycalmpink, R.drawable.cubycalmpink,
            R.drawable.cubyhappy, R.drawable.cubyhappy,
            R.drawable.cubyhappypink, R.drawable.cubyhappypink,
            R.drawable.cubysad, R.drawable.cubysad,
            R.drawable.cubysadpink, R.drawable.cubysadpink,
            R.drawable.cubysleepy, R.drawable.cubysleepy,
            R.drawable.cubysleepypink, R.drawable.cubysleepypink
    };

    private ImageView firstCard, secondCard;
    private boolean isBusy = false;
    private int matchedPairs = 0;

    private int cardSize;
    private int margin = 8;

    private static final int NUM_COLUMNS = 4;
    private static final int NUM_ROWS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_memory_game);

        gridLayout = findViewById(R.id.gridLayout);

        calculateCardSize();
        shuffleImages();
        createBoard();
    }

    private void calculateCardSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // Adjust available height by subtracting header and padding (tweak as needed)
        int availableHeight = screenHeight - (int) (280 * metrics.density);

        int cardWidth = (screenWidth - margin * 8) / NUM_COLUMNS;
        int cardHeight = (availableHeight - margin * 10) / NUM_ROWS;

        cardSize = Math.min(cardWidth, cardHeight);
    }

    private void shuffleImages() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int img : images) list.add(img);
        Collections.shuffle(list);
        for (int i = 0; i < images.length; i++) {
            images[i] = list.get(i);
        }
    }

    private void createBoard() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(NUM_COLUMNS);
        gridLayout.setRowCount(NUM_ROWS);

        for (int i = 0; i < images.length; i++) {
            final ImageView card = new ImageView(this);

            card.setImageResource(R.drawable.cubycard);
            card.setTag(images[i]);
            card.setScaleType(ImageView.ScaleType.CENTER_CROP);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardSize;
            params.height = cardSize;
            params.setMargins(margin, margin, margin, margin);
            card.setLayoutParams(params);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isBusy || !card.isEnabled()) return;

                    card.setImageResource((int) card.getTag());

                    if (firstCard == null) {
                        firstCard = card;
                    } else if (firstCard != card) {
                        secondCard = card;
                        checkMatch();
                    }
                }
            });

            gridLayout.addView(card);
        }
    }

    private void checkMatch() {
        isBusy = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firstCard.getTag().equals(secondCard.getTag())) {
                    firstCard.setEnabled(false);
                    secondCard.setEnabled(false);
                    matchedPairs++;

                    if (matchedPairs == images.length / 2) {
                        showGreatJobDialog();
                    }
                } else {
                    firstCard.setImageResource(R.drawable.cubycard);
                    secondCard.setImageResource(R.drawable.cubycard);
                }

                firstCard = null;
                secondCard = null;
                isBusy = false;
            }
        }, 700);
    }

    private void showGreatJobDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("🎉 Great Job!")
                .setMessage("You matched all the cards!\nPlay again?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialogInterface, which) -> restartGame())
                .setNegativeButton("Back", (dialogInterface, which) -> {
                    Intent intent = new Intent(MemoryGame.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.black));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.black));
    }

    private void restartGame() {
        matchedPairs = 0;
        shuffleImages();
        createBoard();
    }
}
