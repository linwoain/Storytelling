package com.linwoain.storytelling;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.linwoain.storytelling.adapter.MuluAdapter;
import com.linwoain.storytelling.bean.BookInfo;
import com.linwoain.storytelling.bean.ChapterBean;
import com.linwoain.storytelling.bus.RxBus;
import com.linwoain.storytelling.config.Constant;
import com.linwoain.storytelling.service.Mp3PlayerService;
import com.linwoain.storytelling.widget.CommonListView;
import com.linwoain.util.CacheUtil;
import com.linwoain.util.LLogUtils;
import com.linwoain.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;

public class MuluActivity extends AppCompatActivity
    implements CommonListView.RefreshCallBack, CommonListView.OnItemClickCallBack,
    View.OnClickListener {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mulu);
    initView();
    initData();
  }

  TextView tvTitle;
  ImageButton button;

  private void initView() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    tvTitle = (TextView) findViewById(R.id.bar_title);

    button = (ImageButton) findViewById(R.id.play);
    button.setOnClickListener(this);
    listview = (CommonListView) findViewById(R.id.listview);
    listview.setRefreshCallBack(this);
    listview.setItemClickCallBack(this);
  }

  private void initData() {

    info = (BookInfo) getIntent().getExtras().getSerializable(Constant.BEAN);

    getSupportActionBar().setTitle(info.getName());
    chapters.addAll(info.getChapter());
    setData();
  }

  private BookInfo info;
  private CommonListView listview;
  private MuluAdapter adapter;
  private Toolbar toolbar;

  private int page = 1;

  List<ChapterBean> chapters = new ArrayList<>();

  @Override public void onRefresh(CommonListView view) {
    page = 1;
    getData();
  }

  @Override public void onLoadMore(CommonListView view) {
    getData();
  }

  private void getData() {
    LLogUtils.i("正在获取" + info.getName() + "第" + page + "页");
    Ion.with(this)
        .load(info.getUrl() + "&pageIndex=" + page)
        .as(BookInfo.class)
        .setCallback(new FutureCallback<BookInfo>() {
          @Override public void onCompleted(Exception e, BookInfo result) {

            if (e == null) {
              List<ChapterBean> chapter = result.getChapter();
              if (chapter != null && !chapter.isEmpty()) {
                if (page == 1) {
                  chapters.clear();
                }
                chapters.addAll(chapter);
                setData();
              } else {
                listview.setNoMore();
                ToastUtil.show("已经是最后一个了");
              }
            } else {
              listview.setLoadMoreError();
            }
          }
        });
  }

  private void setData() {
    for (ChapterBean chapter : chapters) {
      chapter.setBookId(info.getBookId());
    }

    page++;
    if (adapter == null) {
      adapter = new MuluAdapter(this, chapters);
      listview.setAdapter(adapter);
    } else {
      adapter.notifyDataSetChanged();
    }
  }

  @Override public void onItemClick(int position, CommonListView view) {
    Mp3PlayerService.openMp3(this, chapters, position);
    LLogUtils.i("播放" + chapters.get(position).getTitle());
  }

  @Override public void onClick(View v) {

    if (true) {
      RxBus.get().post(Constant.PROGRESS_MULU,new ChapterBean());
      return;
    }

    ChapterBean c = (ChapterBean) CacheUtil.getBaseBean(Constant.MULU + info.getBookId());
    int i = 0;
    if (c != null) {
      i = chapters.indexOf(c);
      if (i < 0) {
        i = 0;
      }
    }
    Mp3PlayerService.openMp3(this, chapters, i);
  }
}
