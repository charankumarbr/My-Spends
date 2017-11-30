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

    private AppPref() {
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
}