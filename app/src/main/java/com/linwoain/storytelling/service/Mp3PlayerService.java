package com.linwoain.storytelling.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.linwoain.storytelling.bean.ChapterBean;
import com.linwoain.storytelling.mp3media.Mp3PlayerManager;
import com.umeng.analytics.MobclickAgent;
import java.io.Serializable;
import java.util.List;

public class Mp3PlayerService extends Service {

  private static final String EXTRA_PARAM1 = "com.linwoain.storytelling.service.extra.PARAM1";
  private static final String EXTRA_PARAM2 = "com.linwoain.storytelling.service.extra.PARAM2";

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  public static void openMp3(Context context, List<ChapterBean> chapters, int postion) {
    MobclickAgent.onEvent(context,"read");
    Intent intent = new Intent(context, Mp3PlayerService.class);
    intent.putExtra(EXTRA_PARAM1, (Serializable) chapters);
    intent.putExtra(EXTRA_PARAM2, postion);
    context.startService(intent);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    onHandleIntent(intent);
    startService(new Intent(this, DaemonService.class));
    return super.onStartCommand(intent, flags, startId);
  }

  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final List<ChapterBean> chapters = (List<ChapterBean>) intent.getSerializableExtra(EXTRA_PARAM1);
      final int position = intent.getIntExtra(EXTRA_PARAM2, 0);
      new Thread() {
        @Override public void run() {
          Mp3PlayerManager.getInstance().play(chapters, position);
        }
      }.start();

    }
  }

  private class NoisyAudioStreamReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
        int state = intent.getIntExtra("state", -1);
        switch (state) {
          case 0:
            //拔出耳机
            Mp3PlayerManager.getInstance().stop();

            break;
          case 1:
            break;
          default:
            break;
        }
      }
      //只监听拔出耳机
      if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
      }
    }
  }

  @Override public void onCreate() {
    super.onCreate();
    //注册广播：
    NoisyAudioStreamReceiver noisyAudioStreamReceiver = new NoisyAudioStreamReceiver();
    IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    filter.addAction(Intent.ACTION_HEADSET_PLUG);
    registerReceiver(noisyAudioStreamReceiver, filter);
  }
}
