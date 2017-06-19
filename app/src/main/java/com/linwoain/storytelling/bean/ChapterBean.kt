package com.linwoain.storytelling.bean

import com.linwoain.bean.BaseBean

data class ChapterBean(var id: Int, var title: String, var number: Int, var audioURL: String, var bookId: Int) : BaseBean()