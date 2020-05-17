package in.phoenix.myspends.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import in.phoenix.myspends.BuildConfig;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;

/**
 * Created by Charan.Br on 12/22/2017.
 */

public final class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String data = "";
        if (null != intent.getData()) {
            data = intent.getData().toString();
            AppLog.d("BootCompletedReceiver", "Data:" + data);
        }

        String action = intent.getAction();
        boolean isPackageReplace = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
        AppLog.d("BootCompletedReceiver", "Action:" + action + " :: PackageReplace:" + isPackageReplace);

        if (data.equals("package:" + BuildConfig.APPLICATION_ID)
                || ((action != null) && action.equals(Intent.ACTION_BOOT_COMPLETED))
                || isPackageReplace) {
            AppLog.d("BootCompletedReceiver", "Registering Alarm");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_HOUR)
                    == -1 ? 20 : AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_HOUR));
            calendar.set(Calendar.MINUTE, AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_MIN)
                    == -1 ? 0 : AppPref.getInstance().getInt(AppConstants.PrefConstants.NOTIFICATION_MIN));
            calendar.set(Calendar.SECOND, 0);
            if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            Intent receiverIntent = new Intent(context, HourTimeReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
}
