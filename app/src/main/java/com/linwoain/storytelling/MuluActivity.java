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
import com.linwoain.storytelling.bus.Progress;
import com.linwoain.storytelling.bus.RxBus;
import com.linwoain.storytelling.config.Constant;
import com.linwoain.storytelling.mp3media.Mp3PlayerManager;
import com.linwoain.storytelling.service.Mp3PlayerService;
import com.linwoain.storytelling.widget.CommonListView;
import com.linwoain.util.CacheUtil;
import com.linwoain.util.LLogUtils;
import com.linwoain.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import rx.Subscription;
import rx.functions.Action1;

public class MuluActivity extends AppCompatActivity
    implements CommonListView.RefreshCallBack, CommonListView.OnItemClickCallBack {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mulu);
    initView();
    initData();
    registerBus();
  }

  private void registerBus() {
    subscribe =
        RxBus.getDefault().toObserverable(Progress.class).subscribe(new Action1<Progress>() {
          @Override public void call(Progress progress) {

            if (progress.getBookId() == info.getBookId()) {
              if (progress.isPlaying()) {
                play.setImageResource(R.drawable.playing);
                play.setOnClickListener(pauseListener);
              }
            }
          }
        });
  }

  @Override protected void onResume() {
    super.onResume();
    if (Mp3PlayerManager.getInstance().getPlayingBookId() == info.getBookId()
        && Mp3PlayerManager.getInstance().isPlaying()) {
      play.setImageResource(R.drawable.playing);
      LLogUtils.log();
      play.setOnClickListener(pauseListener);
    }
  }

  private View.OnClickListener replayListener = new View.OnClickListener() {
    @Override public void onClick(View v) {

      if (Mp3PlayerManager.getInstance().getPlayingBookId() == info.getBookId()) {
        play.setImageResource(R.drawable.playing);
        Mp3PlayerManager.getInstance().restart();
        play.setOnClickListener(pauseListener);
      }
    }
  };
  private View.OnClickListener playListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      LLogUtils.log();
      ChapterBean c = (ChapterBean) CacheUtil.getBaseBean(Constant.MULU + info.getBookId());
      int i = 0;
      if (c != null) {
        i = chapters.indexOf(c);
        if (i < 0) {
          i = 0;
        }
      }
      Mp3PlayerService.openMp3(MuluActivity.this, chapters, i);
    }
  };

  private View.OnClickListener pauseListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      if (Mp3PlayerManager.getInstance().getPlayingBookId() == info.getBookId()) {
        play.setImageResource(R.drawable.play_selector);
        Mp3PlayerManager.getInstance().pause();
        play.setOnClickListener(replayListener);
        LLogUtils.log();
      }
    }
  };

  Subscription subscribe;
  TextView tvTitle;
  ImageButton play;

  @Override protected void onDestroy() {
    super.onDestroy();
    if (subscribe.isUnsubscribed()) {
      subscribe.unsubscribe();
    }
  }

  private void initView() {
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    tvTitle = (TextView) findViewById(R.id.bar_title);

    play = (ImageButton) findViewById(R.id.play);
    play.setOnClickListener(playListener);
    listview = (CommonListView) findViewById(R.id.listview);
    listview.setRefreshCallBack(this);
    listview.setItemClickCallBack(this);
  }

  private void initData() {

    info = (BookInfo) getIntent().getExtras().getSerializable(Constant.BEAN);
    tvTitle.setText(info.getName());
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
                ToastUtil.show("已到结尾");
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
  }
}
