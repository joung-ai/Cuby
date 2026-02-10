package com.example.cuby.ui.drawing;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cuby.R;
import com.example.cuby.data.AppRepository;
import com.example.cuby.ui.views.DrawingView;
import com.example.cuby.utils.DateUtils;
import com.example.cuby.utils.FileUtils;

public class DrawingActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private EditText etReflection;
    private RadioGroup rgMood;
    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        // 🔙 Custom toolbar (Drawing screen)
        TextView title = findViewById(R.id.toolbarTitleText);
        ImageView icon = findViewById(R.id.toolbarIcon);
        View backBtn = findViewById(R.id.btnBack);

        title.setText("Draw Your Plant");

        icon.setVisibility(View.GONE); // or set a drawing icon if you want

        backBtn.setOnClickListener(v -> finish()); // ⬅️ back to Garden


        repository = AppRepository.getInstance(getApplication());
        
        drawingView = findViewById(R.id.drawingView);
        etReflection = findViewById(R.id.etReflection);
        rgMood = findViewById(R.id.rgMood);
        
        findViewById(R.id.btnClear).setOnClickListener(v -> drawingView.startNew());
        
        findViewById(R.id.btnSave).setOnClickListener(v -> saveEntry());
        
        // Color pickers (simplified)
        findViewById(R.id.btnColorGreen).setOnClickListener(v -> drawingView.setColor("#4CAF50"));
        findViewById(R.id.btnColorRed).setOnClickListener(v -> drawingView.setColor("#F44336"));
        findViewById(R.id.btnColorBlue).setOnClickListener(v -> drawingView.setColor("#2196F3"));
        findViewById(R.id.btnColorPicker).setOnClickListener(v -> openColorPicker());
        findViewById(R.id.btnEraser).setOnClickListener(v -> {
            drawingView.enableEraser();
            Toast.makeText(this, "Eraser on 🧽", Toast.LENGTH_SHORT).show();
        });



    }

    private void saveEntry() {

        if (drawingView.getBitmap() == null) {
            Toast.makeText(this, "Draw something first 🌱", Toast.LENGTH_SHORT).show();
            return;
        }

        String reflection = etReflection.getText().toString().trim();

        String date = DateUtils.getTodayDate();
        String filename = "plant_" + date + "_" + System.currentTimeMillis() + ".png";

        String path = FileUtils.saveBitmap(
                this,
                drawingView.getBitmap(),
                filename
        );

        if (path == null) {
            Toast.makeText(this, "Failed to save drawing", Toast.LENGTH_SHORT).show();
            return;
        }

        int moodId = rgMood.getCheckedRadioButtonId();
        String mood = "HAPPY";

        if (moodId == R.id.rbCalm) mood = "CALM";
        else if (moodId == R.id.rbAnxious) mood = "ANXIOUS";
        else if (moodId == R.id.rbSad) mood = "SAD";


        // 🌸 RETURN RESULT TO GARDEN
        Intent result = new Intent();
        result.putExtra("drawing_path", path);
        result.putExtra("drawing_mood", mood);
        result.putExtra("drawing_reflection", reflection);

        setResult(RESULT_OK, result);

        Toast.makeText(this, "Your plant is ready 🌱", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void openColorPicker() {

        // Color names + values (clear & readable)
        final String[][] colors = {
                {"Black", "#000000"},
                {"White", "#FFFFFF"},
                {"Red", "#F44336"},
                {"Pink", "#E91E63"},
                {"Purple", "#9C27B0"},
                {"Indigo", "#3F51B5"},
                {"Blue", "#2196F3"},
                {"Light Blue", "#03A9F4"},
                {"Teal", "#009688"},
                {"Green", "#4CAF50"},
                {"Yellow", "#FFEB3B"},
                {"Orange", "#FF9800"},
                {"Brown", "#795548"}
        };

        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_color_picker, null);

        ViewGroup colorList = dialogView.findViewById(R.id.colorList);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Pick a color 🎨")
                .setView(dialogView)
                .create();

        // Dynamically add color rows
        for (String[] color : colors) {

            View row = getLayoutInflater()
                    .inflate(R.layout.item_color_option, colorList, false);

            View colorPreview = row.findViewById(R.id.viewColor);
            TextView colorName = row.findViewById(R.id.tvColorName);

            colorPreview.setBackgroundColor(
                    android.graphics.Color.parseColor(color[1])
            );
            colorName.setText(color[0]);

            row.setOnClickListener(v -> {
                drawingView.setColor(color[1]);
                dialog.dismiss();
            });

            colorList.addView(row);
        }

        dialog.show();
    }

}
