package `in`.phoenix.myspends.database

import `in`.phoenix.myspends.model.WAEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

/**
 * Created by Charan on March 07, 2021
 */
@Dao
interface WAHistoryDao {

    @Query("SELECT count(*) FROM wa_entity")
    fun getCount(): Single<Int>

    @Query("SELECT * FROM wa_entity")
    fun getAll(): List<WAEntity>

    @Query("SELECT count(*) FROM wa_entity WHERE code LIKE :code AND number LIKE :mobileNumber")
    fun findMatching(code: String, mobileNumber: String): Single<Int>

    @Insert
    fun insert(entry: WAEntity): Long

    @Insert
    fun insertAll(vararg entries: WAEntity): List<Long>

    @Delete
    fun delete(waEntity: WAEntity): Single<Int>

}