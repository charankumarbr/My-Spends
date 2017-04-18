package in.charanbr.expensetracker.controller;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.Switch;

import in.charanbr.expensetracker.R;
import in.charanbr.expensetracker.customview.CustomTextView;
import in.charanbr.expensetracker.database.DBConstants;
import in.charanbr.expensetracker.database.DBManager;
import in.charanbr.expensetracker.util.AppLog;
import in.charanbr.expensetracker.util.AppUtil;

/**
 * Created by Charan.Br on 4/7/2017.
 */

public class PaymentTypeAdapter extends CursorAdapter {

    private Context mContext;

    private int mIndexPriId;
    private int mIndexName;
    private int mIndexIsActive;
    private int mIndexCreatedOn;

    private OnStatusChangedListener mListener;

    public PaymentTypeAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        mContext = context;
        mListener = (OnStatusChangedListener) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View paymentTypeView = LayoutInflater.from(context).inflate(R.layout.layout_payment_type, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.tvPaymentTypeName = (CustomTextView) paymentTypeView.findViewById(R.id.lpt_textview_ptype_name);
        holder.swToggleActive = (Switch) paymentTypeView.findViewById(R.id.lpt_switch_active);
        paymentTypeView.setTag(holder);

        mIndexCreatedOn = cursor.getColumnIndex(DBConstants.COLUMN.CREATED_ON);
        mIndexIsActive = cursor.getColumnIndex(DBConstants.COLUMN.IS_ACTIVE);
        mIndexName = cursor.getColumnIndex(DBConstants.COLUMN.NAME);
        mIndexPriId = cursor.getColumnIndex(BaseColumns._ID);

        return paymentTypeView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (view.getTag() == null) {
            AppUtil.showToast("NULL TAG");

        } else {
            ViewHolder holder = (ViewHolder) view.getTag();

            String createdOn = cursor.getString(mIndexCreatedOn);
            AppLog.d("Created On", createdOn);
            int isActive = cursor.getInt(mIndexIsActive);
            if (createdOn.equals("DEFAULT")) {
                holder.swToggleActive.setChecked(true);
                holder.swToggleActive.setEnabled(false);

            } else {
                holder.swToggleActive.setEnabled(true);
                holder.swToggleActive.setTag(cursor.getInt(mIndexPriId));
                holder.swToggleActive.setChecked((cursor.getInt(mIndexIsActive) == 1) ? true : false);
                holder.swToggleActive.setOnCheckedChangeListener(togglePaymentType);
            }

            holder.tvPaymentTypeName.setText(cursor.getString(mIndexName));
        }

    }

    private CompoundButton.OnCheckedChangeListener togglePaymentType = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d("onCheckedChange", buttonView.getText().toString() + isChecked);
            int primaryKey = (int) buttonView.getTag();
            DBManager.togglePaymentType(primaryKey, isChecked);
            if (null != mListener) {
                mListener.onStatusChanged();
            }
        }
    };

    class ViewHolder {
        CustomTextView tvPaymentTypeName;
        Switch swToggleActive;
    }

    public interface OnStatusChangedListener {
        void onStatusChanged();
    }
}
