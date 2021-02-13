package io.github.freedomformyanmar.argus.appupdate

import com.parse.ParseObject
import com.parse.ParseQuery

class AppUpdateNetworkSource {

    fun getLatestUpdate(deviceVersionCode: Long): AppUpdate {
        val appUpdateQuery = ParseQuery.getQuery<ParseObject>("AppUpdate").apply {
            orderByDescending("updatedAt")
        }
        val appUpdateObject = appUpdateQuery.first

        val versionCode = appUpdateObject.getNumber("versionCode")!!.toLong()
        val apkUrl = appUpdateObject.getParseFile("appFile")!!.url

        return AppUpdate(
            latestVersionCode = versionCode,
            requireForcedUpdate = false,
            selfHostedLink = apkUrl
        )
    }

}