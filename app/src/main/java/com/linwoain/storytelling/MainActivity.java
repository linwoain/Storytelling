package com.linwoain.storytelling;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.linwoain.storytelling.adapter.BookAdapter;
import com.linwoain.storytelling.bean.BookInfo;
import com.linwoain.storytelling.bean.ChapterBean;
import com.linwoain.storytelling.bus.RxBus;
import com.linwoain.storytelling.config.Constant;
import com.linwoain.util.CacheUtil;
import com.linwoain.util.LLogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

  private String flowerUrl =
      "http://42.121.125.229:8080/audible-book/service/audioBooksV2/getBookChaptersByPage?market=k-app360&refer=SearchResultBookList&dir=DESC&token=QUi2Cp%2BU09ITUXjudFZ2CtiCKUWfP1%2FT5KepiuHKXM77WDCYJwpQNoxu37pltHhH&pageSize=20&bookId=190518&imsi=460072521701419&ver=3.9.1&appKey=audibleBook";
  private String huaxuyinUrl =
      "http://42.121.125.229:8080/audible-book/service/audioBooksV2/getBookChaptersByPage?market=k-app360&refer=SearchResultBookList&dir=ASC&token=QUi2Cp%2BU09ITUXjudFZ2CtiCKUWfP1%2FT5KepiuHKXM77WDCYJwpQNoxu37pltHhH&pageSize=20&bookId=212947&imsi=460072521701419&ver=3.9.1&appKey=audibleBook";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    LLogUtils.setTag("book");
    initView();
    getData();
    registerEventBus();
  }

  private void initView() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    listView = (ListView) findViewById(R.id.listview);
    RxAdapterView.itemClicks(listView)
        .throttleFirst(500, TimeUnit.MILLISECONDS)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Integer>() {
          @Override public void call(Integer integer) {
            BookInfo bean = books.get(integer);
            startActivity(
                new Intent(MainActivity.this, MuluActivity.class).putExtra(Constant.BEAN, bean));
          }
        });
  }

  ListView listView;

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  List<BookInfo> books = new ArrayList<>();
  BookAdapter adapter;

  private void getData() {
    Ion.with(this).load(flowerUrl).as(BookInfo.class).setCallback(new FutureCallback<BookInfo>() {
      @Override public void onCompleted(Exception e, BookInfo result) {
        if (e == null) {
          result.setName("花千骨");
          result.setUrl(flowerUrl);
          result.setBookId(190518);
          books.add(result);
          refreshProgress();
          setData();
        } else {
          e.printStackTrace();
        }
      }
    });
    Ion.with(this).load(huaxuyinUrl).as(BookInfo.class).setCallback(new FutureCallback<BookInfo>() {
      @Override public void onCompleted(Exception e, BookInfo result) {
        if (e == null) {
          result.setName("华胥引");
          result.setUrl(huaxuyinUrl);
          result.setBookId(212947);
          books.add(result);
          refreshProgress();
          setData();
        } else {
          e.printStackTrace();
        }
      }
    });
  }


  private void registerEventBus() {
    register = RxBus.get().register(Constant.PROGRESS_MULU, ChapterBean.class);
    register.subscribe(new Action1<ChapterBean>() {
      @Override public void call(ChapterBean v) {
        LLogUtils.log();
        for (BookInfo book : books) {
          if (book.getBookId() == v.getBookId()) {
            book.setTitle(v.getTitle());
            setData();
          }
        }
      }
    });
  }

  Observable<ChapterBean> register;

  @Override protected void onDestroy() {
    RxBus.get().unregister(Constant.PROGRESS_MULU, register);
    super.onDestroy();

  }

  @Override protected void onRestart() {
    super.onRestart();
    refreshProgress();
  }

  private void refreshProgress() {
    for (BookInfo book : books) {
      ChapterBean bean = (ChapterBean) CacheUtil.getBaseBean(Constant.MULU + book.getBookId());
      if (bean != null) {
        LLogUtils.i(bean.getTitle());
        book.setTitle(bean.getTitle());
        setData();
      }
    }
  }

  private void setData() {
    if (adapter == null) {
      adapter = new BookAdapter(this, books);
      listView.setAdapter(adapter);
    } else {
      adapter.notifyDataSetChanged();
    }
  }
}