package com.cocobiz.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cocobiz.app.data.local.dao.DealerDao
import com.cocobiz.app.data.local.dao.SalesEntryDao
import com.cocobiz.app.data.local.dao.SettingsDao
import com.cocobiz.app.data.local.dao.UserProfileDao
import com.cocobiz.app.data.local.entity.DealerEntity
import com.cocobiz.app.data.local.entity.SalesEntryEntity
import com.cocobiz.app.data.local.entity.SettingsEntity
import com.cocobiz.app.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        DealerEntity::class,
        SalesEntryEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun dealerDao(): DealerDao
    abstract fun salesEntryDao(): SalesEntryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        const val DATABASE_NAME = "cocobiz_db"
    }
}
