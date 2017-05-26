package in.charanbr.expensetracker.database;

/**
 * Created by Charan.Br on 2/11/2017.
 */

public interface DBConstants {

    String DB_NAME = "in_charanbr_expensetracker.db";

    int DB_VERSION = 2;

    int PAYMENT_MODE_COUNT = 4;

    interface TableName {
        String PAYMENT_TYPE = "table_payment_type";
        String PAYMENT_MODE = "table_payment_mode";
        String EXPENSE = "table_expense";
    }

    interface LoaderId {
        int EXPENSE = 3205;
        int PAYMENT_TYPE = 1663;
        int EXPENSES_LIST = 6955;
    }

    interface COLUMN {
        String NAME = "name";
        String PAYMENT_TYPE_ID = "payment_type_id";
        String CREATED_ON = "created_on";
        String IS_ACTIVE = "is_active";
        String EXPENSE_DATE = "expense_date";
        String AMOUNT = "amount";
        String PAYMENT_MODE_ID = "payment_mode_id";
        //String DESCRIPTION = "description";
        String NOTE = "note";
        String PAYMENT_TYPE_PRI_ID = "payment_type_pri_id";
        String UPDATED_ON = "updated_on";
        String EXPENSE_ON = "expense_on";
    }

    interface PAYMENT_MODE {
        String CASH = "Cash";
        String CREDIT_CARD = "Credit Card";
        String DEBIT_CARD = "Debit Card";
        String WALLET = "Wallet";
        String NET_BANKING = "Net Banking";

        int CASH_ID = 0;
        int CREDIT_CARD_ID = 1;
        int DEBIT_CARD_ID = 2;
        int WALLET_ID = 3;
        int NET_BANKING_ID = 4;
    }

}
