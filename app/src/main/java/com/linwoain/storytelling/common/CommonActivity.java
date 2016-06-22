package com.linwoain.storytelling.common;

import android.support.v7.app.AppCompatActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by wuxuejian on 2016/6/22.
 */
public class CommonActivity extends AppCompatActivity{
  public void onResume() {
    super.onResume();
    MobclickAgent.onResume(this);
  }
  public void onPause() {
    super.onPause();
    MobclickAgent.onPause(this);
  }

}
