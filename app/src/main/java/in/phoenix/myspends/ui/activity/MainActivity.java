package in.phoenix.myspends.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ListView;
import android.widget.ProgressBar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import in.phoenix.myspends.BuildConfig;
import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.NewExpenseAdapter;
import in.phoenix.myspends.customview.ButteryProgressBar;
import in.phoenix.myspends.customview.CustomTextView;
import in.phoenix.myspends.database.DBManager;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.parser.FSSpendsParser;
import in.phoenix.myspends.parser.SpendsParser;
import in.phoenix.myspends.ui.fragment.AddExpenseFragment;
import in.phoenix.myspends.ui.fragment.AddPaymentTypeFragment;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

public class MainActivity extends BaseActivity implements AddExpenseFragment.OnAddExpenseListener,
        SpendsParser.SpendsParserListener, NewExpenseAdapter.OnLoadingListener {

    //private CalendarView mCalendarDate;

    private ExpenseDate mCalendarExpenseDate;

    private ListView mLvExpense;

    private NewExpenseAdapter mExpenseAdapter;

    private CustomTextView mCTvMonthlyExpenseInfo = null;
    private CustomTextView mCTvMonthlyExpense = null;
    private CustomTextView mCTvTotalExpense = null;
    private CustomTextView mCTvNoExpense = null;
    private CustomTextView mCTvExpenseHeader = null;

    //private BottomSheetBehavior bottomSheetBehavior = null;

    private ProgressBar mPbLoading;

    private ButteryProgressBar mBpbLoading = null;
    private boolean isRefresh = false;

    private String mLastKey = null;

    private CustomTextView mCTvNoSpends = null;

    //private DocumentSnapshot mLastSnapshot = null;
    private long mLastExpense = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.am_toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white, null));

        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        //CalendarView mCalendarDate = (CalendarView) findViewById(R.id.am_calendar_date);
        //mCalendarDate.setOnDateChangeListener(onDateChangeListener);
        mCalendarExpenseDate = AppUtil.convertToDate(System.currentTimeMillis());

        mLvExpense = (ListView) findViewById(R.id.am_lv_spends);

        mCTvMonthlyExpenseInfo = (CustomTextView) findViewById(R.id.am_ctextview_month_info);
        mCTvMonthlyExpense = (CustomTextView) findViewById(R.id.am_ctextview_month_expense);
        //mCTvTotalExpense = (CustomTextView) findViewById(R.id.am_textview_total_expense);
        //mCTvNoExpense = (CustomTextView) findViewById(R.id.am_textview_no_expense);
        //mCTvExpenseHeader = (CustomTextView) findViewById(R.id.am_textview_expense_header);

        findViewById(R.id.am_layout_month_expense).setOnClickListener(clickListener);

        //bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.am_card_layout_bottom));

        //getLoaderManager().initLoader(DBConstants.LoaderId.EXPENSE, null, this);
        //mLvExpense.setOnItemClickListener(itemClickListener);

        Float dimen = getResources().getDimension(R.dimen.title_text_size);
        String value = getString(R.string.value);
        DisplayMetrics displayMetrics = MySpends.APP_CONTEXT.getResources().getDisplayMetrics();
        AppLog.d("TestDensity", "Dimen:" + dimen + "::value:" + value + "::Density:" + displayMetrics.density + "::ScaledDensity:" + displayMetrics.scaledDensity);
        Float typedValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 19, displayMetrics);
        AppLog.d("TestDensity", "TypedValue:" + typedValue);

        mBpbLoading = findViewById(R.id.am_bpb_loading);
        mPbLoading = findViewById(R.id.am_pb_loading);
        mCTvNoSpends = findViewById(R.id.am_ctv_no_spends);
        getExpenses();
    }

    private void getExpenses() {
        //getLoaderManager().restartLoader(DBConstants.LoaderId.EXPENSE, null, this);
        if (null == mExpenseAdapter) {
            mPbLoading.setVisibility(View.VISIBLE);

        } else if (isRefresh) {
            mLastExpense = -1;
            mBpbLoading.setVisibility(View.VISIBLE);
        }

        FirebaseDB.initDb().getFsSpends(mLastExpense, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                boolean isFromCache = documentSnapshots.getMetadata().isFromCache();
                AppLog.d("MainActivity", "Spends Firestore:onSuccess :: isFromCache:" + isFromCache);

                //FirebaseDB.initDb().listenSpends();

                if (!documentSnapshots.isEmpty()) {
                    //mLastSnapshot = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    new FSSpendsParser(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            documentSnapshots.iterator());

                } else {
                    AppLog.d("MainActivity", "Spends: Empty! ::" + "Count: ZERO");

                    hideDialog();
                    if (isRefresh) {
                        isRefresh = false;

                        if (null != mExpenseAdapter) {
                            mExpenseAdapter = null;
                            mLvExpense.setAdapter(null);
                        }

                        mLvExpense.setVisibility(View.GONE);
                        mCTvNoSpends.setText(R.string.no_spends_tracked);
                        mCTvNoSpends.setVisibility(View.VISIBLE);

                    } else if (null != mExpenseAdapter) {
                        if (mExpenseAdapter.isLoading()) {
                            //AppUtil.showToast("Fetched all your spends.");
                            mExpenseAdapter.setIsLoading(false);
                            mExpenseAdapter.setIsLoadingRequired(false);
                            mExpenseAdapter.notifyDataSetChanged();

                        } else {
                            AppUtil.showToast("No Spends tracked!");
                            mExpenseAdapter = null;
                            mLvExpense.setAdapter(null);
                            mLvExpense.setVisibility(View.GONE);
                            mCTvNoSpends.setText(R.string.no_spends_tracked);
                            mCTvNoSpends.setVisibility(View.VISIBLE);
                        }
                    } else {
                        //AppUtil.showToast("No Spends tracked!");
                        mLvExpense.setVisibility(View.GONE);
                        mCTvNoSpends.setText(R.string.no_spends_tracked);
                        mCTvNoSpends.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppLog.d("MainActivity", "Spends Firestore:onFailure", e);

                hideDialog();

                if (isRefresh) {
                    isRefresh = false;

                } else if (null != mExpenseAdapter) {
                    if (mExpenseAdapter.isLoading()) {
                        mExpenseAdapter.setIsLoading(false);
                        //mExpenseAdapter.setIsLoadingRequired(false);
                        mExpenseAdapter.notifyDataSetChanged();

                    } else {
                        AppUtil.showToast("Unable to refresh your spends.");
                    }
                } else {
                    AppUtil.showToast("Unable to fetch your spends.");
                    mLvExpense.setVisibility(View.GONE);
                    mCTvNoSpends.setText(R.string.unable_fetch_spends);
                    mCTvNoSpends.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hideDialog() {
        mPbLoading.setVisibility(View.GONE);

        if (isRefresh) {
            mBpbLoading.setVisibility(View.INVISIBLE);
        }
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

            } else if (v.getId() == R.id.le_layout_expense) {
                int position = (int) v.getTag();
                navigateToViewExpense(position, v);
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

        } else if (item.getItemId() == R.id.menu_logout) {
            confirmLogout();
            return true;

        } else if (item.getItemId() == R.id.menu_profile) {
            if (AppUtil.isUserLoggedIn()) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));

            } else {
                AppUtil.showToast("User not logged in.");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {
        AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (AppUtil.isConnected()) {
                            AuthUI.getInstance()
                                    .signOut(MainActivity.this)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                AppPref.getInstance().clearAll();
                                                Intent newIntent = new Intent(MainActivity.this, LaunchDeciderActivity.class);
                                                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(newIntent);
                                                finish();

                                            } else {
                                                AppUtil.showToast("Unable to logout.");
                                            }
                                        }
                                    });
                        } else {
                            AppUtil.showToast(R.string.no_internet);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        logoutBuilder.create().show();
    }

    private void showAboutAppDialog() {
        AlertDialog.Builder aboutappDialog = new AlertDialog.Builder(MainActivity.this);
        //aboutappDialog.setTitle(getString(R.string.about) + " " + getString(R.string.app_name));
        aboutappDialog.setTitle(getString(R.string.app_name));
        aboutappDialog.setMessage("Version: " + BuildConfig.VERSION_NAME + "\n\n"
                + getString(R.string.about_app_msg));
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

    /*private final CalendarView.OnDateChangeListener onDateChangeListener = new CalendarView.OnDateChangeListener() {
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
    };*/

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
            //final int expensePrimaryKey = (int) view.findViewById(R.id.le_textview_amount).getTag();
            navigateToViewExpense(position, view);
        }
    };

    private void navigateToViewExpense(final int position, View view) {
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(300);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent viewExpenseIntent = new Intent(MainActivity.this, ViewExpenseActivity.class);
                viewExpenseIntent.putExtra(AppConstants.Bundle.EXPENSE, mExpenseAdapter.getItem(position));
                startActivityForResult(viewExpenseIntent, AppConstants.VIEW_EXPENSE_CODE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*AppLog.d("onBackPressed", "State::" + bottomSheetBehavior.getState());
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED
                && mCTvNoExpense.getVisibility() == View.GONE) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        } else {
            super.onBackPressed();
        }*/
        FirebaseDB.initDb().detachPaymentTypes();
        FirebaseDB.initDb().detachSpendsListener();
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DBConstants.LoaderId.EXPENSE) {
            return new ExpenseCursorLoader(MainActivity.this, mCalendarExpenseDate, AppConstants.LoaderConstants.LOADER_EXPENSE);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == DBConstants.LoaderId.EXPENSE) {
            *//*if (null != data && data.getCount() > 0) {
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
            }*//*
            getMonthlyExpense();
        }
    }*/

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

    /*@Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == DBConstants.LoaderId.EXPENSE) {
            if (null != mExpenseAdapter) {
                //mExpenseAdapter.swapCursor(null);
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.VIEW_EXPENSE_CODE || requestCode == AppConstants.NEW_EXPENSE_CODE
                || requestCode == AppConstants.EXPENSE_LIST_CODE) {
            if (resultCode == RESULT_OK) {
                isRefresh = true;
                AppLog.d("MainActivity", "Spends: Refresh");
                getExpenses();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        AppLog.d("main", "onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    public void onSpendsParsed(ArrayList<NewExpense> spends) {
        mLastKey = null;
        AppLog.d("MainActivity", "onSpendsParsed:" + spends.size());
        if (spends.size() > 0) {
            setSpends(spends);
        }

        if (isRefresh) {
            isRefresh = false;
            mBpbLoading.setVisibility(View.INVISIBLE);
        }

        mPbLoading.setVisibility(View.GONE);
    }

    private void setSpends(ArrayList<NewExpense> spends) {
        if (null == mExpenseAdapter) {
            mExpenseAdapter = new NewExpenseAdapter(MainActivity.this, spends, clickListener);
            mLvExpense.setAdapter(mExpenseAdapter);

        } else {
            if (isRefresh) {
                mExpenseAdapter.setData(spends);
                mLvExpense.setAdapter(mExpenseAdapter);
                //mExpenseAdapter.notifyDataSetChanged();
                AppLog.d("MainActivity", "Spends: Refresh Done");

            } else if (mExpenseAdapter.isLoading()) {
                mExpenseAdapter.addSpends(spends);
            }
        }

        if (mLvExpense.getVisibility() != View.VISIBLE) {
            mLvExpense.setVisibility(View.VISIBLE);
        }

        if (mCTvNoSpends.getVisibility() != View.GONE) {
            mCTvNoSpends.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoading(long lastExpenseDate) {
        AppLog.d("MainActivity", "onLoading: Expense Date:" + lastExpenseDate);
        if (-1 != lastExpenseDate) {
            //mLastKey = lastKey;
            AppLog.d("MainActivity", "onLoading: Key:" + lastExpenseDate);
            mLastExpense = lastExpenseDate;
            getExpenses();
        }
    }
}
