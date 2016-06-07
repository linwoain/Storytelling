package com.linwoain.storytelling;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.linwoain.storytelling.adapter.MuluAdapter;
import com.linwoain.storytelling.bean.BookInfo;
import com.linwoain.storytelling.bean.ChapterBean;
import com.linwoain.storytelling.bus.Progress;
import com.linwoain.storytelling.bus.RxBus;
import com.linwoain.storytelling.config.Constant;
import com.linwoain.storytelling.mp3media.Mp3PlayerManager;
import com.linwoain.storytelling.service.Mp3PlayerService;
import com.linwoain.util.CacheUtil;
import com.linwoain.util.LLogUtils;
import java.util.ArrayList;
import java.util.List;
import rx.Subscription;
import rx.functions.Action1;

public class MuluActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

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
                showPlay();

                play.setOnClickListener(pauseListener);
              }
            }
          }
        });
  }

  private void showPlay() {
    Drawable drawable = play.getDrawable();
    if (drawable instanceof AnimationDrawable) {
      AnimationDrawable ad = (AnimationDrawable) drawable;
      ad.start();
    }
  }

  @Override protected void onResume() {
    super.onResume();
    if (Mp3PlayerManager.getInstance().getPlayingBookId() == info.getBookId()
        && Mp3PlayerManager.getInstance().isPlaying()) {
      play.setImageResource(R.drawable.playing);
      showPlay();
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
    assert play != null;
    play.setOnClickListener(playListener);
    listview = (ListView) findViewById(R.id.listview);
    assert listview != null;
    listview.setOnItemClickListener(this);
  }

  private void initData() {

    info = (BookInfo) getIntent().getExtras().getSerializable(Constant.BEAN);
    tvTitle.setText(info.getName());
    chapters.addAll(info.getChapter());
    setData();
  }

  private BookInfo info;
  private ListView listview;
  private MuluAdapter adapter;
  private Toolbar toolbar;

  List<ChapterBean> chapters = new ArrayList<>();

  private void setData() {
    for (ChapterBean chapter : chapters) {
      chapter.setBookId(info.getBookId());
    }

    if (adapter == null) {
      adapter = new MuluAdapter(this, chapters);
      listview.setAdapter(adapter);
    } else {
      adapter.notifyDataSetChanged();
    }
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Mp3PlayerService.openMp3(this, chapters, position);
  }
}
