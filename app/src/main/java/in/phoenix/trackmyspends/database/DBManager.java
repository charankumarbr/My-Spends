package in.phoenix.trackmyspends.database;

import android.database.Cursor;

import java.util.ArrayList;

import in.phoenix.trackmyspends.model.Expense;
import in.phoenix.trackmyspends.model.ExpenseDate;
import in.phoenix.trackmyspends.model.PaymentMode;
import in.phoenix.trackmyspends.model.PaymentType;

/**
 * Created by Charan.Br on 2/11/2017.
 */

public final class DBManager {

    public static ArrayList<PaymentType> getPaymentTypes(boolean isActive) {
        return DBAdapter.fetchPaymentTypes(isActive);
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

    /*public static ArrayList<Expense> getExpense(ExpenseDate expenseDate) {
        return DBAdapter.fetchExpense(expenseDate);
    }*/

    public static Cursor getExpense(ExpenseDate expenseDate) {
        return DBAdapter.fetchExpense(expenseDate);
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

    public static Cursor getExpense(ExpenseDate fromDate, ExpenseDate toDate, Integer[] paidBy) {
        return DBAdapter.fetchExpense(fromDate, toDate, paidBy);
    }
}
