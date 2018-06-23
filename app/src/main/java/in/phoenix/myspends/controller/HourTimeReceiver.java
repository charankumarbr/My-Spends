package in.phoenix.myspends.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 4/25/2017.
 */

public final class HourTimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ExpenseDate expenseDate = AppUtil.convertToDate(Calendar.getInstance().getTimeInMillis());

        AppUtil.createNotification(context, expenseDate);
    }
}
