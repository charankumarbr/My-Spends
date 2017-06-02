package in.phoenix.trackmyspends;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import in.phoenix.trackmyspends.controller.HourTimeReceiver;
import in.phoenix.trackmyspends.util.AppConstants;
import in.phoenix.trackmyspends.util.AppPref;

/**
 * Created by Charan.Br on 2/11/2017.
 */

public class TrackMySpends extends Application {

    public static Context APP_CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();
        APP_CONTEXT = this;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_HOUR)
                == -1 ? 20 : AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_HOUR));
        calendar.set(Calendar.MINUTE, AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_MIN)
                == -1 ? 0 : AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_MIN));
        calendar.set(Calendar.SECOND, 0);
        if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent receiverIntent = new Intent(this, HourTimeReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
