package in.phoenix.trackmyspends.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

import in.phoenix.trackmyspends.TrackMySpends;
import in.phoenix.trackmyspends.model.Expense;
import in.phoenix.trackmyspends.model.ExpenseDate;
import in.phoenix.trackmyspends.model.PaymentMode;
import in.phoenix.trackmyspends.model.PaymentType;

/**
 * Created by Charan.Br on 2/11/2017.
 */

class DBAdapter {

    private static final ExpenseTrackerDB expenseTrackerDBHelper = new ExpenseTrackerDB(TrackMySpends.APP_CONTEXT);

    public static ArrayList<PaymentType> fetchPaymentTypes(boolean isActive) {

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        ArrayList<PaymentType> paymentTypes = null;

        Cursor cursor = null;

        if (isActive) {
            cursor = database.rawQuery("SELECT * FROM " + DBConstants.TableName.PAYMENT_TYPE
                    + " WHERE " + DBConstants.COLUMN.IS_ACTIVE + " = 1", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM " + DBConstants.TableName.PAYMENT_TYPE, null);
        }

        try {
            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                paymentTypes = new ArrayList<>();
                int indexPrimary = cursor.getColumnIndex(BaseColumns._ID);
                int indexName = cursor.getColumnIndex(DBConstants.COLUMN.NAME);
                do {
                    PaymentType paymentType = new PaymentType();
                    paymentType.setId(cursor.getInt(indexPrimary));
                    paymentType.setName(cursor.getString(indexName));
                    paymentTypes.add(paymentType);

                } while (cursor.moveToNext());
            }
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            cursor = null;
            if (null != database && database.isOpen()) {
                database.close();
            }
            database = null;
        }

        return paymentTypes;
    }

    public static ArrayList<PaymentMode> fetchPaymentModes() {

        ArrayList<PaymentMode> paymentModes = null;

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + DBConstants.TableName.PAYMENT_MODE, null);

        try {
            if (null != cursor && cursor.getCount() > 0) {
                paymentModes = new ArrayList<>();
                cursor.moveToFirst();
                int indexPrimary = cursor.getColumnIndex(BaseColumns._ID);
                int indexName = cursor.getColumnIndex(DBConstants.COLUMN.NAME);
                int indexTypeId = cursor.getColumnIndex(DBConstants.COLUMN.PAYMENT_MODE_ID);

                do {
                    PaymentMode paymentMode = new PaymentMode();
                    paymentMode.setId(cursor.getInt(indexPrimary));
                    paymentMode.setName(cursor.getString(indexName));
                    paymentMode.setModeId(cursor.getInt(indexTypeId));
                    paymentModes.add(paymentMode);

                } while (cursor.moveToNext());
            }

        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            cursor = null;
            if (null != database && database.isOpen()) {
                database.close();
            }
            database = null;
        }

