package in.charanbr.expensetracker.controller;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import in.charanbr.expensetracker.database.DBManager;
import in.charanbr.expensetracker.model.ExpenseDate;
import in.charanbr.expensetracker.util.AppConstants;

/**
 * Created by Charan.Br on 3/2/2017.
 */

public final class ExpenseCursorLoader extends CursorLoader {

    private ExpenseDate mExpenseDate;

    private int mLoaderType = -1;

    public ExpenseCursorLoader(Context context, ExpenseDate expenseDate, int loaderType) {
        super(context);
        mExpenseDate = expenseDate;
        mLoaderType = loaderType;
    }

    public ExpenseCursorLoader(Context context, int loaderType) {
        super(context);
        mLoaderType = loaderType;
    }

    @Override
    public Cursor loadInBackground() {
        switch (mLoaderType) {
            case AppConstants.LoaderConstants.LOADER_EXPENSE:
                return DBManager.getExpense(mExpenseDate);

            case AppConstants.LoaderConstants.LOADER_PAYMENT:
                return DBManager.getPaymentTypesCursor();

            case AppConstants.LoaderConstants.LOADER_EXPENSES_LIST:
                return DBManager.getMonthlyExpenses(mExpenseDate);
        }

        return null;
    }
}
