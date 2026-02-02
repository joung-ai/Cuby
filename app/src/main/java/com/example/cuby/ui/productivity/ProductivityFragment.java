package com.example.cuby.ui.productivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cuby.R;
import com.example.cuby.logic.PomodoroFragment;

public class ProductivityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.activity_productivity_techniques, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ===== TOOLBAR =====
        TextView title = view.findViewById(R.id.toolbarTitleText);
        ImageView icon = view.findViewById(R.id.toolbarIcon);
        View backBtn = view.findViewById(R.id.btnBack);

        title.setText("Productivity");

        icon.setImageResource(R.drawable.ic_productivity);
        icon.setVisibility(View.VISIBLE);

        backBtn.findViewById(R.id.btnBack)
                .setOnClickListener(v ->
                        requireActivity()
                                .getOnBackPressedDispatcher()
                                .onBackPressed()
                );

        // ===== BUTTONS =====
        View btnFocus = view.findViewById(R.id.btnFocus);
        View btnPomodoro = view.findViewById(R.id.btnPomodoro);

        btnFocus.setOnClickListener(v -> {
            // TODO: navigate to Focus mode
            // Example:
            // navigateTo(new FocusFragment());
        });

        btnPomodoro.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new PomodoroFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}
