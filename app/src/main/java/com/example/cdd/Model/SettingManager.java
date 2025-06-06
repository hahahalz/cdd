package com.example.cdd.Model;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import java.io.IOException;

public class SettingManager {
    private static SettingManager instance;
    private MediaPlayer mediaPlayer;
    private Context context;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private int currentMusicResId = -1;

    private SettingManager(Context context) {
        this.context = context.getApplicationContext();
        mediaPlayer = new MediaPlayer();
    }

    public static SettingManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingManager(context);
        }
        return instance;
    }

    // 播放音乐（支持从头播放或恢复播放）
    public void playOrResumeMusic(int musicResId) {
        if (currentMusicResId == musicResId && isPaused) {
            // 恢复已暂停的音乐
            resumeMusic();
        } else {
            // 播放新音乐
            playMusic(musicResId);
        }
    }

    // 播放新音乐
    private void playMusic(int musicResId) {
        stopMusic(); // 停止当前播放
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + musicResId));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                isPaused = false;
                currentMusicResId = musicResId;
            });
            mediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 暂停音乐
    public void pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            isPaused = true;
        }
    }

    // 恢复音乐
    public void resumeMusic() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.start();
            isPlaying = true;
            isPaused = false;
        }
    }

    // 停止音乐
    public void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            isPlaying = false;
            isPaused = false;
            currentMusicResId = -1;
        }
    }

    // 获取当前状态
    public MusicState getState() {
        if (isPlaying) return MusicState.PLAYING;
        if (isPaused) return MusicState.PAUSED;
        return MusicState.STOPPED;
    }

    // 音乐状态枚举
    public enum MusicState {
        PLAYING, PAUSED, STOPPED
    }
}