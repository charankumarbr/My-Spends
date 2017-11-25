package in.phoenix.myspends.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UnknownFormatConversionException;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.model.ExpenseDate;

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

    public static ExpenseDate getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return new ExpenseDate(1, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }

    public static ExpenseDate getCurrentDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return new ExpenseDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
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
}
