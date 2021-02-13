package io.github.freedomformyanmar.argus.appupdate

data class AppUpdate(
  val latestVersionCode: Long,
  val requireForcedUpdate: Boolean,
  val selfHostedLink: String
)