package com.linwoain.storytelling

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import com.linwoain.storytelling.adapter.BookAdapter
import com.linwoain.storytelling.bean.BookInfo
import com.linwoain.storytelling.bus.Progress
import com.linwoain.storytelling.config.Constant
import com.linwoain.util.CacheUtil
import com.linwoain.util.GsonUtil
import com.linwoain.util.LLogUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.Serializable
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    internal var books: MutableList<BookInfo> = Collections.synchronizedList(ArrayList())
    internal var adapter: BookAdapter? = null
    private val flowerUrl = "http://42.121.125.229:8080/audible-book/service/audioBooksV2/getBookChaptersByPage?market=k-app360&refer=SearchResultBookList&dir=DESC&token=QUi2Cp%2BU09ITUXjudFZ2CtiCKUWfP1%2FT5KepiuHKXM77WDCYJwpQNoxu37pltHhH&pageSize=200&bookId=190518&imsi=460072521701419&ver=3.9.1&appKey=audibleBook"
    private val huaxuyinUrl = "http://42.121.125.229:8080/audible-book/service/audioBooksV2/getBookChaptersByPage?market=k-app360&refer=SearchResultBookList&dir=ASC&token=QUi2Cp%2BU09ITUXjudFZ2CtiCKUWfP1%2FT5KepiuHKXM77WDCYJwpQNoxu37pltHhH&pageSize=200&bookId=212947&imsi=460072521701419&ver=3.9.1&appKey=audibleBook"

    val BUNDLE_KEY = "bundle_key"
    val BUNDLE_KEY_BOOKS = "bundle_key_books"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerEventBus()
        initView()
        val tmpBooks = savedInstanceState?.getSerializable(BUNDLE_KEY_BOOKS)
        LLogUtils.d("savedInstanceState == null is ${savedInstanceState == null}, and tmpBooks == null is ${tmpBooks == null}")
        if (tmpBooks != null) {
            books = tmpBooks as ArrayList<BookInfo>
            setData()
            toast("恢复现场")
        } else {
            toast("获取数据")

            getData()
        }
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        LLogUtils.i("outState == null is ${outState == null} and books.size = ${books.size}")
        toast("保存现场")
        if (books.size != 0) {
            val data = Bundle()
            data.putBundle(BUNDLE_KEY, outState)
            data.putSerializable(BUNDLE_KEY_BOOKS, books as Serializable)
            super.onSaveInstanceState(data)
        } else {
            super.onSaveInstanceState(outState)

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
                if (!CacheUtil.getBoolean(Constant.NIGHT_MODE)) {//非夜间模式时，设置为夜间模式
                    delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    CacheUtil.save(Constant.NIGHT_MODE, true)
                    recreate()
                } else {
                    delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    CacheUtil.save(Constant.NIGHT_MODE, false)
                    recreate()
                }
            }
            else -> toast("应该不可能发生的事情发生了😯")
        }

        return true
    }

    private fun getData() {
        doAsync {
            val flowerText = URL(flowerUrl).readText()
            val result = GsonUtil.get(flowerText, BookInfo::class.java)
            result.name = "花千骨"
            result.url = flowerUrl
            result.bookId = 190518
            books.add(result)
            uiThread {
                setData()
            }
        }

        doAsync {
            val huaxuyinText = URL(huaxuyinUrl).readText()
            val result = GsonUtil.get(huaxuyinText, BookInfo::class.java)
            result.name = "华胥引"
            result.url = huaxuyinUrl
            result.bookId = 212947
            books.add(result)
            uiThread {
                setData()
            }

        }

    }

    private fun registerEventBus() {
        EventBus.getDefault().register(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(progress: Progress) {
        for (book in books) {
            if (book.bookId == progress.bookId) {
                book.title = progress.title
                setData()
            }
        }

    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
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
