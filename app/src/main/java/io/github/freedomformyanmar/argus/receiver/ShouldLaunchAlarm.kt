package io.github.freedomformyanmar.argus.receiver

import io.github.freedomformyanmar.argus.encoder.SmsEncoderDecoder
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

object ShouldLaunchAlarm {

    fun check(
        mySecretCode: String,
        message: String,
        clock: Clock = Clock.systemDefaultZone()
    ): Boolean {
        try {
            val theirCode = SmsEncoderDecoder.decodeSecretCode(message)
            val theirTime = SmsEncoderDecoder.decodeTimeStamp(message)
            if (mySecretCode == theirCode) {
                val myTime = Instant.now(clock)
                val secondsBetween = ChronoUnit.SECONDS.between(theirTime, myTime)
                return secondsBetween <= 60
            }
            return false
        } catch (exception: Exception) {
            return false
        }
    }
}