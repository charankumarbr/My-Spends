package in.phoenix.myspends.util;

import in.phoenix.myspends.BuildConfig;
import timber.log.Timber;

/**
 * Created by Charan.Br on 2/10/2017.
 */

public final class AppLog {

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            //Log.d(tag, message);
            Timber.tag(tag);
            Timber.d(message);
        }
    }

    public static void d(String tag, String message, Throwable e) {
        if (BuildConfig.DEBUG) {
            //Log.d(tag, message, e);
            Timber.tag(tag);
            Timber.d(message);
            Timber.d(e);
        }
    }
}
