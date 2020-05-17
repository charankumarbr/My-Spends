package in.phoenix.myspends.util;

import android.content.Context;
import android.content.SharedPreferences;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;

/**
 * Created by Charan.Br on 4/10/2017.
 */

public final class AppPref {

    private final SharedPreferences mSharedPref;

    private static AppPref APP_PREF = null;

    public AppPref(Context context) {
        mSharedPref = context.getSharedPreferences(MySpends.APP_CONTEXT.getString(R.string.pref_name), Context.MODE_PRIVATE);
    }

    public AppPref() {
        mSharedPref = MySpends.APP_CONTEXT.getSharedPreferences(MySpends.APP_CONTEXT.getString(R.string.pref_name), Context.MODE_PRIVATE);
    }

    public static AppPref getInstance() {
        if (APP_PREF == null) {
            APP_PREF = new AppPref();
        }

        return APP_PREF;
    }

    public String getString(String key) {
        return mSharedPref.getString(key, null);
    }

    public void putString(String key, String value) {
        mSharedPref.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        mSharedPref.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return mSharedPref.getInt(key, -1);
    }

    public void clearAll() {
        mSharedPref.edit().clear().apply();
    }

    public void putLong(String key, long value) {
        mSharedPref.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return mSharedPref.getLong(key, 0);
    }

    public void incrementAppOpenCount() {
        int currentCount = mSharedPref.getInt(AppConstants.PrefConstants.LAUNCH_COUNT, 0);
        AppLog.d("AppPref", "incrementAppOpenCount: Pre:" + currentCount);
        if (currentCount >= 0) {
            currentCount++;
            //currentCount = currentCount % AppConstants.APP_RATE_FREQUENCY;
            AppLog.d("AppPref", "incrementAppOpenCount: Post:" + currentCount);
            mSharedPref.edit().putInt(AppConstants.PrefConstants.LAUNCH_COUNT, currentCount).apply();
        }
    }

    public void appRated() {
        mSharedPref.edit().putInt(AppConstants.PrefConstants.LAUNCH_COUNT, -1).apply();
    }
}
