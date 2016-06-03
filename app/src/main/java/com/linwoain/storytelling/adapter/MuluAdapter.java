package com.linwoain.storytelling.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.linwoain.library.LViewHelper;
import com.linwoain.library.LinAdapter;
import com.linwoain.storytelling.bean.ChapterBean;
import java.util.List;

/**
 * create by wuxuejian on 2016/6/3
 */
public class MuluAdapter extends LinAdapter<ChapterBean> {
  /**
   * LinAdapter通用的构造方法
   *
   * @param context 传入的上下文
   * @param beans 要显示的数据源封装好的列表
   */
  public MuluAdapter(Activity context, List<ChapterBean> beans) {
    super(context, beans);
  }

  @Override protected View LgetView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LViewHelper.getView(android.R.layout.simple_list_item_1, context);
    }
    ((TextView) convertView).setText(beans.get(position).getTitle());

    return convertView;
  }
}
