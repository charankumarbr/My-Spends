package `in`.phoenix.myspends.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Created by Charan on November 21, 2020
 */
object AppCrashLogger {

    fun reportException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}