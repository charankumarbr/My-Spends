package in.phoenix.myspends.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Charan.Br on 4/9/2018.
 */

public final class CategoryChartData extends ExpandableGroup<NewExpense> {

    private int categoryId;

    private String categoryName;

    private ArrayList<NewExpense> expenses;

    private Float categoryTotal;

    public CategoryChartData(String title, List<NewExpense> items) {
        super(title, items);
    }

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
