package `in`.phoenix.myspends.database

import `in`.phoenix.myspends.MySpends
import android.content.Context
import androidx.room.Room

/**
 * Created by Charan on March 07, 2021
 */
object AppDatabaseProvider {

    private lateinit var appDatabase: MySpendsDatabase

    fun getAppDatabase(applicationContext: Context): MySpendsDatabase {
        if (!this::appDatabase.isInitialized) {
            appDatabase = Room.databaseBuilder(
                    MySpends.APP_CONTEXT,
                    MySpendsDatabase::class.java, DBConstants.DB_NAME
            ).build()
        }

        return appDatabase
    }
}