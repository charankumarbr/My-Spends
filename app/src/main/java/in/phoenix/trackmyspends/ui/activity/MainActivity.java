package in.phoenix.trackmyspends.ui.activity;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
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

import java.util.Locale;

import in.phoenix.trackmyspends.BuildConfig;
import in.phoenix.trackmyspends.TrackMySpends;
import in.phoenix.trackmyspends.R;
import in.phoenix.trackmyspends.controller.ExpenseAdapter;
import in.phoenix.trackmyspends.controller.ExpenseCursorLoader;
import in.phoenix.trackmyspends.customview.BottomSheetListView;
import in.phoenix.trackmyspends.customview.CustomTextView;
import in.phoenix.trackmyspends.database.DBConstants;
import in.phoenix.trackmyspends.database.DBManager;
import in.phoenix.trackmyspends.model.ExpenseDate;
import in.phoenix.trackmyspends.ui.fragment.AddExpenseFragment;
import in.phoenix.trackmyspends.ui.fragment.AddPaymentTypeFragment;
import in.phoenix.trackmyspends.util.AppConstants;
import in.phoenix.trackmyspends.util.AppLog;
import in.phoenix.trackmyspends.util.AppPref;
import in.phoenix.trackmyspends.util.AppUtil;

public class MainActivity extends BaseActivity implements AddExpenseFragment.OnAddExpenseListener, LoaderManager.LoaderCallbacks<Cursor> {

    private CalendarView mCalendarDate;

    private ExpenseDate mCalendarExpenseDate;

    private BottomSheetListView mLvExpense;

    private ExpenseAdapter mExpenseAdapter;

    private CustomTextView mCTvMonthlyExpenseInfo = null;
    private CustomTextView mCTvMonthlyExpense = null;
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

        mCTvMonthlyExpenseInfo = (CustomTextView) findViewById(R.id.am_ctextview_month_info);
        mCTvMonthlyExpense = (CustomTextView) findViewById(R.id.am_ctextview_month_expense);
        mCTvTotalExpense = (CustomTextView) findViewById(R.id.am_textview_total_expense);
        mCTvNoExpense = (CustomTextView) findViewById(R.id.am_textview_no_expense);
        mCTvExpenseHeader = (CustomTextView) findViewById(R.id.am_textview_expense_header);

