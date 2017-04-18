package in.charanbr.expensetracker.util;

/**
 * Created by Charan.Br on 3/2/2017.
 */

public interface AppConstants {

    interface Bundle {
        String EXPENSE_PRIMARY_KEY = "expensePrimaryKey";
        String EXPENSE = "expense";
        String EXPENSE_DATE = "expenseDate";
    }

    int VIEW_EXPENSE_CODE = 3832;
    int EDIT_EXPENSE_CODE = 3385;
    int NEW_EXPENSE_CODE = 5323;

    String FLOAT_FORMAT = "%.02f";

    String BLANK_NOTE_TEMPLATE = "...";

    int CURRENCY_HANDLER_SUCCESS = 3744;
    int CURRENCY_HANDLER_FAILURE = 3745;
    String CURRENCY_LIST_FILE_NAME = "countries_json.txt";

    interface LoaderConstants {
        int LOADER_EXPENSE = 9932;
        int LOADER_PAYMENT = 9901;
    }

    interface PrefConstants {
        String APP_SETUP = "appSetup";
        String CURRENCY = "currency";
    }
}