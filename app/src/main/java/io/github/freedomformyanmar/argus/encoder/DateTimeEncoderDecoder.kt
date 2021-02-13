package io.github.freedomformyanmar.argus.encoder

import java.time.Instant
import java.util.*

class DateTimeEncoderDecoder {

    companion object {
        private const val DEFAULT_LENGTH = 20
        private const val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private val lower = upper.toLowerCase(Locale.ROOT)
        private const val digits = "0123456789"
        private val symbols = upper + lower + digits
    }

    fun encodeCurrentDateTime(): String {
        return encodeDateTime(Instant.now())
    }

    fun encodeDateTime(instant: Instant): String {
        return encode(instant.epochSecond)
    }

    fun decodeDateTime(s: String): Instant {
        return Instant.ofEpochSecond(decode(s))
    }

    /**
     * Copied from https://stackoverflow.com/a/2938559/3125020
     */

    private fun decode(s: String): Long {
        val length = symbols.length
        var num: Long = 0
        for (ch in s.toCharArray()) {
            num *= length.toLong()
            num += symbols.indexOf(ch).toLong()
        }
        return num
    }

    private fun encode(num: Long): String {
        var seed = num
        val length = symbols.length
        val sb = StringBuilder()
        while (seed != 0L) {
            sb.append(symbols[(seed % length).toInt()])
            seed /= length.toLong()
        }
        return sb.reverse().toString()
    }


}