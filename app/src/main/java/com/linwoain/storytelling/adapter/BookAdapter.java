package com.linwoain.storytelling.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.koushikdutta.ion.Ion;
import com.linwoain.library.LViewHelper;
import com.linwoain.library.LinAdapter;
import com.linwoain.storytelling.R;
import com.linwoain.storytelling.bean.BookInfo;
import com.linwoain.util.LLStringTools;
import java.util.List;

/**
 * create by wuxuejian on 2016/6/3
 */
public class BookAdapter extends LinAdapter<BookInfo> {
  /**
   * LinAdapter通用的构造方法
   *
   * @param context 传入的上下文
   * @param beans 要显示的数据源封装好的列表
   */
  public BookAdapter(Activity context, List<BookInfo> beans) {
    super(context, beans);
  }

  protected View LgetView(int position, View convertView, ViewGroup parent) {
    if (convertView==null) {
      convertView = LViewHelper.getView(R.layout.item_main, context);
    }
    ImageView icon = ViewHolders.get(convertView, R.id.icon);
    //ImageButton start = ViewHolders.get(convertView, R.id.start);
    BookInfo bookInfo = beans.get(position);
    Ion.with(icon)
        .error(R.drawable.listen)
        .load(bookInfo.getCoverImage());
    ((TextView) ViewHolders.get(convertView, R.id.title)).setText(bookInfo.getName());
    String title = bookInfo.getTitle();
    if (!LLStringTools.isEmpty(title)) {
      ((TextView) ViewHolders.get(convertView, R.id.status)).setText(
          String.format("已经听到：%s", title));
    }

    return convertView;
  }
}
