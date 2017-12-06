package in.phoenix.myspends.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.ExpenseAdapter;
import in.phoenix.myspends.controller.NewExpenseAdapter;
import in.phoenix.myspends.customview.CustomTextView;
import in.phoenix.myspends.database.DBManager;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.Expense;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.parser.SpendsParser;
import in.phoenix.myspends.ui.fragment.DatePickerFragment;
import in.phoenix.myspends.ui.fragment.PaidByFragment;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;

public class ReportActivity extends BaseActivity implements View.OnClickListener, DatePickerFragment.OnDatePickedListener, PaidByFragment.OnPaidBySelectedListener, SpendsParser.SpendsParserListener {

    private long mFromMillis = 0;
    private long mToMillis = 0;
    private String mPaidBy = null;

    private ArrayList<Expense> mExpenseReport = null;

    private ProgressBar mPbLoading = null;

    private ListView mLvExpenses = null;

    private CustomTextView mCTvMsg = null;

    private NewExpenseAdapter mExpenseAdapter = null;

    private String mLastKey = null;

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

        if (null != getSupportActionBar()) {
            getSupportActionBar().setElevation(0f);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        findViewById(R.id.ar_ctextview_date).setOnClickListener(this);
        findViewById(R.id.ar_ctextview_paid_by).setOnClickListener(this);
        mPbLoading = (ProgressBar) findViewById(R.id.ar_progressbar_loading);

        mLvExpenses = (ListView) findViewById(R.id.ar_listview_expenses);
        //mLvExpenses.setOnItemClickListener(itemClickListener);

        mCTvMsg = (CustomTextView) findViewById(R.id.ar_ctextview_msg);
        mFromMillis = AppUtil.getFirstDayOfMonth();
        mToMillis = AppUtil.getCurrentDayOfMonth();
        AppLog.d("ReportActivity", "From:" + mFromMillis + ":: To:" + mToMillis);
        getExpenses();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ar_ctextview_date) {
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mFromMillis, mToMillis);
            datePickerFragment.show(getSupportFragmentManager(), "DatePickerFragment");

        } else if (v.getId() == R.id.ar_ctextview_paid_by) {
            PaidByFragment paidByFragment = PaidByFragment.newInstance(mPaidBy);
            paidByFragment.show(getSupportFragmentManager(), "PaidByFragment");
        }
    }

    @Override
    public void onDatePicked(long fromDate, long toDate) {
        if (0 != fromDate && 0 != toDate) {
            if (mFromMillis != fromDate || mToMillis != toDate) {
                mFromMillis = fromDate;
                mToMillis = toDate;
                getExpenses();
            }
        }
    }

    @Override
    public void onPaidBySelected(String paidByKey) {
        if (null == paidByKey && null == mPaidBy) {
            //-- nothing to do --//

        } else {
            if (null != mPaidBy && mPaidBy.equals(paidByKey)) {
                //-- same paid by selection, nothing to do --//

            } else {
                mPaidBy = paidByKey;
                getExpenses();
            }
        }
    }

    private void getExpenses() {
        if ((0 != mFromMillis && 0 != mToMillis)) {

            if (AppUtil.isConnected()) {
                if (AppUtil.isUserLoggedIn()) {
                    mPbLoading.setVisibility(View.VISIBLE);
                    mCTvMsg.setVisibility(View.GONE);

                    FirebaseDB.initDb().getSpends(mFromMillis, mToMillis, null, new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (null != dataSnapshot) {
                                AppLog.d("ReportActivity", "onDataChange: 1");
                                if (null != dataSnapshot.getValue() && dataSnapshot.getChildrenCount() > 0) {
                                    AppLog.d("ReportActivity", "onDataChange: 2");
                                    new SpendsParser(ReportActivity.this, mLastKey).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (null != databaseError) {
                                AppLog.d("ReportActivity", "onCancelled:" + databaseError.getMessage());

                            } else {
                                AppLog.d("ReportActivity", "onCancelled: ERROR!!");
                            }
                        }
                    });

            /*Cursor cursor = DBManager.getExpense(null, null, null);
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
                mCTvMsg.setText(R.string.no_expenses_found_in_filter);
                mCTvMsg.setVisibility(View.VISIBLE);
            }*/
                } else {
                    AppUtil.showToast("User not logged in.");
                }
            } else {
                AppUtil.showSnackbar(mViewComplete, getString(R.string.no_internet));
            }
        } else {
            AppUtil.showSnackbar(mViewComplete, "Please select the dates...");
        }
    }

    private final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onSpendsParsed(ArrayList<NewExpense> spends) {
        if (null != spends && spends.size() > 0) {
            setSpends(spends);
        }
    }

    private void setSpends(ArrayList<NewExpense> spends) {
        if (null == mExpenseAdapter) {
            mExpenseAdapter = new NewExpenseAdapter(ReportActivity.this, spends, null);
            mLvExpenses.setAdapter(mExpenseAdapter);
            mLvExpenses.setVisibility(View.VISIBLE);

        } else {

        }
        mPbLoading.setVisibility(View.GONE);
    }
}
