package com.linwoain.storytelling.mp3media

import android.media.AudioManager
import android.media.MediaPlayer
import com.linwoain.storytelling.bean.ChapterBean
import com.linwoain.storytelling.bus.Progress
import com.linwoain.storytelling.config.Constant
import com.linwoain.util.CacheUtil
import com.linwoain.util.LLogUtils
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.util.*

/**
 * create by linwoain on 2016/6/3
 */
class Mp3PlayerManager private constructor() : MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private var curChapters: List<ChapterBean> = ArrayList()

    fun play(chapters: List<ChapterBean>, position: Int) {
        if (player != null) {
            player?.reset()
        } else {
            player = MediaPlayer()
            player?.setOnPreparedListener(this)
            player?.setOnErrorListener(this)
            player?.setOnCompletionListener(this)
            player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }

        curPos = position
        curChapters = chapters
        val (_, _, _, audioURL) = chapters[position]

        preparePlay(audioURL)
    }

    private fun preparePlay(audioURL: String) {
        try {
            player?.setDataSource(audioURL)
            player?.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onPrepared(mp: MediaPlayer) {
        val content = curChapters[curPos]
        CacheUtil.save(Constant.MULU + content.bookId, content)
        EventBus.getDefault().post(content)
        EventBus.getDefault().post(Progress(mp.duration, content.bookId, true, curPos, content.title))
        mp.start()
    }

    override fun onCompletion(mp: MediaPlayer) {
        curPos++
        if (curChapters.size >= curPos + 1) {
            play(curChapters, curPos)
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        LLogUtils.e("播放出错了" + what)
        return false
    }

    fun pause() {

        if (player?.isPlaying!!) {
            player?.pause()
        }
    }

    fun restart() {

        if (player != null) {
            player?.start()
        }
    }

    val playingBookId: Int
        get() {
            if (curChapters.isEmpty()) {
                return -1
            }
            return curChapters[curPos].bookId
        }

    val isPlaying: Boolean
        get() = player != null && player?.isPlaying!!

    fun stop() {

        if (player != null) {
            player?.stop()
            player?.release()
            player = null
        }
    }

    companion object {
        private var player: MediaPlayer? = null
        private var curPos: Int = 0

        val instance = Mp3PlayerManager()
    }
}