package com.linwoain.storytelling.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import com.linwoain.storytelling.R
import org.jetbrains.anko.doAsync

/**
 * @author AigeStudio
 * *
 * @since 2016-05-05
 */
class CancelService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val builder = NotificationCompat.Builder(this)
        builder.setSmallIcon(R.drawable.listen)
        startForeground(250, builder.build())

        doAsync {
            SystemClock.sleep(1000)
            stopForeground(true)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(250)
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }
}