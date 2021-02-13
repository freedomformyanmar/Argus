package io.github.freedomformyanmar.argus.appupdate

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

class AppVersionProvider constructor(
  private val context: Context
) {

  fun versionCode(): Long {
    try {
      val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
      return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        pInfo.longVersionCode
      } else {
        pInfo.versionCode.toLong()
      }
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
      return 0L
    }
  }

  fun versionName(): String {
    try {
      val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
      return pInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
      return ""
    }
  }

}