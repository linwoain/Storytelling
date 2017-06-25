package com.linwoain.storytelling

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.linwoain.storytelling.adapter.MuluAdapter
import com.linwoain.storytelling.bean.BookInfo
import com.linwoain.storytelling.bean.ChapterBean
import com.linwoain.storytelling.bean.Novel
import com.linwoain.storytelling.bus.Progress
import com.linwoain.storytelling.config.Constant
import com.linwoain.storytelling.mp3media.Mp3PlayerManager
import com.linwoain.storytelling.service.Mp3PlayerService
import com.linwoain.storytelling.utils.process
import com.linwoain.util.CacheUtil
import com.linwoain.util.GsonUtil
import kotlinx.android.synthetic.main.activity_mulu.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class MuluActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mulu)
        initView()
        initData()
        registerBus()
    }

    private fun registerBus() {
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(progress: Progress) {
        if (progress.bookId == info.bookId) {
            if (progress.isPlaying) {
                play.setImageResource(R.drawable.playing)
                showPlay()

                play.setOnClickListener(pauseListener)
            }
        }
    }


    private val playListener = View.OnClickListener {
        val c = CacheUtil.getBaseBean(Constant.MULU + info.bookId) as ChapterBean
        var i = chapters.indexOf(c)
        if (i < 0) {
            i = 0
        }
        Mp3PlayerService.openMp3(this@MuluActivity, chapters, i)
    }
    val replayListener: View.OnClickListener = View.OnClickListener {
        if (Mp3PlayerManager.instance.playingBookId == info.bookId) {
            play.setImageResource(R.drawable.playing)
            Mp3PlayerManager.instance.restart()
            play.setOnClickListener(pauseListener)
        }
    }
    val pauseListener = View.OnClickListener {
        if (Mp3PlayerManager.instance.playingBookId == info.bookId) {
            play.setImageResource(R.drawable.play_selector)
            Mp3PlayerManager.instance.pause()
            play.setOnClickListener(replayListener)
        }
    }

    private fun initData() {

        bar_title.text = info.name
        val bookId = info.bookId
        getDataFromNet(bookId)


    }

    companion object {
        private val NOTE_GET_URL = "http://42.121.125.229:8080/audible-book/service/audioBooksV2/getBookChaptersByPage?dir=DESC&&pageSize=200&bookId="
    }

    private fun getDataFromNet(bookId: Int) {
        process(NOTE_GET_URL + bookId) {
            val bookInfo = GsonUtil.get(it, BookInfo::class.java)
            chapters.addAll(bookInfo.chapter)
            setData()
        }

    }

    val info by lazy {
        intent.extras.getSerializable(Constant.BEAN) as Novel
    }

    private fun showPlay() {
        val drawable = play.drawable
        if (drawable is AnimationDrawable) {
            drawable.start()
        }
    }

    public override fun onResume() {
        super.onResume()
        if (Mp3PlayerManager.instance.playingBookId == info.bookId && Mp3PlayerManager.instance.isPlaying) {
            play.setImageResource(R.drawable.playing)
            showPlay()
            play.setOnClickListener(pauseListener)
        }
    }

    internal var chapters: MutableList<ChapterBean> = ArrayList()

    public override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        listview.setOnItemClickListener { _, _, position, _ -> Mp3PlayerService.openMp3(this, chapters, position) }
        play.setOnClickListener(playListener)
    }

    private fun setData() {
        for (chapter in chapters) {
            chapter.bookId = info.bookId
        }
        val adapter = MuluAdapter(this, chapters)
        listview.adapter = adapter
    }
}