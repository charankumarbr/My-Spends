package in.phoenix.myspends.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UnknownFormatConversionException;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.PaymentMode;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.ui.activity.LaunchDeciderActivity;
import in.phoenix.myspends.ui.activity.NewExpenseActivity;

/**
 * Created by Charan.Br on 2/10/2017.
 */

public final class AppUtil {

    /**
     * Convert millis to date - dd-MM-yyyy format
     *
     * @param millis Millis to be converted to ExpenseDate
     * @return ExpenseDate
     */
    public static ExpenseDate convertToDate(long millis) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(new Date(millis));
        String[] dateParts = date.split("-");

        return new ExpenseDate(Integer.parseInt(dateParts[0]), (Integer.parseInt(dateParts[1]) - 1),
                Integer.parseInt(dateParts[2]));
    }

    public static String convertToDateDB(long millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy h:mm:ss a");
        return simpleDateFormat.format(new Date(millis));
    }

    public static void showToast(int stringId) {
        Toast.makeText(MySpends.APP_CONTEXT, MySpends.APP_CONTEXT.getString(stringId), Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String message) {
        Toast.makeText(MySpends.APP_CONTEXT, message, Toast.LENGTH_SHORT).show();
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = MySpends.APP_CONTEXT.getResources().getDisplayMetrics();
        //int one = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        int two = (int) ((dp * displayMetrics.density) + 0.5);
        //float three = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        //AppLog.d("dpToPx", one + "::" + two + "::" + three);
        // SELECT distinct substr(expense_date, 4) from table_expense;
        return two;
    }

    public static void toggleKeyboard(boolean toShow) {
        InputMethodManager inputMethodManager = (InputMethodManager) MySpends.APP_CONTEXT.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (toShow) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        } else {
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public static String dateDBToString(String dateInString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy h:mm:ss a");
        Date strDate = simpleDateFormat.parse(dateInString);
        StringBuilder builder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(strDate);
        builder.append(getThDate(calendar.get(Calendar.DATE)));
        builder.append(" ");
        builder.append(getShortMonth(calendar.get(Calendar.MONTH)));
        builder.append(" ");
        builder.append(calendar.get(Calendar.YEAR));

        return builder.toString();
    }

    public static String dateDBToString(long timeInMillis) throws ParseException {
        StringBuilder builder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        builder.append(getThDate(calendar.get(Calendar.DATE)));
        builder.append(" ");
        builder.append(getShortMonth(calendar.get(Calendar.MONTH)));
        builder.append(" ");
        builder.append(calendar.get(Calendar.YEAR));

        return builder.toString();
    }

    public static String getShortMonth(int month) {
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
        }

        AppLog.d("ExpenseDate", "Month:" + month, new UnknownFormatConversionException("Wrong month::" + month));
        return "";
    }

    public static String getMonth(int month) {
        switch (month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
        }

        AppLog.d("ExpenseDate", "Month:" + month, new UnknownFormatConversionException("Wrong month::" + month));
        return "";
    }

    public static String getThDate(int dayOfMonth) {
        switch (dayOfMonth) {
            case 1:
            case 21:
            case 31:
                return dayOfMonth + "st";

            case 2:
            case 22:
                return dayOfMonth + "nd";

            case 3:
            case 23:
                return dayOfMonth + "rd";

            default:
                return dayOfMonth + "th";
        }
    }

    public static String getStringAmount(String amountInString) throws NumberFormatException {
        Float amount = Float.valueOf(amountInString);
        return String.format(Locale.ENGLISH, AppConstants.FLOAT_FORMAT, amount);
    }

    public static ArrayList<Currency> getAllCurrency() throws IOException, JSONException {
        ArrayList<Currency> currencies = null;

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(MySpends.APP_CONTEXT.getAssets()
                        .open(AppConstants.CURRENCY_LIST_FILE_NAME)));

        StringBuilder builder = new StringBuilder();
        String mLine = bufferedReader.readLine();
        while (mLine != null) {
            builder.append(mLine); // process line
            mLine = bufferedReader.readLine();
        }

        bufferedReader.close();

        JSONArray jsonArray = new JSONArray(builder.toString());
        if (jsonArray.length() > 0) {
            currencies = new ArrayList<>();
            for (int index = 0; index < jsonArray.length(); index++) {
                Currency currency = new Currency();
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                currency.setCurrencySymbol(jsonObject.getString("symbol"));
                currency.setCurrencyName(jsonObject.getString("name"));
                currency.setCurrencyCode(jsonObject.getString("code"));
                currencies.add(currency);
                currency = null;
            }
        }

        if (null != currencies) {
            Collections.sort(currencies, new Comparator<Currency>() {
                @Override
                public int compare(Currency o1, Currency o2) {
                    return o1.getCurrencyCode().compareTo(o2.getCurrencyCode());
                }
            });
        }

        return currencies;
    }

    public static void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackbar(View view, int stringId) {
        Snackbar.make(view, stringId, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackbarWithAction(View view, String message,
                                              String actionName, View.OnClickListener clickListener) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.setAction(actionName, clickListener);
        snackbar.show();
    }

    public static long getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getCurrentDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTimeInMillis();
    }

    public static boolean isUserLoggedIn() {
        return null != FirebaseAuth.getInstance().getCurrentUser();
    }

    public static boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) MySpends.APP_CONTEXT.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return true;
            }
        }

        return false;
    }

    public static Float getFloatAmount(String amountInString) {
        return Float.valueOf(amountInString);
    }

    public static String getPaidByForKey(String paymentTypeKey) {

        PaymentType paymentType = MySpends.getPaymentTypeForKey(paymentTypeKey);

        if (null != paymentType) {
            StringBuilder builder = new StringBuilder();
            builder.append(paymentType.getName());

            if (!paymentType.getKey().equals("0") && !(paymentType.getCreatedOn() == 0)) {
                builder.append(" (");
                builder.append(PaymentMode.getModeName(paymentType.getPaymentModeId()));
                builder.append(")");
            }

            return builder.toString();
        }


        return "";
    }

    public static void createNotification(Context context, ExpenseDate expenseDate) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = null;
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(MySpends.APP_CONTEXT);
        if (null == AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY)) {
            //-- no currency setup, get it first --//
            notificationIntent = new Intent(context, LaunchDeciderActivity.class);

        } else {
            notificationIntent = new Intent(context, NewExpenseActivity.class);
            notificationIntent.putExtra(AppConstants.Bundle.EXPENSE_DATE, expenseDate);
            //taskStackBuilder.addParentStack(NewExpenseActivity.class);
        }

        notificationIntent.putExtra(AppConstants.Bundle.VIA_NOTIFICATION, true);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //taskStackBuilder.addNextIntent(notificationIntent);
        taskStackBuilder.addNextIntentWithParentStack(notificationIntent);

        //int random = new Random().nextInt(500);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,
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

        notificationManager.notify(20332, builder.build());
    }

    public static void addDynamicShortcut() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = MySpends.APP_CONTEXT.getSystemService(ShortcutManager.class);
            AppLog.d("LaunchDecider", "add: Shortcut Count:" + shortcutManager.getDynamicShortcuts().size());
            if (shortcutManager.getDynamicShortcuts().size() == 0) {
                //-- Application restored or no shortcut added. Need to re-publish dynamic shortcuts. --//
                Intent shortcutIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("myspends://www.myspends.co.in/addSpend"));
                shortcutIntent.putExtra(AppConstants.Bundle.VIA_NOTIFICATION, true);
                //shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ShortcutInfo shortcut = new ShortcutInfo.Builder(MySpends.APP_CONTEXT, "as1")
                        .setShortLabel("New Spend")
                        .setLongLabel("New Spend")
                        .setIcon(Icon.createWithResource(MySpends.APP_CONTEXT, R.drawable.ic_new_shortcut_24dp))
                        .setIntent(shortcutIntent)
                        .build();

                shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
            }
        }

    }

    public static void removeDynamicShortcut() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = MySpends.APP_CONTEXT.getSystemService(ShortcutManager.class);
            AppLog.d("LaunchDecider", "remove: Shortcut Count:" + shortcutManager.getDynamicShortcuts().size());
            if (shortcutManager.getDynamicShortcuts().size() > 0) {
                shortcutManager.removeAllDynamicShortcuts();
            }
        }
    }

    public static boolean doesContainRestrictedChar(String note) {

        String[] restrictedChars = {":", "\"", "\\", "*", "%"};
        for (String restrictedChar : restrictedChars) {
            if (note.contains(restrictedChar)) {
                return true;
            }
        }

        return false;
    }

}
