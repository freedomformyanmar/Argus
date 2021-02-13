package io.github.freedomformyanmar.argus.user

import android.content.Context
import androidx.datastore.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserCache constructor(
    context: Context
) {
    private val userDataStore = context.createDataStore("user.pb", UserSerializer)

    suspend fun getUser(): User? {
        try {
            return with(userDataStore.data.first()) {
                if (this == null) return@with null
                User(
                    number = this.number,
                    secretCode = this.secret
                )
            }
        } catch (exception: Exception) {
            return null
        }
    }

    suspend fun userFlow(): Flow<User?> {
        return userDataStore.data.map { userProto ->
            if (userProto == null) null
            else {
                User(
                    number = userProto.number,
                    secretCode = userProto.secret
                )
            }
        }
    }

    suspend fun saveUser(user: User) {
        userDataStore.updateData { userProto ->
            UserProto(
                number = user.number,
                secret = user.secretCode
            )
        }
    }
}
