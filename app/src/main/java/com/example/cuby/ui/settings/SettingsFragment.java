package com.example.cuby.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.cuby.R;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        MaterialSwitch switchNotifications = view.findViewById(R.id.switchNotifications);
        switchNotifications.setChecked(true);
        
        view.findViewById(R.id.btnResetData).setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                .setTitle("Reset Data?")
                .setMessage("This will delete all your progress. Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(getContext(), "Data Reset (Simulated)", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
        });
        
        view.findViewById(R.id.cardDisclaimer).setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                .setTitle("Disclaimer")
                .setMessage("Cuby is for reflection and support, not medical advice. If you are in crisis, please contact emergency services.")
                .setPositiveButton("OK", null)
                .show();
        });
    }
}
