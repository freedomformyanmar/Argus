package io.github.freedomformyanmar.argus.appupdate

import android.content.Context
import androidx.datastore.createDataStore
import kotlinx.coroutines.flow.first

class AppUpdateCacheSource constructor(context: Context) {

  private val appUpdateDataStore = context.createDataStore("app_update.pb", AppUpdateSerializer)

  suspend fun getLatestUpdate(): AppUpdate? {
    try {
      return with(appUpdateDataStore.data.first()) {
        AppUpdate(
          latestVersionCode = this.latest_version_code,
          requireForcedUpdate = this.require_forced_update,
          selfHostedLink = this.download_link
        )
      }
    } catch (exception: Exception) {
      return null
    }
  }

  suspend fun putLatestUpdate(appUpdate: AppUpdate) {
    appUpdateDataStore.updateData { appUpdateProto ->
      with(appUpdate) {
        AppUpdateProto(
          latest_version_code = this.latestVersionCode,
          require_forced_update = this.requireForcedUpdate,
          download_link = this.selfHostedLink
        )
      }
    }
  }

}