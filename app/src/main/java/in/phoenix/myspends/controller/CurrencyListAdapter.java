package in.phoenix.myspends.controller;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.model.Currency;
import in.phoenix.myspends.ui.activity.AppSetupActivity;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 4/10/2017.
 */

public final class CurrencyListAdapter extends BaseAdapter {

    private final AppSetupActivity mContext;

    private ArrayList<Currency> mCurrencies;

    private int mSelectedPosition = -1;
    private Currency mSelectedCurrency = null;

    /*@Inject
    public CurrencyListAdapter(AppSetupActivity context) {
        mContext = context;
    }*/

    @Inject
    public CurrencyListAdapter(AppSetupActivity context, ArrayList<Currency> currencies) {
        mContext = context;
        mCurrencies = currencies;
    }

    public void setCurrencies(ArrayList<Currency> currencies) {
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
            holder.cTvCurrencyCode = convertView.findViewById(R.id.lc_ctextview_code);
            holder.cTvCurrencyName = convertView.findViewById(R.id.lc_ctextview_name);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Currency currency = getItem(position);
        if (mSelectedCurrency != null &&
                mSelectedCurrency.getCurrencySymbol().equals(currency.getCurrencySymbol()) &&
                mSelectedCurrency.getCurrencyName().equals(currency.getCurrencyName()) &&
                mSelectedCurrency.getCurrencyCode().equals(currency.getCurrencyCode())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setBackgroundColor(MySpends.APP_CONTEXT.getResources().getColor(R.color.colorAccent, null));
                holder.cTvCurrencyName.setTextColor(mContext.getResources().getColor(android.R.color.white, null));
                holder.cTvCurrencyCode.setTextColor(mContext.getResources().getColor(android.R.color.white, null));

            } else {
                convertView.setBackgroundColor(MySpends.APP_CONTEXT.getResources().getColor(R.color.colorAccent));
                holder.cTvCurrencyName.setTextColor(mContext.getResources().getColor(android.R.color.white));
                holder.cTvCurrencyCode.setTextColor(mContext.getResources().getColor(android.R.color.white));
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                convertView.setBackgroundColor(MySpends.APP_CONTEXT.getResources().getColor(android.R.color.white, null));
                /*holder.cTvCurrencyName.setTextColor(mContext.getResources().getColor(R.color.primary_text, null));
                holder.cTvCurrencyCode.setTextColor(mContext.getResources().getColor(R.color.primary_text, null));*/
                holder.cTvCurrencyName.setTextColor(AppUtil.getPrimaryTextColor());
                holder.cTvCurrencyCode.setTextColor(AppUtil.getPrimaryTextColor());

            } else {
                convertView.setBackgroundColor(MySpends.APP_CONTEXT.getResources().getColor(android.R.color.white));
                /*holder.cTvCurrencyName.setTextColor(mContext.getResources().getColor(R.color.primary_text));
                holder.cTvCurrencyCode.setTextColor(mContext.getResources().getColor(R.color.primary_text));*/
                holder.cTvCurrencyName.setTextColor(AppUtil.getPrimaryTextColor());
                holder.cTvCurrencyCode.setTextColor(AppUtil.getPrimaryTextColor());
            }
        }

        holder.cTvCurrencyCode.setText(currency.getCurrencyCode());
        holder.cTvCurrencyName.setText(currency.getCurrencyName() + " (" +
                currency.getCurrencySymbol() + ")");

        return convertView;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        mSelectedCurrency = mCurrencies.get(position);
        notifyDataSetChanged();
    }

    public void setData(ArrayList<Currency> collect) {
        mCurrencies = collect;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView cTvCurrencyCode;
        TextView cTvCurrencyName;
    }
}
