package `in`.phoenix.myspends.database

/**
 * Created by Charan on March 07, 2021
 */
sealed class DBResponse<T> {

    data class Success<T>(val data: T): DBResponse<T>() {

    }

    data class Failed<Nothing>(val message: String): DBResponse<Nothing>()

}
