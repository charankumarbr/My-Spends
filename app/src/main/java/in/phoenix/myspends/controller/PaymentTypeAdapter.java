package in.phoenix.myspends.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;

import in.phoenix.myspends.R;
import in.phoenix.myspends.customview.CustomTextView;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 4/7/2017.
 */

public class PaymentTypeAdapter extends BaseAdapter {

    private final Context mContext;

    private final OnStatusChangedListener mListener;

    private ArrayList<PaymentType> mPaymentTypes;

    public PaymentTypeAdapter(Context context, ArrayList<PaymentType> paymentTypes) {
        mContext = context;
        mListener = (OnStatusChangedListener) context;
        if (null != paymentTypes) {
            mPaymentTypes = new ArrayList<>(paymentTypes);
        }
    }

    private final CompoundButton.OnCheckedChangeListener togglePaymentType = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            AppLog.d("onCheckedChange", buttonView.getText().toString() + isChecked);
            if (AppUtil.isConnected()) {
                if (AppUtil.isUserLoggedIn()) {
                    if (null != mListener) {
                        String primaryKey = (String) buttonView.getTag();
                        //DBManager.togglePaymentType(primaryKey, isChecked);
                        mListener.onStatusChanged(primaryKey, isChecked);
                    }
                } else {
                    AppUtil.showToast("Unable to recognise!");
                    buttonView.toggle();
                }
            } else {
                AppUtil.showToast(R.string.no_internet);
                buttonView.toggle();
            }
        }
    };

    @Override
    public int getCount() {
        if (null == mPaymentTypes) {
            return 0;
        }

        AppLog.d("PaymentTypeAdapter", "getCount:" + mPaymentTypes.size());
        return mPaymentTypes.size();
    }

    @Override
    public PaymentType getItem(int position) {
        return mPaymentTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;

        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_payment_type, parent, false);
            holder = new ViewHolder();
            holder.tvPaymentTypeName = view.findViewById(R.id.lpt_textview_ptype_name);
            holder.swToggleActive = view.findViewById(R.id.lpt_switch_active);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        PaymentType paymentType = getItem(position);

        long createdOn = paymentType.getCreatedOn();
        AppLog.d("PaymentTypeAdapter", "Created On:" + createdOn);
        boolean isActive = paymentType.isActive();
        if (createdOn == 0) {
            //-- cash payment type --//
            holder.swToggleActive.setChecked(true);
            holder.swToggleActive.setEnabled(false);

        } else {
            holder.swToggleActive.setEnabled(true);
            holder.swToggleActive.setTag(paymentType.getKey());
            holder.swToggleActive.setOnCheckedChangeListener(null);
            holder.swToggleActive.setChecked(isActive);
            holder.swToggleActive.setOnCheckedChangeListener(togglePaymentType);
            //holder.swToggleActive.setOnClickListener();
        }

        holder.tvPaymentTypeName.setText(paymentType.getName());

        return view;
    }

    public void setData(ArrayList<PaymentType> spends) {
        if (null != spends && spends.size() > 0) {
            if (null == mPaymentTypes) {
                mPaymentTypes = new ArrayList<>();

            } else {
                mPaymentTypes.clear();
            }
            mPaymentTypes.addAll(spends);
            notifyDataSetChanged();
        }
    }

    /*private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.lpt_switch_active) {

            }
        }
    };*/

    class ViewHolder {
        CustomTextView tvPaymentTypeName;
        Switch swToggleActive;
    }

    public interface OnStatusChangedListener {
        void onStatusChanged(String paymentTypeKey, boolean isChecked);
    }
}
