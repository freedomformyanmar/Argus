package io.github.freedomformyanmar.argus

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.github.freedomformyanmar.argus.receiver.SMSBroadcastReceiver


class KeepAliveService : Service() {

    companion object {
        private const val CHANNEL_ID = "keep_alive"
        private val smsBroadcastReceiver = SMSBroadcastReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Keep Alive Service",
                NotificationManager.IMPORTANCE_NONE
            ).apply {
                importance = NotificationManager.IMPORTANCE_DEFAULT
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)// 1
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Argus")
            .setContentText("Argus is watching")
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(2, notification)

        return START_NOT_STICKY
    }


    override fun onCreate() {
        super.onCreate()
        registerReceiver(smsBroadcastReceiver, IntentFilter().also {
            it.addAction("android.provider.Telephony.SMS_RECEIVED")
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}