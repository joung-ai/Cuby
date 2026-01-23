package com.example.cuby.logic;

import com.example.cuby.data.AppRepository;
import com.example.cuby.model.DailyLog;

public class TaskCompletionManager {

    private final AppRepository repository;

    public TaskCompletionManager(AppRepository repository) {
        this.repository = repository;
    }

    /**
     * Marks the CURRENT task as completed and advances progress.
     * Unlocks seed ONLY after all tasks are completed.
     */
    public void completeCurrentTask(String date, int totalTasks) {

        if (date == null || totalTasks <= 0) return;

        DailyLog log = repository.getDailyLogSync(date);

        // Safety checks
        if (log == null) return;
        if (log.taskCompleted) return;

        // Advance to next task
        log.currentTaskIndex++;

        // Check if all tasks are done
        if (log.currentTaskIndex >= totalTasks) {
            log.taskCompleted = true;
            log.seedUnlocked = true;
            log.taskId = null; // clear active task
        }

        log.lastUpdated = System.currentTimeMillis();

        repository.insertDailyLog(log);
    }
}
