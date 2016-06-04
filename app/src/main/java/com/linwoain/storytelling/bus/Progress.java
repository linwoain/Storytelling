package com.linwoain.storytelling.bus;

import com.linwoain.bean.BaseBean;

/**
 * create by wuxuejian on 2016/6/3
 */
public class Progress extends BaseBean {

  private int bookId;
  private int number;
  private int duration;
  private boolean playing;
  private String title;

  public Progress(int duration, int bookId, boolean playing, int number, String title) {
    this.duration = duration;
    this.bookId = bookId;
    this.playing = playing;
    this.number = number;
    this.title = title;
  }

  public Progress() {

  }

  public boolean isPlaying() {
    return playing;
  }

  public void setPlaying(boolean playing) {
    this.playing = playing;
  }

  public int getBookId() {
    return bookId;
  }

  public void setBookId(int bookId) {
    this.bookId = bookId;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
