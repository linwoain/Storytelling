package com.linwoain.storytelling

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import com.linwoain.storytelling.adapter.BookAdapter
import com.linwoain.storytelling.bean.Novel
import com.linwoain.storytelling.bean.NovelWrapper
import com.linwoain.storytelling.bus.Progress
import com.linwoain.storytelling.config.Constant
import com.linwoain.storytelling.utils.process
import com.linwoain.util.CacheUtil
import com.linwoain.util.GsonUtil
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.litepal.crud.DataSupport
import java.util.*


class MainActivity : AppCompatActivity() {

    internal var books: ArrayList<Novel> = ArrayList()

    internal var adapter: BookAdapter? = null

    companion object {
        private val NOVEL_LIST_URL = "https://bwg.linwoain.com/novel"
        private val BUNDLE_BOOKS = "BUNDLE_BOOKS"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerEventBus()
        initView()
        val tempBooks = savedInstanceState?.getSerializable(BUNDLE_BOOKS)
        if (tempBooks != null) {
            books.addAll(tempBooks as ArrayList<Novel>)
        }
        if (books.size == 0) {
            getData()
        } else {
            setData()
        }

    }


    private fun initView() {
        setSupportActionBar(toolbar)
        listview.setOnItemClickListener { _, _, position, _ -> startActivity<MuluActivity>(Constant.BEAN to books[position]) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> startActivity<AboutActivity>()
            R.id.action_night -> {
                val nightMode = CacheUtil.getBoolean(Constant.NIGHT_MODE)
                val builder = StringBuilder(nightMode.toString())

                if (!nightMode) {//非夜间模式时，设置为夜间模式
                    delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    CacheUtil.save(Constant.NIGHT_MODE, true)
                    builder.append(" -> true")
                    recreate()
                } else {
                    delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    CacheUtil.save(Constant.NIGHT_MODE, false)
                    builder.append(" -> false")

                    recreate()
                }
                Logger.d(builder.toString())
            }
            else -> toast("应该不可能发生的事情发生了😯")
        }

        return true
    }

    private fun getData() {

        val data = getDataFromDB()
        if (data.isNotEmpty()) {
            Logger.d("book data from db")
            books.addAll(data)
            setData()
            return
        }
        getDataFromNet()


    }

    private fun getDataFromNet() {
        process(NOVEL_LIST_URL) {
            val wrapper = GsonUtil.get(it, NovelWrapper::class.java)
            books.addAll(wrapper.novels)
            setDataToDB()
            setData()
        }
    }

    private fun setDataToDB() {
        books.forEach {
            it.save()
        }
    }

    private fun getDataFromDB(): List<Novel> {
        return DataSupport.findAll(Novel::class.java)

    }

    private fun registerEventBus() {
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(progress: Progress) {
        books.forEach {
            if (it.bookId == progress.bookId) {
                it.title = progress.title
                setData()
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (books.size == 2) {
            outState?.putSerializable(BUNDLE_BOOKS, books)
        }
    }


    private fun setData() {
        if (adapter == null) {
            adapter = BookAdapter(this, books)
            listview.adapter = adapter
        } else {
            adapter?.notifyDataSetChanged()
        }
    }
}
