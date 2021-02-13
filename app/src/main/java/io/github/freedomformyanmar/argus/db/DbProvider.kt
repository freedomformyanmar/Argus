package io.github.freedomformyanmar.argus.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver

object DbProvider {

    private var db: ArgusDatabase? = null

    fun getInstance(context: Context): ArgusDatabase {
        if (db == null) {
            val driver = AndroidSqliteDriver(ArgusDatabase.Schema, context, "argus.db")
            db = ArgusDatabase(driver)
        }
        return db!!
    }
}