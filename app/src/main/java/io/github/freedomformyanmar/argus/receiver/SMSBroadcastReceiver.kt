package io.github.freedomformyanmar.argus.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsMessage
import androidx.core.app.NotificationCompat
import com.aungkyawpaing.mmphonenumber.normalizer.MyanmarPhoneNumberNormalizer
import com.parse.Parse.getApplicationContext
import io.github.freedomformyanmar.argus.ArgusApp
import io.github.freedomformyanmar.argus.R
import io.github.freedomformyanmar.argus.alarm.AlarmActivity
import io.github.freedomformyanmar.argus.user.UserCache
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class SMSBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "alerts"
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("Receive SMS")
        val userCache = UserCache(context!!)

        val bundle = intent!!.extras
        val msgs: Array<SmsMessage?>
        val format = bundle!!.getString("format")
        val pdus = bundle["pdus"] as Array<Any>?

        if (pdus != null) {

            msgs = arrayOfNulls(pdus.size)
            for (index in msgs.indices) {
                // Check Android version and use appropriate createFromPdu.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // If Android version M or newer:
                    msgs[index] = SmsMessage.createFromPdu(pdus[index] as ByteArray, format)
                } else {
                    // If Android version L or older:
                    msgs[index] = SmsMessage.createFromPdu(pdus[index] as ByteArray)
                }
                // Build the message to show.
                val number = MyanmarPhoneNumberNormalizer.Builder().build()
                    .normalize(msgs[index]!!.originatingAddress!!)
                val message = msgs[index]!!.messageBody
                // Log and display the SMS message.
                GlobalScope.launch {
                    userCache.getUser()?.let { user ->
                        if (ShouldLaunchAlarm.check(user.secretCode, message)) {
//                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || ArgusApp.isInBackground.not()) {
//
//                            } else {
//
//                            }
                            context.startActivity(AlarmActivity.newIntent(context, number))
                            showNotification(context, number)
                        } else {
                            Timber.d("Incorrect secret code")
                        }
                    }
                }
            }
        }
    }

    private fun showNotification(context: Context, number: String) {
        val notificationIntent = AlarmActivity.newIntent(context, number)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri: Uri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().packageName + "/" + R.raw.custom_alarm
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(
                    soundUri, AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
                enableLights(true)
                lightColor = Color.RED
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)// 1
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("$number is calling for help!")
            .setContentText("Tap to open alarm")
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setSound(soundUri, AudioManager.STREAM_ALARM)
            .setVibrate(longArrayOf(0, 500, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true /*isHighPriority*/)
            .build()

        notificationManager.notify(100, notification)
    }


}