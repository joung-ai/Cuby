package com.example.cuby.ui.focus;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.cuby.R;

public class FocusActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;  // The root layout to add/remove the button
    private Button startFocusButton;      // The dynamically created button
    private View focusOverlay;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);


        // Initialize NotificationManager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Find the root layout and overlay
        rootLayout = findViewById(R.id.constraintLayout);
        focusOverlay = findViewById(R.id.focusOverlay);
        View stopFocusButton = findViewById(R.id.stopFocusButton);

        // Create the Start button programmatically
        startFocusButton = new Button(this);
        startFocusButton.setText("Start Focus Mode");
        startFocusButton.setId(View.generateViewId());  // Generate a unique ID

        // Set layout params to center it (similar to XML constraints)
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        startFocusButton.setLayoutParams(params);

        // Add the button to the root layout
        rootLayout.addView(startFocusButton);

        // Set click listener for start button
        startFocusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if notification policy access is granted
                if (notificationManager.isNotificationPolicyAccessGranted()) {
                    // Enable DND to suppress notifications
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                    Toast.makeText(FocusActivity.this, "Focus Mode started.", Toast.LENGTH_SHORT).show();
                } else {
                    // Request permission
                    Toast.makeText(FocusActivity.this, "Grant notification access in Settings to suppress notifications.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                    return;  // Don't start Focus Mode yet
                }

                // Show the overlay and remove the button from the layout
                focusOverlay.setVisibility(View.VISIBLE);
                rootLayout.removeView(startFocusButton);
            }
        });

        // Set click listener for stop button
        stopFocusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable DND to restore notifications
                if (notificationManager.isNotificationPolicyAccessGranted()) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                }

                // Hide the overlay and re-add the button to the layout
                focusOverlay.setVisibility(View.GONE);
                rootLayout.addView(startFocusButton);
                Toast.makeText(FocusActivity.this, "Focus Mode stopped.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}