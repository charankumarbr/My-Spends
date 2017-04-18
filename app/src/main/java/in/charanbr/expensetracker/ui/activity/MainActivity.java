package in.charanbr.expensetracker.ui.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.CalendarView;

import java.text.NumberFormat;

import in.charanbr.expensetracker.ui.fragment.AddExpenseFragment;
import in.charanbr.expensetracker.ui.fragment.AddPaymentTypeFragment;
import in.charanbr.expensetracker.ExpenseTracker;
import in.charanbr.expensetracker.R;
import in.charanbr.expensetracker.controller.ExpenseAdapter;
import in.charanbr.expensetracker.controller.ExpenseCursorLoader;
import in.charanbr.expensetracker.customview.BottomSheetListView;
import in.charanbr.expensetracker.customview.CustomTextView;
import in.charanbr.expensetracker.database.DBConstants;
import in.charanbr.expensetracker.database.DBManager;
import in.charanbr.expensetracker.model.ExpenseDate;
import in.charanbr.expensetracker.util.AppConstants;
import in.charanbr.expensetracker.util.AppLog;
import in.charanbr.expensetracker.util.AppPref;
import in.charanbr.expensetracker.util.AppUtil;

public class MainActivity extends BaseActivity implements AddExpenseFragment.OnAddExpenseListener, LoaderManager.LoaderCallbacks<Cursor>
{

    private CalendarView mCalendarDate;

    private ExpenseDate mCalendarExpenseDate;

    private BottomSheetListView mLvExpense;

    private ExpenseAdapter mExpenseAdapter;

    private CustomTextView mCTvTotalExpense = null;
    private CustomTextView mCTvNoExpense = null;
    private CustomTextView mCTvExpenseHeader = null;

    private BottomSheetBehavior bottomSheetBehavior = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.lt_toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white, null));

        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        mCalendarDate = (CalendarView) findViewById(R.id.am_calendar_date);
        mCalendarDate.setOnDateChangeListener(onDateChangeListener);
        mCalendarExpenseDate = AppUtil.convertToDate(mCalendarDate.getDate());

        mLvExpense = (BottomSheetListView) findViewById(R.id.am_listview_expense);

        mCTvTotalExpense = (CustomTextView) findViewById(R.id.am_textview_total_expense);
        mCTvNoExpense = (CustomTextView) findViewById(R.id.am_textview_no_expense);
        mCTvExpenseHeader = (CustomTextView) findViewById(R.id.am_textview_expense_header);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.am_card_layout_bottom));

        getLoaderManager().initLoader(DBConstants.LoaderId.EXPENSE, null, this);
        mLvExpense.setOnItemClickListener(itemClickListener);

        Float dimen = getResources().getDimension(R.dimen.title_text_size);
        String value = getString(R.string.value);
        DisplayMetrics displayMetrics = ExpenseTracker.APP_CONTEXT.getResources().getDisplayMetrics();
        AppLog.d("TestDensity", "Dimen:" + dimen + "::value:" + value + "::Density:" + displayMetrics.density + "::ScaledDensity:" + displayMetrics.scaledDensity);
        Float typedValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 19, displayMetrics);
        AppLog.d("TestDensity", "TypedValue:" + typedValue);
    }

    private void getExpenses() {
        getLoaderManager().restartLoader(DBConstants.LoaderId.EXPENSE, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_expense) {
            //showAddExpenseDialog();
            Intent newExpenseIntent = new Intent(MainActivity.this, NewExpenseActivity.class);
            newExpenseIntent.putExtra(AppConstants.Bundle.EXPENSE_DATE, mCalendarExpenseDate);
            startActivityForResult(newExpenseIntent, AppConstants.NEW_EXPENSE_CODE);
            return true;

        } else if (item.getItemId() == R.id.menu_payment) {
            //showPaymentDialog();
            startActivity(new Intent(MainActivity.this, PaymentActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPaymentDialog() {
        AddPaymentTypeFragment addPaymentTypeFragment = AddPaymentTypeFragment.newInstance().newInstance();
        addPaymentTypeFragment.show(getSupportFragmentManager(), "AddPaymentTFragment");
    }

    private CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            AppLog.d("Calendar Click", "DoM:" + dayOfMonth + "::Month:" + month + "::Year:" + year);
            mCalendarExpenseDate = new ExpenseDate(dayOfMonth, month, year);
            AppLog.d("State", " " + bottomSheetBehavior.getState());
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            getExpenses();
        }
    };

    private void showAddExpenseDialog() {
        AddExpenseFragment expenseFragment = AddExpenseFragment.newInstance(mCalendarExpenseDate);
        expenseFragment.show(getSupportFragmentManager(), "AddExpenseDFragment");
    }

    @Override
    public void onExpenseAdded() {
        getExpenses();
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
                    Intent viewExpenseIntent = new Intent(MainActivity.this, ViewExpenseActivity.class);
                    viewExpenseIntent.putExtra(AppConstants.Bundle.EXPENSE_PRIMARY_KEY, expensePrimaryKey);
                    startActivityForResult(viewExpenseIntent, AppConstants.VIEW_EXPENSE_CODE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation1);
        }
    };

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ExpenseCursorLoader(MainActivity.this, mCalendarExpenseDate, AppConstants.LoaderConstants.LOADER_EXPENSE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null != data && data.getCount() > 0) {
            AppLog.d("Height", "::" + bottomSheetBehavior.getPeekHeight() + "::State:" + bottomSheetBehavior.getState());
            bottomSheetBehavior.setPeekHeight(AppUtil.dpToPx(120));

            mLvExpense.setVisibility(View.VISIBLE);
            mCTvNoExpense.setVisibility(View.GONE);

            if (null == mLvExpense.getAdapter()) {
                mExpenseAdapter = new ExpenseAdapter(MainActivity.this, data);

            } else {
                mExpenseAdapter.swapCursor(data);
            }
            mLvExpense.setAdapter(mExpenseAdapter);
            Float totalAmount = DBManager.getTotalExpenses(mCalendarExpenseDate);
            if (null != totalAmount) {
                mCTvTotalExpense.setText(AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY)
                        + " " + String.format(AppConstants.FLOAT_FORMAT, totalAmount));

            } else {
                mCTvTotalExpense.setText("");
            }

        } else {
            mLvExpense.setVisibility(View.GONE);
            mCTvTotalExpense.setText(AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY)
                    + " " + "0.00");
            mCTvNoExpense.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setPeekHeight(AppUtil.dpToPx(150));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (null != mExpenseAdapter) {
            mExpenseAdapter.swapCursor(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.VIEW_EXPENSE_CODE || requestCode == AppConstants.NEW_EXPENSE_CODE) {
            if (resultCode == RESULT_OK) {
                getExpenses();
            }
        }
    }
}
