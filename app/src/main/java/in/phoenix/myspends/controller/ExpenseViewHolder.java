package in.phoenix.myspends.controller;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppUtil;

public final class ExpenseViewHolder extends ChildViewHolder {

    private TextView tvNote;
    private TextView tvAmount;
    private TextView tvPaymentTypeName;
    private TextView tvExpenseDate;
    private TextView tvExpCategoryName;

    private String mCurrencySymbol;

    private ExpenseDate mExpenseDate;

    public ExpenseViewHolder(View view, String currencySymbol) {
        super(view);
        tvAmount = view.findViewById(R.id.lre_textview_amount);
        tvNote = view.findViewById(R.id.lre_textview_payment_note);
        tvPaymentTypeName = view.findViewById(R.id.lre_textview_payment_type);
        tvExpenseDate = view.findViewById(R.id.lre_textview_date);
        tvExpCategoryName = view.findViewById(R.id.lre_textview_category);

        mCurrencySymbol = currencySymbol;
    }

    public void onBindExpense(NewExpense expense) {

        tvAmount.setText(mCurrencySymbol + AppUtil.getStringAmount(String.valueOf(expense.getAmount())));
        tvNote.setText(TextUtils.isEmpty(expense.getNote()) ? AppConstants.BLANK_NOTE_TEMPLATE : expense.getNote());
        tvPaymentTypeName.setText(/*mContext.getString(R.string.paid_by_) + " " +*/ AppUtil.getPaidByForKey(expense.getPaymentTypeKey()));
        tvAmount.setTag(expense.getId());

        if (null == mExpenseDate) {
            mExpenseDate = new ExpenseDate(expense.getExpenseDate());

        } else {
            mExpenseDate.changeDate(expense.getExpenseDate());
        }
        tvExpenseDate.setText(mExpenseDate.getListDate());
        tvExpenseDate.setVisibility(View.VISIBLE);

        tvExpCategoryName.setText(expense.getCategoryId() > 0 ? MySpends.getCategoryName
                (expense.getCategoryId()) : AppConstants.BLANK_NOTE_TEMPLATE);

    }
}
