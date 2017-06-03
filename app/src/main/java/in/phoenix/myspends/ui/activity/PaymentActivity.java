package in.phoenix.myspends.ui.activity;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import in.phoenix.myspends.R;
import in.phoenix.myspends.controller.ExpenseCursorLoader;
import in.phoenix.myspends.controller.PaymentTypeAdapter;
import in.phoenix.myspends.database.DBConstants;
import in.phoenix.myspends.ui.fragment.AddPaymentTypeFragment;
import in.phoenix.myspends.util.AppConstants;
import in.phoenix.myspends.util.AppUtil;

public class PaymentActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, PaymentTypeAdapter.OnStatusChangedListener
        , AddPaymentTypeFragment.OnPaymentTypeListener {

    private ListView mLvPayment = null;

    private PaymentTypeAdapter mPaymentAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.lt_toolbar);
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
        getLoaderManager().initLoader(DBConstants.LoaderId.PAYMENT_TYPE, null, this);
    }

    @Override
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
    }

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
        getLoaderManager().restartLoader(DBConstants.LoaderId.PAYMENT_TYPE, null, this);
    }

    @Override
    public void onPaymentTypeAdded() {
        AppUtil.showSnackbar(mViewComplete, "Payment type added!");
        getLoaderManager().restartLoader(DBConstants.LoaderId.PAYMENT_TYPE, null, this);
    }
}
