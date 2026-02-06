package com.example.cuby.ui.drawing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    }

    private void saveEntry() {
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

        // 🌸 RETURN RESULT TO GARDEN
        Intent result = new Intent();
        result.putExtra("drawing_path", path);
        setResult(RESULT_OK, result);

        Toast.makeText(this, "Your plant is ready 🌱", Toast.LENGTH_SHORT).show();
        finish();
    }
}
