package in.phoenix.myspends.controller;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import in.phoenix.myspends.R;
import in.phoenix.myspends.customview.CustomTextView;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

/**
 * Created by Charan.Br on 11/29/2017.
 */

public final class NewExpenseAdapter extends BaseAdapter {

    private Context mContext;

    private String mCurrencySymbol;
    private ArrayList<NewExpense> mSpends;

    private ExpenseDate mExpenseDate;

    private ExpenseDate mPrevExpenseDate;

    private View.OnClickListener mClickListener;

    private boolean mIsLoading = false;
    private boolean mIsLoadingRequired = true;
    private OnLoadingListener mListener = null;

    public NewExpenseAdapter(Context context, ArrayList<NewExpense> spends, View.OnClickListener clickListener) {
        mContext = context;
        mCurrencySymbol = AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY);
        if (null != spends && spends.size() > 0) {
            mSpends = new ArrayList<>(spends);
        }
        mClickListener = clickListener;
        if (context instanceof OnLoadingListener) {
            mListener = (OnLoadingListener) context;
        }
    }

    @Override
    public int getCount() {
        if (null == mSpends) {
            return 0;
        }

        if (mIsLoadingRequired) {
            return mSpends.size() + 1;
        }

        return mSpends.size();
    }

    @Override
    public NewExpense getItem(int index) {
        return mSpends.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ExpenseHolder holder = null;

        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_expense, parent, false);
            holder = new ExpenseHolder();
            holder.tvAmount = (CustomTextView) view.findViewById(R.id.le_textview_amount);
            holder.tvNote = (CustomTextView) view.findViewById(R.id.le_textview_payment_note);
            holder.tvPaymentTypeName = (CustomTextView) view.findViewById(R.id.le_textview_payment_type);
            holder.tvExpenseDate = (CustomTextView) view.findViewById(R.id.le_textview_date);
            holder.tvMonth = view.findViewById(R.id.le_tv_month);
            holder.vLayoutExpense = view.findViewById(R.id.le_layout_expense);
            holder.vRLayoutExpense = view.findViewById(R.id.le_rlayout_expense);
            holder.vPbLoading = view.findViewById(R.id.le_layout_loading);
            holder.vSpendsEnd = view.findViewById(R.id.le_tv_spends_end);

            view.setTag(holder);

        } else {
            holder = (ExpenseHolder) view.getTag();
        }

        if (position < mSpends.size()) {
            holder.vPbLoading.setVisibility(View.GONE);
            holder.vRLayoutExpense.setVisibility(View.VISIBLE);
            holder.vSpendsEnd.setVisibility(View.GONE);

            NewExpense expense = getItem(position);
            holder.tvAmount.setText(mCurrencySymbol + " " + AppUtil.getStringAmount(String.valueOf(expense.getAmount())));
            holder.tvNote.setText(TextUtils.isEmpty(expense.getNote()) ? AppConstants.BLANK_NOTE_TEMPLATE : expense.getNote());
            holder.tvPaymentTypeName.setText(mContext.getString(R.string.paid_by) + " " + AppUtil.getPaidByForKey(expense.getPaymentTypeKey()));
            holder.tvAmount.setTag(expense.getId());

            if (null == mExpenseDate) {
                mExpenseDate = new ExpenseDate(expense.getExpenseDate());

            } else {
                mExpenseDate.changeDate(expense.getExpenseDate());
            }
            holder.tvExpenseDate.setText(mExpenseDate.getListDate());
            holder.tvExpenseDate.setVisibility(View.VISIBLE);

            if (position == 0) {
                holder.tvMonth.setVisibility(View.VISIBLE);
                holder.tvMonth.setText(AppUtil.getMonth(mExpenseDate.getMonth()));

            } else {
                if (null == mPrevExpenseDate) {
                    mPrevExpenseDate = new ExpenseDate(getItem(position - 1).getExpenseDate());

                } else {
                    mPrevExpenseDate.changeDate(getItem(position - 1).getExpenseDate());
                }

                if ((mExpenseDate.getMonth() != mPrevExpenseDate.getMonth()) || mExpenseDate.getYear() != mPrevExpenseDate.getYear()) {

                    if (mExpenseDate.getYear() != mPrevExpenseDate.getYear()) {
                        holder.tvMonth.setVisibility(View.VISIBLE);
                        holder.tvMonth.setText(AppUtil.getShortMonth(mExpenseDate.getMonth()) + " " + mExpenseDate.getYear());

                    } else {
                        holder.tvMonth.setVisibility(View.VISIBLE);
                        holder.tvMonth.setText(AppUtil.getMonth(mExpenseDate.getMonth()));
                    }

                } else {
                    holder.tvMonth.setVisibility(View.GONE);
                }
            }

            if (null != mClickListener) {
                holder.vLayoutExpense.setTag(position);
                holder.vLayoutExpense.setOnClickListener(mClickListener);
            }

            if (!mIsLoadingRequired) {
                holder.vSpendsEnd.setVisibility(View.VISIBLE);
            }
        } else {
            holder.vPbLoading.setVisibility(View.VISIBLE);
            holder.vRLayoutExpense.setVisibility(View.GONE);
            holder.vSpendsEnd.setVisibility(View.GONE);

            AppLog.d("NewExpenseAdapter", "1");
            if (!mIsLoading && mIsLoadingRequired) {
                AppLog.d("NewExpenseAdapter", "2");
                if (null != mListener) {
                    AppLog.d("NewExpenseAdapter", "3");
                    mIsLoading = true;
                    mListener.onLoading(getItem(mSpends.size() - 1).getExpenseDate());
                }
            }
        }

        return view;
    }

    public void setData(ArrayList<NewExpense> spends) {
        if (null != spends) {
            if (null == mSpends) {
                mSpends = new ArrayList<>(spends);

            } else {
                mSpends.clear();
                mSpends.addAll(spends);
            }
        }
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.mIsLoading = isLoading;
    }

    public void setIsLoadingRequired(boolean isLoadingRequired) {
        this.mIsLoadingRequired = isLoadingRequired;
    }

    public void addSpends(ArrayList<NewExpense> spends) {
        if (null ==  mSpends) {
            mSpends = new ArrayList<>(spends);

        } else {
            mSpends.addAll(spends);
        }
        mIsLoading = false;
        notifyDataSetChanged();
    }

    public Float calculateTotal() {
        Float totalAmount = 0f;
        if (null != mSpends) {
            for (int index = 0; index < mSpends.size(); index++) {
                totalAmount += mSpends.get(index).getAmount();
            }
        }
        return totalAmount;
    }

    class ExpenseHolder {
        CustomTextView tvNote;
        CustomTextView tvAmount;
        CustomTextView tvPaymentTypeName;
        CustomTextView tvExpenseDate;
        CustomTextView tvMonth;
        View vLayoutExpense;
        View vRLayoutExpense;
        View vPbLoading;
        View vSpendsEnd;
    }

    public interface OnLoadingListener {
        void onLoading(long lastExpenseDate);
    }
}
