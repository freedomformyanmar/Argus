package io.github.freedomformyanmar.argus

import io.github.freedomformyanmar.argus.encoder.RandomString
import io.github.freedomformyanmar.argus.encoder.SmsEncoderDecoder
import org.junit.Assert
import org.junit.Test
import java.time.Instant

class SmsEncoderDecoderTest {

    @Test
    fun testCaseOne() {
        val secretCode = RandomString().nextString()
        val timestamp = Instant.ofEpochSecond(1000L)

        val encodedString = SmsEncoderDecoder.encodeSos(secretCode, timestamp)

        Assert.assertEquals(secretCode, SmsEncoderDecoder.decodeSecretCode(encodedString))
        Assert.assertEquals(timestamp, SmsEncoderDecoder.decodeTimeStamp(encodedString))
    }

}