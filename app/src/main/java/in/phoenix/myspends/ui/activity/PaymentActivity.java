package in.phoenix.myspends.ui.activity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.phoenix.myspends.MySpends;
import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.PaymentTypeAdapter;
import in.phoenix.myspends.database.FirebaseDB;
import in.phoenix.myspends.model.PaymentType;
import in.phoenix.myspends.parser.PaymentTypeParser;
import in.phoenix.myspends.ui.fragment.AddPaymentTypeFragment;
import in.phoenix.myspends.util.AppLog;
import in.phoenix.myspends.util.AppUtil;

public class PaymentActivity extends BaseActivity implements PaymentTypeAdapter.OnStatusChangedListener
        , AddPaymentTypeFragment.OnPaymentTypeListener, PaymentTypeParser.PaymentTypeParserListener {

    private ListView mLvPayment = null;

    private PaymentTypeAdapter mPaymentAdapter = null;

    private ProgressBar mPbLoading = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ap_toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white, null));

        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
        toolbar.setTitle("Payment Types");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        initLayout();

        mLvPayment = (ListView) findViewById(R.id.ap_listview_payment);
        mPbLoading = findViewById(R.id.ap_pb_loading);
        //getLoaderManager().initLoader(DBConstants.LoaderId.PAYMENT_TYPE, null, this);

        getPaymentTypes();
    }

    private void getPaymentTypes() {
        mPbLoading.setVisibility(View.VISIBLE);
        FirebaseDB.initDb().getPaymentTypes(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot) {
                    AppLog.d("PaymentType", "Count:" + dataSnapshot.getChildrenCount());
                    if (dataSnapshot.getChildrenCount() > 0) {
                        new PaymentTypeParser(PaymentActivity.this).executeOnExecutor(
                                AsyncTask.THREAD_POOL_EXECUTOR, dataSnapshot.getChildren());

                    } else {
                        onPaymentTypesParsed(null, false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mPbLoading.setVisibility(View.GONE);
                if (null != databaseError) {
                    AppLog.d("PaymentType", "Error:" + databaseError.getDetails() + "::" + databaseError.getMessage());

                } else {
                    AppLog.d("PaymentType", "Error!!");
                }
            }
        });
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ExpenseCursorLoader(PaymentActivity.this, AppConstants.LoaderConstants.LOADER_PAYMENT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null != data) {
            if (null == mLvPayment.getAdapter()) {
                mPaymentAdapter = new PaymentTypeAdapter(PaymentActivity.this, data);
                mLvPayment.setAdapter(mPaymentAdapter);

            } else {
                mPaymentAdapter.swapCursor(data);
                mPaymentAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (null != mPaymentAdapter) {
            mPaymentAdapter.swapCursor(null);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_payment_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;

        } else if (item.getItemId() == R.id.menu_add_payment_type) {
            showPaymentTypeDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPaymentTypeDialog() {
        AddPaymentTypeFragment addPaymentTypeFragment = AddPaymentTypeFragment.newInstance();
        addPaymentTypeFragment.show(getSupportFragmentManager(), "AddPaymentTFragment");
    }

    @Override
    public void onStatusChanged() {
        //getLoaderManager().restartLoader(DBConstants.LoaderId.PAYMENT_TYPE, null, this);
    }

    @Override
    public void onPaymentTypeAdded() {
        AppUtil.showSnackbar(mViewComplete, "Payment type added!");
        //getPaymentTypes();
        mViewComplete.postDelayed(new Runnable() {
            @Override
            public void run() {
                onPaymentTypesParsed(MySpends.getAllPaymentTypes(), true);
            }
        }, 500);
    }

    @Override
    public void onPaymentTypesParsed(ArrayList<PaymentType> spends, boolean isCashPaymentTypeAdded) {
        mPbLoading.setVisibility(View.GONE);
        if (null == spends) {
            spends = new ArrayList<>();
        }

        if (!isCashPaymentTypeAdded) {
            spends.add(0, PaymentType.getCashPaymentType());
        }

        if (null == mPaymentAdapter) {
            mPaymentAdapter = new PaymentTypeAdapter(PaymentActivity.this, spends);
            mLvPayment.setAdapter(mPaymentAdapter);

        } else {
            mPaymentAdapter.setData(spends);
        }
    }
}