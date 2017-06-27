package com.linwoain.storytelling.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by linwoain on 2017/6/27.
 */
/**
 * 自定义TextView实现跑马灯效果
 * Created by baochen on 2016/1/25.
 */
class MyTextView : TextView {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }



    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
    private fun init() {
        setLines(1)
        ellipsize = TextUtils.TruncateAt.MARQUEE

    }
    //重写isFocused方法，让其一直返回true
    override fun isFocused(): Boolean {
        return true
    }
}