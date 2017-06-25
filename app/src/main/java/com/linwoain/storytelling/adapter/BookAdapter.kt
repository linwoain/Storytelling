package com.linwoain.storytelling.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.koushikdutta.ion.Ion
import com.linwoain.library.LViewHelper
import com.linwoain.library.LinAdapter
import com.linwoain.storytelling.R
import com.linwoain.storytelling.bean.Novel
import com.linwoain.util.LLStringTools

/**
 * create by linwoain on 2016/6/3
 *  LinAdapter通用的构造方法
 * @param context 传入的上下文
 * @param beans 要显示的数据源封装好的列表
 */
class BookAdapter(context: Activity, beans: List<Novel>) : LinAdapter<Novel>(context, beans) {

    override fun LgetView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        if (convertView == null) {
            convertView = LViewHelper.getView(R.layout.item_main, context)
        }
        val novel = beans[position]
        val icon = LinAdapter.ViewHolders.get<ImageView>(convertView, R.id.icon)
        Ion.with(icon).placeholder(R.drawable.listen).error(R.drawable.listen).load(novel.icon)

        ViewHolders.get<TextView>(convertView, R.id.title).text = novel.name
        if (!LLStringTools.isEmpty(novel.title)) {
            ViewHolders.get<TextView>(convertView, R.id.status).text = String.format("已经听到：%s", novel.title)
        }


        return convertView!!
    }
}
