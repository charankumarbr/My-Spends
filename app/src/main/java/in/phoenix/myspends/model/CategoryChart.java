package in.phoenix.myspends.model;

import java.util.ArrayList;

/**
 * Created by Charan.Br on 4/9/2018.
 */

public final class CategoryChart {

    private ArrayList<CategoryChartData> categoryChartData;

    private Float grandTotal;

    public ArrayList<CategoryChartData> getCategoryChartData() {
        return categoryChartData;
    }

    public void setCategoryChartData(ArrayList<CategoryChartData> categoryChartData) {
        this.categoryChartData = categoryChartData;
    }

    public Float getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Float grandTotal) {
        this.grandTotal = grandTotal;
    }
}
