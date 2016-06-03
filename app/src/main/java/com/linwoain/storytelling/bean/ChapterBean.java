package com.linwoain.storytelling.bean;

import com.linwoain.bean.BaseBean;

public class ChapterBean extends BaseBean {
  private int id;
  private String title;
  private int number;
  private String audioURL;
  private int bookId;

  public int getBookId() {
    return bookId;
  }

  public void setBookId(int bookId) {
    this.bookId = bookId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public String getAudioURL() {
    return audioURL;
  }

  public void setAudioURL(String audioURL) {
    this.audioURL = audioURL;
  }


  @Override public boolean equals(Object o) {
    if (o == null || !(o instanceof ChapterBean)) {
      return false;
    }
    ChapterBean c = (ChapterBean) o;

    return this.getId() == c.getId();
  }

  @Override public int hashCode() {
    return id;
  }
}