package com.linwoain.storytelling.config

import android.app.Application
import com.linwoain.library.LApplication
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import org.litepal.LitePal

/**
 * Created by linwoain on 2016/6/3.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LApplication.init(this)
        val strategy = PrettyFormatStrategy.newBuilder().tag("book").methodCount(1).showThreadInfo(false).build()
        Logger.addLogAdapter(AndroidLogAdapter(strategy))
        LitePal.initialize(this)

    }
}
