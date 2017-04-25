package in.charanbr.expensetracker.ui.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import in.charanbr.expensetracker.R;
import in.charanbr.expensetracker.controller.ExpenseAdapter;
import in.charanbr.expensetracker.controller.ExpenseCursorLoader;
import in.charanbr.expensetracker.customview.CustomTextView;
import in.charanbr.expensetracker.database.DBConstants;
import in.charanbr.expensetracker.model.ExpenseDate;
import in.charanbr.expensetracker.util.AppConstants;
import in.charanbr.expensetracker.util.AppLog;
import in.charanbr.expensetracker.util.AppUtil;

public class ExpensesListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ExpenseDate mExpenseDate;

    private ListView mLvExpenses;

    private ExpenseAdapter mExpenseAdapter;

    private CustomTextView mCTvNoExpenses;

    private int mResult = RESULT_CANCELED;

    private ProgressBar mPbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_list);

        if (getIntent().hasExtra(AppConstants.Bundle.EXPENSE_DATE)) {
            mExpenseDate = getIntent().getParcelableExtra(AppConstants.Bundle.EXPENSE_DATE);

        } else {
            onDestroy();
        }

        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.ael_toolbar);
        String titlePart = AppUtil.getMonth(mExpenseDate.getMonth()) + " " + mExpenseDate.getYear();
        toolbar.setTitle(titlePart + " Expenses");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getLoaderManager().initLoader(DBConstants.LoaderId.EXPENSES_LIST, null, this);

        mLvExpenses = (ListView) findViewById(R.id.ael_listview_expenses_list);
        mCTvNoExpenses = (CustomTextView) findViewById(R.id.ael_ctextview_no_expenses);

        mPbLoading = (ProgressBar) findViewById(R.id.ael_progress_loading);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ExpenseCursorLoader(ExpensesListActivity.this, mExpenseDate,
                AppConstants.LoaderConstants.LOADER_EXPENSES_LIST);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == DBConstants.LoaderId.EXPENSES_LIST) {
            if (mPbLoading.getVisibility() != View.GONE) {
                mPbLoading.setVisibility(View.GONE);
            }

            if (null != data && data.getCount() > 0) {
                mLvExpenses.setVisibility(View.VISIBLE);
                mCTvNoExpenses.setVisibility(View.GONE);
                AppUtil.showToast("" + data.getCount() + " records");
                if (null == mLvExpenses.getAdapter()) {
                    mExpenseAdapter = new ExpenseAdapter(ExpensesListActivity.this, data, true);
                    mLvExpenses.setAdapter(mExpenseAdapter);
                    mLvExpenses.setOnItemClickListener(itemClickListener);

                } else {
                    mExpenseAdapter.swapCursor(data);
                }

            } else {
                mLvExpenses.setVisibility(View.GONE);
                mCTvNoExpenses.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == DBConstants.LoaderId.EXPENSES_LIST) {
            if (mPbLoading.getVisibility() != View.GONE) {
                mPbLoading.setVisibility(View.GONE);
            }

            if (null != mExpenseAdapter) {
                mExpenseAdapter.swapCursor(null);
            }
        }
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int expensePrimaryKey = (int) view.findViewById(R.id.le_textview_amount).getTag();
            Animation animation = new AlphaAnimation(0.3f, 1.0f);
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent viewExpenseIntent = new Intent(ExpensesListActivity.this, ViewExpenseActivity.class);
                    viewExpenseIntent.putExtra(AppConstants.Bundle.EXPENSE_PRIMARY_KEY, expensePrimaryKey);
                    startActivityForResult(viewExpenseIntent, AppConstants.VIEW_EXPENSE_CODE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation);
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
        setResult(mResult);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.VIEW_EXPENSE_CODE) {
            if (resultCode == RESULT_OK) {
                mResult = RESULT_OK;
                mPbLoading.setVisibility(View.VISIBLE);
                getLoaderManager().restartLoader(DBConstants.LoaderId.EXPENSES_LIST, null, this);
            }
        }
    }

}
