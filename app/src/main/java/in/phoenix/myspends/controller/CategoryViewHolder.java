package in.phoenix.myspends.controller;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import in.phoenix.myspends.R;
import in.phoenix.myspends.model.CategoryChartData;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;

public final class CategoryViewHolder extends GroupViewHolder {

    private TextView tvTotal;
    private TextView tvGrandTotal;

    private TextView tvCategoryName;
    private TextView tvCategoryTotal;
    private View vSpendPercentage;
    private TextView tvCategoryPercentage;

    private String mCurrencySymbol;
    private Float mPixel1Percent = 0F;

    public CategoryViewHolder(View groupView, int pixelHundredPercent, String currencySymbol) {
        super(groupView);
        tvCategoryName = groupView.findViewById(R.id.lc_tv_category_name);
        tvCategoryTotal = groupView.findViewById(R.id.lc_tv_category_total);
        tvCategoryPercentage = groupView.findViewById(R.id.lc_tv_category_percentage);
        vSpendPercentage = groupView.findViewById(R.id.lc_v_percent);

        tvTotal = groupView.findViewById(R.id.lc_tv_month);
        tvGrandTotal = groupView.findViewById(R.id.lc_tv_grand_total);

        mCurrencySymbol = currencySymbol;
        mPixel1Percent = pixelHundredPercent / 100f;
    }

    public void setCategoryData(int flatPosition, ExpandableGroup categoryData, Float grandTotal) {

        int visibility;
        if (flatPosition == 0) {
            visibility = View.VISIBLE;
            tvGrandTotal.setText(mCurrencySymbol + AppUtil.getStringAmount(String.valueOf(grandTotal)));

        } else {
            visibility = View.GONE;
        }
        tvTotal.setVisibility(visibility);
        tvGrandTotal.setVisibility(visibility);

        tvCategoryName.setText(categoryData.getTitle());
        if (categoryData instanceof CategoryChartData) {
            CategoryChartData categoryCData = (CategoryChartData) categoryData;
            tvCategoryTotal.setText(mCurrencySymbol + AppUtil.getStringAmount(String.valueOf(categoryCData.getCategoryTotal())));

            Float categoryPercentage = getCategoryPercentage(categoryCData.getCategoryTotal(), grandTotal);
            tvCategoryPercentage.setText(AppUtil.getStringAmount(String.valueOf(categoryPercentage)) + " % of Total Spends");
            int percentPixel = getSpendsPercent(categoryPercentage);
            AppLog.d("CategoryViewHolder", "Percent Pixel:" + percentPixel + ":: Name:" + categoryData.getTitle());
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) vSpendPercentage.getLayoutParams();
            params.width = percentPixel;
            vSpendPercentage.setLayoutParams(params);
        }
    }

    private Float getCategoryPercentage(Float categoryTotal, Float grandTotal) {
        return (categoryTotal / grandTotal) * 100;
    }

    private int getSpendsPercent(Float categoryPercentage) {
        Float percentWidth = mPixel1Percent * categoryPercentage;
        if (percentWidth < 1f) {
            return 1;
        }
        return (int) (percentWidth + 0.5);
    }
}
