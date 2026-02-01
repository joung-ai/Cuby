package com.example.cuby.alarm;

import java.util.UUID;

public class AlarmItem {

    public String time;
    public UUID workId;
    public boolean enabled;

    public AlarmItem(String time, UUID workId) {
        this.time = time;
        this.workId = workId;
        this.enabled = true;
    }
}
