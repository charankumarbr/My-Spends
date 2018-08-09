package in.phoenix.myspends.controller;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.model.CategoryChart;
import in.phoenix.myspends.model.CategoryChartData;
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

    private boolean mIsSpendsChartEnabled = false;
    private int mSpendsChartCount = 0;
    private CategoryChart mCategoryChart;
    private Float mGrandTotal;
    private Float mPixel1Percent = 0F;

    /*private Animation mAnimUp4mBottom;
    private Animation mAnimDown4mTop;*/
    private int mLastPos = -1;

    public NewExpenseAdapter(Context context, ArrayList<NewExpense> spends, View.OnClickListener clickListener) {
        mContext = context;
        mCurrencySymbol = AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY) + " ";
        if (null != spends && spends.size() > 0) {
            mSpends = new ArrayList<>(spends);
        }
        mClickListener = clickListener;
        if (context instanceof OnLoadingListener) {
            mListener = (OnLoadingListener) context;
        }
        /*mAnimUp4mBottom = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
        mAnimDown4mTop = AnimationUtils.loadAnimation(context, R.anim.down_from_top);
        mAnimUp4mBottom.setDuration(200);
        mAnimDown4mTop.setDuration(200);*/
    }

    @Override
    public int getCount() {
        if (null == mSpends) {
            return 0;
        }

        if (mIsLoadingRequired) {
            return mSpends.size() + 1;
        }

        if (mIsSpendsChartEnabled) {
            return mSpends.size() + mSpendsChartCount;
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
            holder.tvAmount = view.findViewById(R.id.le_textview_amount);
            holder.tvNote = view.findViewById(R.id.le_textview_payment_note);
            holder.tvPaymentTypeName = view.findViewById(R.id.le_textview_payment_type);
            holder.tvExpenseDate = view.findViewById(R.id.le_textview_date);
            holder.tvExpCategoryName = view.findViewById(R.id.le_textview_category);
            holder.tvMonth = view.findViewById(R.id.le_tv_month);
            holder.vLayoutExpense = view.findViewById(R.id.le_layout_expense);
            holder.vRLayoutExpense = view.findViewById(R.id.le_rlayout_expense);
            holder.vPbLoading = view.findViewById(R.id.le_layout_loading);
            holder.vSpendsEnd = view.findViewById(R.id.le_tv_spends_end);

            holder.vSpends = view.findViewById(R.id.le_layout_chart);
            holder.tvCategoryName = view.findViewById(R.id.le_tv_category_name);
            holder.tvCategoryTotal = view.findViewById(R.id.le_tv_category_total);
            holder.tvCategoryPercentage = view.findViewById(R.id.le_tv_category_percentage);
            holder.vSpendPercentage = view.findViewById(R.id.le_v_percent);
            holder.tvGrandTotal = view.findViewById(R.id.le_tv_grand_total);

            view.setTag(holder);

        } else {
            holder = (ExpenseHolder) view.getTag();
        }

        if (position < mSpends.size()) {
            holder.vPbLoading.setVisibility(View.GONE);
            holder.vRLayoutExpense.setVisibility(View.VISIBLE);
            holder.vLayoutExpense.setVisibility(View.VISIBLE);
            holder.vSpendsEnd.setVisibility(View.GONE);
            holder.vSpends.setVisibility(View.GONE);
            holder.tvGrandTotal.setVisibility(View.GONE);

            NewExpense expense = getItem(position);
            holder.tvAmount.setText(mCurrencySymbol + AppUtil.getStringAmount(String.valueOf(expense.getAmount())));
            holder.tvNote.setText(TextUtils.isEmpty(expense.getNote()) ? AppConstants.BLANK_NOTE_TEMPLATE : expense.getNote());
            holder.tvPaymentTypeName.setText(/*mContext.getString(R.string.paid_by_) + " " +*/ AppUtil.getPaidByForKey(expense.getPaymentTypeKey()));
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

            holder.tvExpCategoryName.setText(expense.getCategoryId() > 0 ? MySpends.getCategoryName
                    (expense.getCategoryId()) : AppConstants.BLANK_NOTE_TEMPLATE);

            if (null != mClickListener) {
                holder.vLayoutExpense.setTag(position);
                holder.vLayoutExpense.setOnClickListener(mClickListener);
            }

            if (!mIsLoadingRequired && position == (getExpensesSize() - 1)) {
                holder.vSpendsEnd.setVisibility(View.VISIBLE);
            }
        } else {
            holder.vLayoutExpense.setVisibility(View.GONE);
            holder.vSpendsEnd.setVisibility(View.GONE);

            AppLog.d("NewExpenseAdapter", "1");
            if (!mIsLoading && mIsLoadingRequired) {
                //-- loading view --//
                holder.vSpends.setVisibility(View.GONE);
                holder.vRLayoutExpense.setVisibility(View.GONE);
                holder.vPbLoading.setVisibility(View.VISIBLE);
                holder.tvGrandTotal.setVisibility(View.GONE);

                AppLog.d("NewExpenseAdapter", "2");
                if (null != mListener) {
                    AppLog.d("NewExpenseAdapter", "3");
                    mIsLoading = true;
                    mListener.onLoading(getItem(mSpends.size() - 1).getExpenseDate());
                }

            } else if (!mIsLoading && !mIsLoadingRequired && mIsSpendsChartEnabled) {

                //-- spends chart view --//
                holder.vSpends.setVisibility(View.VISIBLE);
                holder.vPbLoading.setVisibility(View.GONE);
                holder.vRLayoutExpense.setVisibility(View.VISIBLE);

                int chartPos = position - getExpensesSize();
                AppLog.d("NewExpenseAdapter", "ChartPos:" + chartPos + " ::Expense Size:" + getExpensesSize() + " ::Pos:" + position);
                if (chartPos == 0) {
                    holder.tvMonth.setVisibility(View.VISIBLE);
                    holder.tvMonth.setText(R.string.spends_chart);
                    holder.tvGrandTotal.setVisibility(View.VISIBLE);
                    holder.tvGrandTotal.setText(mCurrencySymbol + AppUtil.getStringAmount(String.valueOf(mCategoryChart.getGrandTotal())));

                } else {
                    holder.tvMonth.setVisibility(View.GONE);
                    holder.tvGrandTotal.setVisibility(View.GONE);
                }

                CategoryChartData chartData = mCategoryChart.getCategoryChartData().get(chartPos);
                holder.tvCategoryName.setText(chartData.getCategoryName().equals(AppConstants.BLANK_NOTE_TEMPLATE) ? "Uncategorised" : chartData.getCategoryName());
                holder.tvCategoryTotal.setText(mCurrencySymbol + AppUtil.getStringAmount(String.valueOf(chartData.getCategoryTotal())));

                Float categoryPercentage = getCategoryPercentage(chartData.getCategoryTotal());
                holder.tvCategoryPercentage.setText(AppUtil.getStringAmount(String.valueOf(categoryPercentage)) + " % of Total Spends");
                int percentPixel = getSpendsPercent(categoryPercentage);
                AppLog.d("NewExpenseAdapter", "Percent Pixel:" + percentPixel + ":: Name:" + chartData.getCategoryName());
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.vSpendPercentage.getLayoutParams();
                params.width = percentPixel;
                holder.vSpendPercentage.setLayoutParams(params);
            }
        }

        //-- anim view from bottom to up or top to down based on ListView scroll direction --//
        /*if (position > mLastPos) {
            view.startAnimation(position > mLastPos ? mAnimUp4mBottom : mAnimDown4mTop);
        }
        mLastPos = position;*/

        return view;
    }

    private int getSpendsPercent(Float categoryPercentage) {
        Float percentWidth = mPixel1Percent * categoryPercentage;
        if (percentWidth < 1f) {
            return 1;
        }
        return (int) (percentWidth + 0.5);
    }

    private Float getCategoryPercentage(Float categoryTotal) {
        return (categoryTotal / mGrandTotal) * 100;
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
        if (null == mSpends) {
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
        TextView tvNote;
        TextView tvAmount;
        TextView tvPaymentTypeName;
        TextView tvExpenseDate;
        TextView tvExpCategoryName;
        TextView tvMonth;
        View vLayoutExpense;
        View vRLayoutExpense;
        View vPbLoading;
        View vSpendsEnd;

        View vSpends;
        TextView tvCategoryName;
        TextView tvCategoryTotal;
        View vSpendPercentage;
        TextView tvCategoryPercentage;
        TextView tvGrandTotal;
    }

    public int getExpensesSize() {
        if (null != mSpends) {
            return mSpends.size();
        }

        return 0;
    }

    public void setSpendsChartData(CategoryChart categoryChart, int pixelHundredPercent) {
        if (null != categoryChart && null != categoryChart.getCategoryChartData()) {
            mSpendsChartCount = categoryChart.getCategoryChartData().size();
            mGrandTotal = categoryChart.getGrandTotal();
            mCategoryChart = categoryChart;
            mPixel1Percent = pixelHundredPercent / 100f;
            AppLog.d("NewExpenseAdapter", "1% pixel:" + mPixel1Percent);
            mIsSpendsChartEnabled = true;

            notifyDataSetChanged();
        }
    }

    public interface OnLoadingListener {
        void onLoading(long lastExpenseDate);
    }

    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }
}
