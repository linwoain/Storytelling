package com.linwoain.storytelling.config

import android.app.Application

import com.linwoain.library.LApplication
import com.linwoain.storytelling.BuildConfig
import com.linwoain.util.LLogUtils

/**
 * Created by snow0358530 on 2016/6/3.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LApplication.init(this)
        LLogUtils.setLogEnable(BuildConfig.DEBUG)
        LLogUtils.setTag("book")

    }
}
