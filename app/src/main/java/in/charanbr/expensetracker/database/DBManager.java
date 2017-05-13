package in.charanbr.expensetracker.database;

import android.database.Cursor;

import java.util.ArrayList;

import in.charanbr.expensetracker.model.Expense;
import in.charanbr.expensetracker.model.ExpenseDate;
import in.charanbr.expensetracker.model.PaymentMode;
import in.charanbr.expensetracker.model.PaymentType;

/**
 * Created by Charan.Br on 2/11/2017.
 */

public final class DBManager {

    public static ArrayList<PaymentType> getPaymentTypes() {
        return DBAdapter.fetchPaymentTypes();
    }

    public static ArrayList<PaymentMode> getPaymentModes() {
        return DBAdapter.fetchPaymentModes();
    }

    public static long addPaymentType(PaymentType paymentType) {
        return DBAdapter.insertPaymentType(paymentType);
    }

    public static boolean checkPaymentTypeName(String modeName, int selectedPaymentTypeId) {
        return DBAdapter.checkPaymentTypeName(modeName, selectedPaymentTypeId);
    }

    public static long addExpense(Expense expense) {
        return DBAdapter.insertExpense(expense);
    }

    /*public static ArrayList<Expense> getExpenses(ExpenseDate expenseDate) {
        return DBAdapter.fetchExpenses(expenseDate);
    }*/

    public static Cursor getExpenses(ExpenseDate expenseDate) {
        return DBAdapter.fetchExpenses(expenseDate);
    }

    public static String getPaymentTypeName(int paymentTypePriId) {
        return DBAdapter.fetchPaymentTypeName(paymentTypePriId);
    }

    public static Float getTotalExpenses(ExpenseDate expenseDate) {
        return DBAdapter.fetchTotalAmount(expenseDate);
    }

    public static Expense getExpense(int expensePrimaryKey) {
        return DBAdapter.fetchExpense(expensePrimaryKey);
    }

    public static Cursor getPaymentTypesCursor() {
        return DBAdapter.fetchAllPaymentTypes();
    }

    public static int togglePaymentType(int primaryKey, boolean isChecked) {
        return DBAdapter.changePaymentTypeStatus(primaryKey, isChecked);
    }

    public static int removeExpense(int expenseId) {
        return DBAdapter.deleteExpense(expenseId);
    }

    public static int updateExpense(Expense expense) {
        return DBAdapter.updateExpense(expense);
    }

    public static String getMonthlyExpensesTotal(ExpenseDate expenseDate) {
        return DBAdapter.fetchMonthlyExpensesTotal(expenseDate);
    }

    public static Cursor getMonthlyExpenses(ExpenseDate expenseDate) {
        return DBAdapter.fetchMonthlyExpenses(expenseDate);
    }

    public static boolean hasExpense(ExpenseDate expenseDate) {
        return DBAdapter.checkExpense(expenseDate);
    }
}
