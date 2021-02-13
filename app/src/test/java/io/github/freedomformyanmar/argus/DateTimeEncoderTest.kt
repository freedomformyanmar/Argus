package io.github.freedomformyanmar.argus

import io.github.freedomformyanmar.argus.encoder.DateTimeEncoderDecoder
import org.junit.Assert
import org.junit.Test
import java.time.Instant

class DateTimeEncoderTest {

    private val dateTimeEncoder = DateTimeEncoderDecoder()

    @Test
    fun testCaseOne() {
        val input = Instant.ofEpochSecond(1000L)

        val encodedString = dateTimeEncoder.encodeDateTime(input)

        Assert.assertEquals(input, dateTimeEncoder.decodeDateTime(encodedString))
    }
}