package com.linwoain.storytelling.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.IBinder
import com.linwoain.storytelling.bean.ChapterBean
import com.linwoain.storytelling.mp3media.Mp3PlayerManager
import org.jetbrains.anko.doAsync
import java.io.Serializable

class Mp3PlayerService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onHandleIntent(intent)
        startService(Intent(this, DaemonService::class.java))
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val chapters = intent.getSerializableExtra(EXTRA_PARAM1) as List<ChapterBean>
            val position = intent.getIntExtra(EXTRA_PARAM2, 0)
            doAsync {
                Mp3PlayerManager.instance.play(chapters, position)
            }

        }
    }

    private inner class NoisyAudioStreamReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                val state = intent.getIntExtra("state", -1)
                when (state) {
                    0 ->
                        //拔出耳机
                        Mp3PlayerManager.instance.stop()
                    1 -> {
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        //注册广播：
        val noisyAudioStreamReceiver = NoisyAudioStreamReceiver()
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        filter.addAction(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(noisyAudioStreamReceiver, filter)
    }

    companion object {

        private val EXTRA_PARAM1 = "com.linwoain.storytelling.service.extra.PARAM1"
        private val EXTRA_PARAM2 = "com.linwoain.storytelling.service.extra.PARAM2"

        fun openMp3(context: Context, chapters: List<ChapterBean>, postion: Int) {
            val intent = Intent(context, Mp3PlayerService::class.java)
            intent.putExtra(EXTRA_PARAM1, chapters as Serializable)
            intent.putExtra(EXTRA_PARAM2, postion)
            context.startService(intent)
        }
    }
}
