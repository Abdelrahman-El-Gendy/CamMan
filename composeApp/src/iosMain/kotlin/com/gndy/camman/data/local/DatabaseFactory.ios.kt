package com.gndy.camman.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<CamManDatabase> {
        val dbFilePath = NSHomeDirectory() + "/${CamManDatabase.DATABASE_NAME}"
        return Room.databaseBuilder<CamManDatabase>(
            name = dbFilePath
        )
    }
}
