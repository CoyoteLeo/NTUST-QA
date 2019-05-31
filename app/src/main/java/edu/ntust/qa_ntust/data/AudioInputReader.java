package edu.ntust.qa_ntust.data;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;

import edu.ntust.qa_ntust.MainActivity;
import edu.ntust.qa_ntust.R;

public class AudioInputReader {
    private final Context mContext;
    private MediaPlayer mPlayer;

    public AudioInputReader(Context context) {
        this.mContext = context;
        initReader();
    }

    private void initReader() {
        // Setup media player
        if(mPlayer == null) {
            mPlayer = MediaPlayer.create(mContext, R.raw.bensoundcute);
            mPlayer.setLooping(true);
            mPlayer.start();
        }
    }

    public void shutdown(boolean isFinishing) {

        if (mPlayer != null) {
            mPlayer.pause();
            if (isFinishing == true) {
                mPlayer.release();
                mPlayer = null;
            }
        }
    }

    public void restart() {

        if (mPlayer != null) {
            mPlayer.start();
        }
    }

}
