package com.example.cuby.ui.breathing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cuby.R;
import com.example.cuby.BoxBreathingActivity;
import com.example.cuby.Four78BreathingActivity;
import com.google.android.material.card.MaterialCardView;

public class BreathingTechniquesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.activity_breathing_techniques,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.toolbarTitleText);
        ImageView icon = view.findViewById(R.id.toolbarIcon);
        View backBtn = view.findViewById(R.id.btnBack);

        title.setText("Meditate and Relax");

        icon.setImageResource(R.drawable.ic_meditate);
        icon.setVisibility(View.VISIBLE);

        backBtn.findViewById(R.id.btnBack)
                .setOnClickListener(v ->
                        requireActivity()
                                .getOnBackPressedDispatcher()
                                .onBackPressed()
                );

        MaterialCardView btnBox = view.findViewById(R.id.btnBoxBreathing);
        MaterialCardView btn478 = view.findViewById(R.id.btn478Breathing);

        // ✅ OPEN BOX BREATHING
        btnBox.setOnClickListener(v -> {
            Intent intent = new Intent(
                    requireContext(),
                    BoxBreathingActivity.class
            );
            startActivity(intent);
        });

        // ✅ OPEN 4-7-8 BREATHING
        btn478.setOnClickListener(v -> {
            Intent intent = new Intent(
                    requireContext(),
                    Four78BreathingActivity.class
            );
            startActivity(intent);
        });
    }
}
