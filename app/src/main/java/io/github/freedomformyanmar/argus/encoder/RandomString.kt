package io.github.freedomformyanmar.argus.encoder

import java.security.SecureRandom
import java.util.*

class RandomString(
    val length: Int = DEFAULT_LENGTH,
    val random: Random = SecureRandom(),
    val symbols: String = alphanum
) {
    companion object {
        private const val DEFAULT_LENGTH = 20
        private const val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private val lower = upper.toLowerCase(Locale.ROOT)
        private const val digits = "0123456789"
        private val alphanum = upper + lower + digits
    }

    /**
     * Generate a random string.
     */
    private val buffer: CharArray = CharArray(length)

    fun nextString(): String {
        for (idx in buffer.indices) buffer[idx] = symbols[random.nextInt(symbols.length)]
        return String(buffer)
    }

    init {
        require(length >= 1)
        require(symbols.length >= 2)
    }
}