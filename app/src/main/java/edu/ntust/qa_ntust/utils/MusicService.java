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

    private Boolean mOn = false;//要不要撥放音樂的開關
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

    //android.content.SharedPreferences類別，可以存小資訊，儲存如帳號、設定、上一次登入時間、遊戲關卡或電子郵件

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mOn = sharedPreferences.getBoolean("play_music", getResources().getBoolean(R.bool.pref_play_music_default));//提取是否要撥放音樂的設定值

        mPlayer = MediaPlayer.create(this, R.raw.bensoundcute);
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {
            mPlayer.setLooping(true);//循環播放
            mPlayer.setVolume(100, 100);//設置音量
            if (mOn && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {//檢查權限
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
    public void onDestroy() {//該activity結束時，要把音樂播放棄釋放掉
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
        if (mPlayer != null && mPlayer.isPlaying()) {//如果撥放器不為空 且 播放棄沒再放音樂
            mPlayer.pause();//停止音樂
            length = mPlayer.getCurrentPosition();//記錄撥放到哪裡

        }
    }

    public void serviceMusic() {
        if (mOn) {//如果開關是開的
            if (mPlayer != null && !mPlayer.isPlaying()) {//如果撥放器不為空 且 播放棄沒再放音樂
                mPlayer.seekTo(length);//把播放的游標一道length的地方
                mPlayer.start();//從游標的地方開始撥放音樂
            }
        } else {//開關是關的
            pauseMusic();//停止撥放音樂
        }
    }


    public boolean onError(MediaPlayer mp, int what, int extra) {//如果撥放音樂時出錯，就把音樂播放棄丟掉

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