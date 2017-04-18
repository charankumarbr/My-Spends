package in.charanbr.expensetracker.util;

import android.content.Context;
import android.content.SharedPreferences;

import in.charanbr.expensetracker.ExpenseTracker;
import in.charanbr.expensetracker.R;

/**
 * Created by Charan.Br on 4/10/2017.
 */

public final class AppPref {

    private SharedPreferences mSharedPref;

    private static AppPref APP_PREF = null;

    private AppPref() {
        mSharedPref = ExpenseTracker.APP_CONTEXT.getSharedPreferences(ExpenseTracker.APP_CONTEXT.getString(R.string.pref_name), Context.MODE_PRIVATE);
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
}
