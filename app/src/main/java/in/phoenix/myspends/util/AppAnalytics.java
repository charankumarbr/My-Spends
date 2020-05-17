package in.phoenix.myspends.util;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

import in.phoenix.myspends.MySpends;

/**
 * Created by Charan.Br on 12/22/2017.
 */

public final class AppAnalytics {

    private final FirebaseAnalytics mFirebaseAnalytics;

    private static AppAnalytics APP_ANALYTICS = null;

    private AppAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MySpends.APP_CONTEXT);
    }

    public static AppAnalytics init() {
        if (null == APP_ANALYTICS) {
            APP_ANALYTICS = new AppAnalytics();
        }

        return APP_ANALYTICS;
    }

    public void logEvent(@NonNull String eventName, @NonNull Bundle eventBundle) {
        mFirebaseAnalytics.logEvent(eventName, eventBundle);
    }
}
