package in.charanbr.expensetracker.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Arrays;

import in.charanbr.expensetracker.R;
import in.charanbr.expensetracker.controller.ExpenseAdapter;
import in.charanbr.expensetracker.customview.CustomTextView;
import in.charanbr.expensetracker.database.DBManager;
import in.charanbr.expensetracker.model.Expense;
import in.charanbr.expensetracker.model.ExpenseDate;
import in.charanbr.expensetracker.ui.fragment.DatePickerFragment;
import in.charanbr.expensetracker.ui.fragment.PaidByFragment;
import in.charanbr.expensetracker.util.AppConstants;
import in.charanbr.expensetracker.util.AppLog;
import in.charanbr.expensetracker.util.AppUtil;

public class ReportActivity extends BaseActivity implements View.OnClickListener, DatePickerFragment.OnDatePickedListener, PaidByFragment.OnPaidBySelectedListener {

    private ExpenseDate mFromDate = null;
    private ExpenseDate mToDate = null;
    private Integer[] mPaidBy = null;

    private ArrayList<Expense> mExpenseReport = null;

    private ProgressBar mPbLoading = null;

    private ListView mLvExpenses = null;

    private CustomTextView mCTvMsg = null;

    private ExpenseAdapter mExpenseAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        init();
    }

    private void init() {
        initLayout();
        Toolbar toolbar = (Toolbar) findViewById(R.id.ar_in_toolbar);
        toolbar.setTitle("Reports");
        setSupportActionBar(toolbar);

        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        findViewById(R.id.ar_ctextview_date).setOnClickListener(this);
        findViewById(R.id.ar_ctextview_paid_by).setOnClickListener(this);
        mPbLoading = (ProgressBar) findViewById(R.id.ar_progressbar_loading);

        mLvExpenses = (ListView) findViewById(R.id.ar_listview_expenses);
        mLvExpenses.setOnItemClickListener(itemClickListener);

        mCTvMsg = (CustomTextView) findViewById(R.id.ar_ctextview_msg);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ar_ctextview_date) {
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mFromDate, mToDate);
            datePickerFragment.show(getSupportFragmentManager(), "DatePickerFragment");

        } else if (v.getId() == R.id.ar_ctextview_paid_by) {
            PaidByFragment paidByFragment = PaidByFragment.newInstance(mPaidBy);
            paidByFragment.show(getSupportFragmentManager(), "PaidByFragment");
        }
    }

    @Override
    public void onDatePicked(ExpenseDate fromDate, ExpenseDate toDate) {
        if (null != fromDate && null != toDate) {
            if ((null == mFromDate || null == mToDate) ||
                    (!mFromDate.toString().equals(fromDate.toString()) || !mToDate.toString().equals(toDate.toString()))) {
                mFromDate = fromDate;
                mToDate = toDate;
                getExpenses();
            }
        }
    }

    @Override
    public void onPaidBySelected(Integer[] paidById) {
        if (null == paidById && null == mPaidBy) {
            //-- nothing to do --//

        } else {
            if (null != mPaidBy && Arrays.deepEquals(mPaidBy, paidById)) {
                //-- same paid by selection, nothing to do --//
            }
            mPaidBy = paidById;
            getExpenses();
        }
    }

    private void getExpenses() {
        if (null != mFromDate || null != mToDate) {
            mPbLoading.setVisibility(View.VISIBLE);
            mCTvMsg.setVisibility(View.GONE);
            Cursor cursor = DBManager.getExpenses(mFromDate, mToDate, mPaidBy);
            mPbLoading.setVisibility(View.GONE);
            if (null != cursor && cursor.getCount() > 0) {
                mLvExpenses.setVisibility(View.VISIBLE);
                if (null == mLvExpenses.getAdapter()) {
                    mExpenseAdapter = new ExpenseAdapter(ReportActivity.this, cursor, true);
                    mLvExpenses.setAdapter(mExpenseAdapter);

                } else {
                    mExpenseAdapter.swapCursor(cursor);
                }

            } else {
                mLvExpenses.setVisibility(View.GONE);
                mCTvMsg.setText("No expenses found in the filter applied.");
                mCTvMsg.setVisibility(View.VISIBLE);
            }
        } else {
            AppUtil.showSnackbar(mViewComplete, "Please select the dates...");
        }
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int expensePrimaryKey = (int) view.findViewById(R.id.le_textview_amount).getTag();
            Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
            animation1.setDuration(300);
            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent viewExpenseIntent = new Intent(ReportActivity.this, ViewExpenseActivity.class);
                    viewExpenseIntent.putExtra(AppConstants.Bundle.EXPENSE_PRIMARY_KEY, expensePrimaryKey);
                    startActivityForResult(viewExpenseIntent, AppConstants.VIEW_EXPENSE_CODE);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation1);
        }
    };
}
