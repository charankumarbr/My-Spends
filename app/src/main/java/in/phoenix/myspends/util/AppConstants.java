package in.phoenix.myspends.util;

/**
 * Created by Charan.Br on 3/2/2017.
 */

public interface AppConstants {

    interface Bundle {
        String EXPENSE_PRIMARY_KEY = "expensePrimaryKey";
        String EXPENSE = "expense";
        String EXPENSE_DATE = "expenseDate";
        String VIA_NOTIFICATION = "viaNotification";
    }

    int VIEW_EXPENSE_CODE = 3832;
    int EDIT_EXPENSE_CODE = 3385;
    int NEW_EXPENSE_CODE = 5323;
    int EXPENSE_LIST_CODE = 3203;

    String FLOAT_FORMAT = "%.02f";

    String BLANK_NOTE_TEMPLATE = "...";

    int CURRENCY_HANDLER_SUCCESS = 3744;
    int CURRENCY_HANDLER_FAILURE = 3745;
    String CURRENCY_LIST_FILE_NAME = "countries_json.txt";

    interface PrefConstants {
        String APP_SETUP = "appSetup";
        String CURRENCY = "currency";
        String NOTIFICATION_HOUR = "notificationHour";
        String NOTIFICATION_MIN = "notificationMin";
        String LAST_APP_OPENED_ON = "lastAppOpenedOn";
    }

    int PAGE_SPENDS_SIZE = 30;

    //-- 128 bit key --//
    String dummy = "iVuNaNKaRcHuGaLu";

    //-- including 1 Payment Type for cash --//
    int MAX_PAYMENT_TYPE_COUNT = 10;

    String[] RESTRICTED_CHARS = {":", "\"", "\\", "*", "%"};

    int MINIMUM_DAY_GAP = 3;
    int MINIMUM_HOUR_GAP = 40;
}