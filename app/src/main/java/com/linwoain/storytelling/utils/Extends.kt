package com.linwoain.storytelling.utils

import android.content.Context
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

/**
 * Created by linwoain on 2017/6/19.
 */

fun Context.process(url: String, f: (text: String) -> Unit): Unit {
    doAsync {
        val text = URL(url).readText()
        uiThread {
            f(text)
        }
    }
}