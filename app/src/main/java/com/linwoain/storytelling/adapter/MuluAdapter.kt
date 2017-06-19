package com.linwoain.storytelling.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.linwoain.library.LViewHelper
import com.linwoain.library.LinAdapter
import com.linwoain.storytelling.bean.ChapterBean

/**
 * create by wuxuejian on 2016/6/3
 */
class MuluAdapter
/**
 * LinAdapter通用的构造方法

 * @param context 传入的上下文
 * *
 * @param beans 要显示的数据源封装好的列表
 */
(context: Activity, beans: List<ChapterBean>) : LinAdapter<ChapterBean>(context, beans) {

    override fun LgetView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        if (convertView == null) {
            convertView = LViewHelper.getView(android.R.layout.simple_list_item_1, context)
        }
        (convertView as TextView).text = beans[position].title

        return convertView
    }
}
