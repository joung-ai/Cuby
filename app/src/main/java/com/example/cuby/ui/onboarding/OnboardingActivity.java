package com.example.cuby.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cuby.R;
import com.example.cuby.data.AppRepository;
import com.example.cuby.model.Inventory;
import com.example.cuby.model.UserProfile;
import com.example.cuby.ui.home.HomeActivity;
import com.example.cuby.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class OnboardingActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etCubyName;
    private RadioGroup rgSkin;
    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(Constants.KEY_ONBOARDING_COMPLETE, false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_onboarding);
        repository = AppRepository.getInstance(getApplication());

        etUsername = findViewById(R.id.etUsername);
        etCubyName = findViewById(R.id.etCubyName);
        rgSkin = findViewById(R.id.rgSkin);
        MaterialButton btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> saveAndProceed());
    }

    private void saveAndProceed() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String cubyName = etCubyName.getText() != null ? etCubyName.getText().toString().trim() : "";

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cubyName.isEmpty()) {
            cubyName = "Cuby";
        }

        String skin = "Default";
        int checkedId = rgSkin.getCheckedRadioButtonId();
        if (checkedId == R.id.rbPink) skin = "Pink";
        else if (checkedId == R.id.rbBlue) skin = "Blue";

        UserProfile profile = new UserProfile();
        profile.username = username;
        profile.cubyName = cubyName;
        profile.cubySkin = skin;
        profile.createdAt = System.currentTimeMillis();
        repository.insertUserProfile(profile);

        Inventory inventory = new Inventory();
        inventory.bloxyFoodCount = 5;
        repository.insertInventory(inventory);

        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(Constants.KEY_ONBOARDING_COMPLETE, true).apply();

        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
