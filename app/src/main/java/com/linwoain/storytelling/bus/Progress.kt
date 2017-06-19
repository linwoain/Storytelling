package com.linwoain.storytelling.bus

import com.linwoain.bean.BaseBean

/**
 * create by linwoain on 2016/6/3
 */
class Progress(var duration: Int, var bookId: Int, var isPlaying: Boolean, var number: Int, var title: String) : BaseBean()
