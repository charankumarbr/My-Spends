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

    interface LoaderConstants {
        int LOADER_EXPENSE = 9932;
        int LOADER_PAYMENT = 9901;
        int LOADER_EXPENSES_LIST = 3282;
    }

    interface PrefConstants {
        String APP_SETUP = "appSetup";
        String CURRENCY = "currency";
        String NOTIFICATION_HOUR = "notificationHour";
        String NOTIFICATION_MIN = "notificationMin";
    }

    int PAGE_SPENDS_SIZE = 6;

    //-- 128 bit key --//
    String dummy = "iVuNaNKaRcHuGaLu";
}