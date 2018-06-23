package in.phoenix.myspends.model;

import java.util.ArrayList;

/**
 * Created by Charan.Br on 4/9/2018.
 */

public final class CategoryChartData {

    private int categoryId;

    private String categoryName;

    private ArrayList<NewExpense> expenses;

    private Float categoryTotal;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<NewExpense> getExpenses() {
        return expenses;
    }

    public void setExpenses(ArrayList<NewExpense> expenses) {
        this.expenses = expenses;
    }

    public Float getCategoryTotal() {
        return categoryTotal;
    }

    public void setCategoryTotal(Float categoryTotal) {
        this.categoryTotal = categoryTotal;
    }

}
