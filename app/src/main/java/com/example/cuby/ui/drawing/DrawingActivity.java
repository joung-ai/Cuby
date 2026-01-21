package com.example.cuby.ui.drawing;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cuby.R;
import com.example.cuby.data.AppRepository;
import com.example.cuby.model.DailyLog;
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
        String mood = "Okay"; // Default
        int moodId = rgMood.getCheckedRadioButtonId();
        
        if (moodId == R.id.rbHappy) mood = "Happy";
        else if (moodId == R.id.rbSad) mood = "Sad";
        else if (moodId == R.id.rbCalm) mood = "Calm";
        else if (moodId == R.id.rbAnxious) mood = "Anxious";

        String date = DateUtils.getTodayDate();
        String filename = "plant_" + date + ".png";
        String path = FileUtils.saveBitmap(this, drawingView.getBitmap(), filename);
        
        DailyLog log = new DailyLog(date);
        log.mood = mood;
        log.reflectionNote = reflection;
        log.seedPlanted = true;
        log.drawingPath = path;
        log.lastUpdated = System.currentTimeMillis();
        
        repository.insertDailyLog(log);
        
        Toast.makeText(this, "Seed planted successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
