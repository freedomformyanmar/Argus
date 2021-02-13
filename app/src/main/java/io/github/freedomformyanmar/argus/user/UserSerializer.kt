package io.github.freedomformyanmar.argus.user

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object UserSerializer : Serializer<UserProto?> {

  override fun readFrom(input: InputStream): UserProto? {
    return try {
      UserProto.ADAPTER.decode(input)
    } catch (exception: IOException) {
      throw CorruptionException("Cannot read proto", exception)
    }
  }

  override fun writeTo(t: UserProto?, output: OutputStream) {
    t?.encode(output)
  }

  override val defaultValue: UserProto?
    get() = null

}