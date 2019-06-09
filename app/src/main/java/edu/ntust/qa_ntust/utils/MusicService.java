package edu.ntust.qa_ntust.utils;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import edu.ntust.qa_ntust.R;

public class MusicService extends Service implements MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new ServiceBinder();

    public void setmOn(Boolean mOn) {
        this.mOn = mOn;
    }

    private Boolean mOn = false;
    MediaPlayer mPlayer;
    private int length = 0;

    public MusicService() {
    }

    public class ServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mOn = sharedPreferences.getBoolean("play_music", getResources().getBoolean(R.bool.pref_play_music_default));

        mPlayer = MediaPlayer.create(this, R.raw.bensoundcute);
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(100, 100);
            if (mOn && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                serviceMusic();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mPlayer.isPlaying()) {
            mPlayer.seekTo(length);
            mPlayer.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
    }

    public void pauseMusic() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            length = mPlayer.getCurrentPosition();

        }
    }

    public void serviceMusic() {
        if (mOn) {
            if (mPlayer != null && !mPlayer.isPlaying()) {
                mPlayer.seekTo(length);
                mPlayer.start();
            }
        } else {
            pauseMusic();
        }
    }


    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        return false;
    }
}