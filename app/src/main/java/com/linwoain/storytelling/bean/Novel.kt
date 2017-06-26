package com.linwoain.storytelling.bean


import org.litepal.crud.DataSupport
import java.io.Serializable

/**
 * Created by linwoain on 2017/6/25.
 */
data class Novel(val bookId: Int, val name: String, val icon: String, var title: String) : Serializable, DataSupport()

data class NovelWrapper(val version: Long, val novels: List<Novel>) : Serializable
