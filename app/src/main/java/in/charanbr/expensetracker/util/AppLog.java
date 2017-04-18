package in.charanbr.expensetracker.util;

import android.util.Log;

import in.charanbr.expensetracker.BuildConfig;

/**
 * Created by Charan.Br on 2/10/2017.
 */

public final class AppLog {

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void d(String tag, String message, Exception e) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message, e);
        }
    }
}
