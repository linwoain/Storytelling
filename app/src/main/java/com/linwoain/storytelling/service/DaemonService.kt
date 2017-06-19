package com.linwoain.storytelling.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import com.linwoain.storytelling.R
import org.jetbrains.anko.doAsync

/**
 * @author AigeStudio
 * *
 * @since 2016-05-05
 */
class DaemonService : Service() {

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val builder = Notification.Builder(this)
            builder.setSmallIcon(R.drawable.listen)
            startForeground(250, builder.build())
            startService(Intent(this, CancelService::class.java))
        } else {
            startForeground(250, Notification())
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        doAsync {
            while (sPower) {
                if (System.currentTimeMillis() >= 123456789000000L) {
                    sPower = false
                }
                SystemClock.sleep(3000)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private var sPower = true
    }
}