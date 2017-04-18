package in.charanbr.expensetracker.controller;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import in.charanbr.expensetracker.ExpenseTracker;
import in.charanbr.expensetracker.R;
import in.charanbr.expensetracker.customview.CustomTextView;
import in.charanbr.expensetracker.model.Currency;

/**
 * Created by Charan.Br on 4/10/2017.
 */

public final class CurrencyListAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<Currency> mCurrencies;

    private int mSelectedPosition = -1;

    public CurrencyListAdapter(Context context, ArrayList<Currency> currencies) {
        mContext = context;
        mCurrencies = currencies;
    }

    @Override
    public int getCount() {
        if (null != mCurrencies) {
            return mCurrencies.size();
        }
        return 0;
    }

    @Override
    public Currency getItem(int position) {
        return mCurrencies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_currency, parent, false);
            holder = new ViewHolder();
            holder.cTvCurrencyCode = (CustomTextView) convertView.findViewById(R.id.lc_ctextview_code);
            holder.cTvCurrencyName = (CustomTextView) convertView.findViewById(R.id.lc_ctextview_name);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mSelectedPosition == position) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setBackgroundColor(ExpenseTracker.APP_CONTEXT.getResources().getColor(R.color.colorAccent, null));
            } else {
                convertView.setBackgroundColor(ExpenseTracker.APP_CONTEXT.getResources().getColor(R.color.colorAccent));
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setBackgroundColor(ExpenseTracker.APP_CONTEXT.getResources().getColor(android.R.color.white, null));
            } else {
                convertView.setBackgroundColor(ExpenseTracker.APP_CONTEXT.getResources().getColor(android.R.color.white));
            }
        }

        Currency currency = getItem(position);
        holder.cTvCurrencyCode.setText(currency.getCurrencyCode());
        holder.cTvCurrencyName.setText(currency.getCurrencyName() + " (" +
                currency.getCurrencySymbol() + ")");

        return convertView;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        CustomTextView cTvCurrencyCode;
        CustomTextView cTvCurrencyName;
    }
}
