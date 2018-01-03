package in.phoenix.myspends.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;

import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.NewExpenseAdapter;
import in.phoenix.myspends.customview.CustomTextView;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.Expense;
import in.phoenix.myspends.model.ExpenseDate;
import in.phoenix.myspends.model.NewExpense;
import in.phoenix.myspends.parser.FSSpendsParser;
import in.phoenix.myspends.parser.SpendsParser;
import in.phoenix.myspends.ui.fragment.DatePickerFragment;
import in.phoenix.myspends.ui.fragment.FilterFragment;
import in.phoenix.myspends.ui.fragment.PaidByFragment;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppPref;
import in.phoenix.myspends.util.AppUtil;

public class ReportActivity extends BaseActivity implements DatePickerFragment.OnDatePickedListener,
        PaidByFragment.OnPaidBySelectedListener, SpendsParser.SpendsParserListener, NewExpenseAdapter.OnLoadingListener,
        FilterFragment.OnFilterListener{

    private long mFromMillis = 0;
    private long mToMillis = 0;
    private String mPaidBy = null;

    private ProgressBar mPbLoading = null;

    private ListView mLvExpenses = null;

    private CustomTextView mCTvFilter = null;
    private CustomTextView mCTvMsg = null;
    private CustomTextView mCTvPaidBy = null;

    private NewExpenseAdapter mExpenseAdapter = null;

    private String mLastKey = null;
    private DocumentSnapshot mLastSnapshot = null;

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

        findViewById(R.id.ar_ctextview_date).setOnClickListener(clickListener);
        mCTvPaidBy = findViewById(R.id.ar_ctextview_paid_by);
        mPbLoading = (ProgressBar) findViewById(R.id.ar_progressbar_loading);

        mLvExpenses = (ListView) findViewById(R.id.ar_listview_expenses);
        //mLvExpenses.setOnItemClickListener(itemClickListener);

        mCTvMsg = (CustomTextView) findViewById(R.id.ar_ctextview_msg);
        mFromMillis = AppUtil.getFirstDayOfMonth();
        mToMillis = AppUtil.getCurrentDayOfMonth();
        AppLog.d("ReportActivity", "From:" + mFromMillis + ":: To:" + mToMillis);
        mCTvFilter = (CustomTextView) findViewById(R.id.ar_layout_filter);
        mCTvFilter.setOnClickListener(clickListener);
        //getExpenses();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.ar_ctextview_date) {
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mFromMillis, mToMillis);
                datePickerFragment.show(getSupportFragmentManager(), "DatePickerFragment");

            } else if (view.getId() == R.id.ar_ctextview_paid_by) {
                PaidByFragment paidByFragment = PaidByFragment.newInstance(mPaidBy);
                paidByFragment.show(getSupportFragmentManager(), "PaidByFragment");

            } else if (view.getId() == R.id.ar_layout_filter) {
                FilterFragment paidByFragment = FilterFragment.newInstance();
                paidByFragment.show(getSupportFragmentManager(), "FilterFragment");
            }
        }
    };

    @Override
    public void onDatePicked(long fromDate, long toDate) {
        if (0 != fromDate && 0 != toDate) {
            if (mFromMillis != fromDate || mToMillis != toDate) {
                mFromMillis = fromDate;
                mToMillis = toDate;
                mCTvPaidBy.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mCTvPaidBy.setOnClickListener(clickListener);
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
                mLastSnapshot = null;
                getExpenses();
            }
        }
    }

    private void getExpenses() {
        if ((0 != mFromMillis && 0 != mToMillis)) {

            if (null == mExpenseAdapter) {
                mPbLoading.setVisibility(View.VISIBLE);
            }

            mCTvMsg.setVisibility(View.GONE);

            FirebaseDB.initDb().getFsSpends(mFromMillis, mToMillis, mPaidBy, mLastSnapshot, new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    boolean isFromCache = documentSnapshots.getMetadata().isFromCache();
                    AppLog.d("ReportActivity", "Spends Firestore:onSuccess :: isCache:" + isFromCache);

                    mMiGetTotal.setVisible(false);

                    if (isFromCache || !AppUtil.isConnected()) {
                        mCTvFilter.setText("Filters (Offline)");

                    } else {
                        mCTvFilter.setText("Filters");
                    }

                    if (!documentSnapshots.isEmpty()) {
                        if (null != mLastSnapshot) {
                            mLastSnapshot = null;
                        }
                        mLastSnapshot = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        new FSSpendsParser(ReportActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                documentSnapshots.iterator());

                    } else {
                        AppLog.d("ReportActivity", "Spends: Empty! ::" + "Count: ZERO");

                        mPbLoading.setVisibility(View.GONE);

                        if (null == mExpenseAdapter) {
                            AppUtil.showToast("No Spends tracked!");
                            mLvExpenses.setVisibility(View.GONE);
                            mCTvMsg.setText(R.string.no_spends_tracked_tune_filters);
                            mCTvMsg.setVisibility(View.VISIBLE);

                        } else {
                            if (mExpenseAdapter.isLoading()) {
                                AppUtil.showToast("Fetched all your spends.");
                                mExpenseAdapter.setIsLoading(false);
                                mExpenseAdapter.setIsLoadingRequired(false);
                                mExpenseAdapter.notifyDataSetChanged();
                                mMiGetTotal.setVisible(true);

                            } else {
                                //-- refresh data --//
                                mExpenseAdapter = null;
                                mLvExpenses.setAdapter(null);
                                mLvExpenses.setVisibility(View.GONE);
                                mCTvMsg.setText(R.string.no_spends_tracked_tune_filters);
                                mCTvMsg.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    mMiGetTotal.setVisible(false);

                    AppLog.d("ReportActivity", "Spends Firestore:onFailure", e);

                    AppUtil.showToast(R.string.unable_fetch_spends);

                    if (null == mExpenseAdapter) {
                        mLvExpenses.setVisibility(View.GONE);
                        mCTvMsg.setVisibility(View.VISIBLE);
                        mPbLoading.setVisibility(View.GONE);

                    } else {
                        if (mExpenseAdapter.isLoading()) {
                            mExpenseAdapter.setIsLoading(false);
                            //mExpenseAdapter.setIsLoadingRequired(false);
                            mExpenseAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
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

        } else if (item.getItemId() == R.id.menu_get_total) {
            if ((null != mExpenseAdapter) && !mExpenseAdapter.isLoading()) {
                new CalculateTotal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onSpendsParsed(ArrayList<NewExpense> spends) {
        if (spends.size() > 0) {
            setSpends(spends);
        }

        mPbLoading.setVisibility(View.GONE);
    }

    private void setSpends(ArrayList<NewExpense> spends) {
        if (null == mExpenseAdapter) {
            mExpenseAdapter = new NewExpenseAdapter(ReportActivity.this, spends, null);
            mLvExpenses.setAdapter(mExpenseAdapter);
            mLvExpenses.setVisibility(View.VISIBLE);

        } else {
            if (mExpenseAdapter.isLoading()) {
                mExpenseAdapter.addSpends(spends);

            } else {
                mExpenseAdapter.setData(spends);
                mLvExpenses.setAdapter(mExpenseAdapter);
            }
        }
    }

    @Override
    public void onLoading(long lastKey) {
        AppLog.d("ReportActivity", "onLoading: Key:" + lastKey);
        if (-1 != lastKey) {
            //mLastKey = lastKey;
            AppLog.d("ReportActivity", "onLoading: Key:" + lastKey);
            getExpenses();
        }
    }

    private MenuItem mMiGetTotal = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reports, menu);
        mMiGetTotal = menu.findItem(R.id.menu_get_total);
        return true;
    }

    @Override
    public void onFilterChanged(long fromDate, long toDate, String paidByKey) {
        AppLog.d("ReportActivity", "onFilterChanged: FromDate:" + fromDate + " :: To Date:" + toDate + " :: Paidby:" + paidByKey);
        if (0 != fromDate && 0 != toDate) {
            boolean isChanged = false;

            ExpenseDate checkerFromDate = new ExpenseDate(mFromMillis);
            ExpenseDate checkerToDate = new ExpenseDate(mToMillis);
            if (!checkerFromDate.isSameExpenseDate(fromDate) || !checkerToDate.isSameExpenseDate(toDate)) {
                mFromMillis = fromDate;
                mToMillis = toDate;
                isChanged = true;
            }

            if ((null == mPaidBy) || (!TextUtils.isEmpty(mPaidBy) && !mPaidBy.equals(paidByKey))) {
                mPaidBy = paidByKey;
                isChanged = true;
            }

            if (isChanged) {
                mLastSnapshot = null;
                mLastKey = null;

                if (null != mExpenseAdapter) {
                    mExpenseAdapter = null;
                    mLvExpenses.setAdapter(null);
                }
                getExpenses();

            } else {
                AppUtil.showToast("Filter not changed!");
            }
        }
    }

    class CalculateTotal extends AsyncTask<Void, Void, Void> {

        ProgressDialog pdLoading;

        String totalAmount = "0.00";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading = new ProgressDialog(ReportActivity.this);
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
            pdLoading.setMessage("Calculating Total...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if ((null != mExpenseAdapter) && !mExpenseAdapter.isLoading()) {

                Float amount = mExpenseAdapter.calculateTotal();
                totalAmount = AppUtil.getStringAmount(String.valueOf(amount));

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isFinishing()) {
                AlertDialog.Builder totalDialog = new AlertDialog.Builder(ReportActivity.this);
                totalDialog.setTitle("Total Spends");
                totalDialog.setMessage("Total of spends is: " + AppPref.getInstance().getString
                        (AppConstants.PrefConstants.CURRENCY) + " " + totalAmount);
                totalDialog.setCancelable(true);
                totalDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                pdLoading.dismiss();
                pdLoading = null;
                totalDialog.create().show();
            }
        }
    }
}
