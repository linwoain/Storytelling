package com.linwoain.storytelling.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import com.linwoain.storytelling.R;

/**
 * @author AigeStudio
 * @since 2016-05-05
 */
public class DaemonService extends Service {
    private static boolean sPower = true;

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.drawable.listen);
            startForeground(250, builder.build());
            startService(new Intent(this, CancelService.class));
        } else {
            startForeground(250, new Notification());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (sPower) {
                    if (System.currentTimeMillis() >= 123456789000000L) {
                        sPower = false;
                    }
                    SystemClock.sleep(3000);
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}