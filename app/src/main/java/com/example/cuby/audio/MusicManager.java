package com.example.cuby.audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.example.cuby.R;

public class MusicManager {

    public enum Track {
        HOME,
        GARDEN
    }

    private static MediaPlayer mediaPlayer;
    private static Track currentTrack = null;

    private static final String PREFS = "cuby_audio";
    private static final String KEY_VOLUME = "music_volume";

    public static void play(Context context, Track track) {
        if (track == currentTrack && mediaPlayer != null && mediaPlayer.isPlaying()) {
            return; // ✅ Already playing correct music
        }

        stopInternal();

        int resId = getTrackRes(track);
        mediaPlayer = MediaPlayer.create(context.getApplicationContext(), resId);
        mediaPlayer.setLooping(true);

        float volume = getSavedVolume(context);
        mediaPlayer.setVolume(volume, volume);

        mediaPlayer.start();
        currentTrack = track;
    }

    private static int getTrackRes(Track track) {
        switch (track) {
            case GARDEN:
                return R.raw.bg_music;
            case HOME:
            default:
                return R.raw.bg_music_garden;
        }
    }

    private static void stopInternal() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public static void resume(Context context) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        } else if (currentTrack != null) {
            play(context, currentTrack);
        }
    }

    public static void release() {
        stopInternal();
        currentTrack = null;
    }

    public static void setVolume(Context context, float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
        saveVolume(context, volume);
    }

    public static float getSavedVolume(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_VOLUME, 0.5f);
    }

    private static void saveVolume(Context context, float volume) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putFloat(KEY_VOLUME, volume).apply();
    }
}
