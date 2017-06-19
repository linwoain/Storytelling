package com.linwoain.storytelling

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.linwoain.storytelling.adapter.BookAdapter
import com.linwoain.storytelling.bean.BookInfo
import com.linwoain.storytelling.bus.Progress
import com.linwoain.storytelling.config.Constant
import com.linwoain.util.GsonUtil
import com.linwoain.util.LLogUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {

    internal var books: MutableList<BookInfo> = Collections.synchronizedList(ArrayList())
    internal var adapter: BookAdapter? = null
    private val flowerUrl = "http://42.121.125.229:8080/audible-book/service/audioBooksV2/getBookChaptersByPage?market=k-app360&refer=SearchResultBookList&dir=DESC&token=QUi2Cp%2BU09ITUXjudFZ2CtiCKUWfP1%2FT5KepiuHKXM77WDCYJwpQNoxu37pltHhH&pageSize=200&bookId=190518&imsi=460072521701419&ver=3.9.1&appKey=audibleBook"
    private val huaxuyinUrl = "http://42.121.125.229:8080/audible-book/service/audioBooksV2/getBookChaptersByPage?market=k-app360&refer=SearchResultBookList&dir=ASC&token=QUi2Cp%2BU09ITUXjudFZ2CtiCKUWfP1%2FT5KepiuHKXM77WDCYJwpQNoxu37pltHhH&pageSize=200&bookId=212947&imsi=460072521701419&ver=3.9.1&appKey=audibleBook"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerEventBus()
        initView()
        getData()
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
        val id = item.itemId
        if (id == R.id.action_settings) {
            startActivity(Intent(this, AboutActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getData() {
        doAsync {
            val flowerText = URL(flowerUrl).readText()
            val result = GsonUtil.get(flowerText, BookInfo::class.java)
            result.name = "花千骨"
            result.url = flowerUrl
            result.bookId = 190518
            books.add(result)
            refreshProgress()
        }

        doAsync {
            val huaxuyinText = URL(huaxuyinUrl).readText()
            val result = GsonUtil.get(huaxuyinText, BookInfo::class.java)
            result.name = "华胥引"
            result.url = huaxuyinUrl
            result.bookId = 212947
            books.add(result)
            refreshProgress()

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


    private fun refreshProgress() {
        runOnUiThread { setData() }
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
