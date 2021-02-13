package io.github.freedomformyanmar.argus.alarm

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.freedomformyanmar.argus.databinding.ActivityAlarmBinding
import io.github.freedomformyanmar.argus.helper.Intents
import io.github.freedomformyanmar.argus.helper.viewBinding


class AlarmActivity : AppCompatActivity() {

    companion object {
        private const val ARG_NUMBER = "number"

        fun newIntent(context: Context, number: String): Intent {
            val intent = Intent(context, AlarmActivity::class.java)
            intent.putExtra(ARG_NUMBER, number)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            return intent
        }
    }

    private val number by lazy {
        intent.getStringExtra(ARG_NUMBER)!!
    }

    private val binding by viewBinding(ActivityAlarmBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.title = ""

        playAlarm()

        binding.apply {
            buttonCloseAlarm.setOnClickListener {
                mediaPlayer.stop()
            }

            buttonPhoneCall.setOnClickListener {
                mediaPlayer.stop()
                try {
                    startActivity(Intents.dialIntent(number))
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }

            tvNumber.text = number
        }
    }

    private val mediaPlayer by lazy {
        val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        MediaPlayer.create(
            applicationContext, alertUri, null, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build(), 1
        )
    }

    private fun playAlarm() {
        //TODO : use custom ringtone for fallback?
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}