package com.example.cuby.ui.garden;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuby.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GardenFragment extends Fragment {

    private GardenViewModel viewModel;
    private TextView tvMonth;
    private Calendar currentCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_garden, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(GardenViewModel.class);
        
        tvMonth = view.findViewById(R.id.tvMonth);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        
        view.findViewById(R.id.btnPrev).setOnClickListener(v -> changeMonth(-1));
        view.findViewById(R.id.btnNext).setOnClickListener(v -> changeMonth(1));
        
        updateMonthDisplay();
    }

    private void changeMonth(int delta) {
        currentCalendar.add(Calendar.MONTH, delta);
        updateMonthDisplay();
    }

    private void updateMonthDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonth.setText(sdf.format(currentCalendar.getTime()));
    }
}
