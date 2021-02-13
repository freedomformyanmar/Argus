package io.github.freedomformyanmar.argus.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "number") val number: String,
    @Json(name = "secretCode") val secretCode: String
)