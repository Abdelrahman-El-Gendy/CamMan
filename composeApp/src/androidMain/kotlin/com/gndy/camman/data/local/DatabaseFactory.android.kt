package com.gndy.camman.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<CamManDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(CamManDatabase.DATABASE_NAME)
        return Room.databaseBuilder<CamManDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
