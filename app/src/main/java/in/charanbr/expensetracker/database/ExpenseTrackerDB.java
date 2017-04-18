package in.charanbr.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Charan.Br on 2/11/2017.
 */

public final class ExpenseTrackerDB extends SQLiteOpenHelper {

    public ExpenseTrackerDB(Context context) {
        super(context, DBConstants.DB_NAME, null, DBConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBConstants.TableName.PAYMENT_MODE + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + DBConstants.COLUMN.NAME + " TEXT,"
                + DBConstants.COLUMN.PAYMENT_MODE_ID + " INTEGER);");

        Cursor countCursor = db.rawQuery("select " + BaseColumns._ID + " from " + DBConstants.TableName.PAYMENT_MODE, null);
        if (null != countCursor && countCursor.getCount() != DBConstants.PAYMENT_MODE_COUNT) {
            db.delete(DBConstants.TableName.PAYMENT_MODE, null, null);
            insertPaymentModes(db);
        }

        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBConstants.TableName.PAYMENT_TYPE + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + DBConstants.COLUMN.NAME + " TEXT,"
                + DBConstants.COLUMN.PAYMENT_TYPE_ID + " TEXT,"
                + DBConstants.COLUMN.CREATED_ON + " TEXT,"
                + DBConstants.COLUMN.IS_ACTIVE + " INTEGER);");
        //-- name + | + payment_mode_id = payment_type_id || Icici1 + | + 1 = Icici1|1
        countCursor = null;
        countCursor = db.rawQuery("select " + BaseColumns._ID + " from " + DBConstants.TableName.PAYMENT_TYPE, null);
        if (null != countCursor && countCursor.getCount() == 0) {
            insertCashPaymentType(db);
        }

        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBConstants.TableName.EXPENSE + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + DBConstants.COLUMN.EXPENSE_DATE + " TEXT,"
                + DBConstants.COLUMN.AMOUNT + " TEXT,"
                + DBConstants.COLUMN.NOTE + " TEXT,"
                + DBConstants.COLUMN.PAYMENT_TYPE_PRI_ID + " INTEGER,"
                + DBConstants.COLUMN.CREATED_ON + " TEXT,"
                + DBConstants.COLUMN.UPDATED_ON + " TEXT);");
    }

    private void insertCashPaymentType(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN.NAME, DBConstants.PAYMENT_MODE.CASH);
        contentValues.put(DBConstants.COLUMN.PAYMENT_TYPE_ID, DBConstants.PAYMENT_MODE.CASH + "|" + DBConstants.PAYMENT_MODE.CASH_ID);
        contentValues.put(DBConstants.COLUMN.CREATED_ON, "DEFAULT");
        contentValues.put(DBConstants.COLUMN.IS_ACTIVE, 1); //-- deemed active while inserting --//
        db.insert(DBConstants.TableName.PAYMENT_TYPE, null, contentValues);
    }

    private void insertPaymentModes(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN.NAME, DBConstants.PAYMENT_MODE.DEBIT_CARD);
        contentValues.put(DBConstants.COLUMN.PAYMENT_MODE_ID, DBConstants.PAYMENT_MODE.DEBIT_CARD_ID);
        db.insert(DBConstants.TableName.PAYMENT_MODE, null, contentValues);

        contentValues = null;
        contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN.NAME, DBConstants.PAYMENT_MODE.CREDIT_CARD);
        contentValues.put(DBConstants.COLUMN.PAYMENT_MODE_ID, DBConstants.PAYMENT_MODE.CREDIT_CARD_ID);
        db.insert(DBConstants.TableName.PAYMENT_MODE, null, contentValues);

        contentValues = null;
        contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN.NAME, DBConstants.PAYMENT_MODE.WALLET);
        contentValues.put(DBConstants.COLUMN.PAYMENT_MODE_ID, DBConstants.PAYMENT_MODE.WALLET_ID);
        db.insert(DBConstants.TableName.PAYMENT_MODE, null, contentValues);

        contentValues = null;
        contentValues = new ContentValues();
        contentValues.put(DBConstants.COLUMN.NAME, DBConstants.PAYMENT_MODE.NET_BANKING);
        contentValues.put(DBConstants.COLUMN.PAYMENT_MODE_ID, DBConstants.PAYMENT_MODE.NET_BANKING_ID);
        db.insert(DBConstants.TableName.PAYMENT_MODE, null, contentValues);

        contentValues = null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}