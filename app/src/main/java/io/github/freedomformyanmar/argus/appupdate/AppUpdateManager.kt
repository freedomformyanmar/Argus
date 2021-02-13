package io.github.freedomformyanmar.argus.appupdate

import android.content.Context
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AppUpdateManager constructor(
  private val context: Context,
) {

  private val appVersionProvider = AppVersionProvider(context)

  private val appUpdateCacheSource = AppUpdateCacheSource(context)

  private val appUpdateNetworkSource = AppUpdateNetworkSource()

  sealed class UpdateResult {

    data class ForcedUpdate(val updateLink: String) : UpdateResult()

    data class RelaxedUpdate(val updateLink: String) : UpdateResult()

    object NotRequired : UpdateResult()
  }

  suspend fun checkForUpdate(): UpdateResult {
    return withContext(Dispatchers.IO) {
      val latestUpdate =
        getLatestAppUpdate() ?: return@withContext UpdateResult.NotRequired
      processLatestAppUpdate(latestUpdate)
    }
  }


  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  internal suspend fun getLatestAppUpdate(): AppUpdate? {
    try {
      val appUpdate = appUpdateNetworkSource.getLatestUpdate(appVersionProvider.versionCode())
      appUpdateCacheSource.putLatestUpdate(appUpdate)
    } catch (exception: Exception) {
      Timber.e(exception)
    }

    return appUpdateCacheSource.getLatestUpdate()
  }

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  internal suspend fun processLatestAppUpdate(appUpdate: AppUpdate): UpdateResult {
    if (appUpdate.latestVersionCode > appVersionProvider.versionCode()) {

      return if (appUpdate.requireForcedUpdate) {
        UpdateResult.ForcedUpdate(getDownloadLink(appUpdate))
      } else {
        UpdateResult.RelaxedUpdate(getDownloadLink(appUpdate))
      }
    }

    return UpdateResult.NotRequired
  }


  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  internal fun getDownloadLink(appUpdate: AppUpdate): String {
    return appUpdate.selfHostedLink
  }

}