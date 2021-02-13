package io.github.freedomformyanmar.argus

import io.github.freedomformyanmar.argus.encoder.RandomString
import io.github.freedomformyanmar.argus.encoder.SmsEncoderDecoder
import io.github.freedomformyanmar.argus.receiver.ShouldLaunchAlarm
import org.junit.Assert
import org.junit.Test
import java.time.*

class ShouldLaunchAlarmTest {

    @Test
    fun testCaseOne() {
        val zoneId = ZoneId.of("Asia/Rangoon")
        val secretCode = "abcdefgijkabcdefgijk"
        val theirTime = ZonedDateTime.of(2021, Month.FEBRUARY.value, 13, 2, 1, 0, 0, zoneId)

        val myTime = ZonedDateTime.of(2021, Month.FEBRUARY.value, 13, 2, 1, 1, 0, zoneId)

        val customClock = Clock.fixed(myTime.toInstant(), zoneId)

        val encodedString = SmsEncoderDecoder.encodeSos(secretCode, theirTime.toInstant())

        Assert.assertEquals(true, ShouldLaunchAlarm.check(secretCode, encodedString, customClock))
    }


    @Test
    fun testCaseTwo() {
        val zoneId = ZoneId.of("Asia/Rangoon")
        val secretCode = "abcdefgijkabcdefgijk"
        val theirTime = ZonedDateTime.of(2021, Month.FEBRUARY.value, 13, 2, 1, 0, 0, zoneId)

        val myTime = ZonedDateTime.of(2021, Month.FEBRUARY.value, 13, 2, 2, 0, 0, zoneId)

        val customClock = Clock.fixed(myTime.toInstant(), zoneId)

        val encodedString = SmsEncoderDecoder.encodeSos(secretCode, theirTime.toInstant())

        Assert.assertEquals(true, ShouldLaunchAlarm.check(secretCode, encodedString, customClock))
    }

    @Test
    fun testCaseThree() {
        val zoneId = ZoneId.of("Asia/Rangoon")
        val secretCode = "abcdefgijkabcdefgijk"
        val theirTime = ZonedDateTime.of(2021, Month.FEBRUARY.value, 13, 2, 1, 0, 0, zoneId)

        val myTime = ZonedDateTime.of(2021, Month.FEBRUARY.value, 13, 2, 2, 1, 0, zoneId)

        val customClock = Clock.fixed(myTime.toInstant(), zoneId)

        val encodedString = SmsEncoderDecoder.encodeSos(secretCode, theirTime.toInstant())

        Assert.assertEquals(false, ShouldLaunchAlarm.check(secretCode, encodedString, customClock))
    }

    @Test
    fun testCaseFour() {
        val zoneId = ZoneId.of("Asia/Rangoon")
        val secretCode = "abcdefgijkabcdefgijk"
        val theirTime = ZonedDateTime.of(2021, Month.FEBRUARY.value, 13, 2, 1, 0, 0, zoneId)

        val myTime = ZonedDateTime.of(2021, Month.FEBRUARY.value, 13, 2, 1, 10, 0, zoneId)

        val customClock = Clock.fixed(myTime.toInstant(), zoneId)

        val encodedString = SmsEncoderDecoder.encodeSos(secretCode, theirTime.toInstant())

        Assert.assertEquals(false, ShouldLaunchAlarm.check("wrongcode", encodedString, customClock))
    }

}