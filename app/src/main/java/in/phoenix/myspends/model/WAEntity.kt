package `in`.phoenix.myspends.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Charan on March 07, 2021
 */
@Entity(tableName = "wa_entity")
data class WAEntity(
        @ColumnInfo(name = "code") val code: String,
        @ColumnInfo(name = "number") val number: String) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0

    @ColumnInfo(name = "addedOn") var addedOn: String = ""
}
