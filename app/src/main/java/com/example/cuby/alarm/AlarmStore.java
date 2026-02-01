package com.example.cuby.alarm;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlarmStore {

    private static final String PREFS_NAME = "saved_alarms";
    private static final String KEY_ALARMS = "alarms";

    public static final List<AlarmItem> alarms = new ArrayList<>();

    public static void load(Context context) {
        alarms.clear();

        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String json = prefs.getString(KEY_ALARMS, null);
        if (json == null) return;

        try {
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                AlarmItem alarm = new AlarmItem(
                        obj.getString("time"),
                        UUID.fromString(obj.getString("workId"))
                );

                alarm.enabled = obj.getBoolean("enabled");
                alarms.add(alarm);
            }
        } catch (Exception ignored) {}
    }

    public static void save(Context context) {
        try {
            JSONArray array = new JSONArray();

            for (AlarmItem alarm : alarms) {
                JSONObject obj = new JSONObject();
                obj.put("time", alarm.time);
                obj.put("enabled", alarm.enabled);
                obj.put("workId", alarm.workId.toString());
                array.put(obj);
            }

            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_ALARMS, array.toString())
                    .apply();

        } catch (Exception ignored) {}
    }
}
