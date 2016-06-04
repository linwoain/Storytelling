package com.linwoain.storytelling.mp3media;

import android.media.AudioManager;
import android.media.MediaPlayer;
import com.linwoain.storytelling.bean.ChapterBean;
import com.linwoain.storytelling.bus.Progress;
import com.linwoain.storytelling.bus.RxBus;
import com.linwoain.storytelling.config.Constant;
import com.linwoain.util.CacheUtil;
import com.linwoain.util.LLogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * create by wuxuejian on 2016/6/3
 */
public class Mp3PlayerManager
    implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener {
  private static MediaPlayer player;
  private List<ChapterBean> curChapters = new ArrayList<>();
  private static int curPos;

  private Mp3PlayerManager() {

  }

  private static Mp3PlayerManager instance = new Mp3PlayerManager();

  public static Mp3PlayerManager getInstance() {
    return instance;
  }

  public void play(final List<ChapterBean> chapters, int position) {
    if (player != null) {
      player.reset();
    } else {
      player = new MediaPlayer();
      player.setOnPreparedListener(this);
      player.setOnErrorListener(this);
      player.setOnCompletionListener(this);
      player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    curPos = position;
    curChapters = chapters;
    ChapterBean chapterBean = chapters.get(position);
    String audioURL = chapterBean.getAudioURL();

    preparePlay(audioURL);
  }

  private void preparePlay(String audioURL) {
    try {
      player.setDataSource(audioURL);
      player.prepare();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override public void onPrepared(MediaPlayer mp) {
    ChapterBean content = curChapters.get(curPos);
    CacheUtil.save(Constant.MULU + content.getBookId(), content);
    RxBus.getDefault().post(content);
    RxBus.getDefault()
        .post(
            new Progress(mp.getDuration(), content.getBookId(), true, curPos, content.getTitle()));
    mp.start();
  }

  @Override public void onCompletion(MediaPlayer mp) {
    curPos++;
    if (curChapters.size() >= curPos + 1) {
      play(curChapters, curPos);
    }
  }

  @Override public boolean onError(MediaPlayer mp, int what, int extra) {
    LLogUtils.e("播放出错了" + what);
    return false;
  }

  public void pause() {

    if (player.isPlaying()) {
      player.pause();
    }
  }

  public void restart() {

    if (player != null) {
      player.start();
    }
  }

  public int getPlayingBookId() {
    if (curChapters.isEmpty()) {
      return -1;
    }
    return curChapters.get(curPos).getBookId();
  }

  public boolean isPlaying() {
    return player != null && player.isPlaying();
  }

  public void stop() {

    if (player != null) {
      player.stop();
      player.release();
      player = null;
    }
  }
}
