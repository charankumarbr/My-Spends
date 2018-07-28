package in.phoenix.myspends.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.NewExpenseAdapter;
import in.phoenix.myspends.controller.ReportAdapter;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.CategoryChart;
import in.phoenix.myspends.model.CategoryChartData;
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
        PaidByFragment.OnPaidBySelectedListener, SpendsParser.SpendsParserListener,
        NewExpenseAdapter.OnLoadingListener, FilterFragment.OnFilterListener {

    private long mFromMillis = 0;
    private long mToMillis = 0;
    private String mPaidBy = null;

    private ProgressBar mPbLoading = null;

    private RecyclerView mLvExpenses = null;

    //private TextView mCTvFilter = null;
    private TextView mCTvMsg = null;
    //private TextView mCTvPaidBy = null;

    private ReportAdapter mExpenseAdapter = null;

    //private String mLastKey = null;
    private DocumentSnapshot mLastSnapshot = null;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        init();
    }

    private void init() {
        initLayout();
        mToolbar = findViewById(R.id.ar_in_toolbar);
        mToolbar.setTitle("Reports");
        setSupportActionBar(mToolbar);

        if (null != getSupportActionBar()) {
            getSupportActionBar().setElevation(0f);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        //findViewById(R.id.ar_ctextview_date).setOnClickListener(clickListener);
        //mCTvPaidBy = findViewById(R.id.ar_ctextview_paid_by);
        mPbLoading = findViewById(R.id.ar_progressbar_loading);

        mLvExpenses = findViewById(R.id.ar_listview_expenses);
        //mLvExpenses.setOnItemClickListener(itemClickListener);

        mCTvMsg = findViewById(R.id.ar_ctextview_msg);
        mFromMillis = AppUtil.getFirstDayOfMonth();
        mToMillis = AppUtil.getCurrentDayOfMonth();
        AppLog.d("ReportActivity", "From:" + mFromMillis + ":: To:" + mToMillis);
        //mCTvFilter = findViewById(R.id.ar_layout_filter);
        //mCTvFilter.setOnClickListener(clickListener);
        //getExpenses();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*if (view.getId() == R.id.ar_ctextview_date) {
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mFromMillis, mToMillis);
                datePickerFragment.show(getSupportFragmentManager(), "DatePickerFragment");

            } else if (view.getId() == R.id.ar_ctextview_paid_by) {
                PaidByFragment paidByFragment = PaidByFragment.newInstance(mPaidBy);
                paidByFragment.show(getSupportFragmentManager(), "PaidByFragment");

            } else
            if (view.getId() == R.id.ar_layout_filter) {
                FilterFragment paidByFragment = FilterFragment.newInstance();
                paidByFragment.show(getSupportFragmentManager(), "FilterFragment");
            }*/
        }
    };

    @Override
    public void onDatePicked(long fromDate, long toDate) {
        if (0 != fromDate && 0 != toDate) {
            if (mFromMillis != fromDate || mToMillis != toDate) {
                mFromMillis = fromDate;
                mToMillis = toDate;
                //mCTvPaidBy.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                //mCTvPaidBy.setOnClickListener(clickListener);
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
                mMiTotal.setVisible(false);
                mMiSpendsChart.setVisible(false);
            }

            mCTvMsg.setVisibility(View.GONE);

            FirebaseDB.initDb().getFsSpends(mFromMillis, mToMillis, mPaidBy, mLastSnapshot, new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    /*boolean isFromCache = documentSnapshots.getMetadata().isFromCache();
                    AppLog.d("ReportActivity", "Spends Firestore:onSuccess :: isCache:" + isFromCache);

                    mMiTotal.setVisible(false);
                    mMiSpendsChart.setVisible(false);

                    if (isFromCache || !AppUtil.isConnected()) {
                        mCTvFilter.setText("Filters (Offline)");

                    } else {
                        mCTvFilter.setText("Filters");
                    }*/

                    if (!documentSnapshots.isEmpty()) {

                        if (null == mSpendsIters) {
                            mSpendsIters = new ArrayList<>();
                        }
                        mSpendsIters.add(documentSnapshots.iterator());

                        if (documentSnapshots.size() == AppConstants.PAGE_SPENDS_SIZE) {
                            if (null != mLastSnapshot) {
                                mLastSnapshot = null;
                            }
                            mLastSnapshot = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            getExpenses();

                        } else {
                            //-- end of the pagination --//
                            new FSSpendsParser(ReportActivity.this).executeOnExecutor(
                                    AsyncTask.THREAD_POOL_EXECUTOR, arrayListToArray());
                        }

                        /*new FSSpendsParser(ReportActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                iter);*/

                    } else {
                        AppLog.d("ReportActivity", "Spends: Empty! ::" + "Count: ZERO");

                        if (null == mSpendsIters) {
                            mPbLoading.setVisibility(View.GONE);
                            AppUtil.showToast("No Spends tracked!");
                            mLvExpenses.setVisibility(View.GONE);
                            mCTvMsg.setText(R.string.no_spends_tracked_tune_filters);
                            mCTvMsg.setVisibility(View.VISIBLE);

                        } else {
                            //-- data of previous page is available --//
                            new FSSpendsParser(ReportActivity.this).executeOnExecutor(
                                    AsyncTask.THREAD_POOL_EXECUTOR, arrayListToArray());

                            /*if (mExpenseAdapter.isLoading()) {
                                endLoading();

                            } else {
                                //-- refresh data --//
                                mExpenseAdapter = null;
                                mLvExpenses.setAdapter(null);
                                mLvExpenses.setVisibility(View.GONE);
                                mCTvMsg.setText(R.string.no_spends_tracked_tune_filters);
                                mCTvMsg.setVisibility(View.VISIBLE);
                            }*/
                        }
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    mMiTotal.setVisible(false);
                    mMiSpendsChart.setVisible(false);

                    AppLog.d("ReportActivity", "Spends Firestore:onFailure", e);

                    AppUtil.showToast(R.string.unable_fetch_spends);

                    if (null == mSpendsIters) {
                        mLvExpenses.setVisibility(View.GONE);
                        mCTvMsg.setText("Something went wrong. Try again.");
                        mCTvMsg.setVisibility(View.VISIBLE);
                        mPbLoading.setVisibility(View.GONE);

                    } else {
                        new FSSpendsParser(ReportActivity.this).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, arrayListToArray());
                        /*if (mExpenseAdapter.isLoading()) {
                            mExpenseAdapter.setIsLoading(false);
                            //mExpenseAdapter.setIsLoadingRequired(false);
                            mExpenseAdapter.notifyDataSetChanged();
                        }*/
                    }
                }
            });
        } else {
            AppUtil.showSnackbar(mViewComplete, "Please select the dates...");
        }
    }

    private Iterator<DocumentSnapshot>[] arrayListToArray() {

        Iterator<DocumentSnapshot>[] iters = null;
        if ((null != mSpendsIters) && mSpendsIters.size() > 0) {
            iters = new Iterator[mSpendsIters.size()];

            for (int index = 0; index < mSpendsIters.size(); index++) {
                iters[index] = mSpendsIters.get(index);
            }
        }

        return iters;
    }

    /*private void endLoading() {
        AppUtil.showToast("Fetched all your spends.");
        *//*mExpenseAdapter.setIsLoading(false);
        mExpenseAdapter.setIsLoadingRequired(false);*//*
        mExpenseAdapter.notifyDataSetChanged();
        mMiTotal.setVisible(true);
        mMiSpendsChart.setVisible(true);
    }*/

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
            /*if ((null != mExpenseAdapter) && !mExpenseAdapter.isLoading()) {
                new CalculateTotal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }*/
            return true;

        } else if (item.getItemId() == R.id.menu_spends_chart) {
            /*if ((null != mExpenseAdapter) && !mExpenseAdapter.isLoading()) {
                new SpendsChart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }*/
            return true;

        } else if (item.getItemId() == R.id.menu_filter) {
            FilterFragment paidByFragment = FilterFragment.newInstance();
            paidByFragment.show(getSupportFragmentManager(), "FilterFragment");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onSpendsParsed(ArrayList<NewExpense> spends) {

        mPbLoading.setVisibility(View.GONE);

        if (spends.size() > 0) {
            //setSpends(spends);
            new SpendsChart(spends).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

        } else {
            AppUtil.showToast("No Spends tracked!");
            mLvExpenses.setVisibility(View.GONE);
            mCTvMsg.setText(R.string.no_spends_tracked_tune_filters);
            mCTvMsg.setVisibility(View.VISIBLE);
        }
    }

    private void setSpends(ArrayList<NewExpense> spends) {

        new SpendsChart(spends).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

        /*if (null == mExpenseAdapter) {
            //mExpenseAdapter = new NewExpenseAdapter(ReportActivity.this, spends, null);
            mExpenseAdapter = new ReportAdapter(ReportActivity.this, )
            mExpenseAdapter.setIsLoadingRequired(false); //-- test --//
            mLvExpenses.setAdapter(mExpenseAdapter);
            mLvExpenses.setVisibility(View.VISIBLE);

        } else {
            if (mExpenseAdapter.isLoading()) {
                mExpenseAdapter.addSpends(spends);

            } else {
                mExpenseAdapter.setData(spends);
                mLvExpenses.setAdapter(mExpenseAdapter);
            }
        }*/

        /*if (spends.size() < AppConstants.PAGE_SPENDS_SIZE) {
            endLoading();
        }*/
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

    private MenuItem mMiTotal = null;
    private MenuItem mMiSpendsChart = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reports, menu);
        mMiTotal = menu.findItem(R.id.menu_get_total);
        mMiSpendsChart = menu.findItem(R.id.menu_spends_chart);
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
                //mLastKey = null;

                mSpendsIters = null;

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
    private ArrayList<Iterator<DocumentSnapshot>> mSpendsIters = null;

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
            /*if ((null != mExpenseAdapter) && !mExpenseAdapter.isLoading()) {
                Float amount = mExpenseAdapter.calculateTotal();
                totalAmount = AppUtil.getStringAmount(String.valueOf(amount));
            }*/
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

    private class SpendsChart extends AsyncTask<Void, Void, Void> implements Comparator<CategoryChartData>{

        private ProgressDialog pdLoading;

        private CategoryChart categoryChart;

        private ArrayList<NewExpense> spends;

        public SpendsChart(ArrayList<NewExpense> spends) {
            this.spends = spends;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading = new ProgressDialog(ReportActivity.this);
            pdLoading.setCancelable(false);
            pdLoading.setCanceledOnTouchOutside(false);
            pdLoading.setMessage("Preparing Spends Chart...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //totalAmount = mExpenseAdapter.calculateTotal();
            int maxIndex = spends.size();
            if (maxIndex > 0) {
                HashMap<Integer, ArrayList<NewExpense>> categoryExpense = new HashMap<>();
                HashMap<Integer, Float> categoryTotal = new HashMap<>();
                Float grandTotal = 0f;
                for (int index = 0; index < maxIndex; index++) {
                    NewExpense anExpense = spends.get(index);
                    int categoryId = anExpense.getCategoryId();

                    ArrayList<NewExpense> expenses;
                    if (categoryExpense.containsKey(categoryId)) {
                        expenses = categoryExpense.get(categoryId);

                    } else {
                        expenses = new ArrayList<>();
                    }
                    expenses.add(anExpense);
                    categoryExpense.put(categoryId, expenses);

                    Float total;
                    if (categoryTotal.containsKey(categoryId)) {
                        total = categoryTotal.get(categoryId);

                    } else {
                        total = 0f;
                    }
                    total += anExpense.getAmount();
                    categoryTotal.put(categoryId, total);

                    grandTotal += anExpense.getAmount();
                }

                if (grandTotal > 0F) {
                    Iterator<Integer> keyIter = categoryExpense.keySet().iterator();
                    ArrayList<CategoryChartData> chartData = new ArrayList<>();
                    do {
                        Integer categoryId = keyIter.next();
                        CategoryChartData categoryChartData = new CategoryChartData(
                                MySpends.getCategoryName(categoryId), categoryExpense.get(categoryId));
                        categoryChartData.setCategoryId(categoryId);
                        categoryChartData.setCategoryName(MySpends.getCategoryName(categoryId));
                        categoryChartData.setCategoryTotal(categoryTotal.get(categoryId));
                        categoryChartData.setExpenses(categoryExpense.get(categoryId));

                        chartData.add(categoryChartData);

                    } while (keyIter.hasNext());

                    if (chartData.size() > 0) {
                        Collections.sort(chartData, this);
                        categoryChart = new CategoryChart();
                        categoryChart.setGrandTotal(grandTotal);
                        categoryChart.setCategoryChartData(chartData);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isFinishing()) {
                if (null != categoryChart) {
                    AppLog.d("ReportActivity", "CategoryChart::Total Size:" + categoryChart.getGrandTotal());

                    pdLoading.dismiss();
                    pdLoading = null;

                    mExpenseAdapter = new ReportAdapter(ReportActivity.this, categoryChart);

                    mToolbar.setSubtitle("Total Spends: " + mExpenseAdapter.getCurrencySymbol() +
                            AppUtil.getStringAmount(String.valueOf(categoryChart.getGrandTotal())));

                    LinearLayoutManager layoutManager = new LinearLayoutManager(ReportActivity.this);
                    mLvExpenses.setLayoutManager(layoutManager);
                    mLvExpenses.setAdapter(mExpenseAdapter);

                    mExpenseAdapter.setOnGroupExpandCollapseListener(new GroupExpandCollapseListener() {
                        @Override
                        public void onGroupExpanded(ExpandableGroup group) {
                            mLvExpenses.smoothScrollBy(0, 100);
                        }

                        @Override
                        public void onGroupCollapsed(ExpandableGroup group) {

                        }
                    });

                } else {
                    pdLoading.dismiss();
                    pdLoading = null;
                    AppUtil.showToast("Unable to prepare your Spends Chart!");
                }
            }
        }

        @Override
        public int compare(CategoryChartData chartData1, CategoryChartData chartData2) {
            return chartData1.getCategoryTotal() < chartData2.getCategoryTotal() ? 1 : -1;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mExpenseAdapter) {
            mExpenseAdapter.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != mExpenseAdapter) {
            mExpenseAdapter.onRestoreInstanceState(savedInstanceState);
        }
    }
}
