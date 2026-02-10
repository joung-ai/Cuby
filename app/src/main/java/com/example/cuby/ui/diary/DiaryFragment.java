package com.example.cuby.ui.diary;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuby.R;
import com.example.cuby.logic.CubyMoodEngine;
import com.example.cuby.model.DiaryEntry;
import com.example.cuby.utils.DateUtils;

public class DiaryFragment extends Fragment {

    private DiaryViewModel viewModel;
    private DiaryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.toolbarTitleText);
        ImageView icon = view.findViewById(R.id.toolbarIcon);
        View backBtn = view.findViewById(R.id.btnBack);

        title.setText("Diary");

        icon.setImageResource(R.drawable.ic_diary);
        icon.setVisibility(View.VISIBLE);

        backBtn.findViewById(R.id.btnBack)
                .setOnClickListener(v ->
                        requireActivity()
                                .getOnBackPressedDispatcher()
                                .onBackPressed()
                );

        viewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DiaryAdapter(this::showEditDialog);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.fabAdd).setOnClickListener(v -> showAddDialog());

        viewModel.getAllEntries().observe(getViewLifecycleOwner(), entries -> adapter.setEntries(entries));
    }

    private void showAddDialog() {
        showEntryDialog(null);
    }

    private void showEditDialog(DiaryEntry entry) {
        showEntryDialog(entry);
    }

    private void showEntryDialog(DiaryEntry entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_diary_entry, null);

        EditText etContent = view.findViewById(R.id.etContent);
        Switch swCuby = view.findViewById(R.id.swCubyMode);
        TextView tvCubyPrompt = view.findViewById(R.id.tvCubyPrompt);

// 📓 notebook styling for the prompt board
        tvCubyPrompt.setBackgroundResource(R.drawable.bg_notebook);
        int pad = (int) (12 * getResources().getDisplayMetrics().density);
        tvCubyPrompt.setPadding(pad, pad, pad, pad);
        tvCubyPrompt.setVisibility(View.GONE);

        Button btnDelete = view.findViewById(R.id.btnDelete);

        swCuby.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvCubyPrompt.setVisibility(View.VISIBLE);
                tvCubyPrompt.setText("Cuby asks: " + CubyMoodEngine.getDailyQuote()); // Reuse quote as prompt
            } else {
                tvCubyPrompt.setVisibility(View.GONE);
            }
        });

        if (entry != null) {
            etContent.setText(entry.content);
            btnDelete.setVisibility(View.VISIBLE);

            btnDelete.setOnClickListener(v -> {

                AlertDialog confirmDialog =
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Delete entry?")
                                .setMessage("This action cannot be undone.")
                                .setNegativeButton("Delete", (dialog, which) -> {
                                    viewModel.delete(entry);
                                    dialog.dismiss();
                                })
                                .setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .create();

                confirmDialog.show();

                // 🖤 Force black buttons
                confirmDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(Color.BLACK);
                confirmDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(Color.BLACK);

                confirmDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
                confirmDialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
            });

        } else {
            btnDelete.setVisibility(View.GONE);
        }

        AlertDialog dialog = builder
                .setView(view)
                .setTitle(entry == null ? "New Entry" : "Edit Entry")
                .setPositiveButton("Save", (d, which) -> {
                    String content = etContent.getText().toString().trim();
                    if (!content.isEmpty()) {
                        if (entry == null) {
                            DiaryEntry newEntry = new DiaryEntry();
                            newEntry.content = content;
                            newEntry.date = DateUtils.getTodayDate();
                            newEntry.createdAt = System.currentTimeMillis();
                            newEntry.updatedAt = System.currentTimeMillis();
                            viewModel.insert(newEntry);
                        } else {
                            entry.content = content;
                            entry.updatedAt = System.currentTimeMillis();
                            viewModel.update(entry);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

// 🖤 FORCE PURE BLACK BUTTON TEXT
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

// Optional polish
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
    }


}