        findViewById(R.id.am_layout_month_expense).setOnClickListener(clickListener);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.am_card_layout_bottom));

        getLoaderManager().initLoader(DBConstants.LoaderId.EXPENSE, null, this);
        mLvExpense.setOnItemClickListener(itemClickListener);

        Float dimen = getResources().getDimension(R.dimen.title_text_size);
        String value = getString(R.string.value);
        DisplayMetrics displayMetrics = TrackMySpends.APP_CONTEXT.getResources().getDisplayMetrics();
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

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.am_layout_month_expense) {
                boolean status = (boolean) mCTvMonthlyExpense.getTag();
                if (status) {
                    Intent monthlyExpenseIntent = new Intent(MainActivity.this, ExpensesListActivity.class);
                    monthlyExpenseIntent.putExtra(AppConstants.Bundle.EXPENSE_DATE, mCalendarExpenseDate);
                    startActivityForResult(monthlyExpenseIntent, AppConstants.EXPENSE_LIST_CODE);

                } else {
                    AppUtil.showSnackbar(findViewById(R.id.activity_main), "No expenses tracked in this month!");
                }
            }
        }
    };

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

        } else if (item.getItemId() == R.id.menu_reports) {
            //AppUtil.showSnackbar(findViewById(R.id.activity_main), "User, wait for an update!");
            startActivity(new Intent(MainActivity.this, ReportActivity.class));
            return true;

        } else if (item.getItemId() == R.id.menu_about) {
            showAboutAppDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutAppDialog() {
        AlertDialog.Builder aboutappDialog = new AlertDialog.Builder(MainActivity.this);
        aboutappDialog.setTitle(getString(R.string.about) + " " + getString(R.string.app_name));
        aboutappDialog.setMessage("Version: " + BuildConfig.VERSION_NAME + "\n\n"
                + getString(R.string.app_name) + " " + getString(R.string.about_app_msg));
        aboutappDialog.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        aboutappDialog.setNegativeButton(getString(R.string.contact_us), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Uri.encode("phoenix.apps.in@gmail.com")));
                //intent.setType("text/plain");
                //intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"phoenix.apps.in@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about) + " " + getString(R.string.app_name) + "-v" + BuildConfig.VERSION_NAME);
                Intent mailer = Intent.createChooser(intent, null);
                startActivity(Intent.createChooser(mailer, "Send email via..."));
                //startActivity(intent);
            }
        });

        if (!isFinishing()) {
            aboutappDialog.create().show();
        }
    }

    private void showPaymentDialog() {
        AddPaymentTypeFragment addPaymentTypeFragment = AddPaymentTypeFragment.newInstance();
        addPaymentTypeFragment.show(getSupportFragmentManager(), "AddPaymentTFragment");
    }

    private final CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
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
        AppLog.d("onBackPressed", "State::" + bottomSheetBehavior.getState());
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED
                && mCTvNoExpense.getVisibility() == View.GONE) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DBConstants.LoaderId.EXPENSE) {
            return new ExpenseCursorLoader(MainActivity.this, mCalendarExpenseDate, AppConstants.LoaderConstants.LOADER_EXPENSE);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == DBConstants.LoaderId.EXPENSE) {
            if (null != data && data.getCount() > 0) {
                AppLog.d("Height", "::" + bottomSheetBehavior.getPeekHeight() + "::State:" + bottomSheetBehavior.getState());
                bottomSheetBehavior.setPeekHeight(AppUtil.dpToPx(120));

                mLvExpense.setVisibility(View.VISIBLE);
                mCTvNoExpense.setVisibility(View.GONE);

                if (null == mLvExpense.getAdapter()) {
                    mExpenseAdapter = new ExpenseAdapter(MainActivity.this, data, false);

                } else {
                    mExpenseAdapter.swapCursor(data);
                }
                mLvExpense.setAdapter(mExpenseAdapter);
                Float totalAmount = DBManager.getTotalExpenses(mCalendarExpenseDate);
                if (null != totalAmount) {
                    mCTvTotalExpense.setText(AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY)
                            + " " + String.format(Locale.ENGLISH, AppConstants.FLOAT_FORMAT, totalAmount));

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
            getMonthlyExpense();
        }
    }

    private void getMonthlyExpense() {
        String monthlyExpense = DBManager.getMonthlyExpensesTotal(mCalendarExpenseDate);
        /*mCTvMonthlyExpenseInfo.setText("Expense for " + AppUtil.getShortMonth(mCalendarExpenseDate.getMonth())
                + " " + mCalendarExpenseDate.getYear());*/
        mCTvMonthlyExpenseInfo.setText(String.format(getString(R.string.value_month_expenses), AppUtil.getShortMonth(mCalendarExpenseDate.getMonth())));

        if (null != monthlyExpense) {
            mCTvMonthlyExpense.setText(String.format("%s %s",
                    AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY),
                    AppUtil.getStringAmount(monthlyExpense)));
            mCTvMonthlyExpense.setTag(Boolean.TRUE);

        } else {
            mCTvMonthlyExpense.setText(String.format("%s 0.00", AppPref.getInstance().getString(AppConstants.PrefConstants.CURRENCY)));
            mCTvMonthlyExpense.setTag(Boolean.FALSE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == DBConstants.LoaderId.EXPENSE) {
            if (null != mExpenseAdapter) {
                mExpenseAdapter.swapCursor(null);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.VIEW_EXPENSE_CODE || requestCode == AppConstants.NEW_EXPENSE_CODE
                || requestCode == AppConstants.EXPENSE_LIST_CODE) {
            if (resultCode == RESULT_OK) {
                getExpenses();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        AppLog.d("main", "onNewIntent");
        super.onNewIntent(intent);
    }
}
