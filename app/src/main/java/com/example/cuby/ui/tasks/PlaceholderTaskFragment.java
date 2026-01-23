package com.example.cuby.ui.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.cuby.R;
import com.example.cuby.data.AppRepository;
import com.example.cuby.logic.DailyTask;
import com.example.cuby.logic.DailyTaskEngine;
import com.example.cuby.logic.TaskCompletionManager;
import com.example.cuby.model.DailyLog;
import java.time.LocalDate;
import java.util.List;

public class PlaceholderTaskFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_placeholder_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView tvTitle = view.findViewById(R.id.tvTaskTitle);
        TextView tvDesc = view.findViewById(R.id.tvTaskDesc);

        String today = LocalDate.now().toString();
        AppRepository repo =
                AppRepository.getInstance(requireActivity().getApplication());

        DailyLog log = repo.getDailyLogSync(today);
        if (log == null || log.mood == null) return;

        List<DailyTask> tasks =
                DailyTaskEngine.generateTaskSequence(log.mood);

        DailyTask currentTask = tasks.get(log.currentTaskIndex);

        tvTitle.setText(currentTask.title);
        tvDesc.setText(currentTask.description);

        view.findViewById(R.id.btnCompleteTask).setOnClickListener(v -> {

            TaskCompletionManager manager =
                    new TaskCompletionManager(repo);

            manager.completeCurrentTask(today, tasks.size());

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
    }
}
