package io.github.freedomformyanmar.argus.encoder

import java.time.Instant

object SmsEncoderDecoder {

    private val dateTimeEncoder = DateTimeEncoderDecoder()

    fun encodeSos(secretCode: String, instant: Instant = Instant.now()): String {
        return "$secretCode${dateTimeEncoder.encodeDateTime(instant)}"
    }

    fun decodeSecretCode(encodedString: String): String {
        return encodedString.substring(0, 20)
    }

    fun decodeTimeStamp(encodedString: String): Instant {
        return dateTimeEncoder.decodeDateTime(encodedString.substring(20))
    }
}