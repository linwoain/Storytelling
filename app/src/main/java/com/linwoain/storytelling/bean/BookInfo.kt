package com.linwoain.storytelling.bean

import com.linwoain.bean.BaseBean

/**
 * create by wuxuejian on 2016/6/3
 */
data class BookInfo(var bookId: Int, var coverImage: String, var chapter: List<ChapterBean>,
                    var name: String, var url: String, var title: String) : BaseBean()