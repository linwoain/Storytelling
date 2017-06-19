package com.linwoain.storytelling.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.linwoain.library.LViewHelper
import com.linwoain.library.LinAdapter
import com.linwoain.storytelling.R
import com.linwoain.storytelling.bean.BookInfo
import com.linwoain.util.LLStringTools
import com.squareup.picasso.Picasso

/**
 * create by linwoain on 2016/6/3
 *  * LinAdapter通用的构造方法

 * @param context 传入的上下文
 * *
 * @param beans 要显示的数据源封装好的列表
 */
class BookAdapter(context: Activity, beans: List<BookInfo>) : LinAdapter<BookInfo>(context, beans) {

    override fun LgetView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        if (convertView == null) {
            convertView = LViewHelper.getView(R.layout.item_main, context)
        }
        val icon = LinAdapter.ViewHolders.get<ImageView>(convertView!!, R.id.icon)
        val (_, coverImage, _, name, _, title) = beans[position]
        Picasso.with(context).load(coverImage).error(R.drawable.listen).into(icon)

        (LinAdapter.ViewHolders.get<View>(convertView, R.id.title) as TextView).text = name
        if (!LLStringTools.isEmpty(title)) {
            (LinAdapter.ViewHolders.get<View>(convertView, R.id.status) as TextView).text = String.format("已经听到：%s", title)
        }

        return convertView
    }
}