        return paymentModes;
    }

    public static boolean checkPaymentTypeName(String typeName, int selectedPaymentTypeId) {

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        Cursor cursor = null;

        boolean status = false;
        try {
            cursor = database.rawQuery("Select * FROM " + DBConstants.TableName.PAYMENT_TYPE + " WHERE " + DBConstants.COLUMN.NAME + " LIKE '"
                    + typeName + "' AND " + DBConstants.COLUMN.PAYMENT_TYPE_ID + " LIKE '%" + selectedPaymentTypeId + "%';", null);

            status = ((null != cursor) && cursor.getCount() > 0);

        } finally {

            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            cursor = null;

            if (null != database && database.isOpen()) {
                database.close();
            }
            database = null;
        }

        return status;

    }

    public static long insertPaymentType(PaymentType paymentType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN.NAME, paymentType.getName());
        contentValues.put(DBConstants.COLUMN.PAYMENT_TYPE_ID, paymentType.getTypeId());
        contentValues.put(DBConstants.COLUMN.CREATED_ON, paymentType.getCreatedOn());
        contentValues.put(DBConstants.COLUMN.IS_ACTIVE, 1); //-- deemed active while inserting --//

        SQLiteDatabase database = expenseTrackerDBHelper.getWritableDatabase();

        long insertCount = -1;

        if (null != database) {
            try {
                insertCount = database.insert(DBConstants.TableName.PAYMENT_TYPE, null, contentValues);

            } finally {
                if (null != database && database.isOpen()) {
                    database.close();
                }
                database = null;
                contentValues = null;
            }
        }

        return insertCount;
    }

    public static long insertExpense(Expense expense) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN.EXPENSE_DATE, expense.getExpenseDate().toString());
        contentValues.put(DBConstants.COLUMN.PAYMENT_TYPE_PRI_ID, expense.getPaymentTypePriId());
        contentValues.put(DBConstants.COLUMN.CREATED_ON, expense.getCreatedOn());
        contentValues.put(DBConstants.COLUMN.AMOUNT, expense.getAmount());
        contentValues.put(DBConstants.COLUMN.NOTE, expense.getNote());
        contentValues.put(DBConstants.COLUMN.UPDATED_ON, expense.getCreatedOn());
        contentValues.put(DBConstants.COLUMN.EXPENSE_ON, expense.getExpenseDate().getTimeInMillis());

        SQLiteDatabase database = expenseTrackerDBHelper.getWritableDatabase();

        long insertCount = -1;

        if (null != database) {
            try {
                insertCount = database.insert(DBConstants.TableName.EXPENSE, null, contentValues);

            } finally {
                if (null != database && database.isOpen()) {
                    database.close();
                }
                database = null;
                contentValues = null;
            }
        }

        return insertCount;
    }

    public static int updateExpense(Expense expense) {

        SQLiteDatabase database = expenseTrackerDBHelper.getWritableDatabase();

        int updateCount = -1;

        try {
            if (null != database) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBConstants.COLUMN.EXPENSE_DATE, expense.getExpenseDate().toString());
                contentValues.put(DBConstants.COLUMN.PAYMENT_TYPE_PRI_ID, expense.getPaymentTypePriId());
                contentValues.put(DBConstants.COLUMN.AMOUNT, expense.getAmount());
                contentValues.put(DBConstants.COLUMN.NOTE, expense.getNote());
                contentValues.put(DBConstants.COLUMN.UPDATED_ON, System.currentTimeMillis());
                updateCount = database.update(DBConstants.TableName.EXPENSE, contentValues, BaseColumns._ID + " = ?", new String[]{String.valueOf(expense.getId())});
            }

        } finally {
            if (null != database && database.isOpen()) {
                database.close();
            }
            database = null;
        }

        return updateCount;
    }

    /*public static ArrayList<Expense> fetchExpense(ExpenseDate expenseDate) {
        ArrayList<Expense> expenses = null;

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();
        if (null != database) {
            Cursor cursor = database.rawQuery("Select * FROM " + DBConstants.TableName.EXPENSE + " WHERE "
                    + DBConstants.COLUMN.EXPENSE_DATE + " LIKE '" + expenseDate.toString() + "';", null);

            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int indexAmount = cursor.getColumnIndex(DBConstants.COLUMN.AMOUNT);
                int indexCreatedOn = cursor.getColumnIndex(DBConstants.COLUMN.CREATED_ON);
                int indexDesc = cursor.getColumnIndex(DBConstants.COLUMN.NOTE);
                int indexPaymentTypePriId = cursor.getColumnIndex(DBConstants.COLUMN.PAYMENT_TYPE_PRI_ID);
                expenses = new ArrayList<>();

                do {
                    Expense expense = new Expense();
                    expense.setAmount(Float.parseFloat(cursor.getString(indexAmount)));
                    expense.setPaymentTypePriId(cursor.getInt(indexPaymentTypePriId));
                    expense.setCreatedOn(cursor.getString(indexCreatedOn));
                    expense.setNote(cursor.getString(indexDesc));

                    expenses.add(expense);
                } while (cursor.moveToNext());
            }
        }
        return expenses;
    }*/

    public static Cursor fetchExpense(ExpenseDate expenseDate) {

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();
        if (null != database) {
            return database.rawQuery("Select * FROM " + DBConstants.TableName.EXPENSE + " WHERE "
                    + DBConstants.COLUMN.EXPENSE_DATE + " LIKE '" + expenseDate.toString() + "';", null);

        }
        return null;
    }

    public static String fetchPaymentTypeName(int paymentTypePriId) {

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        String paymentTypeName = "";

        if (null != database) {
            Cursor cursor = null;
            try {
                cursor = database.rawQuery("Select * FROM " + DBConstants.TableName.PAYMENT_TYPE
                        + " WHERE " + BaseColumns._ID + " = " + paymentTypePriId, null);

                if (null != cursor && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    StringBuilder paymentType = new StringBuilder();
                    paymentType.append(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN.NAME)));

                    String paymentTypeId = cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN.PAYMENT_TYPE_ID));
                    if (paymentTypeId.contains("|")) {
                        int paymentModeId = Integer.parseInt(paymentTypeId.split("\\|")[1]);
                        if (paymentModeId != 0) {
                            paymentType.append(" (");
                            switch (paymentModeId) {
                                case DBConstants.PAYMENT_MODE.DEBIT_CARD_ID:
                                    paymentType.append(DBConstants.PAYMENT_MODE.DEBIT_CARD);
                                    break;
                                case DBConstants.PAYMENT_MODE.CREDIT_CARD_ID:
                                    paymentType.append(DBConstants.PAYMENT_MODE.CREDIT_CARD);
                                    break;
                                case DBConstants.PAYMENT_MODE.WALLET_ID:
                                    paymentType.append(DBConstants.PAYMENT_MODE.WALLET);
                                    break;
                                case DBConstants.PAYMENT_MODE.NET_BANKING_ID:
                                    paymentType.append(DBConstants.PAYMENT_MODE.NET_BANKING);
                                    break;
                            }
                            paymentType.append(")");
                        }

                    }
                    paymentTypeName = paymentType.toString();
                }
            } finally {
                if (null != cursor && !cursor.isClosed()) {
                    cursor.close();
                }
                cursor = null;

                if (null != database && database.isOpen()) {
                    database.close();
                }
                database = null;
            }
        }

        return paymentTypeName;
    }

    public static Float fetchTotalAmount(ExpenseDate expenseDate) {

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        Cursor cursor = null;

        Float totalAmount = null;

        try {
            cursor = database.rawQuery("SELECT SUM(" + DBConstants.COLUMN.AMOUNT + ") FROM " + DBConstants.TableName.EXPENSE
                    + " WHERE " + DBConstants.COLUMN.EXPENSE_DATE + " LIKE '" + expenseDate.toString() + "';", null);

            if (null != cursor && cursor.moveToFirst()) {
                totalAmount = cursor.getFloat(0);
            }

        } finally {

            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
            cursor = null;

            if (null != database && database.isOpen()) {
                database.close();
            }
            database = null;
        }

        return totalAmount;
    }

    public static Expense fetchExpense(int expensePrimaryKey) {

        Expense expense = null;

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        if (null != database) {
            Cursor cursor = database.query(DBConstants.TableName.EXPENSE, null, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(expensePrimaryKey)}, null, null, null);

            try {
                if (null != cursor && cursor.moveToFirst()) {
                    expense = new Expense();
                    expense.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                    expense.setCreatedOn(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN.CREATED_ON)));
                    expense.setUpdatedOn(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN.UPDATED_ON)));
                    expense.setPaymentTypePriId(cursor.getInt(cursor.getColumnIndex(DBConstants.COLUMN.PAYMENT_TYPE_PRI_ID)));
                    expense.setAmount(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN.AMOUNT)));
                    expense.setExpenseDate(new ExpenseDate(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN.EXPENSE_DATE))));
                    expense.setNote(cursor.getString(cursor.getColumnIndex(DBConstants.COLUMN.NOTE)));
                }

            } finally {
                if (null != cursor && !cursor.isClosed()) {
                    cursor.close();
                }
                cursor = null;

                if (database.isOpen()) {
                    database.close();
                }
                database = null;
            }
        }

        return expense;
    }

    public static Cursor fetchAllPaymentTypes() {

        Cursor allPaymentTypesCursor = null;

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        if (null != database) {
            allPaymentTypesCursor = database.rawQuery("SELECT * FROM " + DBConstants.TableName.PAYMENT_TYPE + ";", null);
        }

        return allPaymentTypesCursor;
    }

    public static int changePaymentTypeStatus(int primaryKey, boolean isChecked) {

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        int updateCount = 0;

        ContentValues contentValues = null;
        if (null != database) {
            try {
                int checkedStatus;

                if (isChecked) {
                    checkedStatus = 1;

                } else {
                    checkedStatus = 0;
                }

                contentValues = new ContentValues();
                contentValues.put(DBConstants.COLUMN.IS_ACTIVE, checkedStatus);
                updateCount = database.update(DBConstants.TableName.PAYMENT_TYPE, contentValues, BaseColumns._ID + " = " + primaryKey, null);

            } finally {
                if (database.isOpen()) {
                    database.close();
                }
                database = null;
                contentValues.clear();
                contentValues = null;
            }
        }

        return updateCount;
    }

    public static int deleteExpense(int expenseId) {

        SQLiteDatabase database = expenseTrackerDBHelper.getWritableDatabase();

        int deleteCount = 0;
        if (null != database) {
            try {
                deleteCount = database.delete(DBConstants.TableName.EXPENSE, BaseColumns._ID + " = ?", new String[]{String.valueOf(expenseId)});

            } finally {
                if (database.isOpen()) {
                    database.close();
                }
                database = null;
            }
        }

        return deleteCount;
    }

    public static String fetchMonthlyExpensesTotal(ExpenseDate expenseDate) {

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        Cursor monthlyExpensesCursor = null;

        String monthlyExpense = null;

        if (null != database) {
            try {
                String sqlSelection = "%" + expenseDate.getMonth() + "|" + expenseDate.getYear();
                monthlyExpensesCursor = database.rawQuery("SELECT SUM(" + DBConstants.COLUMN.AMOUNT + ") FROM " + DBConstants.TableName.EXPENSE +
                        " WHERE " + DBConstants.COLUMN.EXPENSE_DATE + " LIKE '" + sqlSelection + "';", null);

                if (null != monthlyExpensesCursor && monthlyExpensesCursor.moveToFirst()) {
                    monthlyExpense = monthlyExpensesCursor.getString(0);
                }

            } finally {
                if (null != monthlyExpensesCursor && !monthlyExpensesCursor.isClosed()) {
                    monthlyExpensesCursor.close();
                }
                monthlyExpensesCursor = null;

                if (null != database && database.isOpen()) {
                    database.close();
                }
                database = null;
            }
        }

        return monthlyExpense;
    }

    public static Cursor fetchMonthlyExpenses(ExpenseDate expenseDate) {

        Cursor cursor = null;

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        if (null != database) {
            String sqlSelection = "%" + expenseDate.getMonth() + "|" + expenseDate.getYear();
            String query = "SELECT * FROM " +
                    DBConstants.TableName.EXPENSE +
                    " WHERE " +
                    DBConstants.COLUMN.EXPENSE_DATE +
                    " LIKE '" + sqlSelection + "'" +
                    " ORDER BY " +
                    BaseColumns._ID + " DESC, " +
                    DBConstants.COLUMN.EXPENSE_ON + " DESC;";

            cursor = database.rawQuery(query, null);
        }

        return cursor;
    }

    public static boolean checkExpense(ExpenseDate expenseDate) {

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        if (null != database) {
            try {
                cursor = database.rawQuery("SELECT count(" + BaseColumns._ID + ") FROM " + DBConstants.TableName.EXPENSE
                        + " WHERE " + DBConstants.COLUMN.EXPENSE_DATE + " LIKE '" + expenseDate.toString() + "';", null);

                if (null != cursor && cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
            } finally {
                if (null != cursor && !cursor.isClosed()) {
                    cursor.close();
                }
                cursor = null;

                if (null != database && database.isOpen()) {
                    database.close();
                }
                database = null;
            }
        }

        return count > 0;
    }

    public static Cursor fetchExpense(ExpenseDate fromDate, ExpenseDate toDate, Integer[] paidBy) {

        Cursor cursor = null;

        SQLiteDatabase database = expenseTrackerDBHelper.getReadableDatabase();

        if (null != database) {
            StringBuilder builder = new StringBuilder();
            builder.append("SELECT * FROM ");
            builder.append(DBConstants.TableName.EXPENSE);
            builder.append(" WHERE ");
            builder.append(DBConstants.COLUMN.EXPENSE_ON);
            builder.append(" >= ");
            builder.append(fromDate.getTimeInMillis());
            builder.append(" AND ");
            builder.append(DBConstants.COLUMN.EXPENSE_ON);
            builder.append(" <= ");
            builder.append(toDate.getTimeInMillis());

            if (null != paidBy) {
                builder.append(" AND (");
                for (int index = 0; index < paidBy.length; index++) {
                    builder.append(DBConstants.COLUMN.PAYMENT_TYPE_PRI_ID);
                    builder.append(" = ");
                    builder.append(paidBy[index]);

                    if (index != paidBy.length - 1) {
                        builder.append(" OR ");
                    }
                }
                builder.append(")");
            }

            builder.append(" ORDER BY ");
            builder.append(DBConstants.COLUMN.EXPENSE_ON);
            builder.append(" DESC, ");
            builder.append(BaseColumns._ID);
            builder.append(" DESC;");
            cursor = database.rawQuery(builder.toString(), null);
        }

        return cursor;
    }
}
