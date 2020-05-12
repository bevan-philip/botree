package com.bphilip.botree

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class MusicService: Service() {

    companion object {
        const val RESOURCE_NAME = "com.bphilip.botree.MusicService.RESOURCE_NAME"
        const val IS_LOOP = "com.bphilip.botree.MusicService.IS_LOOP"
    }

    private var mMediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.getIntExtra(RESOURCE_NAME, 0).let {
            mMediaPlayer = MediaPlayer.create(applicationContext, it)
        }
        intent.getBooleanExtra(IS_LOOP, false).let {
            if (it) {
                mMediaPlayer?.isLooping = true
            }
            else {
                mMediaPlayer?.setOnCompletionListener {
                    onDestroy()
                }
            }
        }

        mMediaPlayer?.start()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // We don't support binding.
        return null
    }

    override fun onDestroy() {
        Log.i("MusicService", "Ending MusicService.")
        super.onDestroy()
        mMediaPlayer?.stop()
        mMediaPlayer?.reset()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }
}

