package com.example.cuby;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class BreathingTechniquesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing_techniques);

        Button btnBox = findViewById(R.id.btnBoxBreathing);
        Button btn478 = findViewById(R.id.btn478Breathing);

        // ✅ OPEN BOX BREATHING
        btnBox.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BreathingTechniquesActivity.this,
                    BoxBreathingActivity.class
            );
            startActivity(intent);
        });

        // ✅ OPEN 4-7-8 BREATHING
        btn478.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BreathingTechniquesActivity.this,
                    Four78BreathingActivity.class
            );
            startActivity(intent);
        });
    }
}
