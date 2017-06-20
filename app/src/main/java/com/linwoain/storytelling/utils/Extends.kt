package com.linwoain.storytelling.utils

import android.content.Context
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

/**
 * Created by linwoain on 2017/6/19.
 */

/**
 * @param url 获取资源的url
 * @param f 获取到资源后执行的操作
 */
fun Context.process(url: String, f: (text: String) -> Unit): Unit {
    doAsync {
        val text = URL(url).readText()
        uiThread {
            f(text)
        }
    }
}
