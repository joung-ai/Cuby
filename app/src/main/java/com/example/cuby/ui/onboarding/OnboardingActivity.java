package com.example.cuby.ui.onboarding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.cuby.R;
import com.example.cuby.data.AppRepository;
import com.example.cuby.model.UserProfile;
import com.example.cuby.notification.InactivityNotificationWorker;
import com.example.cuby.ui.home.HomeActivity;
import com.example.cuby.utils.Constants;

public class OnboardingActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etCubyName;
    private RadioGroup rgSkin;
    private Button btnStart;
    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markUserActive(); // Reset inactivity timer here

        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(Constants.KEY_USER_SETUP_COMPLETE, false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        repository = AppRepository.getInstance(getApplication());

        etUsername = findViewById(R.id.etUsername);
        etCubyName = findViewById(R.id.etCubyName);
        rgSkin = findViewById(R.id.rgSkin);
        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> saveAndStart());
    }

    @Override
    protected void onResume() {
        super.onResume();
        markUserActive(); // Reset inactivity timer on resume
    }

    private void saveAndStart() {
        String username = etUsername.getText().toString().trim();
        String cubyName = etCubyName.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Please enter your name");
            return;
        }

        if (TextUtils.isEmpty(cubyName)) {
            etCubyName.setError("Please name your Cuby");
            return;
        }

        int selectedSkinId = rgSkin.getCheckedRadioButtonId();
        String skin = "Default";
        if (selectedSkinId == R.id.rbSkinBlue) {
            skin = "Blue";
        } else if (selectedSkinId == R.id.rbSkinPink) {
            skin = "Pink";
        }

        UserProfile profile = new UserProfile();
        profile.id = 1;
        profile.username = username;
        profile.cubyName = cubyName;
        profile.cubySkin = skin;
        profile.createdAt = System.currentTimeMillis();

        repository.insertUserProfile(profile);

        getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.KEY_USER_SETUP_COMPLETE, true)
                .apply();

        Toast.makeText(this, "Welcome to Cuby!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void markUserActive() {
        SharedPreferences prefs = getSharedPreferences("inactivity_prefs", Context.MODE_PRIVATE);

        prefs.edit()
                .putLong("last_active_time", System.currentTimeMillis())
                .apply();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(InactivityNotificationWorker.class)
                        .build();

        WorkManager.getInstance(this).enqueue(request);
    }
}
