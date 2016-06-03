package com.linwoain.storytelling.config;

import android.app.Application;
import com.linwoain.library.LApplication;
import com.linwoain.storytelling.BuildConfig;
import com.linwoain.util.LLogUtils;

/**
 * Created by snow0358530 on 2016/6/3.
 */
public class App extends Application{

  @Override public void onCreate() {
    super.onCreate();
    LApplication.init(this);
    LLogUtils.setLogEnable(BuildConfig.DEBUG);
  }
}
