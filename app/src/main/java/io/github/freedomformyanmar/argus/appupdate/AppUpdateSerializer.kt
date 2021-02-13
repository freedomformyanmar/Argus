package io.github.freedomformyanmar.argus.appupdate

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object AppUpdateSerializer : Serializer<AppUpdateProto> {

  override fun readFrom(input: InputStream): AppUpdateProto {
    return try {
      AppUpdateProto.ADAPTER.decode(input)
    } catch (exception: IOException) {
      throw CorruptionException("Cannot read proto", exception)
    }
  }

  override fun writeTo(
    t: AppUpdateProto,
    output: OutputStream
  ) {
    t.encode(output)
  }

  override val defaultValue: AppUpdateProto
    get() = AppUpdateProto()

}