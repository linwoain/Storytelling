package com.linwoain.storytelling.bean

import org.litepal.annotation.Column
import org.litepal.crud.DataSupport
import java.io.Serializable

data class ChapterBean(@Column(unique = true) val id: Int, val title: String, val number: Int, val audioURL: String, var bookId: Int) : Serializable, DataSupport()