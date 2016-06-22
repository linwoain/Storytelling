package com.linwoain.storytelling.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * Created by wuxuejian on 2016/6/22.
 */
public class CommonActivity extends AppCompatActivity{
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    PushAgent.getInstance(this).onAppStart();
  }

  public void onResume() {
    super.onResume();
    MobclickAgent.onResume(this);
  }
  public void onPause() {
    super.onPause();
    MobclickAgent.onPause(this);
  }

}
