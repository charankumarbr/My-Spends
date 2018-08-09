package in.phoenix.myspends.controller;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import in.phoenix.myspends.R;
import in.phoenix.myspends.model.CategoryChart;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

public final class ReportAdapter extends ExpandableRecyclerViewAdapter<CategoryViewHolder, ExpenseViewHolder> {

    private LayoutInflater inflater;
    private CategoryChart categoryChart;
    private int pixelHundredPercent;
    private String mCurrencySymbol;

    public ReportAdapter(Context context, CategoryChart groups) {
        super(groups.getCategoryChartData());
        inflater = LayoutInflater.from(context);
        this.categoryChart = groups;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp20 = AppUtil.dpToPx(20);
        AppLog.d("ReportAdapter", "Width:" + displayMetrics.widthPixels + "::20 dp:" + dp20);
        pixelHundredPercent = displayMetrics.widthPixels - dp20;
        mCurrencySymbol = AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY) + " ";
    }

    @Override
    public CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_category, parent, false);
        return new CategoryViewHolder(view, pixelHundredPercent, mCurrencySymbol);
    }

    @Override
    public ExpenseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_report_expense, parent, false);
        return new ExpenseViewHolder(view, mCurrencySymbol);
    }

    @Override
    public void onBindChildViewHolder(ExpenseViewHolder holder, int flatPosition, ExpandableGroup
            group, int childIndex) {
        final NewExpense expense = (NewExpense) group.getItems().get(childIndex);
        holder.onBindExpense(expense);

    }

    @Override
    public void onBindGroupViewHolder(CategoryViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setCategoryData(flatPosition, group, categoryChart.getGrandTotal());
    }

    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }
}
