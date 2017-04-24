package in.charanbr.expensetracker.controller;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import in.charanbr.expensetracker.R;
import in.charanbr.expensetracker.customview.CustomTextView;
import in.charanbr.expensetracker.database.DBConstants;
import in.charanbr.expensetracker.database.DBManager;
import in.charanbr.expensetracker.util.AppConstants;
import in.charanbr.expensetracker.util.AppPref;
import in.charanbr.expensetracker.util.AppUtil;

/**
 * Created by Charan.Br on 2/24/2017.
 */

public final class ExpenseAdapter extends CursorAdapter {

    private Context mContext;

    private String mCurrencySymbol;

    private int mIndexAmount;
    private int mIndexDesc;
    private int mIndexPTPriId;
    private int mIndexPriId;

    public ExpenseAdapter(Context context, Cursor c) {
        super(context, c, false);
        mContext = context;
        mCurrencySymbol = AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY);
        /*numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);*/
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_expense, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.tvAmount = (CustomTextView) view.findViewById(R.id.le_textview_amount);
        holder.tvNote = (CustomTextView) view.findViewById(R.id.le_textview_payment_note);
        holder.tvPaymentTypeName = (CustomTextView) view.findViewById(R.id.le_textview_payment_type);
        view.setTag(holder);

        mIndexAmount = cursor.getColumnIndex(DBConstants.COLUMN.AMOUNT);
        mIndexDesc = cursor.getColumnIndex(DBConstants.COLUMN.NOTE);
        mIndexPTPriId = cursor.getColumnIndex(DBConstants.COLUMN.PAYMENT_TYPE_PRI_ID);
        mIndexPriId = cursor.getColumnIndex(BaseColumns._ID);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (view.getTag() == null) {
            AppUtil.showToast("NULL TAG");

        } else {

            ViewHolder holder = (ViewHolder) view.getTag();

            String amountInString = cursor.getString(mIndexAmount);
            holder.tvAmount.setText(mCurrencySymbol + " " + amountInString);

            holder.tvNote.setText(cursor.getString(mIndexDesc));

            holder.tvPaymentTypeName.setText(mContext.getString(R.string.paid_by) + " " +
                    DBManager.getPaymentTypeName(cursor.getInt(mIndexPTPriId)));
            holder.tvAmount.setTag(cursor.getInt(mIndexPriId));
        }
    }

    class ViewHolder {
        CustomTextView tvNote;
        CustomTextView tvAmount;
        CustomTextView tvPaymentTypeName;
    }

}
