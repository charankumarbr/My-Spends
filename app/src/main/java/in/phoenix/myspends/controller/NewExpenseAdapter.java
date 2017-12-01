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

    public NewExpenseAdapter(Context context, ArrayList<NewExpense> spends, View.OnClickListener clickListener) {
        mContext = context;
        mCurrencySymbol = AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY);
        if (null != spends && spends.size() > 0) {
            mSpends = new ArrayList<>(spends);
        }
        mClickListener = clickListener;
    }

    @Override
    public int getCount() {
        if (null == mSpends) {
            return 0;
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

            view.setTag(holder);

        } else {
            holder = (ExpenseHolder) view.getTag();
        }

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

            if (mExpenseDate.getMonth() != mPrevExpenseDate.getMonth()) {
                holder.tvMonth.setVisibility(View.VISIBLE);
                holder.tvMonth.setText(AppUtil.getMonth(mExpenseDate.getMonth()));

            } else {
                holder.tvMonth.setVisibility(View.GONE);
            }
        }

        if (null != mClickListener) {
            holder.vLayoutExpense.setTag(position);
            holder.vLayoutExpense.setOnClickListener(mClickListener);
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

    class ExpenseHolder {
        CustomTextView tvNote;
        CustomTextView tvAmount;
        CustomTextView tvPaymentTypeName;
        CustomTextView tvExpenseDate;
        CustomTextView tvMonth;
        View vLayoutExpense;
    }
}
