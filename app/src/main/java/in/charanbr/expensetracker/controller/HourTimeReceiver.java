package in.charanbr.expensetracker.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.Calendar;
import java.util.Random;

import in.charanbr.expensetracker.R;
import in.charanbr.expensetracker.database.DBManager;
import in.charanbr.expensetracker.model.ExpenseDate;
import in.charanbr.expensetracker.ui.activity.NewExpenseActivity;
import in.charanbr.expensetracker.util.AppConstants;
import in.charanbr.expensetracker.util.AppUtil;

/**
 * Created by Charan.Br on 4/25/2017.
 */

public final class HourTimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ExpenseDate expenseDate = AppUtil.convertToDate(Calendar.getInstance().getTimeInMillis());
        if (!DBManager.hasExpense(expenseDate)) {
            long when = System.currentTimeMillis();
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Intent notificationIntent = new Intent(context, NewExpenseActivity.class);
            notificationIntent.putExtra(AppConstants.Bundle.EXPENSE_DATE, expenseDate);
            notificationIntent.putExtra(AppConstants.Bundle.VIA_NOTIFICATION, true);
            notificationIntent.putExtra("check", Calendar.getInstance().getTimeInMillis());
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addParentStack(NewExpenseActivity.class);
            taskStackBuilder.addNextIntent(notificationIntent);

            int random = new Random().nextInt(500);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(random,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentText("Track your today's expenses!")
                    .setSound(alarmSound)
                    .setAutoCancel(true)
                    .setWhen(when)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{1000, 1000, 1000});

            notificationManager.notify(random, builder.build());
        }
    }
}
