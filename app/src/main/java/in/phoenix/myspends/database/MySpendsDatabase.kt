package `in`.phoenix.myspends.database

import `in`.phoenix.myspends.model.WAEntity
import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by Charan on March 07, 2021
 */
@Database(entities = [WAEntity::class], version = 1, exportSchema = false)
abstract class MySpendsDatabase : RoomDatabase() {
    abstract fun waHistoryDao(): WAHistoryDao
}